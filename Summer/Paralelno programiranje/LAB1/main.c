#include <mpi.h>
#include <stdio.h>
#include <windows.h> 
#include <time.h>

#define NEED_FORK_TAG 1
#define SEND_FORK_TAG 2
#define LEFT_FORK_TAG 3
#define RIGHT_FORK_TAG 4

int rightFork(int* leftForkDirty, int* hasLeftFork, int* rightForkDirty, int* hasRightFork, int* rank, int* left, int* right, int* sendRightFork, int* leftFirst, int* sendLeftFork, int* recvMsg);

int leftFork(int *leftForkDirty, int *hasLeftFork, int *rightForkDirty, int *hasRightFork, int *rank, int *left, int *right, int *sendRightFork, int *leftFirst, int *sendLeftFork, int *recvMsg) {
	int sendMsg;

	if (*recvMsg == RIGHT_FORK_TAG) {
		if (*leftForkDirty == 1) {

			sendMsg = LEFT_FORK_TAG;
			MPI_Send(&sendMsg, 1, MPI_INT, *left, SEND_FORK_TAG, MPI_COMM_WORLD);
			*leftForkDirty = 0;
			*hasLeftFork = 0;

		}
		else {
			*sendLeftFork = 1;
			if (*sendRightFork == 1) {
				*leftFirst = 0;
			}
			else {
				*leftFirst = 1;
			}
		}
		return 1;
	}
	else if (*recvMsg == LEFT_FORK_TAG) {

		if (*hasRightFork == 1) {
			int temp = rightFork(leftForkDirty, hasLeftFork, rightForkDirty, hasRightFork, rank, left, right, sendRightFork, leftFirst, sendLeftFork, recvMsg);
			if (temp == 1) {
				return 0;
			}
		}
		return 2;


	}
	
	
	

	
}

int rightFork(int* leftForkDirty, int* hasLeftFork, int* rightForkDirty, int* hasRightFork, int *rank, int *left, int *right, int* sendRightFork, int* leftFirst, int* sendLeftFork, int* recvMsg) {
	int sendMsg;
	if (*recvMsg == LEFT_FORK_TAG) {
		
		if (*rightForkDirty == 1) {

			sendMsg = RIGHT_FORK_TAG;
			MPI_Send(&sendMsg, 1, MPI_INT, *right, SEND_FORK_TAG, MPI_COMM_WORLD);
			*hasRightFork = 0;
			*rightForkDirty = 0;

		}
		else {

			*sendRightFork = 1;
			if (*sendLeftFork == 1) {
				*leftFirst = 1;
			}
			else {
				*leftFirst = 0;
			}
		}
		return 1;
	}
	else if (*recvMsg == RIGHT_FORK_TAG) {

		if (*hasLeftFork == 1) {
			int temp = leftFork(leftForkDirty, hasLeftFork, rightForkDirty, hasRightFork, rank, left, right, sendRightFork, leftFirst, sendLeftFork, recvMsg);
			if (temp == 1) {
				return 0;
			}
		}
		return 2;
	}


}

