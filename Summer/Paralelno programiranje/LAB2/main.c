#include <mpi.h>
#include <stdio.h>
#include <windows.h> 
#include <time.h>
#include <stdbool.h>
#include <stdlib.h>
#include <math.h>

#define ROWS 6
#define COLS 7
#define HUMAN 1
#define CPU 2
#define DEPTH 8
#define SEND_BOARD_TAG 1
#define WORKER_REQUESTS_TASK_TAG 2
#define WORKER_RESPONDS_TO_TASK_TAG 3
#define MASTER_SENDS_TASK_TAG 4
#define MASTER_ENDS_SENDING_TAG 5
#define MASTER_ENDS_GAME 6
#define NULL_VALUE_FOR_TASKS -10000.0

struct WinnerReturn {
	bool winnerExist;
	int winner;
};

void printString(char str[]) {
	printf("%s\n", str);
	fflush(stdout);
}



void printBoard(int board[ROWS][COLS]) {
	for (int i = 0; i < ROWS; i++) {
		for (int j = 0; j < COLS; j++) {
			printf("%d ", board[i][j]);
		}
		printf("\n");
		fflush(stdout);
	}
}
int highestRowOfColumn(int board[ROWS][COLS], int column) { //column od 0
	
	int row = 0;
	for (int i = 0; i < ROWS; i++) {
		if (board[i][column] != 0) {
			row = i;
			break;
		}
	}

	return row;
}

void newMove(int board[ROWS][COLS], int player, int column) { //column od 1
	int insertRow = 5;
	for (int i = 0; i < ROWS; i++) {
		if (board[i][column - 1] == 0) {
			insertRow = i;
		}
	}
	board[insertRow][column - 1] = player;

}

void removeMove(int board[ROWS][COLS], int column) { //column od 1
	int deleteRow = 5;
	for (int i = 0; i < ROWS; i++) {
		if (board[i][column - 1] != 0) {
			deleteRow = i;
			break;
		}
	}
	board[deleteRow][column - 1] = 0;
}

int getUserMove(int board[ROWS][COLS]) {
	int move;
	while (1) {
		
		printf("Koji je sljedeci potez? Potez moze biti jedan stupac od 1 do 7. ");
		fflush(stdout);
		int scan = scanf_s("%d", &move);

		if (move < 1 || move > 7) {
			printString("Potez nije dopusten. Mora biti broj u intervalu [1,7]!");
		}
		else {
			if (board[0][move - 1] != 0) {
				printString("Stupac je pun!");
				printf("Koji je sljedeci potez? Potez moze biti jedan stupac od 1 do 7. ");
				fflush(stdout);
				scanf_s("%d", &move);
			}
			newMove(board, 1, move);
			return move;
		}

	}
}




struct WinnerReturn gameEnd(int board[ROWS][COLS], int lastCol) { //lastCol od 1
	int seq, player, row, col, r, c;
	bool winner = false;
	struct WinnerReturn isWinner;
	//assert(lastCol <= COLS);

	col = lastCol - 1;
	row = highestRowOfColumn(board, col);
	//printf("Row = %d\n", row);
	//fflush(stdout);

	player = board[row][col];
	
	//uspravno
	if (row <= 2) {
		seq = 1;
		r = row + 1;
		while (r <= ROWS - 1 && board[r][col] == player) {
			seq++;
			r++;
		}
		if (seq > 3) {
			//printString("Uspravno!");
			isWinner.winnerExist = true;
			isWinner.winner = player;
			return isWinner;
		}
	}

	//vodoravno
	seq = 0;
	c = col;
	while ((c - 1) >= 0 && board[row][c - 1] == player) {
		c--;
	}
	while (c < COLS && board[row][c] == player) {
		seq++;
		c++;
	}
	if (seq > 3) {
		//printString("Vodoravno");
		isWinner.winnerExist = true;
		isWinner.winner = player;
		return isWinner;
	}

