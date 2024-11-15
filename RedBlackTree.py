class Node:
    def __init__(self, key):
        self.key = key
        self.left = None
        self.right = None
        self.parent = None
        self.seatId = None
        self.color = "RED"  # New nodes are always red
        self.size = 1  # For order statistics

class RedBlackTree:
    def __init__(self):
        self.NIL = Node(None)  # Sentinel node
        self.NIL.color = "BLACK"
        self.NIL.size = 0
        self.root = self.NIL

    #rotate the tree left to make it balanced
    def left_rotate(self, x):
        y = x.right
        x.right = y.left
        if y.left != self.NIL:
            y.left.parent = x
        y.parent = x.parent
        if x.parent == self.NIL:
            self.root = y
        elif x == x.parent.left:
            x.parent.left = y
        else:
            x.parent.right = y
        y.left = x
        x.parent = y
        # Update sizes
        y.size = x.size
        x.size = x.left.size + x.right.size + 1


    #rotates the tree right to balance the tree
    def right_rotate(self, x):
        y = x.left
        x.left = y.right
        if y.right != self.NIL:
            y.right.parent = x
        y.parent = x.parent
        if x.parent == self.NIL:
            self.root = y
        elif x == x.parent.right:
            x.parent.right = y
        else:
            x.parent.left = y
        y.right = x
        x.parent = y
        # Update sizes
        y.size = x.size
        x.size = x.left.size + x.right.size + 1

    #inserts a key(userId) to the tree, followed by the balancing if needed
    def insert(self, key, seatId):
        node = Node(key)
        node.seatId = seatId
        node.left = self.NIL
        node.right = self.NIL
        
        y = self.NIL
        x = self.root
        
        # Find position to insert
        while x != self.NIL:
            y = x
            x.size += 1  # Update size while traversing
            if node.key < x.key:
                x = x.left
            else:
                x = x.right
        
        node.parent = y
        if y == self.NIL:
            self.root = node
        elif node.key < y.key:
            y.left = node
        else:
            y.right = node
            
        self._insert_fixup(node)

    #helper function to balance the tree after an insertion
    def _insert_fixup(self, node):
        while node.parent.color == "RED":
            if node.parent == node.parent.parent.left:
                y = node.parent.parent.right
                if y.color == "RED":
                    node.parent.color = "BLACK"
                    y.color = "BLACK"
                    node.parent.parent.color = "RED"
                    node = node.parent.parent
                else:
                    if node == node.parent.right:
                        node = node.parent
                        self.left_rotate(node)
                    node.parent.color = "BLACK"
                    node.parent.parent.color = "RED"
                    self.right_rotate(node.parent.parent)
            else:
                y = node.parent.parent.left
                if y.color == "RED":
                    node.parent.color = "BLACK"
                    y.color = "BLACK"
                    node.parent.parent.color = "RED"
                    node = node.parent.parent
                else:
                    if node == node.parent.left:
                        node = node.parent
                        self.right_rotate(node)
                    node.parent.color = "BLACK"
                    node.parent.parent.color = "RED"
                    self.left_rotate(node.parent.parent)
            if node == self.root:
                break
        self.root.color = "BLACK"

    #replaces one subtree with another while balancing the rbl tree
    def transplant(self, u, v):
        if u.parent == self.NIL:
            self.root = v
        elif u == u.parent.left:
            u.parent.left = v
        else:
            u.parent.right = v
        v.parent = u.parent

    #deletes the node(userID) from the tree, followed by balancing if needed
    def delete(self, key):
        z = self.search(key)
        if z:
            self._delete_node(z)
            return True
        return False

    #helper function to delete the node
    def _delete_node(self, z):
        y = z
        y_original_color = y.color
        
        if z.left == self.NIL:
            x = z.right
            self.transplant(z, z.right)
            # Update sizes
            current = x.parent
            while current != self.NIL:
                current.size -= 1
                current = current.parent
        elif z.right == self.NIL:
            x = z.left
            self.transplant(z, z.left)
            # Update sizes
            current = x.parent
            while current != self.NIL:
                current.size -= 1
                current = current.parent
        else:
            y = self._minimum(z.right)
            y_original_color = y.color
            x = y.right
            
            if y.parent == z:
                x.parent = y
            else:
                self.transplant(y, y.right)
                y.right = z.right
                y.right.parent = y
            
            self.transplant(z, y)
            y.left = z.left
            y.left.parent = y
            y.color = z.color
            
            # Update sizes
            y.size = z.size
            current = x.parent
            while current != y:
                current.size -= 1
                current = current.parent
        
        if y_original_color == "BLACK":
            self._delete_fixup(x)

    #fixes the structure and coloring of the rbl tree once the node is deleted
    def _delete_fixup(self, x):
        while x != self.root and x.color == "BLACK":
            if x == x.parent.left:
                w = x.parent.right
                if w.color == "RED":
                    w.color = "BLACK"
                    x.parent.color = "RED"
                    self.left_rotate(x.parent)
                    w = x.parent.right
                if w.left.color == "BLACK" and w.right.color == "BLACK":
                    w.color = "RED"
                    x = x.parent
                else:
                    if w.right.color == "BLACK":
                        w.left.color = "BLACK"
                        w.color = "RED"
                        self.right_rotate(w)
                        w = x.parent.right
                    w.color = x.parent.color
                    x.parent.color = "BLACK"
                    w.right.color = "BLACK"
                    self.left_rotate(x.parent)
                    x = self.root
            else:
                w = x.parent.left
                if w.color == "RED":
                    w.color = "BLACK"
                    x.parent.color = "RED"
                    self.right_rotate(x.parent)
                    w = x.parent.left
                if w.right.color == "BLACK" and w.left.color == "BLACK":
                    w.color = "RED"
                    x = x.parent
                else:
                    if w.left.color == "BLACK":
                        w.right.color = "BLACK"
                        w.color = "RED"
                        self.left_rotate(w)
                        w = x.parent.left
                    w.color = x.parent.color
                    x.parent.color = "BLACK"
                    w.left.color = "BLACK"
                    self.right_rotate(x.parent)
                    x = self.root
        x.color = "BLACK"


    #searches a key(userID) in the RBL tree
    def search(self, key):
        res  = self._search_recursive(self.root, key)
        if res == self.NIL:
            return None
        return res

    #helper function search the node in the tree
    def _search_recursive(self, node, key):
        if node == self.NIL or key == node.key:
            return node
        if key < node.key:
            return self._search_recursive(node.left, key)
        return self._search_recursive(node.right, key)

    def _minimum(self, node):
        while node.left != self.NIL:
            node = node.left
        return node

    def _maximum(self, node):
        while node.right != self.NIL:
            node = node.right
        return node
    
    #traverses the entire tree in order
    def inorder_traversal(self):
        result = []
        self._inorder_recursive(self.root, result)
        return result

    def _inorder_recursive(self, node, result):
        if node != self.NIL:
            self._inorder_recursive(node.left, result)
            result.append(node)
            self._inorder_recursive(node.right, result)

  