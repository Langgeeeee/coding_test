
1. quiz.py (Python)

Implement the following functions:

def reverse_list(l: list):
    """
    Reverse a list without using any built-in functions.
    The function should return a reversed list.
    Input l is a list that may contain any type of data.
    """
    pass
 
def solve_sudoku(matrix):
    """
    Write a program to solve a 9x9 Sudoku board.
    The board must be completed so that every row, column, and 3x3 section
    contains all digits from 1 to 9.
    Input: a 9x9 matrix representing the board.
    """
    pass
2. webapp

Requirements (Java)
Build a web service with the following at least two API endpoints:

Image Upload and Compression Endpoint
Allows users to upload an image and provides a compressed version of that image.
All logic must be implemented within the service itself.
The compression algorithm does not need to be advanced — basic functionality is sufficient; compression quality doesn’t matter.
Need to consider that the API is being accessed concurrently.
Processing History Endpoint
Returns the history of processed images.
No database configuration is required.
All storage must be in-memory only.
Restrictions

Do not use any external services such as cloud platforms (e.g., S3), serverless environments, or third-party SDKs/APIs.
No additional external dependencies should be introduced.
README Requirement
Please include a README file that explains how to review and test your application.

Important Notes

This assignment evaluates your design and development skills.
Make your service robust and reliable. Consider edge cases.
Additional features are not required, but feel free to include them if you believe they are essential.
Focus on code quality over feature quantity.
3. review.py (Python)

Review and refactor the following five code snippets. Identify any issues, explain the problems, and provide corrected versions.

# Review 1
def add_to_list(value, my_list=[]):
    my_list.append(value)
    return my_list
 
# Review 2
def format_greeting(name, age):
    return "Hello, my name is {name} and I am {age} years old."
 
# Review 3
class Counter:
    count = 0
    def __init__(self):
        self.count += 1
    def get_count(self):
        return self.count
 
# Review 4
import threading
class SafeCounter:
    def __init__(self):
        self.count = 0
    def increment(self):
        self.count += 1
 
def worker(counter):
    for _ in range(1000):
        counter.increment()
 
counter = SafeCounter()
threads = []
for _ in range(10):
    t = threading.Thread(target=worker, args=(counter,))
    t.start()
    threads.append(t)
 
for t in threads:
    t.join()
 
# Review 5
def count_occurrences(lst):
    counts = {}
    for item in lst:
        if item in counts:
            counts[item] =+ 1
        else:
            counts[item] = 1
    return counts
