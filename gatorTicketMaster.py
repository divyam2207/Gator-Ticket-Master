from BinaryHeap import BinaryHeap
from WaitlistMaxBinaryHeap import WaitlistHeap
from RedBlackTree import RedBlackTree
import time, argparse

# Initialize global variables
unassigned_seats = BinaryHeap()
waitlist = WaitlistHeap()
red_black_tree = RedBlackTree()
max_seats = 0
result = ""
timeCount = 1.0001

# Function to append output to result and clear it after each command
def append_result(new_text):
    global result
    result += new_text 

# Initialize seat availability and binary heap for unassigned seats
def Initialize(seatCount=0):
    if not isinstance(seatCount, int) or seatCount <= 0:
        append_result(f"Invalid input. Please provide a valid number of seats.\n")
        return
    
    global unassigned_seats, waitlist, red_black_tree, max_seats
    append_result(f"{seatCount} Seats are made available for reservation\n")
    waitlist = WaitlistHeap()
    unassigned_seats = BinaryHeap()
    for i in range(1, seatCount + 1):
        unassigned_seats.insert(i)
    max_seats = seatCount

# Display the total available seats and waitlist size
def Available():
    global unassigned_seats, waitlist
    append_result(f"Total Seats Available : {len(unassigned_seats.heap)}, Waitlist : {len(waitlist.heap)}\n")

# Reserve a seat for a user if available; else add user to waitlist
def Reserve(userId, userPriority):
    global unassigned_seats, waitlist, red_black_tree, timeCount
    if not unassigned_seats.heap:
        waitlist.insert(userId, userPriority, timestamp=time.time()*timeCount)
        append_result(f"User {userId} is added to the waiting list\n")
        timeCount += 0.0001
    else:
        seatId = unassigned_seats.extract_min()
        red_black_tree.insert(userId, seatId)
        append_result(f"User {userId} reserved seat {seatId}\n")

# Cancel a user's reservation if found, assign the seat to a waitlisted user if needed
def Cancel(seatId, userId):
    global unassigned_seats, waitlist, red_black_tree
    node = red_black_tree.search(userId)
    if node and node.seatId == seatId:
        append_result(f"User {userId} canceled their reservation\n")
        red_black_tree.delete(userId)
        if waitlist.heap:
            top_waitlister = waitlist.extract_max()
            red_black_tree.insert(top_waitlister[2], seatId)
            append_result(f"User {top_waitlister[2]} reserved seat {seatId}\n")
        else:
            unassigned_seats.insert(seatId)
    else:
        append_result(f"User {userId} has no reservation for seat {seatId} to cancel\n")

# Remove a user from the waitlist if they exist there
def ExitWaitlist(userId):
    if userId in waitlist.user_positions:
        waitlist.remove_user(userId)
        append_result(f"User {userId} is removed from the waiting list\n")
    else:
        append_result(f"User {userId} is not in waitlist\n")

# Update the priority of a waitlisted user
def UpdatePriority(userId, userPriority):
    if userId in waitlist.user_positions:
        waitlist.update_priority(userId, userPriority)
        append_result(f"User {userId} priority has been updated to {userPriority}\n")
    else:
        append_result(f"User {userId} priority is not updated\n")

# Add new seats and assign to top-priority waitlisted users if any
def AddSeats(count):
    global max_seats
    if not isinstance(count, int) or count <= 0:  # Added check for positive number
        append_result("Invalid input. Please provide a valid number of seats.\n")
        return
        
    
    append_result(f"Additional {count} Seats are made available for reservation\n")
    
    # Add all new seats first
    new_seats = []
    for i in range(count):
        max_seats += 1
        new_seats.append(max_seats)
        
    # If waitlist exists, assign seats to waitlisted users
    if waitlist.heap:
        for seat_id in new_seats:
            if not waitlist.heap:  # Break if waitlist becomes empty
                unassigned_seats.insert(seat_id)  # Add remaining seats to unassigned
            else:
                top_waitlister = waitlist.extract_max()
                red_black_tree.insert(top_waitlister[2], seat_id)
                append_result(f"User {top_waitlister[2]} reserved seat {seat_id}\n")
    else:
        # If no waitlist, add all seats to unassigned
        for seat_id in new_seats:
            unassigned_seats.insert(seat_id)

