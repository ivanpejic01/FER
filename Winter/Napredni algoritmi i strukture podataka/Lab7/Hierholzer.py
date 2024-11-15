from collections import deque
from typing import List, Dict, Tuple


Graph = Dict[str, List[str]]
Circuit = List[str]
EulerPath = List[str]


def augmented_hierholzer(G: Graph, start: str) -> Tuple[EulerPath, List[Circuit]]:
    """
    Args:
        G (Graph): A Graph as an adjacency matrix. Assumed to be Eulerian.
        start (str): Starting node for the Hierholzer algorithm.
    Returns:
        Tuple[EulerPath, List[Circuit]]: A tuple containing an Eulerian path in the Euler graph
        and a list of all the circuits found on the path.
    """
    stack = deque()
    stack.append(start)
    print("Na stog sam dodao vrh ", start)
    
    #TODO: 
    cycle = []
    cycles = []
    path = []
    while stack:
        u = stack[-1]
        adj = G[u]
        if len(adj) > 0:
            v = G[u][0]
            stack.append(v)
            print("Na stog sam dodao vrh ", v)
            G[u].remove(v)
            G[v].remove(u)
        else:
            if len(stack) > 1:
                cycle.append(stack[-1])
                if (len(cycle) > 2 and cycle[0] == cycle[-1]):
                    print("ciklus ", cycle)
                    cycles.append(cycle)
                    cycle = []
                    
            
            print("Skidam sa stoga ", stack[-1])
            path.append(stack.pop())

            
            
            
    return path, cycles

import copy

G = {'a': ['b', 'c', 'd', 'e'],
     'b': ['a', 'd', 'e'],
     'c': ['a', 'e'],
     'd': ['a', 'b', 'e'],
     'e': ['a', 'b', 'c', 'd']}
     
G1 = copy.deepcopy(G)

path, circles = augmented_hierholzer(G1, 'b')
path.reverse()

assert path == ['b', 'a', 'c', 'e', 'a', 'd', 'b', 'e', 'd']
assert circles == [['d', 'e', 'b', 'd'], ['e', 'b', 'd', 'a', 'e'], ['a', 'e', 'c', 'a'], ['b', 'd', 'a', 'e', 'c', 'a', 'b']]

G1 = copy.deepcopy(G)

path, circles = augmented_hierholzer(G1, 'd')
path.reverse()

assert path == ['d', 'a', 'b', 'd', 'e', 'a', 'c', 'e', 'b']
assert circles == [['e', 'c', 'a', 'e'], ['b', 'e', 'c', 'a', 'e', 'd', 'b'], ['a', 'e', 'd', 'b', 'a'], ['d', 'b', 'a', 'd']]