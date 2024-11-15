import time

class WaitlistHeap:
    def __init__(self):
        self.heap = []  # [(priority, timestamp, userId), ....]
        self.user_positions = {}  # To track user positions for quick updates/removal

    #custom comparison function to first compare the priority and in the event of a tie, compares the timestamp at which the item entered
    def compare(self, item1, item2):
        """
        Returns True if item1 has higher precedence than item2
        Higher priority number = higher precedence
        If priorities equal, earlier timestamp = higher precedence
        """
        priority1, timestamp1, _ = item1
        priority2, timestamp2, _ = item2
        if priority1 != priority2:
            return priority1 > priority2  # Higher priority wins
        return timestamp1 < timestamp2    # Earlier timestamp wins

    #inserts an item to the heap, ensuring the heap structure
    def insert(self, userId, priority, timestamp=None):
        if timestamp is None:
            timestamp = time.time_ns()
        waitlist_obj = (priority, timestamp, userId)
        self.heap.append(waitlist_obj)
        self.user_positions[userId] = len(self.heap) - 1
        self.heapify_bottom_up(len(self.heap) - 1)

    #gets the max item from the heap without removing it
    def get_min(self):
        if not self.heap:
            return None
        return self.heap[0]

    #removes the max(top) item from the heap, followed by balancing the heap to preserve the heap structure
    def extract_max(self):
        if not self.heap:
            return None
        max_val = self.heap[0]
        _, _, userId = max_val
        self.user_positions.pop(userId, None)
        
        if len(self.heap) > 1:
            self.heap[0] = self.heap[-1]
            _, _, userId = self.heap[0]
            self.user_positions[userId] = 0
        
        self.heap.pop()
        if self.heap:
            self.heapify_top_down(0)
        
        return max_val

    #removes the user from the heap
    def remove_user(self, userId):
        if userId not in self.user_positions:
            return None
        
        idx = self.user_positions[userId]
        removed_item = self.heap[idx]
        
        self.heap[idx] = self.heap[-1]
        _, _, last_userId = self.heap[idx]
        self.user_positions[last_userId] = idx
        
        self.heap.pop()
        self.user_positions.pop(userId)
        
        if idx < len(self.heap):
            self.heapify_bottom_up(idx)
            self.heapify_top_down(idx)
        
        return removed_item

    #updates the priority of the user and then restructring the heap based on the compare function
    def update_priority(self, userId, new_priority):
        if userId not in self.user_positions:
            return False
        
        idx = self.user_positions[userId]
        old_timestamp = self.heap[idx][1]
        self.heap[idx] = (new_priority, old_timestamp, userId)
        
        # Need to heapify both ways since priority could increase or decrease
        self.heapify_bottom_up(idx)
        self.heapify_top_down(idx)
        return True

    #special function to ensure the heap structure via bottom-up approach
    def heapify_bottom_up(self, idx):
        while idx > 0:
            parent_idx = (idx - 1) // 2
            if self.compare(self.heap[idx], self.heap[parent_idx]):
                # Update positions dictionary
                _, _, userId_idx = self.heap[idx]
                _, _, userId_parent = self.heap[parent_idx]
                self.user_positions[userId_idx] = parent_idx
                self.user_positions[userId_parent] = idx
                # Swap elements
                self.heap[parent_idx], self.heap[idx] = self.heap[idx], self.heap[parent_idx]
                idx = parent_idx
            else:
                break

    #preserves the heap structure via top-down approach
    def heapify_top_down(self, idx):
        size = len(self.heap)
        while True:
            largest = idx
            left = 2 * idx + 1
            right = 2 * idx + 2

            # Changed logic: now properly finds the largest among parent and children
            if left < size and self.compare(self.heap[left], self.heap[largest]):
                largest = left
            if right < size and self.compare(self.heap[right], self.heap[largest]):
                largest = right

            if largest != idx:
                # Update positions dictionary
                _, _, userId_idx = self.heap[idx]
                _, _, userId_largest = self.heap[largest]
                self.user_positions[userId_idx] = largest
                self.user_positions[userId_largest] = idx
                # Swap elements
                self.heap[idx], self.heap[largest] = self.heap[largest], self.heap[idx]
                idx = largest
            else:
                break

    #returns the size of the heap
    def get_size(self):
        return len(self.heap)

    #checks if the heap is empty
    def is_empty(self):
        return len(self.heap) == 0