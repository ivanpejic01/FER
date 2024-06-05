#define _CRT_SECURE_NO_WARNINGS
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <CL/cl.h>
#include <time.h>

char SOURCE_FILE[] = "kernel.cl";

float countPi(int n) {
	float h = 1.0f / (float)n;
	float sum = 0.0f;


	for (int i = 1; i <= n; i++) {
		float x = h * ((float)i - 0.5f);
		sum += 4.0f / (1.0f + x * x);
	}

	float serialPi = h * sum;
	return serialPi;
}

int main(int argc, char** argv) {

	int n;
	clock_t start, end;	
	float cpu_time_used;
	clock_t start_s, end_s;
	float cpu_time_used_s;

	printf("Koliko elemenata reda zelite? ");
	scanf("%d", &n);

	printf("Zadano je %d elemenata reda\n", n);

	FILE* file = fopen(SOURCE_FILE, "rb");

	if (!file) {
		perror("Error opening file");
		return -1;
	}

	fseek(file, 0, SEEK_END);
	long fileSize = ftell(file);
	rewind(file);

	char* buffer = (char*)malloc(fileSize + 1);

	size_t bytesRead = fread(buffer, 1, fileSize, file);
	//printf("filesize = %d a bytesread = %d\n", fileSize, bytesRead);
	if (bytesRead != fileSize) {
		fprintf(stderr, "Error reading file: %s\n", SOURCE_FILE);
		fclose(file);
		free(buffer);
		return NULL;
	}

	buffer[fileSize] = '\0';
	//printf("Velicina bajtova = %d\n", bytesRead);
	//printf("Cijeli program je %s\n", buffer);
	fclose(file);

	start = clock();

	cl_platform_id platform;
	clGetPlatformIDs(1, &platform, NULL);

	cl_device_id device; 
	clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, NULL);

	/*cl_device_fp_config double_support;
	clGetDeviceInfo(device, CL_DEVICE_DOUBLE_FP_CONFIG, sizeof(double_support), &double_support, NULL);
	if (double_support == 0) {
		printf("Device does not support double precision floating point.\n");
		return -1;
	}*/

	cl_context context;
	context = clCreateContext(0, 1, &device, NULL, NULL, NULL);

	cl_command_queue queue = clCreateCommandQueue(context, device, 0, NULL);

	cl_program program = clCreateProgramWithSource(context,
		1, //broj stringova = broj kernel funkcija
		&buffer, //kod kernela
		NULL,
		NULL
	);


	clBuildProgram(program,
		1,
		&device,
		NULL,
		NULL, NULL);

	cl_kernel kernel = clCreateKernel(program, "computePi", NULL);

	float* resultArray = (float*)malloc(n * sizeof(float));
	cl_mem result_buffer = clCreateBuffer(context,
		CL_MEM_WRITE_ONLY, //spremnik readonly, kopiranje niza inputArray u memoriju uredaja
		n * sizeof(float), //velicina ulaznog niza
		NULL,
		NULL
	);

	int num = n;
	clSetKernelArg(kernel, 0, sizeof(cl_mem), (void*)&result_buffer);
	clSetKernelArg(kernel, 1, sizeof(int), &num);

	size_t global_work_size = (int) n; //postojat ce n/4 dretve, dakle svaka obavi trecinu zadataka
	size_t local_work_size = 64;

	clEnqueueNDRangeKernel(queue,
		kernel,
		1, NULL, //broj dimenzija i posmak indeksa
		&global_work_size, //broj dretvi
		&local_work_size, //velicina radne grupe, ako se ne navede onda je 1
		NULL, NULL, NULL
	);

	clFinish(queue);

	end = clock();

	clEnqueueReadBuffer(
		queue,
		result_buffer,
		CL_TRUE,
		0,
		n * sizeof(float),
		resultArray,
		NULL, NULL, NULL
	);

	float numPi = 0.0;
	for (int i = 0; i < global_work_size; i++) {
		numPi += resultArray[i];
	}
	cpu_time_used = (float)(end - start) / CLOCKS_PER_SEC;
	printf("Paralelni pi = %f\n", numPi);
	printf("Paralelna izvedba trajala je %f sekundi\n", cpu_time_used);

	start_s = clock();
	float serialPi = countPi(n);
	printf("Slijedni pi = %f\n", serialPi);
	end_s = clock();
	cpu_time_used_s = (float)(end_s - start_s) / CLOCKS_PER_SEC;
	printf("Slijedna izvedba trajala je %f sekundi\n", cpu_time_used_s);

	float speedup = (float)cpu_time_used_s / cpu_time_used;
	printf("Ubrzanje = %f\n", speedup);
	

	//printf("Gotov kernel\n");
	clReleaseKernel(kernel);
	clReleaseProgram(program);
	clReleaseContext(context);
	clReleaseCommandQueue(queue);


	return 0;
}