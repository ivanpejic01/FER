
kernel void computePi(global float* results, const int n) {

    int myId = get_global_id(0);
    //printf("Kernel with myId = %d\n", myId);
    int size = get_global_size(0);
    //printf("Global size = %d\n", size);
    int localSize = get_local_size(0);
    //printf("Local size = %d\n", localSize);

    float h = 1.0f / (float)n;
    float sum = 0.0f;
    for (int i = myId + 1; i <= n; i += size) {
        float x = h * ((float)i - 0.5f);
        sum += 4.0f / (1.0f + x * x);
    }
    float myPi = h * sum;
    //printf("My id = %d my pi = %f\n", myId, myPi);
    results[myId] = myPi;
}


