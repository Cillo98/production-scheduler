# M Factories produce N items
This program's goal is to schedule the production of N items in a company that owns M factories.
Each factory can produce any type of item. However, different types of items have different production
costs in different factories. Once a factory starts working on an item, it must complete it. A factory
can work on one item at a time. The problem is to find the best production scheduling to maximize the
average production cost of the items.

## The problem
Following is a description of the problem's parameters with examples.

### Input
The program will read a file with the following structure:
    - The first line contains the number of test cases in the file  
    - All test cases are preceded by an empty line  
    - A test case's first line contains two numbers: N and M, separated by a space  
    - Each of the following N lines contains the production costs of an item in every M factory (costs are separated by a space)  

Example of an input file with 3 tests:
```
3  

3 4  

1 100 100 100  
99 1 99 99  
98 98 1 98  

4 5

1 5 4 1 3  
4 7 20 2 4  
2 3 9 3 5  
3 4 15 4 2  

3 4

1 100 100 100  
1 99 99 99  
98 98 1 98  
```
  
### Output
The program must print in the console the best average production time of each test case.

Example of the console output of the previous example:
> Test: 0	Average Cost: 1.000000  
  Test: 1	Average Cost: 2.000000  
  Test: 2	Average Cost: 1.333333  
  
## The solution
Following is the algorithm designed to solve the problem with maximum efficiency. The algorithm provides a time complexity of _O(N log N)_ and a space efficiency of _8(N\*M) + 4(N+M) + 16 bytes_.

### Algorithm
Once the input data is loaded, the algorithm takes the following steps for each of the test cases:  
1. Instantiate the variables needed by the algorithm:  
    - **Z**: integer matrix _NxM_ with the costs of _N_ items in each of the _M_ factories;  
    - **W**: array of _M_ integers with the waiting times to produce the next item in
        each factory. In other words, the time that the factory will be working: to
        produce two items with costs 3 and 20, the factory will take 23, that is the
        time that another item must wait before starting production;  
    - **added**: array of _N_ booleans to keep track of what items are already scheduled for production  
    - **bestItems**: array of M LinkedLists of integers that represent the ordered queue of the best items that each factory can produce. If a factory can produce item 1 in 5, item 2 in 3 and item 3 in 19, its queue will be _{2, 1, 3}_: it takes the least to make item 2, more to make item 1 and even more to make item 3.  
2. Generate the queues of best items (_bestItems_). Once this step is done, popping from any list
    will return the index of the item that a factory can make in the fastest time  
3. Pick one by one the item that can be produced in the least time. For each factory only the least-cost item
    is considered. The production cost of an item in factory is calculated as the sum of the production
    cost of the item in the factory and the waiting time of the factory to produce the next item. 
    _NOTE: before an item is picked, each list is popped until the head is not added yet to production_
4. Output the average production cost for the items picked

Following is the pseudo code:
```
Z <- matrix from file  
W[N] <- 0, 0, ..., 0  
added[N] <- false, false, ..., false  
bestItems[M][N] <- 0,..,0; 0,..,0; ...  

for (f from 0 to M-1)  
    list <- 0  
    for (i from 1 to N-1)  
        list <- i, based on Z[i][f] // binary insertion  
    bestItems[f] <- list  

for (_ from 0 to N-1)  
    updateBestItems()  
    bestFactory <- getBestFactory()
    item <- bestItems[bestFactory][0]
    added[item] <- true
    W[bestFactory] <- W[bestFactory] + Z[item][bestFactory]
    totalCost <- totalCost + W[bestFactory]

updateBestItems():  
    for (factoryQueue in bestItems)  
        while (added[factoryQueue[0]])
            factoryQueue.pop()

int getBestFactory():  
    leastCost <- infinity
    indexLeastCost <- 0
    for (f from 0 to M)
        if (Z[bestItems[f][0]][f] + W[f] < leastCost)
            leastCost = Z[bestItems[f][0]][f] + W[f]
            indexLeastCost = f
    return indexLeastCost
```

### Efficiency Analysis
This algorithm's time complexity is analyzed by looking at the pseudocode and its space efficiency is analyzed by calculating the memory requirement of the implementation of the algorithm in Java.

#### Time Complexity
Dividing the pseudocode into chunks, the following number of operations per chunk can be found:

1. Instantiate the variables needed by the algorithm: _N\*M + M + N + M_
    - Z: _N\*M_  
    - W: _M_
    - added: _N_ 
    - bestItems: _M_
2. Generate the queues of best items (_bestItems_): _M*(N log N)_
    - For each factory (_M_), build an ordered array of indices using Binary Insertion Sort (_N log N)
3. Pick one by one the item that can be produced in the least time: _M_
    - Update heads: _M_ (every time an item is produced, that item must be removed from the other _M-1_ factories)
    - Pick best factory: _M_
    - Update costs: _1_
4. Output the average production cost for the items picked: _1_
Putting them together, the total time complexity is _O( \[N\*M + M + N + M\] + \[M*(N log N)\] + \[M\] + \[1\] ) = O(M*(N log N))_.

However, considering that _M_ has an upper constraint of 50: _M <= 50_, it can be inferred that the algorithm has an overall time complexity of **O(N log N)**.
![Time complexity graph](/time.png?raw=true "Time complexity")

#### Space Efficiency
The algorithm makes use of the following variables (in the format {name, type, size}):
    - Z, int, _N x M_
    - W, int, _M_ 
    - added, int, _N_ 
    - bestItems, LinkedList, _M_; LinkedList, int, _N_ ==> bestItems, int, _M x N_
    - tests, int, 1
    - totalCost, int, 1
    - avg, double, 1

Not considering extra costs related to object allocation in Java and knowing that an integer is 4 bytes and a double is 8 bytes, the total memory usage can be calculated as  
_memory = 4 \* (N\*M) + 4 \* M + 4 \* N + 4 \* (N\*M) + 4 \* 1 + 4 \* 1 + 8 \* 1 =_  
_= 4 \* (2\*N\*M + M + N + 2) + 8 =_ **8(N\*M) + 4(N+M) + 16 bytes**  

For a large input, say _M = 50_ and _N = 1000_, the memory requirement is 404KB.
![Space complexity graph](/space.png?raw=true "Sapce complexity")