# Print all current seat reservations ordered by seat number
def PrintReservations():
    reservations = red_black_tree.inorder_traversal()
    reservations.sort(key=lambda x: x.seatId)
    for each in reservations:
        append_result(f"Seat {each.seatId}, User {each.key}\n")

# Release seats for users within a range and update waitlist if necessary
def ReleaseSeats(userId1, userId2):
    if not isinstance(userId1, int) or not isinstance(userId2, int) or userId1 > userId2:
        append_result(f"Invalid input. Please provide valid range of users.\n")
        return
    # Since it's guaranteed that userId2 >= userId1, we don't need validation
    # First check if waitlist is empty to determine initial message

    
    append_result(f"Reservations of the Users in the range [{userId1}, {userId2}] are released\n")
    
    # Create a list of (userId, seatId) pairs that will be released
    released_seats = []
    for userId in range(userId1, userId2 + 1):
        node = red_black_tree.search(userId)
        if node:
            seatId = node.seatId
            released_seats.append((userId, seatId))
            red_black_tree.delete(userId)
            unassigned_seats.insert(seatId)
        else:
            # Remove from waitlist if present
            waitlist.remove_user(userId)
    
    # Process waitlist for each released seat
    if waitlist.heap and released_seats:
        for _ in range(len(released_seats)):
            if not waitlist.heap:  # Break if waitlist becomes empty
                break
            top_waitlister = waitlist.extract_max()
            seatId = unassigned_seats.extract_min()
            red_black_tree.insert(top_waitlister[2], seatId)
            append_result(f"User {top_waitlister[2]} reserved seat {seatId}\n")

# Terminate the program
def Quit() -> bool:
    append_result(f"Program Terminated!!\n")
    return True

# Main function
def main():
    global result
    parser = argparse.ArgumentParser(description="Gator Ticket Master")
    parser.add_argument("input_filename", help="Specify the input file name")
    args = parser.parse_args()

    input_file = args.input_filename
    output_file = input_file.replace(".txt", "_output_file.txt")
    
    with open(input_file, 'r', encoding='utf-8') as f:
        commands = f.read().strip().splitlines()

    with open(output_file, 'w', encoding='utf-8') as op:
        for command in commands:
            command = command.strip()
            try:
                # Parse the function name and arguments from the command line
                func_name, args = command.split("(")
                func_name = func_name.strip()
                args = args.strip(")").split(",") if args.strip(")") else []
                args = [int(arg.strip()) for arg in args if arg.strip().isdigit()]

                should_quit = False
                # Call the corresponding function
                if func_name == "Initialize":
                    Initialize(*args)
                elif func_name == "Reserve":
                    Reserve(*args)
                elif func_name == "Available":
                    Available()
                elif func_name == "ExitWaitlist":
                    ExitWaitlist(*args)
                elif func_name == "UpdatePriority":
                    UpdatePriority(*args)
                elif func_name == "AddSeats":
                    AddSeats(*args)
                elif func_name == "Cancel":
                    Cancel(*args)
                elif func_name == "PrintReservations":
                    PrintReservations()
                elif func_name == "ReleaseSeats":
                    ReleaseSeats(*args)
                elif func_name == "Quit":
                    should_quit = Quit()
                    
                
                # Write result to output file
                op.write(result)
                result = ""  # Clear result after each command

                if should_quit:
                    break
            except Exception as e:
                op.write(f"Error processing command '{command}': {str(e)}\n")

if __name__ == '__main__':
    main()
