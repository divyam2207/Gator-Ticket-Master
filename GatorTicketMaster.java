import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatorTicketMaster {

    // Global variables to match Python structure
    private static BinaryHeap unassignedSeats;
    private static WaitlistHeap waitlist;
    private static RedBlackTree redBlackTree;
    private static int maxSeats = 0;
    private static StringBuilder result = new StringBuilder();
    
    // Time counter to simulate the Python unique timestamp logic for stability
    private static double timeCount = 1.0001;

    // --- Helper Methods ---

    private static void appendResult(String text) {
        result.append(text);
    }

    // --- Core Functions ---

    public static void initialize(int seatCount) {
        if (seatCount <= 0) {
            appendResult("Invalid input. Please provide a valid number of seats.\n");
            return;
        }

        appendResult(seatCount + " Seats are made available for reservation\n");
        waitlist = new WaitlistHeap();
        unassignedSeats = new BinaryHeap();
        redBlackTree = new RedBlackTree();
        
        for (int i = 1; i <= seatCount; i++) {
            unassignedSeats.insert(i);
        }
        maxSeats = seatCount;
    }

    public static void available() {
        int seatsAvail = unassignedSeats.getSize();
        int waitlistSize = waitlist.getSize();
        appendResult("Total Seats Available : " + seatsAvail + ", Waitlist : " + waitlistSize + "\n");
    }

    public static void reserve(int userId, int userPriority) {
        if (unassignedSeats.isEmpty()) {
            // Add to waitlist
            // We mimic Python's time.time()*timeCount logic. 
            // In Java, System.nanoTime() is sufficient for ordering, 
            // but we use the double logic to strictly follow the provided logic.
            double timestamp = System.currentTimeMillis() * timeCount;
            waitlist.insert(userId, userPriority, timestamp);
            appendResult("User " + userId + " is added to the waiting list\n");
            timeCount += 0.0001;
        } else {
            int seatId = unassignedSeats.extractMin();
            redBlackTree.insert(userId, seatId);
            appendResult("User " + userId + " reserved seat " + seatId + "\n");
        }
    }

    public static void cancel(int seatId, int userId) {
        RedBlackTree.Node node = redBlackTree.search(userId);
        if (node != null && node.seatId == seatId) {
            appendResult("User " + userId + " canceled their reservation\n");
            redBlackTree.delete(userId);
            
            if (!waitlist.isEmpty()) {
                WaitlistHeap.WaitlistNode topWaitlister = waitlist.extractMax();
                redBlackTree.insert(topWaitlister.userId, seatId);
                appendResult("User " + topWaitlister.userId + " reserved seat " + seatId + "\n");
            } else {
                unassignedSeats.insert(seatId);
            }
        } else {
            appendResult("User " + userId + " has no reservation for seat " + seatId + " to cancel\n");
        }
    }

    public static void exitWaitlist(int userId) {
        if (waitlist.contains(userId)) {
            waitlist.removeUser(userId);
            appendResult("User " + userId + " is removed from the waiting list\n");
        } else {
            appendResult("User " + userId + " is not in waitlist\n");
        }
    }

    public static void updatePriority(int userId, int userPriority) {
        if (waitlist.contains(userId)) {
            waitlist.updatePriority(userId, userPriority);
            appendResult("User " + userId + " priority has been updated to " + userPriority + "\n");
        } else {
            appendResult("User " + userId + " priority is not updated\n");
        }
    }

    public static void addSeats(int count) {
        if (count <= 0) {
            appendResult("Invalid input. Please provide a valid number of seats.\n");
            return;
        }

        appendResult("Additional " + count + " Seats are made available for reservation\n");
        
        List<Integer> newSeats = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            maxSeats++;
            newSeats.add(maxSeats);
        }

        if (!waitlist.isEmpty()) {
            for (int seatId : newSeats) {
                if (waitlist.isEmpty()) {
                    unassignedSeats.insert(seatId);
                } else {
                    WaitlistHeap.WaitlistNode topWaitlister = waitlist.extractMax();
                    redBlackTree.insert(topWaitlister.userId, seatId);
                    appendResult("User " + topWaitlister.userId + " reserved seat " + seatId + "\n");
                }
            }
        } else {
            for (int seatId : newSeats) {
                unassignedSeats.insert(seatId);
            }
        }
    }

    public static void printReservations() {
        List<RedBlackTree.Node> reservations = redBlackTree.inorderTraversal();
        // Sort by seatId
        Collections.sort(reservations, Comparator.comparingInt(node -> node.seatId));
        
        for (RedBlackTree.Node node : reservations) {
            appendResult("Seat " + node.seatId + ", User " + node.key + "\n");
        }
    }

    public static void releaseSeats(int userId1, int userId2) {
        if (userId1 > userId2) {
            appendResult("Invalid input. Please provide valid range of users.\n");
            return;
        }

        appendResult("Reservations of the Users in the range [" + userId1 + ", " + userId2 + "] are released\n");

        List<int[]> releasedSeats = new ArrayList<>(); // Pairs of {userId, seatId}

        for (int userId = userId1; userId <= userId2; userId++) {
            RedBlackTree.Node node = redBlackTree.search(userId);
            if (node != null) {
                int seatId = node.seatId;
                releasedSeats.add(new int[]{userId, seatId});
                redBlackTree.delete(userId);
                unassignedSeats.insert(seatId);
            } else {
                waitlist.removeUser(userId);
            }
        }

        if (!waitlist.isEmpty() && !releasedSeats.isEmpty()) {
            for (int i = 0; i < releasedSeats.size(); i++) {
                if (waitlist.isEmpty()) break;
                
                WaitlistHeap.WaitlistNode topWaitlister = waitlist.extractMax();
                int seatId = unassignedSeats.extractMin();
                redBlackTree.insert(topWaitlister.userId, seatId);
                appendResult("User " + topWaitlister.userId + " reserved seat " + seatId + "\n");
            }
        }
    }

    public static boolean quit() {
        appendResult("Program Terminated!!\n");
        return true;
    }

    // --- Main Driver ---

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide an input filename.");
            return;
        }

        String inputFile = args[0];
        // Python code replaces .txt with _output_file.txt
        String outputFile = inputFile.replace(".txt", "_output_file.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    // Parse command: FunctionName(arg1, arg2)
                    int openParen = line.indexOf('(');
                    int closeParen = line.indexOf(')');
                    
                    if (openParen == -1 || closeParen == -1) continue;

                    String funcName = line.substring(0, openParen).trim();
                    String argsStr = line.substring(openParen + 1, closeParen).trim();
                    
                    String[] stringArgs = argsStr.isEmpty() ? new String[0] : argsStr.split(",");
                    List<Integer> intArgs = new ArrayList<>();
                    for (String s : stringArgs) {
                        String trimmed = s.trim();
                        if (!trimmed.isEmpty()) {
                            intArgs.add(Integer.parseInt(trimmed));
                        }
                    }

                    boolean shouldQuit = false;

                    switch (funcName) {
                        case "Initialize":
                            if (!intArgs.isEmpty()) initialize(intArgs.get(0));
                            break;
                        case "Reserve":
                            if (intArgs.size() >= 2) reserve(intArgs.get(0), intArgs.get(1));
                            break;
                        case "Cancel":
                            if (intArgs.size() >= 2) cancel(intArgs.get(0), intArgs.get(1));
                            break;
                        case "Available":
                            available();
                            break;
                        case "ExitWaitlist":
                            if (!intArgs.isEmpty()) exitWaitlist(intArgs.get(0));
                            break;
                        case "UpdatePriority":
                            if (intArgs.size() >= 2) updatePriority(intArgs.get(0), intArgs.get(1));
                            break;
                        case "AddSeats":
                            if (!intArgs.isEmpty()) addSeats(intArgs.get(0));
                            break;
                        case "PrintReservations":
                            printReservations();
                            break;
                        case "ReleaseSeats":
                            if (intArgs.size() >= 2) releaseSeats(intArgs.get(0), intArgs.get(1));
                            break;
                        case "Quit":
                            shouldQuit = quit();
                            break;
                        default:
                            break;
                    }

                    writer.write(result.toString());
                    result.setLength(0); // Clear buffer

                    if (shouldQuit) break;

                } catch (Exception e) {
                    writer.write("Error processing command '" + line + "': " + e.getMessage() + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // DATA STRUCTURE IMPLEMENTATIONS
    // ==========================================

    // --- Binary Heap (Min Heap) ---
    static class BinaryHeap {
        private List<Integer> heap;

        public BinaryHeap() {
            this.heap = new ArrayList<>();
        }

        public boolean isEmpty() {
            return heap.isEmpty();
        }

        public int getSize() {
            return heap.size();
        }

        public void insert(int item) {
            heap.add(item);
            heapifyBottomUp(heap.size() - 1);
        }

        public int extractMin() {
            if (heap.isEmpty()) return 0;
            int minVal = heap.get(0);
            int lastVal = heap.remove(heap.size() - 1);
            
            if (!heap.isEmpty()) {
                heap.set(0, lastVal);
                heapifyTopDown(0);
            }
            return minVal;
        }

        // remove_arbitrary implementation from Python logic
        public void removeArbitrary(int item) {
            int idx = heap.indexOf(item);
            if (idx == -1) {
                System.out.println("Not Found in the heap!");
                return;
            }

            int lastVal = heap.get(heap.size() - 1);
            heap.set(idx, lastVal);
            heap.remove(heap.size() - 1);

            if (idx < heap.size()) {
                heapifyBottomUp(idx);
                heapifyTopDown(idx);
            }
        }

        private void heapifyBottomUp(int idx) {
            int parentIdx = (idx - 1) / 2;
            while (idx > 0 && heap.get(parentIdx) > heap.get(idx)) {
                swap(parentIdx, idx);
                idx = parentIdx;
                parentIdx = (idx - 1) / 2;
            }
        }

        private void heapifyTopDown(int idx) {
            int leftIdx = 2 * idx + 1;
            int rightIdx = 2 * idx + 2;
            int smallest = idx;

            if (leftIdx < heap.size() && heap.get(leftIdx) < heap.get(smallest)) {
                smallest = leftIdx;
            }
            if (rightIdx < heap.size() && heap.get(rightIdx) < heap.get(smallest)) {
                smallest = rightIdx;
            }

            if (smallest != idx) {
                swap(idx, smallest);
                heapifyTopDown(smallest);
            }
        }

        private void swap(int i, int j) {
            int temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);
        }
    }

    // --- Waitlist Max Binary Heap ---
    static class WaitlistHeap {
        
        static class WaitlistNode {
            int userId;
            int priority;
            double timestamp;

            WaitlistNode(int userId, int priority, double timestamp) {
                this.userId = userId;
                this.priority = priority;
                this.timestamp = timestamp;
            }
        }

        private List<WaitlistNode> heap;
        private Map<Integer, Integer> userPositions;

        public WaitlistHeap() {
            this.heap = new ArrayList<>();
            this.userPositions = new HashMap<>();
        }

        public boolean isEmpty() {
            return heap.isEmpty();
        }

        public int getSize() {
            return heap.size();
        }

        public boolean contains(int userId) {
            return userPositions.containsKey(userId);
        }

        /**
         * Returns true if node1 has higher precedence than node2
         */
        private boolean compare(WaitlistNode node1, WaitlistNode node2) {
            if (node1.priority != node2.priority) {
                return node1.priority > node2.priority;
            }
            return node1.timestamp < node2.timestamp;
        }

        public void insert(int userId, int priority, double timestamp) {
            WaitlistNode node = new WaitlistNode(userId, priority, timestamp);
            heap.add(node);
            userPositions.put(userId, heap.size() - 1);
            heapifyBottomUp(heap.size() - 1);
        }

        public WaitlistNode extractMax() {
            if (heap.isEmpty()) return null;
            
            WaitlistNode maxVal = heap.get(0);
            userPositions.remove(maxVal.userId);
            
            WaitlistNode lastVal = heap.remove(heap.size() - 1);
            
            if (!heap.isEmpty()) {
                heap.set(0, lastVal);
                userPositions.put(lastVal.userId, 0);
                heapifyTopDown(0);
            }
            
            return maxVal;
        }

        public void removeUser(int userId) {
            if (!userPositions.containsKey(userId)) return;

            int idx = userPositions.get(userId);
            userPositions.remove(userId);

            // If removing the last element
            if (idx == heap.size() - 1) {
                heap.remove(heap.size() - 1);
                return;
            }

            WaitlistNode lastVal = heap.remove(heap.size() - 1);
            heap.set(idx, lastVal);
            userPositions.put(lastVal.userId, idx);

            // Heapify both ways since we replaced the node with an arbitrary value
            heapifyBottomUp(idx);
            heapifyTopDown(idx);
        }

        public void updatePriority(int userId, int newPriority) {
            if (!userPositions.containsKey(userId)) return;

            int idx = userPositions.get(userId);
            WaitlistNode node = heap.get(idx);
            node.priority = newPriority;
            
            // Heapify both ways
            heapifyBottomUp(idx);
            heapifyTopDown(idx);
        }

        private void heapifyBottomUp(int idx) {
            while (idx > 0) {
                int parentIdx = (idx - 1) / 2;
                if (compare(heap.get(idx), heap.get(parentIdx))) {
                    swap(idx, parentIdx);
                    idx = parentIdx;
                } else {
                    break;
                }
            }
        }

        private void heapifyTopDown(int idx) {
            int size = heap.size();
            while (true) {
                int largest = idx;
                int left = 2 * idx + 1;
                int right = 2 * idx + 2;

                if (left < size && compare(heap.get(left), heap.get(largest))) {
                    largest = left;
                }
                if (right < size && compare(heap.get(right), heap.get(largest))) {
                    largest = right;
                }

                if (largest != idx) {
                    swap(idx, largest);
                    idx = largest;
                } else {
                    break;
                }
            }
        }

        private void swap(int i, int j) {
            WaitlistNode temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);

            // Update positions in map
            userPositions.put(heap.get(i).userId, i);
            userPositions.put(heap.get(j).userId, j);
        }
    }

    // --- Red Black Tree ---
    static class RedBlackTree {
        
        enum Color { RED, BLACK }

        static class Node {
            Integer key; // userId
            int seatId;
            Node left, right, parent;
            Color color;
            int size;

            Node(Integer key) {
                this.key = key;
                this.color = Color.RED;
                this.size = 1;
            }
        }

        private final Node NIL;
        private Node root;

        public RedBlackTree() {
            NIL = new Node(null);
            NIL.color = Color.BLACK;
            NIL.size = 0;
            // self reference for strict NIL behavior
            NIL.left = NIL;
            NIL.right = NIL; 
            root = NIL;
        }

        private void leftRotate(Node x) {
            Node y = x.right;
            x.right = y.left;
            if (y.left != NIL) {
                y.left.parent = x;
            }
            y.parent = x.parent;
            if (x.parent == NIL) {
                root = y;
            } else if (x == x.parent.left) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
            y.left = x;
            x.parent = y;

            // Update sizes
            y.size = x.size;
            x.size = x.left.size + x.right.size + 1;
        }

        private void rightRotate(Node x) {
            Node y = x.left;
            x.left = y.right;
            if (y.right != NIL) {
                y.right.parent = x;
            }
            y.parent = x.parent;
            if (x.parent == NIL) {
                root = y;
            } else if (x == x.parent.right) {
                x.parent.right = y;
            } else {
                x.parent.left = y;
            }
            y.right = x;
            x.parent = y;

            // Update sizes
            y.size = x.size;
            x.size = x.left.size + x.right.size + 1;
        }

        public void insert(int key, int seatId) {
            Node node = new Node(key);
            node.seatId = seatId;
            node.left = NIL;
            node.right = NIL;

            Node y = NIL;
            Node x = root;

            while (x != NIL) {
                y = x;
                x.size++;
                if (node.key < x.key) {
                    x = x.left;
                } else {
                    x = x.right;
                }
            }

            node.parent = y;
            if (y == NIL) {
                root = node;
            } else if (node.key < y.key) {
                y.left = node;
            } else {
                y.right = node;
            }

            insertFixup(node);
        }

        private void insertFixup(Node node) {
            while (node.parent.color == Color.RED) {
                if (node.parent == node.parent.parent.left) {
                    Node y = node.parent.parent.right;
                    if (y.color == Color.RED) {
                        node.parent.color = Color.BLACK;
                        y.color = Color.BLACK;
                        node.parent.parent.color = Color.RED;
                        node = node.parent.parent;
                    } else {
                        if (node == node.parent.right) {
                            node = node.parent;
                            leftRotate(node);
                        }
                        node.parent.color = Color.BLACK;
                        node.parent.parent.color = Color.RED;
                        rightRotate(node.parent.parent);
                    }
                } else {
                    Node y = node.parent.parent.left;
                    if (y.color == Color.RED) {
                        node.parent.color = Color.BLACK;
                        y.color = Color.BLACK;
                        node.parent.parent.color = Color.RED;
                        node = node.parent.parent;
                    } else {
                        if (node == node.parent.left) {
                            node = node.parent;
                            rightRotate(node);
                        }
                        node.parent.color = Color.BLACK;
                        node.parent.parent.color = Color.RED;
                        leftRotate(node.parent.parent);
                    }
                }
                if (node == root) break;
            }
            root.color = Color.BLACK;
        }

        private void transplant(Node u, Node v) {
            if (u.parent == NIL) {
                root = v;
            } else if (u == u.parent.left) {
                u.parent.left = v;
            } else {
                u.parent.right = v;
            }
            v.parent = u.parent;
        }

        public boolean delete(int key) {
            Node z = search(key);
            if (z != null) {
                deleteNode(z);
                return true;
            }
            return false;
        }

        private void deleteNode(Node z) {
            Node y = z;
            Color yOriginalColor = y.color;
            Node x;

            if (z.left == NIL) {
                x = z.right;
                transplant(z, z.right);
                updateSizeUpwards(x.parent);
            } else if (z.right == NIL) {
                x = z.left;
                transplant(z, z.left);
                updateSizeUpwards(x.parent);
            } else {
                y = minimum(z.right);
                yOriginalColor = y.color;
                x = y.right;

                if (y.parent == z) {
                    x.parent = y;
                } else {
                    transplant(y, y.right);
                    y.right = z.right;
                    y.right.parent = y;
                }

                transplant(z, y);
                y.left = z.left;
                y.left.parent = y;
                y.color = z.color;

                y.size = z.size;
                updateSizeUpwards(x.parent);
            }

            if (yOriginalColor == Color.BLACK) {
                deleteFixup(x);
            }
        }

        private void updateSizeUpwards(Node node) {
            while (node != NIL) {
                node.size--;
                node = node.parent;
            }
        }

        private void deleteFixup(Node x) {
            while (x != root && x.color == Color.BLACK) {
                if (x == x.parent.left) {
                    Node w = x.parent.right;
                    if (w.color == Color.RED) {
                        w.color = Color.BLACK;
                        x.parent.color = Color.RED;
                        leftRotate(x.parent);
                        w = x.parent.right;
                    }
                    if (w.left.color == Color.BLACK && w.right.color == Color.BLACK) {
                        w.color = Color.RED;
                        x = x.parent;
                    } else {
                        if (w.right.color == Color.BLACK) {
                            w.left.color = Color.BLACK;
                            w.color = Color.RED;
                            rightRotate(w);
                            w = x.parent.right;
                        }
                        w.color = x.parent.color;
                        x.parent.color = Color.BLACK;
                        w.right.color = Color.BLACK;
                        leftRotate(x.parent);
                        x = root;
                    }
                } else {
                    Node w = x.parent.left;
                    if (w.color == Color.RED) {
                        w.color = Color.BLACK;
                        x.parent.color = Color.RED;
                        rightRotate(x.parent);
                        w = x.parent.left;
                    }
                    if (w.right.color == Color.BLACK && w.left.color == Color.BLACK) {
                        w.color = Color.RED;
                        x = x.parent;
                    } else {
                        if (w.left.color == Color.BLACK) {
                            w.right.color = Color.BLACK;
                            w.color = Color.RED;
                            leftRotate(w);
                            w = x.parent.left;
                        }
                        w.color = x.parent.color;
                        x.parent.color = Color.BLACK;
                        w.left.color = Color.BLACK;
                        rightRotate(x.parent);
                        x = root;
                    }
                }
            }
            x.color = Color.BLACK;
        }

        public Node search(int key) {
            Node res = searchRecursive(root, key);
            return (res == NIL) ? null : res;
        }

        private Node searchRecursive(Node node, int key) {
            if (node == NIL || key == node.key) {
                return node;
            }
            if (key < node.key) {
                return searchRecursive(node.left, key);
            }
            return searchRecursive(node.right, key);
        }

        private Node minimum(Node node) {
            while (node.left != NIL) {
                node = node.left;
            }
            return node;
        }

        public List<Node> inorderTraversal() {
            List<Node> result = new ArrayList<>();
            inorderRecursive(root, result);
            return result;
        }

        private void inorderRecursive(Node node, List<Node> result) {
            if (node != NIL) {
                inorderRecursive(node.left, result);
                result.add(node);
                inorderRecursive(node.right, result);
            }
        }
    }
}
