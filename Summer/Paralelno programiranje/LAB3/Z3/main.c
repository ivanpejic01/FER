#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stddef.h>

#include "arraymalloc.h"
#include "boundary.h"
#include "jacobi.h"
#include "cfdio.h"

#include<CL/cl.h>
char SOURCE_FILE[] = "kernel.cl";


void boundarypsifloat(float* psi, int m, int n, int b, int h, int w) {


	int i, j;

	//BCs on bottom edge

	for (i = b + 1;i <= b + w - 1;i++)
	{
		psi[i * (m + 2) + 0] = (float)(i - b);
	}

	for (i = b + w;i <= m;i++)
	{
		psi[i * (m + 2) + 0] = (float)(w);
	}

	//BCS on RHS

	for (j = 1; j <= h; j++)
	{
		psi[(m + 1) * (m + 2) + j] = (float)w;
	}

	for (j = h + 1;j <= h + w - 1; j++)
	{
		psi[(m + 1) * (m + 2) + j] = (float)(w - j + h);
	}
}

int main(int argc, char **argv)
{
	int printfreq=1000; //output frequency
	double error, bnorm;
	double tolerance=0.0; //tolerance for convergence. <=0 means do not check

	//main arrays
	double *psi;
	//temporary versions of main arrays
	double *psitmp;

	//command line arguments
	int scalefactor, numiter;

	//simulation sizes
	int bbase=10;
	int hbase=15;
	int wbase=5;
	int mbase=32;
	int nbase=32;

	int irrotational = 1, checkerr = 0;

	int m,n,b,h,w;
	int iter;
	int i,j;

	double tstart, tstop, ttot, titer;

	//do we stop because of tolerance?
	if (tolerance > 0) {checkerr=1;}

	//check command line parameters and parse them

	if (argc <3|| argc >4) {
		printf("Usage: cfd <scale> <numiter>\n");
		return 0;
	}

	scalefactor=atoi(argv[1]);
	numiter=atoi(argv[2]);

	if(!checkerr) {
		printf("Scale Factor = %i, iterations = %i\n",scalefactor, numiter);
	}
	else {
		printf("Scale Factor = %i, iterations = %i, tolerance= %g\n",scalefactor,numiter,tolerance);
	}

	printf("Irrotational flow\n");

	//Calculate b, h & w and m & n
	b = bbase*scalefactor;
	h = hbase*scalefactor;
	w = wbase*scalefactor;
	m = mbase*scalefactor;
	n = nbase*scalefactor;

	printf("Running CFD on %d x %d grid in serial\n",m,n);

	//allocate arrays
	psi = (double*)malloc((m + 2) * (n + 2) * sizeof(double));
	psitmp = (double *) malloc((m+2)*(n+2)*sizeof(double));

	//zero the psi array
	for (i=0;i<m+2;i++) {
		for(j=0;j<n+2;j++) {
			psi[i*(m+2)+j]=0.0;
		}
	}

	//set the psi boundary conditions
	boundarypsi(psi,m,n,b,h,w);

	//compute normalisation factor for error
	bnorm=0.0;

	for (i=0;i<m+2;i++) {
			for (j=0;j<n+2;j++) {
			bnorm += psi[i*(m+2)+j]*psi[i*(m+2)+j];
		}
	}
	bnorm=sqrt(bnorm);
	printf("Bnorm1 = %f\n", bnorm);
	//begin iterative Jacobi loop
	printf("\nStarting main loop...\n\n");
	tstart = gettime();

	for (iter = 1;iter <= numiter;iter++) {

		//calculate psi for next iteration
		jacobistep(psitmp,psi,m,n);
	
		//calculate current error if required
		if (checkerr || iter == numiter) {
			error = deltasq(psitmp,psi,m,n);
			printf("Error before sqrt %f\n", error);
			error=sqrt(error);
			error=error/bnorm;
		}

		//quit early if we have reached required tolerance
		if (checkerr) {
			if (error < tolerance) {
				printf("Converged on iteration %d\n",iter);
				break;
			}
		}

		//copy back
		for(i=1;i<=m;i++) {
			for(j=1;j<=n;j++) {
				psi[i*(m+2)+j]=psitmp[i*(m+2)+j];
			}
		}

		//print loop information
		if(iter%printfreq == 0) {
			if (!checkerr) {
				printf("Completed iteration %d\n",iter);
			}
			else {
				printf("Completed iteration %d, error = %g\n",iter,error);
			}
		}
	}	// iter

	if (iter > numiter) iter=numiter;

	tstop=gettime();

	ttot=tstop-tstart;
	titer=ttot/(double)iter;

	//print out some stats
	printf("\n... finished\n");
	printf("After %d iterations, the error is %g\n",iter,error);
	printf("Time for %d iterations was %g seconds\n",iter,ttot);
	printf("Each iteration took %g seconds\n",titer);




	//output results
	//writedatafiles(psi,m,n, scalefactor);
	//writeplotfile(m,n,scalefactor);

	//free un-needed arrays
	free(psi);
	free(psitmp);

	//paralelno izvodenje - moj zadatak
	float* psi_p = (float*)malloc((m + 2) * (n + 2) * sizeof(float));
	float* psitmp_p = (float*)malloc((m + 2) * (n + 2) * sizeof(float));



	for (i = 0;i < m + 2;i++) {
		for (j = 0;j < n + 2;j++) {
			psi_p[i * (m + 2) + j] = 0.0f;
		}
	}

	//set the psi boundary conditions
	boundarypsifloat(psi_p, m, n, b, h, w);


	//compute normalisation factor for error
	bnorm = 0.0;

	for (i = 0;i < m + 2;i++) {
		for (j = 0;j < n + 2;j++) {
			bnorm += psi_p[i * (m + 2) + j] * psi_p[i * (m + 2) + j];
		}
	}
	bnorm = sqrt(bnorm);
	printf("Bnorm2 = %f\n", bnorm);

	float tstart_p, tstop_p, ttot_p, titer_p;
	float error_p;

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
	//printf("Cijeli program je \n%s\n", buffer);
	fclose(file);

	cl_int err;

	cl_platform_id platform;
	clGetPlatformIDs(1, &platform, NULL);

	cl_device_id device;
	clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, NULL);

	cl_context context;
	context = clCreateContext(0, 1, &device, NULL, NULL, NULL);

	cl_command_queue queue = clCreateCommandQueue(context, device, 0, NULL);

	cl_program program = clCreateProgramWithSource(context,
		1, //broj stringova = broj kernel funkcija
		&buffer, //kod kernela
		NULL,
		NULL
	);

	//printf("CLProgram zavrsen\n");


	err = clBuildProgram(program,
		1,
		&device,
		NULL,
		NULL, NULL);

	if (err != CL_SUCCESS) {
		// Get build log
		size_t log_size;
		clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, NULL, &log_size);
		char* log = (char*)malloc(log_size);
		clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, log_size, log, NULL);
		printf("Build log:\n%s\n", log);
		free(log);
		//check_error(err, "clBuildProgram");
	}
	//printf("Build zavrsen\n");
	cl_kernel kernelJacobi = clCreateKernel(program, "parallelJacobi", NULL);
	cl_kernel kernelDeltasq = clCreateKernel(program, "parallelDeltaSq", NULL);
	//printf("Kernel zavrsen\n");
	cl_mem input_buffer = clCreateBuffer(context,
		CL_MEM_READ_WRITE, //spremnik readonly, kopiranje niza inputArray u memoriju uredaja
		(m + 2) * (n + 2) * sizeof(float), //velicina ulaznog niza
		NULL,
		NULL
	);

	cl_mem output_buffer = clCreateBuffer(context,
		CL_MEM_READ_WRITE,
		(m + 2) * (n + 2) * sizeof(float),
		NULL,
		NULL
	);

	cl_mem deltasq_result_buffer = clCreateBuffer(context,
		CL_MEM_WRITE_ONLY,
		(m + 2) * (n + 2) * sizeof(float),
		NULL,
		NULL
	);
	//printf("Buferi zavrsen\n");
	size_t global_work_size[2] = { m, n };
	size_t local_work_size[2] = { 16, 16 };


	//begin iterative Jacobi loop
	printf("\nStarting parallel loop...\n\n");
	tstart_p = gettime();
	float errorP = 0.0f;
	//dio koji se paralelizira
	for (iter = 1;iter <= numiter;iter++) {
		clEnqueueWriteBuffer(queue, input_buffer, CL_TRUE, 0, (m + 2) * (n + 2) * sizeof(float), psi_p, 0, NULL, NULL);
		clEnqueueWriteBuffer(queue, output_buffer, CL_TRUE, 0, (m + 2) * (n + 2) * sizeof(float), psitmp_p, 0, NULL, NULL);
		//calculate psi for next iteration
		//jacobistep(psitmp, psi, m, n); -> ovo je serijski, to mijenjam
		
		clSetKernelArg(kernelJacobi, 0, sizeof(cl_mem), (void*)&input_buffer);
		clSetKernelArg(kernelJacobi, 1, sizeof(cl_mem), (void*)&output_buffer);
		clSetKernelArg(kernelJacobi, 2, sizeof(int), &m);
		clSetKernelArg(kernelJacobi, 3, sizeof(int), &n);
		clEnqueueNDRangeKernel(queue,
			kernelJacobi,
			2, NULL, //broj dimenzija i posmak indeksa
			global_work_size, //broj dretvi
			local_work_size, //velicina radne grupe, ako se ne navede onda je 1
			0, NULL, NULL
		);

		clFinish(queue);

		//printf("jacobi parallel zavrsen\n");
		clEnqueueReadBuffer(
			queue,
			output_buffer,
			CL_TRUE,
			0,
			(m + 2) * (n + 2) * sizeof(float),
			psitmp_p,
			NULL, NULL, NULL
		);

		clEnqueueReadBuffer(
			queue,
			input_buffer,
			CL_TRUE,
			0,
			(m + 2) * (n + 2) * sizeof(float),
			psi_p,
			NULL, NULL, NULL
		);


		
		//calculate current error if required

		if (checkerr || iter == numiter) {
			//error = deltasq(psitmp_p, psi_p, m, n); -> ovo isto treba paralelizirati
			
			clSetKernelArg(kernelDeltasq, 0, sizeof(cl_mem), (void*)&input_buffer);
			clSetKernelArg(kernelDeltasq, 1, sizeof(cl_mem), (void*)&output_buffer);
			clSetKernelArg(kernelDeltasq, 2, sizeof(cl_mem), (void*)&deltasq_result_buffer);
			clSetKernelArg(kernelDeltasq, 3, sizeof(int), &m);
			clSetKernelArg(kernelDeltasq, 4, sizeof(int), &n);
			clEnqueueNDRangeKernel(queue,
				kernelDeltasq,
				2, NULL, //broj dimenzija i posmak indeksa
				global_work_size, //broj dretvi
				local_work_size, //velicina radne grupe, ako se ne navede onda je 1
				0, NULL, NULL
			);

			clFinish(queue);
			//printf("deltasq parallel zavrsen\n");
			float* resultArray = (float*)malloc((m + 2) * (n + 2) * sizeof(float));
			clEnqueueReadBuffer(
				queue,
				deltasq_result_buffer,
				CL_TRUE,
				0,
				(m + 2) * (n + 2) * sizeof(float),
				resultArray,
				NULL, NULL, NULL
			);		
			errorP = 0.0f;

			for (int k = 1; k <= m; k++) {
				for (int l = 1; l <= n; l++) {
					errorP += resultArray[k * (m + 2) + l];
				}
			}
			
			//printf("Error before sqrt = %f\n", errorP);
			errorP = sqrt(errorP);
			errorP = errorP / (float)bnorm;
			//printf("New error = %f\n", errorP);
		}

		//quit early if we have reached required tolerance
		if (checkerr) {
			if (errorP < tolerance) {
				printf("Converged on iteration %d\n", iter);
				break;
			}
		}
		//clEnqueueCopyBuffer(queue, output_buffer, input_buffer, 0, 0, (m + 2) * (n + 2) * sizeof(float), 0, NULL, NULL);
		for (i = 1;i <= m;i++) {
			for (j = 1;j <= n;j++) {
				psi_p[i * (m + 2) + j] = psitmp_p[i * (m + 2) + j];
			}
		}
		//print loop information
		if (iter % printfreq == 0) {
			if (!checkerr) {
				printf("Completed iteration %d\n", iter);
			}
			else {
				printf("Completed iteration %d, error = %g\n", iter, errorP);
			}
		}
	}	// iter

	if (iter > numiter) iter = numiter;
	


	tstop_p = gettime();

	ttot_p = tstop_p - tstart_p;
	titer_p = ttot_p / (float)iter;

	//print out some stats
	printf("\n... finished\n");
	printf("After %d iterations, the error is %g\n", iter, errorP);
	printf("Time for %d iterations was %g seconds\n", iter, ttot_p);
	printf("Each iteration took %g seconds\n", titer_p);

	float speedup = ttot / ttot_p;
	printf("Speedup = %f\n", speedup);

	clReleaseMemObject(input_buffer);
	clReleaseMemObject(output_buffer);
	clReleaseMemObject(deltasq_result_buffer);
	clReleaseKernel(kernelJacobi);
	clReleaseKernel(kernelDeltasq);
	clReleaseProgram(program);
	clReleaseCommandQueue(queue);
	clReleaseContext(context);
	free(psi_p);
	free(psitmp_p);
	free(buffer);
	return 0;
}
