#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <CL/cl.h>
#include <time.h>

#define N 1048576


char SOURCE_FILE[] = "kernel.cl";

int main(int argc, char** argv)
{
	clock_t start, end;
	float cpu_time_used;
	int* inputArray = (int*)malloc(N * sizeof(int));
	//inicijalizacija ulaznog niza na N brojeva, svaki je broj redni broj u nizu
	for (int i = 0; i < N; i++) {
		inputArray[i] = i + 1;
	}

	//printf("Zadnji element = %d\n", inputArray[N - 1]);

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
	//1. inicijalizacija platforme i uredaja
	//platforme: OpenCL, CUDA
	cl_platform_id platform;
	clGetPlatformIDs(1, &platform, NULL); //zelim jednu platformu

	//dohvatiti konkretni uredaj, CPU ili GPU
	cl_device_id device; //lista uredaja
	clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, NULL); //dodaj 1 uredaj u listu

	//2. stvaranje konteksta i reda izvodenja
	//stvaranje konteksta
	cl_context context;
	context = clCreateContext(0, 1, &device, NULL, NULL, NULL); //prvi parametar opcionalan
	//drugi parametar broj uredaja, treci je lista uredaja

	//inicijalizacija reda izvodenja
	cl_command_queue queue = clCreateCommandQueue(context, device, 0, NULL); //3 parametar je opc
	//zadnji parametar - ne zelim kod greske


	//3. ucitavanje i prevodenje programa (kernela)
	
	cl_program program = clCreateProgramWithSource(context,
		1, //broj stringova broj kernel funkcija
		&buffer, //kod kernela
		NULL,
		NULL
	);

	//prevodenje programa
	clBuildProgram(program,
		1,
		&device,
		NULL,
		NULL, NULL);


	//kernel
	cl_kernel kernel = clCreateKernel(program, "primeNumber", NULL);

	//4. priprema podataka

	cl_mem a_buffer = clCreateBuffer(context,
		CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, //spremnik readonly, kopiranje niza inputArray u memoriju uredaja
		N * sizeof(int), //velicina ulaznog niza
		inputArray,
		NULL
	); // ovaj spremnik je readonly
	int result = 0;
	cl_mem result_buffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, sizeof(int), &result, NULL); //pisanje


	//5. argumenti kernel funkcije
	int n = N;
	clSetKernelArg(kernel, 0, sizeof(cl_mem), (void*)&a_buffer);
	clSetKernelArg(kernel, 1, sizeof(cl_mem), (void*)&result_buffer);
	clSetKernelArg(kernel, 2, sizeof(int), &n);

	//6. izvodenje kernela, kerneli idu u red cekanja koji je default slijedan
	size_t global_work_size = n;
	size_t local_work_size = 64 * 2;

	clEnqueueNDRangeKernel(queue,
		kernel,
		1, NULL, //broj dimenzija i posmak indeksa
		&global_work_size, //broj dretvi
		&local_work_size, //velicina radne grupe, ako se ne navede onda je 1
		NULL, NULL, NULL
	);

	clFinish(queue);

	//7. citanje izlaznih podataka
	//c_buffer mi treba za citanje ali ga nemam
	
	clEnqueueReadBuffer(
		queue,
		result_buffer,
		CL_TRUE,
		0,
		sizeof(int),
		&result,
		NULL, NULL, NULL
	);

	end = clock();
	cpu_time_used = ((float)(end - start)) / CLOCKS_PER_SEC;

	printf("Broj prostih brojeva u skupu [1, %d] = %d\n", n, result);
	printf("Vrijeme izvrsavanja: %f sekundi\n", cpu_time_used);


	clReleaseKernel(kernel);
	clReleaseProgram(program);
	clReleaseContext(context);
	clReleaseCommandQueue(queue);

	return 0;
}
