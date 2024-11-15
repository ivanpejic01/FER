import heapq

TOL_DEC = 3
TOLERANCE = 10**-TOL_DEC


class Node:
    """Node in a Huffman tree
    """

    def __init__(self, prob, symbol, left=None, right=None):
        self.prob = prob  # probability of symbol
        self.symbol = symbol
        self.left = left
        self.right = right

        # incoming tree direction to node (0/1) - root has ''
        self.code = ''

    def __lt__(self, other: 'Node') -> bool:
        """enables comparisons between objects

        Args:
            other (Node): other object in comparison

        Returns:
            bool: True if self is LESS THAN other,
                  False otherwise
        """
        # TODO: ovdje dodajte svoj kod za usporedbu. Pazite na numeričku toleranciju!
        if roundToDecimals(self.prob, TOL_DEC) != roundToDecimals(other.prob, TOL_DEC):
            return self.prob < other.prob + TOLERANCE
        else:
            return self.symbol < other.symbol
        pass


def Huffman_tree(symbol_with_probs: dict) -> Node:
    """Builds Huffman tree

    Args:
        symbol_with_probs (dict): dictionary symbol-probability that describes the problem

    Returns:
        Node: root of the built Huffman tree
    """
    symbols = symbol_with_probs.keys()
    nodes_queue = []

    # TODO: ovdje dovršite izgradnju stabla
    # HINT: spajanje dva stringa s1 i s2 u sortirani se moze postici sa: ''.join(sorted(s1+s2))
    # HINT: za rad sa prioritetnim redom vam mogu zatrebati metode heapq.heappop i heapq.heappush
    print("Simboli = ", symbols)
    for znak in symbols:
        heapq.heappush(nodes_queue, Node(symbol_with_probs[znak], znak))

    while len(nodes_queue) > 1:
        prvi = heapq.heappop(nodes_queue)
        drugi = heapq.heappop(nodes_queue)
        novi = Node(prvi.prob + drugi.prob,
                    ''.join(sorted(prvi.symbol + drugi.symbol)), prvi, drugi)
        novi.left.code = "0"
        novi.right.code = "1"
        heapq.heappush(nodes_queue, novi)
        print("Novi cvor ", novi.symbol, " ", novi.prob)

    return nodes_queue[0]


####################### IT'S BETTER NOT TO MODIFY THE CODE BELOW ##############


def calculate_codes(node: Node, val: str = '', codes=dict()) -> dict:
    # calculates codewords for Huffman subtree starting from node

    newVal = val + str(node.code)

    if (node.left):
        calculate_codes(node.left, newVal, codes)
    if (node.right):
        calculate_codes(node.right, newVal, codes)

    if (not node.left and not node.right):
        codes[node.symbol] = newVal

    return codes


def Huffman_encode(data: str, coding: dict) -> str:
    # encodes
    encoding_output = []
    for c in data:
        encoding_output.append(coding[c])
    string = ''.join([str(item) for item in encoding_output])
    return string


def Huffman_decode(encoded_data: str, huffman_tree: Node) -> str:
    tree_head = huffman_tree
    decoded_output = []
    for x in encoded_data:
        if x == '1':
            huffman_tree = huffman_tree.right
        elif x == '0':
            huffman_tree = huffman_tree.left
        # check if leaf
        if huffman_tree.left is None and huffman_tree.right is None:
            decoded_output.append(huffman_tree.symbol)
            huffman_tree = tree_head

    string = ''.join([str(item) for item in decoded_output])
    return string


def roundToDecimals(num: float, decimals: int) -> float:
    """Rounds number to significant decimals

    Args:
        num (float): number to round
        decimals (int): number of significant decimals

    Returns:
        float: rounded number
    """
    return round(num*10**decimals)/10**decimals


rjecnik = {"A": 0.4,
           "B": 0.2,
           "C": 0.14,
           "D": 0.13,
           "E": 0.13}
stablo = Huffman_tree(rjecnik)
tablica = calculate_codes(stablo)
for symbol, code in tablica.items():
    print("Symbol: ", symbol, " code: ", code)
enkodirani_string = Huffman_encode("ABCABCABCE", tablica)
print(enkodirani_string)
dekodirani_string = Huffman_decode("01110111", stablo)
print(dekodirani_string)