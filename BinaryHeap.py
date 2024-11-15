import time

class BinaryHeap:

    def __init__(self) -> None:
        self.heap = []

    #insert into the heap, maintaining the heap structure
    def insert(self, item) -> None:
        self.heap.append(item)
        self.heapify_bottom_up(len(self.heap)-1)
    
    #get the min value(top), without extraacting it
    def get_min(self) -> int:
        if not self.heap:
            return 0
        return self.heap[0]
    
    #extracts and removes the min value from the heap, followed by restructring the heap to preserve the heap properties
    def extract_min(self) -> int:
        if not self.heap:
            return 0
        min_val = self.heap[0]
        self.heap[0] = self.heap[-1]
        self.heap.pop()
        self.heapify_top_down(0)

        return min_val
    
    #removes a specific node anywhere from the heap, followed by restructring the heap to preserve the heap properties.
    def remove_arbitrary(self, item) -> None:
        try:
            idx = self.heap.index(item)

            self.heap[idx], self.heap[-1] = self.heap[-1], self.heap[idx]
            self.heap.pop()

            if idx < len(self.heap):
                self.heap.heapify_bottom_up(idx)
                self.heap.heapify_top_down(idx)
        except ValueError:
            print("Not Found in the heap!")
        
        
        
    #function to balance/restructure via bottom-up approach the heap to ensure the heap properties
    def heapify_bottom_up(self, idx) -> None:
        parent_idx = (idx-1)//2
        while parent_idx >=0 and self.heap[parent_idx] > self.heap[idx]:
            self.heap[parent_idx], self.heap[idx] = self.heap[idx], self.heap[parent_idx]
            idx = parent_idx
            parent_idx = (idx-1)//2
    
    #function to balance the heap via top-down approach to ensure the heap properties
    def heapify_top_down(self, idx) -> None:
        left_idx = 2 * idx + 1
        right_idx = 2 * idx + 2
        smallest = idx

        if left_idx < len(self.heap) and self.heap[left_idx] < self.heap[smallest]:
            smallest = left_idx
        
        if right_idx < len(self.heap) and self.heap[right_idx] < self.heap[smallest]:
            smallest = right_idx

        if smallest != idx:
            self.heap[idx], self.heap[smallest] = self.heap[smallest], self.heap[idx]
            self.heapify_top_down(smallest)






        


        
