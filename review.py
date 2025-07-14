# Review 1
# def add_to_list(value, my_list=[]):
# argument is created when the function is defined, it's like a global variable, this may lead to unexpected behavior.

def add_to_list(value, my_list=None):

    if my_list is None:
        my_list = []
    my_list.append(value)

    return my_list

# Review 2
def format_greeting(name, age):
    # return "Hello, my name is {name} and I am {age} years old."
    # this is a print format mistake, we can use f-string to fix it
    return f"Hello, my name is {name} and I am {age} years old."

# Review 3
class Counter:
    count = 0
    def __init__(self):
        # self.count += 1
        # count is a class variable not an instance variable
        Counter.count += 1
    def get_count(self):
        # return self.count
        # count is a class variable not an instance variable
        return Counter.count

# Review 4
import threading

class SafeCounter:
    def __init__(self):
        self.count = 0
        self.lock = threading.Lock()

    def increment(self):
    #    self.count += 1
    # it is not atomic, we should use lock to make sure thread-safe
        with self.lock:
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
            #counts[item] = + 1
            counts[item] += 1   #there is a typo, should not be "=+"
        else:
            counts[item] = 1
    return counts


# if __name__ == "__main__":

    # add_to_list(1, [2,3])
    # print(format_greeting('wenlg', 23))
    # print(count_occurrences([1,2,3,1]))
