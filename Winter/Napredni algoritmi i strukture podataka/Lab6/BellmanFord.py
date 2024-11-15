from typing import Tuple, List, Union, Generator

INF = float('inf')
AdjMatrix = List[List[int]]


def create_adj_matrix(n_nodes: int) -> AdjMatrix:
    """
    Create a (weighted) adjacency matrix for a graph with `n_nodes` nodes.
    All of the weights are set to 0.

    Args:
        n_nodes (int): Number of nodes in the graph.

    Returns:
        AdjMatrix: The adjacency matrix as a list of lists.
    """
    return [[0] * n_nodes for _ in range(n_nodes)]


class BellmanFordNode:
    """
    Helper class for solving problems with the BellmanFordAlgorithm.

    Attributes
    ----------
    d: int | float
        The current distance to a node. Infinite if the node is unreachable.
    prev: int
        The index of the previous node in the solution graph.
    """

    def __init__(self, d_value=INF, prev_node=None):
        self.d = d_value
        self.prev = prev_node

    def __str__(self):
        return f'{self.prev}/{self.d}'

    def __repr__(self):
        return f'BellmanFordNode(d={self.d}, prev={self.prev})'

    def __eq__(self, other):
        if isinstance(other, BellmanFordNode):
            return (self.d, self.prev) == (other.d, other.prev)
        return False


class NegativeCycleError(Exception):
    """Class used to raise an error when a negative cycle is present in a graph"""
    pass


EdgeIterator = Generator[Tuple[int, int, int], None, None]


class BellmanFordSolution:
    """Class which holds the solution to a Bellman-Ford problem.

    Attributes
    ----------

    start: int
        The index of the starting node in the solution.
    distances: List[BellmanFordNode]
        List of distances to each of the nodes, indexed the same way as the adjacency matrix. The node indices should be the same.
    """

    def __init__(self, start: int, solution: List[BellmanFordNode]) -> None:
        self.start = start
        self.distances = solution

    def path_to(self, node: int) -> Tuple[List[int], int]:
        """Fetches the path and total distance to a node from the starting node.
        If there is no path to the wanted node, returns an empty list and INF.

        Args:
            node (int): The index of the node whose path we wish to fetch.

        Returns:
            Tuple[List[int], int]: The list of node indices in the order they are traversed and the total distance as an integer.
        """
        if node >= len(self.distances) or node < 0:
            raise ValueError(
                f'Invalid node passed as argument to function, must be within: [0, {len(self.distances) - 1}]')
        path = []
        # TODO: Implement the path finding and fill out the distance

        cvor = node
        distance = self.distances[node].d

        print("Inicijani cvor: ", node)
        print("Udaljenost: ", distance)
        if (distance is not INF):
            while cvor is not None:
                print("Dodajem cvor u path: ", cvor)
                path.insert(0, cvor)
                cvor = self.distances[cvor].prev

        return path, distance

    @staticmethod
    def _create_initial_solution(nodes: int) -> List[BellmanFordNode]:
        """Static method which creates a starting point for the inital solution of the Bellman-Ford problem.

        Args:
            nodes (int): Number of nodes in the graph for which we are solving.
        """
        return [BellmanFordNode() for _ in range(nodes)]

    @staticmethod
    def edges(W: AdjMatrix) -> EdgeIterator:
        """Helper static method for iterating over the edges of the graph in a for-each fashion.

        Usage: 'for node_a, node_b, weight in BellmanFordSolution.edges(W): ...'

        Args:
            W (AdjMatrix): The weighted adjacency matrix of the graph.

        Returns:
            EdgeIterator: Iterator over the edges of the graph.
        """
        for i, row in enumerate(W):
            for j, val in enumerate(row):
                if val != 0:
                    yield i, j, val

    @staticmethod
    def solve(W: AdjMatrix, start: int) -> 'BellmanFordSolution':
        """Solve the Bellman-Ford problem on the graph represented by the given (weighted) adjacency matrix,
        starting from the `start` node.

        Args:
            W (AdjMatrix): The (weighted) adjacency matrix of the graph.
            start (int): The starting node of the problem.
        Throws:
            NegativeCycleError: If there is a negative cycle in the graph.        
        Returns:
            BellmanFordSolution: The BellmanFordSolution object containing the solution to the problem and the starting node.

        """
        n_nodes = len(W)
        D = BellmanFordSolution._create_initial_solution(n_nodes)
        # TODO: Solve the problem with the Bellman-Ford algorithm.
        # Throw a NegativeCycleError if there is a negative cycle in the graph, i.e. if there is no solution.

        D[start].d = 0

        for i in range(n_nodes - 1):
            for pocetak, kraj, tezina in BellmanFordSolution.edges(W):
                if D[pocetak].d + tezina < D[kraj].d:
                    D[kraj].d = D[pocetak].d + tezina
                    D[kraj].prev = pocetak

        for pocetak, kraj, tezina in BellmanFordSolution.edges(W):
            if D[pocetak].d + tezina < D[kraj].d:
                raise NegativeCycleError(f'Negativni ciklus detektiran!')

        return BellmanFordSolution(start, D)

    def __repr__(self) -> str:
        return f'BellmanFordSolution({self.start}, {self.distances})'


W = create_adj_matrix(6)
W[0] = [0, 10, 20, 0, 0, 0]
W[1] = [0, 0, 0, 50, 10, 0]
W[2] = [0, 0, 0, 20, 33, 0]
W[3] = [0, 0, 0, 0, -20, -2]
W[4] = [0, 0, 0, 0, 0, 1]
W[5] = [0, 0, 0, 0, 0, 0]

solution = BellmanFordSolution.solve(W, 0)
path, distance = solution.path_to(3)
print(f"Expected Path and Distance: ([0, 2, 3], 40)")
print(f"Your Path and Distance: ({path}, {distance})")
