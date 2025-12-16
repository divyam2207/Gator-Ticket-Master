
# Gator Ticket Master  

**Author**: Divyam Dubey  


---

## Project Overview  
The **Gator Ticket Master** system streamlines seat reservations for Gator events, automating processes like seat allocation, cancellations, and waitlist management. The system:  
- Manages seat reservations dynamically, adding seats as demand increases.  
- Prioritizes users on the waitlist based on priority and earliest request timestamps.  
- Cancels reservations and efficiently reallocates seats.  
- Detects and handles unusual activity with administrative seat releases.  

### Key Features  
1. **Seat Assignment**: Allocates seats in ascending order.  
2. **Waitlist Management**: Maintains a priority-based waitlist for users.  
3. **Dynamic Seat Allocation**: Adds seats dynamically based on demand.  
4. **Admin Control**: Supports bulk seat releases in cases of unusual activity.  

---

## Java Implementation
An alternative implementation of the system is available in Java. It follows the exact same logic and file formats as the Python version.

### Key Files
* **`GatorTicketMaster.java`**: Contains the main controller and all data structure classes (Binary Heap, Red-Black Tree, Waitlist Heap) as inner classes.

### Usage
To run the Java version with an input file (e.g., `test1.txt`):

1. **Compile the code:**
   ```bash
   javac GatorTicketMaster.java

## Project Structure  
### Main Components  
- **`gatorTicketMaster.py`**: Handles user interactions and implements core functions.  
- **`BinaryHeap.py`**: Manages unassigned seat IDs.  
- **`RedBlackTree.py`**: Tracks active reservations efficiently.  
- **`WaitlistMaxBinaryHeap.py`**: Manages the priority-based waitlist.  

### Core Functions  
1. **`Initialize(seatCount: int)`**: Sets up the initial seating arrangement.  
2. **`Reserve(userId: int, userPriority: int)`**: Reserves a seat or adds the user to the waitlist.  
3. **`Cancel(seatId: int, userId: int)`**: Cancels a reservation and reassigns the seat if needed.  
4. **`Available()`**: Displays current seat availability and waitlist status.  
5. **`ExitWaitlist(userId: int)`**: Removes a user from the waitlist.  
6. **`UpdatePriority(userId: int, userPriority: int)`**: Updates a user’s priority in the waitlist.  
7. **`AddSeats(count: int)`**: Adds new seats to the system and assigns them to waitlisted users.  
8. **`PrintReservations()`**: Lists all current reservations.  
9. **`ReleaseSeats(userId1: int, userId2: int)`**: Cancels reservations within a range of user IDs.  
10. **`Quit()`**: Ends the program execution.  

---

## Key Data Structures  
### Binary Heap (Min Heap)  
- **Purpose**: Stores unassigned seats.  
- **Complexity**: O(log n) for insertions and minimum extraction.  
- **Key Methods**:  
  - `insert(item)`  
  - `extract_min()`  
  - `remove_arbitrary(item)`  

### Red-Black Tree  
- **Purpose**: Manages active reservations.  
- **Complexity**: O(log n) for search, insertion, and deletion.  
- **Key Methods**:  
  - `insert(key, seatId)`  
  - `delete(key)`  
  - `search(key)`  
  - `inorder_traversal()`  

### Max Binary Heap  
- **Purpose**: Manages the waitlist with priority-based ordering.  
- **Complexity**: O(log n) for insertions and maximum extraction.  
- **Key Methods**:  
  - `insert(userId, priority, timestamp)`  
  - `extract_max()`  
  - `update_priority(userId, new_priority)`  
  - `remove_user(userId)`  

---

## Data Flow  
1. **Input Processing**: Reads commands from an input file and executes corresponding functions.  
2. **Seat Management**: Tracks unassigned and reserved seats using the Binary Heap and Red-Black Tree.  
3. **Waitlist Management**: Handles user priorities and timestamps with a Max Binary Heap.  
4. **Output Generation**: Writes operation results to an output file.  

---

## Implementation Details  
### Data Structures and Rationale  
1. **Binary Heap (Min Heap)**: Efficiently assigns the lowest available seat.  
2. **Red-Black Tree**: Ensures fast operations for reservation management.  
3. **Max Binary Heap**: Prioritizes waitlisted users based on priority and timestamps.  

### Additional Features  
- **Dynamic Seat Addition**: Adds new seats and assigns them to waitlisted users.  
- **Timestamp Usage**: Ensures fairness for users with equal priority.  
- **Batch Seat Release**: Handles bulk cancellations or administrative actions.  

---

## Time Complexity  
| **Operation**          | **Complexity**   |  
|-------------------------|------------------|  
| `Initialize`           | O(n log n)       |  
| `Reserve`              | O(log n)         |  
| `Cancel`               | O(log n)         |  
| `Available`            | O(1)             |  
| `ExitWaitlist`         | O(log n)         |  
| `UpdatePriority`       | O(log n)         |  
| `AddSeats`             | O(m log n)       |  
| `PrintReservations`    | O(n)             |  
| `ReleaseSeats`         | O((k + m) log n) |  

---

## Space Complexity  
- **Binary Heap**: Unassigned seats.  
- **Red-Black Tree**: Active reservations.  
- **Max Binary Heap**: Waitlist.  

**Overall**: O(n), where n is the total number of seats.  

---

## Usage  
1. Run the program using the Python interpreter.  
2. Input commands via a file to initialize and interact with the system.  

---

## Performance Considerations  
- Optimized for large-scale events using efficient data structures.  
- Designed for dynamic seat additions and priority updates.  

---

## Error Handling  
- Validates inputs to prevent invalid operations.  
- Handles edge cases like non-existent reservations and updates for non-waitlisted users.  

---
