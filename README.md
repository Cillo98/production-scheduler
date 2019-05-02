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
> 3  
>
> 3 4  
>
> 1 100 100 100  
  99 1 99 99  
  98 98 1 98  
>
> 4 5
>
> 1 5 4 1 3  
  4 7 20 2 4  
  2 3 9 3 5  
  3 4 15 4 2  
>
> 3 4
>
> 1 100 100 100  
  1 99 99 99  
  98 98 1 98  
  
### Output
The program must print in the console the best average production time of each test case.

Example of the console output of the previous example:
> Test: 0	Average Cost: 1.000000  
  Test: 1	Average Cost: 2.000000  
  Test: 2	Average Cost: 1.333333  
  
## The solution
Following is the algorithm designed to solve the problem with maximum efficiency.

### Algorithm
Once the input data is loaded, the algorithm takes the following steps for each of the test cases:
    1. Instantiate the variables needed by the algorithm:
        - **Z**: integer matrix _NxM_ with the costs of _N_ items in each of the _M_ factories;
        - **W**: array of _M_ integers with the waiting times to produce the next item in
        each factory. In other words, the time that the factory will be working: to
        produce two items with costs 3 and 20, the factory will take 23, that is the
        time that another item must wait before starting production;
        - **added**: array of _N_ booleans to keep track of what items are already scheduled for production
        - **bestItems**: array of M LinkedLists of integers that represent the ordered queue of the
        best items that each factory can produce. If a factory can produce item 1 in 5, item 2 in 3 and item
        3 in 19, its queue will be _{2, 1, 3}_: it takes the least to make item 2, more to make item 1 and even
        more to make item 3.
    2. Generate the queues of best items (_bestItems_). Once this step is done, popping from any list
    will return the index of the item that a factory can make in the fastest time
    3. Pick one by one the item that can be produced in the least time. For each factory only the least-cost item
    is considered. The production cost of an item in factory is calculated as the sum of the production
    cost of the item in the factory and the waiting time of the factory to produce the next item.
    4. Output the average production cost for the items picked

Following is the pseudo code:
'''
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
'''