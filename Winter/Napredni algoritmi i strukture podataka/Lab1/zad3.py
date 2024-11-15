import numpy as np

vector_a = np.array([[1], [3], [5]])
vector_b = np.array([[2], [4], [6]])

mat_mul = vector_a @ vector_b.T
vect_dot = np.array(vector_a.flatten() @ vector_b.flatten())
print("vect_dot = ", vect_dot)
print("tip = ", type(vect_dot))
mat_exp = mat_mul * mat_mul
first_index = 0
second_index = 0
sub_mat = [[0 for i in range(2)] for j in range(2)]

for i in range(1, len(vector_a)):
    second_index = 0
    for j in range(1, len(vector_b)):
        sub_mat[first_index][second_index] = mat_exp[i][j]
        second_index = second_index + 1
    first_index = first_index + 1