	//koso s lijeva nadesno
	seq = 0; r = row; c = col;
	while ((c - 1) >= 0 && (r + 1) >= ROWS - 1 && board[r + 1][c - 1] == player)
	{
		c--; r++;
	}
	while (c < COLS && r < ROWS && board[r][c] == player)
	{
		c++; r++; seq++;
	}

	if (seq > 3) {
		//printString("S lijeva nadesno");
		isWinner.winnerExist = true;
		isWinner.winner = player;
		return isWinner;
	}

	//koso s desna nalijevo
	seq = 0; r = row; c = col;
	while ((c - 1) >= 0 && (r + 1) < ROWS && board[r + 1][c - 1] == player)
	{
		c--; r++;
	}
	while (c < COLS && r >= 0 && board[r][c] == player)
	{
		c++; r--; seq++;
	}
	if (seq > 3) {
		//printString("S desna nalijevo");
		isWinner.winnerExist = true;
		isWinner.winner = player;
		return isWinner;
	}

	isWinner.winnerExist = false;
	isWinner.winner = 0;
	return isWinner;

}

double evaluate(int board[ROWS][COLS], int lastMover, int lastCol, int iDepth) { //lastCol od 1

	int newMover;
	double dResult, dTotal;
	bool bAllLose = true, bAllWin = true;
	int iMoves;


	struct WinnerReturn gameResult = gameEnd(board, lastCol);
	if (gameResult.winnerExist) { //ispitivanje je li igra gotova
		if (gameResult.winner == CPU) {
			return 1;
		}
		else {
			return -1;
		}
	}

	//ako igra nije gotova

	if (lastMover == CPU) {
		newMover = HUMAN;
	}
	else {
		newMover = CPU;
	}
	if (iDepth == 0) {
		return 0;
	}
	iDepth--;

	dTotal = 0;
	iMoves = 0; //broj mogucih poteza na ovoj razini

	for (int column = 1; column <= COLS; column++) {
		if (board[0][column - 1] == 0) { //ako stupac nije pun
			iMoves++;
			newMove(board, newMover, column);
			dResult = evaluate(board, newMover, column, iDepth);
			removeMove(board, column);
			if (dResult > -1) {
				bAllLose = false;
			}
			if (dResult != 1) {
				bAllWin = false;
			}
			if (dResult == 1 && newMover == CPU) {
				return 1;
			}
			if (dResult == -1 && newMover == HUMAN) {
				return -1;
			}
			dTotal += dResult;
		}
	}

	if (bAllWin == true) {
		return 1;
	}
	if (bAllLose == true) {
		return -1;
	}

	dTotal = (double)dTotal/iMoves;
	return dTotal;

}

void flattenBoard(int board[ROWS][COLS], int flattenedBoard[], int rows, int cols) {
	for (int i = 0; i < ROWS; i++) {
		for (int j = 0; j < COLS; j++) {
			flattenedBoard[i * COLS + j] = board[i][j];
		}
	}
}

bool arrayContainsElement(int array[], int size, int element) {
	for (int i = 0; i < size; i++) {
		if (array[i] == element) {
			return true;
		}
	}
	return false;
}

void appendToTasksInUse(int tasksInUse[], int size, int element) {
	for (int i = 0; i < size; i++) {
		if (tasksInUse[i] == -1) {
			tasksInUse[i] = element;
			break;
		}
	}
}

int findMaxIndexInDoubleArray(double array[], int size, int firstIndex) {
	int maxIndex = firstIndex;
	double maxElement = array[firstIndex];
	for (int i = 0; i < size; i++) {
		if (i != firstIndex) {
			if (array[i] >= maxElement) {
				maxElement = array[i];
				maxIndex = i;
			}
		}
	}

	return maxIndex;
}

