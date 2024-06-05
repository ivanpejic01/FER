kernel void parallelJacobi(global float* input, global float* output, const int m, const int n) {

	int i = get_global_id(0) + 1;
	int j = get_global_id(1) + 1;
	//printf("Pokrenut kernel, id1 = %d id2 = %d\n", i, j);
	if (i <= m && j <= n) {
		output[i*(m+2)+j] = 0.25 * (input[(i-1)*(m+2)+j] + input[(i+1)*(m+2)+j] + input[i*(m+2)+j-1] + input[i*(m+2)+j+1]);
	}

}

kernel void parallelDeltaSq(global float* old, global float* new, global float* result, const int m, const int n) {

	int i = get_global_id(0) + 1;
	int j = get_global_id(1) + 1;
	float tmp;

	if (i <= m && j <= n) {
		//printf("oduzimam %f - %f\n", new[i * (m + 2) + j], old[i * ( m + 2) + j]);
		tmp = new[i * (m + 2) + j] - old[i * ( m + 2) + j];
		result[i * (m + 2) + j] = tmp * tmp;
	}

}
