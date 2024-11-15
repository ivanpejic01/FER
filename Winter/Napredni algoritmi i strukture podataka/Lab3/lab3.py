from __future__ import annotations  # ne stavljati u Edgar
from math import floor, ceil
from typing import Tuple


class BTNode:
    """
    Class representing a single node in B-tree containing integer values.
    ...
    Attributes
    ----------

    value: int
        Value stored in the node.
    left: list, optional
        Left child of the current node. Can be None.
    deg: int, default 5 
        Degree of the tree. Default value is set to five.
    parent; BTNode, optional
        Parent of the current node. Can be None.
    """

    def __init__(self, value: int = None, left: list = None, deg: int = 5, parent: BTNode = None) -> None:
        if value is not None:
            self.left = [None, value, None]
        else:
            self.left = left
            for item in self.left:
                if type(item) is BTNode:
                    item.parent = self
        self.deg = deg
        self.parent = parent

    def search_value(self, value: int) -> Tuple[BTNode, bool, int]:
        """
        Search values in the connected nodes.
        Args:
            value (int): The value we are searching for.
        Returns:
            BTNode: The node containing the searched value.
            bool: If the value is found.
            int: Index of the value.
        """
        for index in range(len(self.left)):
            item = self.left[index]
            if type(item) is int:
                left_child = self.left[index-1]
                if value == item:
                    return (self, True, index)
                if value < item:
                    if left_child is not None:
                        return left_child.search_value(value)
                    else:
                        return (self, False, index)
        right_child = self.left[len(self.left) - 1]
        if right_child is not None:
            return right_child.search_value(value)
        else:
            return (self, False, len(self.left))

    def keys(self) -> int:
        """
        Returns number of keys in the node.
        Returns:
            int: Number of keys in the current node.
        """
        return int((len(self.left) - 1) / 2)

    def min_keys(self) -> int:
        """
        Returns minimal number of keys in the current node.
        Returns:
            int: Minimal number of keys in the node.
        """
        return ceil(self.deg / 2) - 1

    def insert_value(self, value: int) -> None:
        """
        Method for inserting valus in the node.
        Args:
            int: Value to be inserted.
        """
        (node, found, index) = self.search_value(value)
        if not found:
            if index < len(node.left):
                node.left[index:index] = [value, None]
            else:
                node.left += [value, None]
            return node._split()
        return None

    def _split(self) -> BTNode:
        """
        Split the overflowing node.
        Returns:
            NTNode: Node after spliting.
        """
        if self.keys() > (self.deg-1):
            i = floor(self.keys() / 2) * 2 + 1
            node_left, node_value, node_right = BTNode(left=self.left[:i], deg=self.deg, parent=self.parent), self.left[i], BTNode(
                left=self.left[i+1:], deg=self.deg, parent=self.parent)
            # TODO: Finish the rest of the splitting algorithm
            if self.parent is None:
                nroot = BTNode(
                    left=[node_left, node_value, node_right], deg=self.deg)
                node_left.parent = nroot
                node_right.parent = nroot
                return nroot
            else:
                for i in range(len(self.parent.left)):
                    if self.parent.left[i] is self:
                        self.parent.left = self.parent.left[:i] + [
                            node_left, node_value, node_right] + self.parent.left[i + 1:]
                        return self.parent._split()

        return None

    def __str__(self) -> str:
        """
        Overwiting str (to string) method for printing the node.
        Returns:
            str: Resulting string.
        """
        res = "|"
        for item in self.left:
            if type(item) is int:
                res += "{}".format(item)
            if type(item) is BTNode:
                res += "[K]"
            if item is None:
                res += "[N]"
        res += "|"
        return res


class BTreeList:
    """
    Class representing the B-Tree.
    ...
    Attributes
    ----------
    value: int
        Value stored in tree.
    deg: int
        Degree of the tree. Default is five.
    """

    def __init__(self, value: int, deg: int = 5) -> None:
        self.root = BTNode(value=value, deg=deg)
        self.deg = deg

    def search_value(self, value: int) -> bool:
        """
        Method returns true if the tree contains value.
        Args:
            value (int): The searched value.
        Returns:
            bool: If value exists.
        """
        (_, found, _) = self.root.search_value(value)
        return found

    def insert_values(self, values: list) -> None:
        """
        Method for inserting values in the tree.
        Args:
            values (list): A list of values to be inserted.
        """
        # TODO: Insert new values and update root
        for value in values:
            nroot = self.root.insert_value(value)
            if nroot is not None:
                self.root = nroot

    def __str__(self) -> str:
        """
        Overwriting str (to string) method for printing the tree.
        Returns:
            str: Resulting string.
        """
        res, q = "", [self.root, "\n"]
        while q != ["\n"]:
            n = q[0]
            q = q[1:]
            if type(n) is str:
                res += n
                q.append("\n")
            else:
                res += str(n)
                for item in n.left:
                    if type(item) is BTNode:
                        q.append(item)
        return res


values = [3, 31, 20, 13, 16, 28, 44, 11]
btree = BTreeList(values[0], deg=3)
btree.insert_values(values[1:])
print(btree)