int main(int argc, char** argv) {
	int sendMsg;
	MPI_Init(NULL, NULL);
	int rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	int n;
	MPI_Comm_size(MPI_COMM_WORLD, &n);


	int left = (rank + 1) % n; // Indeks lijevog susjeda
	int right = (rank + n - 1) % n; // Indeks desnog susjeda
	srand(time(NULL) + rank);

	int hasLeftFork;
	int hasRightFork;
	int leftForkDirty = 1;
	int rightForkDirty = 1;
	int sendLeftFork = 0;
	int sendRightFork = 0;
	int leftFirst = 1;


	if (rank == 0) {
		hasLeftFork = 1;
		hasRightFork = 1;
	}

	else if (rank == n - 1) {
		hasLeftFork = 0;
		hasRightFork = 0;
	}
	else {
		hasLeftFork = 1;
		hasRightFork = 0;
	}
	if (n > 1) {
		while (1) {
			//faza dok mislim
			int sleepingTime = rand() % 5 + 1;
			for (int j = 0; j < rank; j++) {
				printf("\t");
			}
			printf("Mislim(%d)\n", rank);
			fflush(stdout);
			while (sleepingTime > 0) {


				int flag = 0;
				MPI_Status status;
				int recvMsg;
				MPI_Iprobe(MPI_ANY_SOURCE, NEED_FORK_TAG, MPI_COMM_WORLD, &flag, &status);

				if (flag == 1) { //netko trazi vilicu

					if (status.MPI_SOURCE == left) {
						if (hasLeftFork == 1) {
							MPI_Recv(&recvMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD, &status);
							leftFork(&leftForkDirty, &hasLeftFork, &rightForkDirty, &hasRightFork, &rank, &left, &right, &sendRightFork, &leftFirst, &sendLeftFork, &recvMsg);
						}
					}
					else if (status.MPI_SOURCE == right) {
						if (hasRightFork == 1) {
							fflush(stdout);
							MPI_Recv(&recvMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD, &status);
							rightFork(&leftForkDirty, &hasLeftFork, &rightForkDirty, &hasRightFork, &rank, &left, &right, &sendRightFork, &leftFirst, &sendLeftFork, &recvMsg);

						}

					}
				}

				sleepingTime--;
				Sleep(1000);
			}
			while (hasLeftFork == 0 || hasRightFork == 0) {
				if (hasLeftFork == 0) {
					for (int j = 0; j < rank; j++) {
						printf("\t");
					}
					printf("Trazim lijevu vilicu (%d)\n", rank);
					fflush(stdout);
					sendMsg = LEFT_FORK_TAG;
					int request;
					MPI_Send(&sendMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD);
				}
				do {
					int flag = 0;
					MPI_Status status;
					int recvMsg;

					MPI_Iprobe(MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &flag, &status);
					if (flag == 1) {

					if (status.MPI_TAG == SEND_FORK_TAG) {
						MPI_Recv(&recvMsg, 1, MPI_INT, left, SEND_FORK_TAG, MPI_COMM_WORLD, &status);
						hasLeftFork = 1;
						leftForkDirty = 0;

					}
					else if (status.MPI_TAG == NEED_FORK_TAG) {
							if (status.MPI_SOURCE == left) {

								MPI_Recv(&recvMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD, &status);
								if (hasLeftFork == 1) {
									
									int fork = leftFork(&leftForkDirty, &hasLeftFork, &rightForkDirty, &hasRightFork, &rank, &left, &right, &sendRightFork, &leftFirst, &sendLeftFork, &recvMsg);
									if (fork == 1) {
										sendMsg = LEFT_FORK_TAG;
										MPI_Send(&sendMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD);
									}
									else if (fork == 0) {
										sendMsg = RIGHT_FORK_TAG;
										MPI_Send(&sendMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD);
									}
									
								}

							}
							else if (status.MPI_SOURCE == right) {

								MPI_Recv(&recvMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD, &status);
								if (hasRightFork == 1) {
						
									int fork = rightFork(&leftForkDirty, &hasLeftFork, &rightForkDirty, &hasRightFork, &rank, &left, &right, &sendRightFork, &leftFirst, &sendLeftFork, &recvMsg);
									if (fork == 1) {
										sendMsg = RIGHT_FORK_TAG;
										MPI_Send(&sendMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD);
									}
									else if (fork == 0) {
										sendMsg = LEFT_FORK_TAG;
										int request;
										MPI_Send(&sendMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD);
									}

								}

							}

						}

					}
				} while (hasLeftFork == 0);

				if (hasRightFork == 0) {
					for (int j = 0; j < rank; j++) {
						printf("\t");
					}
					printf("Trazim desnu vilicu (%d)\n", rank);
					fflush(stdout);
					sendMsg = RIGHT_FORK_TAG;
					int request;
					MPI_Send(&sendMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD);
				}
				do {
					int flag = 0;
					MPI_Status status;
					int recvMsg;

					MPI_Iprobe(MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &flag, &status);
					if (flag == 1) {
					if (status.MPI_TAG == SEND_FORK_TAG) {
						MPI_Recv(&recvMsg, 1, MPI_INT, right, SEND_FORK_TAG, MPI_COMM_WORLD, &status);

						hasRightFork = 1;
						rightForkDirty = 0;

					}
					else if (status.MPI_TAG == NEED_FORK_TAG) {
						if (status.MPI_SOURCE == left) {
							MPI_Recv(&recvMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD, &status);
							if (hasLeftFork == 1) {

								int fork = leftFork(&leftForkDirty, &hasLeftFork, &rightForkDirty, &hasRightFork, &rank, &left, &right, &sendRightFork, &leftFirst, &sendLeftFork, &recvMsg);
								if (fork == 1) {
									sendMsg = LEFT_FORK_TAG;
									MPI_Send(&sendMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD);
								}
								else if (fork == 0) {
									sendMsg = RIGHT_FORK_TAG;
									MPI_Send(&sendMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD);
								}

							}

						}
						else if (status.MPI_SOURCE == right) {
							MPI_Recv(&recvMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD, &status);
							if (hasRightFork == 1) {

								int fork = rightFork(&leftForkDirty, &hasLeftFork, &rightForkDirty, &hasRightFork, &rank, &left, &right, &sendRightFork, &leftFirst, &sendLeftFork, &recvMsg);
								if (fork == 1) {
									sendMsg = RIGHT_FORK_TAG;
									MPI_Send(&sendMsg, 1, MPI_INT, right, NEED_FORK_TAG, MPI_COMM_WORLD);
								}
								else if (fork == 0) {
									sendMsg = LEFT_FORK_TAG;
									int request;
									MPI_Send(&sendMsg, 1, MPI_INT, left, NEED_FORK_TAG, MPI_COMM_WORLD);
								}

							}

						}
					}


					}

				} while (hasRightFork == 0);

			}

			//faza dok jedem
			if (hasLeftFork == 1 && hasRightFork == 1) {
				int sleepTime = (rand() % 5 + 1);
				for (int j = 0; j < rank; j++) {
					printf("\t");
				}
				printf("Jedem (%d)\n", rank);
				fflush(stdout);

				leftForkDirty = 1;
				rightForkDirty = 1;
				int flag = 0;
				MPI_Status status;
				int recvMsg;
				if (leftFirst == 1) {
					if (sendLeftFork == 1) {
						sendMsg = LEFT_FORK_TAG;

						MPI_Send(&sendMsg, 1, MPI_INT, left, SEND_FORK_TAG, MPI_COMM_WORLD);
						leftForkDirty = 0;
						hasLeftFork = 0;
					}
					if (sendRightFork == 1) {
						sendMsg = RIGHT_FORK_TAG;
						MPI_Send(&sendMsg, 1, MPI_INT, right, SEND_FORK_TAG, MPI_COMM_WORLD);
						rightForkDirty = 0;
						hasRightFork = 0;
					}
				}
				else {
					if (sendRightFork == 1) {
						sendMsg = RIGHT_FORK_TAG;
						MPI_Send(&sendMsg, 1, MPI_INT, right, SEND_FORK_TAG, MPI_COMM_WORLD);
						rightForkDirty = 0;
						hasRightFork = 0;
					}
					if (sendLeftFork == 1) {
						sendMsg = LEFT_FORK_TAG;
						MPI_Send(&sendMsg, 1, MPI_INT, left, SEND_FORK_TAG, MPI_COMM_WORLD);
						leftForkDirty = 0;
						hasLeftFork = 0;
					}
				}
				sendLeftFork = 0;
				sendRightFork = 0;
				leftFirst = 1;
				Sleep(sleepTime * 1000);

			}

		}
	}


	else {
		printf("Nedovoljan broj filozofa koji je zadan!\n");
	}
	MPI_Finalize();
	return 0;
}