int main(int argc, char** argv) {

	MPI_Init(NULL, NULL);
	int rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	int size;
	MPI_Comm_size(MPI_COMM_WORLD, &size);
	if (argv[1]) {
		int tasksDepth = atoi(argv[1]);
		printf("Tasks depth int = %d\n", tasksDepth);
		fflush(stdout);
	}
	else {
		printf("Tasks depth = %s\n", argv[1]);
		fflush(stdout);
	}


	if (rank == 0) {
		printString("Ja sam glavni proces");
		clock_t start, end;
		double cpu_time_used;


		int board[ROWS][COLS] = { 0 }; //inicijalizacija igrace ploce
		struct WinnerReturn isWinner;



		bool gameEnded = false;

		while (!gameEnded) {

			int move = getUserMove(board);
			printBoard(board);
			start = clock();
			isWinner = gameEnd(board, move);
			if (isWinner.winnerExist) {
				gameEnded = true;
				printString("Human won game!");
				for (int i = 1; i < size; i++) {
					MPI_Send(1, 1, MPI_INT, i, MASTER_ENDS_GAME, MPI_COMM_WORLD);
				}
				break;
			}

			double tasks[7][7]; //inicijalizacija 49 zadataka
			int tasksInUse[7][7];

			//inicijalizacija 49 zadataka
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 7; j++) {
					tasks[i][j] = NULL_VALUE_FOR_TASKS;
				}
			}

			//incijalizacija polja za vodenje evidencije koristenja zadataka
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 7; j++) {
					tasksInUse[i][j] = 0;
				}
			}
				
			for (int i = 1; i < size; i++) {
				MPI_Send(board, ROWS * COLS, MPI_INT, i, SEND_BOARD_TAG, MPI_COMM_WORLD); //slanje ploce svim procesima
			}

			//printString("Poslao sam svim workerima plocu koja izgleda ovako");
			//printBoard(board);

			while (true) {
				MPI_Status status;
				double recvMessage[3];
				//printString("Cekam poruku od workera");
				MPI_Probe(MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
				int tag = status.MPI_TAG;
				//printString("Primio poruku od workera");

				if (tag == WORKER_REQUESTS_TASK_TAG) { //worker trazi zadatak
					bool found = false;
					MPI_Recv(recvMessage, 3, MPI_DOUBLE, MPI_ANY_SOURCE, WORKER_REQUESTS_TASK_TAG, MPI_COMM_WORLD, &status);
					int workerRank = status.MPI_SOURCE;
					
					//printString("Worker trazi zadatak");

					for (int i = 0; i < 7; i++) {
						for (int j = 0; j < 7; j++) {
							if (tasksInUse[i][j] == 0 && tasks[i][j] == NULL_VALUE_FOR_TASKS) {

								found = true;
								int sendMsg[2] = { i, j };
								
								MPI_Send(sendMsg, 2, MPI_INT, workerRank, MASTER_SENDS_TASK_TAG, MPI_COMM_WORLD);
								tasksInUse[i][j] = 1;
								//printf("Poslao zadatak %d %d workeru %d\n", i, j, workerRank);
								//fflush(stdout);
								break;
							}
						}
						if (found) {
							break;
						}
					}
					if (!found) {
						for (int i = 1; i < size; i++) {
							int sendMsg[2] = { 1, 2 };

							MPI_Send(sendMsg, 2, MPI_INT, i, MASTER_ENDS_SENDING_TAG, MPI_COMM_WORLD);
							//printString("Nema vise slobodnih zadataka");
						}
						break;
					}
				}
				else if (tag == WORKER_RESPONDS_TO_TASK_TAG) {
					MPI_Recv(recvMessage, 3, MPI_DOUBLE, MPI_ANY_SOURCE, WORKER_RESPONDS_TO_TASK_TAG, MPI_COMM_WORLD, &status);
					//printString("Worker odgovara na zadatak");
					double probability = recvMessage[0];
					int firstIndex = (int)recvMessage[1] - 1;
					int secondIndex = (int)recvMessage[2] - 1;
					//printf("Worker poslao %f %d %d\n", probability, firstIndex, secondIndex);
					//fflush(stdout);

					tasks[firstIndex][secondIndex] = probability;
					
				}
				
			}
			end = clock();
			cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;
			printf("Vrijeme izvrsavanja: %f sekundi\n", cpu_time_used);
			fflush(stdout);

			printString("Polje tasks nakon obavljenog posla");
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 7; j++) {
					printf("%f ", tasks[i][j]);
				}
				printf("\n");
				fflush(stdout);
			}

			//racunanje poteza kad su svi potezi skupljeni
			double movesProbabilities[7] = {-1.0};

			for (int i = 0; i < 7; i++) {
				double sum = 0.0;
				for (int j = 0; j < 7; j++) {
					if (tasks[i][j] != NULL_VALUE_FOR_TASKS) {
						sum += tasks[i][j];
					}
				}
				movesProbabilities[i] = (double)sum / 7;
			}

			for (int i = 0; i < 7; i++) {
				printf("Potez = %d vjerojatnost = %f\n", i + 1, movesProbabilities[i]);
				fflush(stdout);
			}

			int bestMove = findMaxIndexInDoubleArray(movesProbabilities, 7, 0);
			while (board[0][bestMove] != 0) {
				movesProbabilities[bestMove] = -movesProbabilities[bestMove];
				bestMove = findMaxIndexInDoubleArray(movesProbabilities, 7, bestMove);
				//printf("Novi najbolji potez = %d\n", bestMove);
				//fflush(stdout);
			}

			printf("Novi potez = %d\n", bestMove + 1);
			fflush(stdout);
			newMove(board, CPU, bestMove + 1);

			struct WinnerReturn isWinner = gameEnd(board, bestMove + 1);

			printString("Ploca nakon poteza CPU-a");
			printBoard(board);

			if (isWinner.winnerExist) {
				gameEnded = true;
				printString("CPU won game!");
				for (int i = 1; i < size; i++) {
					int sendMsg[2] = { 1, 2 };
					MPI_Send(sendMsg, 2, MPI_INT, i, MASTER_ENDS_GAME, MPI_COMM_WORLD);
				}
				break;
			}

			bool allNonZeros = true;
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					if (board[i][j] == 0) {
						allNonZeros = false;
					}
				}
			}

			if (allNonZeros && !isWinner.winnerExist) {
				gameEnded = true;
				printString("Draw game!");
				break;
			}

		}

	}
	else {
		int receivedBoard[ROWS][COLS];
		while (true) {
			//printString("Ja sam slave i cekam plocu");
			MPI_Recv(receivedBoard, ROWS * COLS, MPI_INT, 0, SEND_BOARD_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			//printString("Ja sam slave i primio sam plocu");
			while (true) {
				double sendMsg[2] = { (double)1, (double)2 };


				MPI_Send(sendMsg, 2, MPI_DOUBLE, 0, WORKER_REQUESTS_TASK_TAG, MPI_COMM_WORLD);
				//printString("Ja sam slave i zelim obaviti zadatak");
				int recvMsg[2];
				MPI_Status status;
				MPI_Recv(recvMsg, 2, MPI_INT, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

				if (status.MPI_TAG == MASTER_ENDS_GAME) {
					exit(1);
					//break;
				}
				if (status.MPI_TAG == MASTER_ENDS_SENDING_TAG) {
					//printString("Ja sam worker i master kaze da nema vise zadataka");
					break;
				}

				else {


					int firstMove = recvMsg[0] + 1;
					int secondMove = recvMsg[1] + 1;
					//printf("Ja sam slave i primio sam zadatak s indeksima %d %d\n", firstMove, secondMove);
					//fflush(stdout);
					newMove(receivedBoard, CPU, firstMove);
					newMove(receivedBoard, HUMAN, secondMove);
					double probability = evaluate(receivedBoard, HUMAN, secondMove, DEPTH);
					removeMove(receivedBoard, secondMove);
					removeMove(receivedBoard, firstMove);

					double response[3] = { probability, (double)firstMove, (double)secondMove };
					MPI_Send(response, 3, MPI_DOUBLE, 0, WORKER_RESPONDS_TO_TASK_TAG, MPI_COMM_WORLD);
					//printString("Ja sam slave i poslao sam zadatak");
					//break;
				}


			}
		}

	}

	
	MPI_Finalize();


	return 0;
}