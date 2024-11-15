import numpy as np
from typing import Tuple


class SimpleLP:
    """
    Represents simple LP problem with tableau and indexing-vector of basis-columns

    members:
        self.tableau - stored simplex tableau

        self.basis  - contains ordered indices of identity submatrix within tableau. 
            This enables reading the solution from tableau
    """

    def __init__(self, A, b, c):
        """Creates simplex tableau assuming canonical form and b>=0 (self.tableau)

        Also creates an index-vector of basis-columns in the tableau (self.basis). 


        Args:
            A (np.array): canonical LP A (matrix)
            b (np.array): canonical LP b (1-D vector)
            c (np.array): canonical LP c (1-D vector)
        """
        c = np.array(c)
        b = np.array(b)
        A = np.array(A)
        m = A.shape[0]
        n = c.shape[0]

        zeroth_row = np.hstack([c, [0]*(m+1)])
        bottom_pack = np.hstack((A, np.identity(m), b.reshape(m, 1)))

        self.tableau = np.vstack((zeroth_row, bottom_pack))

        self.basis = np.arange(n, n+m) # identity submatrix in such specific problems is in the last columns

    def readSolution(self) -> Tuple[np.float32, np.array]:
        """Reads off the full solution from the tableau

        Returns:
            Tuple[np.float32, np.array]: returns objective value and 1-D vector of decisions
        """

        # TODO: add your code for objective function value here (None is not solution)
        obj = -1 * self.tableau[0, -1] #tražena vrijednost funkcije

        decisions = np.zeros((self.tableau.shape[1]-1))
        decisions[self.basis] = self.tableau[1:, -1] # uses basis index vector to assign values to basic variables

        return obj, decisions


class NaiveSimplex:
    """

    Represents all functionalities of "naive" simplex

    Raises:
        ValueError: if pivot row is 0

    """

    @staticmethod
    def pivot(lp_problem: SimpleLP, row: int, col: int) -> SimpleLP:
        """Does Gauss-Jordan elimination around the (row,col) 
        element of simplex tableau

        Args:
            lp_problem (SimpleLP): LP over which to operate
            row (int): pivot's row
            col (int): pivot's column

        Raises:
            ValueError: pivot cannot be in the row of the reduction factors

        Returns:
            SimpleLP: returns reference to LP (for chaining)
        """
        
        if row == 0: # cannot pivot around zeroth row in tableau
            raise ValueError

        # TODO: add your code here - tableau must be pivoted and basis updated (watch the order in basis vector!)

        pivot = lp_problem.tableau[row, col] #nađi pivota
        lp_problem.tableau[row, :] = lp_problem.tableau[row, :]/pivot  #sve elemente u retku podijeli s pivotom da dobijes 1 na mjestu pivota

        print('Tablica prije GJ eliminacije: \n', lp_problem.tableau)
        #Gauss-Jordanova eliminacija
        for i in range(lp_problem.tableau.shape[0]): #iteriraj po retcima
            if i != row:
                lp_problem.tableau[i, :] -= lp_problem.tableau[i, col] * lp_problem.tableau[row, :] #poništi element iznad pivota na nulu, a ostali kako ispadnu

        print('Baza prije azuriranja:\n', lp_problem.basis)

        lp_problem.basis[row - 1] = col #ažuriraj bazu


        return lp_problem

    @staticmethod
    def solve(lp_problem: SimpleLP) -> SimpleLP:
        """Solves the input LP

        Args:
            lp_problem (SimpleLP): Input LP to be solved

        Returns:
            SimpleLP: returns None if the problem is unbounded, or reference to the LP otherwise
        """

        # TODO: add your code here - should call pivot method, like this: NaiveSimplex.pivot(lp_problem)
        #vrti petlju dok god postoji negativnih brojeva u nultom retku tablice
        uvjet = True
        iteracije = 1000
        brojac = 0
        while (brojac < iteracije):
            nulti_redak = lp_problem.tableau[0, :]

            if any (nulti_redak < 0):
                uvjet = False
            if not uvjet:
                minimum = float('inf')
                minimum_redak = 0
                stupac_pivota = np.argmin(nulti_redak)
                broj_stupaca = lp_problem.tableau.shape[1]
                for i in range(lp_problem.tableau.shape[0]):
                    indeks = lp_problem.tableau.shape[1] - 1
                    if ((lp_problem.tableau[i, indeks]/lp_problem.tableau[i,stupac_pivota]) < minimum):
                        minimum_redak = i
                        minimum = (lp_problem.tableau[i, indeks]/lp_problem.tableau[i,stupac_pivota])
                print(minimum_redak, stupac_pivota)
                
                NaiveSimplex.pivot(lp_problem, minimum_redak, stupac_pivota)
            brojac+=1
            print("Nova tablica:\n", lp_problem.tableau)

        return lp_problem
    

A = np.array([[3, 3], [-5, 6]], dtype=np.float32)
b = np.array([1, 10], dtype=np.float32)
c = np.array([-8, 8], dtype=np.float32)

lp_problem = SimpleLP(A, b, c)

print(lp_problem.readSolution())


# ispis je:
# (-0.0, array([ 0.,  0.,  1., 10.]))

NaiveSimplex.pivot(lp_problem,1,1) # (krivo odabrana) pivot operacija koja izaziva pogorsanje fje cilja

print('Tableau nakon pivota:\n', lp_problem.tableau)

# ispis je:
""" Tableau nakon pivota:
[[-16.           0.          -2.66666667   0.          -2.66666667]
    [  1.           1.           0.33333333   0.           0.33333333]
    [-11.           0.          -2.           1.           8.        ]] """

print('Basis nakon pivota:', lp_problem.basis)

# ispis je:
# Basis nakon pivota: [1 3]

lp_problem = SimpleLP(A, b, c)

print(NaiveSimplex.solve(lp_problem).readSolution())

# ispis je:
# (-2.6666666666666665, array([ 0.33333333,  0.        ,  0.        , 11.66666667]))