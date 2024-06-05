kernel void primeNumber(global int *A, global int *result, const int n) 
{
	int globalId = get_global_id(0);
	int globalSize = get_global_size(0);
	int flag = 0;
	int counter = 0;

	//printf("Globalni id = %d\n", globalId);
	//printf("Global size = %d\n", globalSize);

	for(int i = globalId; i < n; i+= globalSize) {
		flag = 0;
		int number = A[i];
		if (number > 1) {
		for (int j = 2; j <= number/2; j++) {
			if ((number % j) == 0) {
				flag = 1;
				break;
			}
		}
		
		if (flag == 0) {
			counter++;
			}
		}
	
	}
	//result += counter;
	atomic_add(result, counter);


	
}



