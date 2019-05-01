import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Scheduler {
//    private int[][] Z = {
//            {1, 5, 4, 1, 3},
//            {4, 7, 20, 2, 4},
//            {2, 3, 9, 3, 5},
//            {3, 4, 15, 4, 2}
//    };
    private int[][] Z;
    //    private int[][] Z = {
//            {1, 100, 100},
//            {99, 1, 99},
//            {98, 1, 98}
//    };
    private boolean[] added;
    private LinkedList<Integer>[] bestItems;

    // waiting time for each factory
    private int[] W;

    public static void main(String[] args) {
        new Scheduler();
    }

    private Scheduler() {
        String filename = "data.txt";
        try {
            // instantiate the file reader
            IO io = new IO(filename);

            // read the number of tests
            int tests = io.getTestCases();

            for (int test = 0; test < tests; test++) {
                System.out.printf("Test %d has %d items in %d factories\n",
                        test, io.getItemNumber(test), io.getFactoryNumber(test));
            }

            Z = io.getTestMatrix(1);
            W = new int[Z[0].length];
            added = new boolean[Z.length];

        } catch (IOException e) {
            System.out.printf("Error loading file. Make sure it is '%s'", filename);
            return;
        }

        // initialize best items
        bestItems = (LinkedList<Integer>[]) new LinkedList[Z[0].length];
        generateQueues();

        // at the beginning no items are added
        for (int i = 0; i < added.length; i++)
            added[i] = false;

        int totalCost = 0;
        for (int i = 0; i < Z.length; i++) {
            // review which one is the best item to produce for each factory
            updateBestItems();

            // get the best factory to produce the next item
            int bestFactory = getBestFactory();
            int item = bestItems[bestFactory].get(0);

            // produce the item. to produce it, do:
            //  1. change the 'added' flag
            //  2. update the total production time of the factory
            //  3. add its cost to the total cost
            added[item] = true;
            W[bestFactory] += Z[item][bestFactory]; // time to make item in bestFactory
            totalCost += W[bestFactory];

            // log
            System.out.printf("Picked item %d,%d with cost %d\n", item, bestFactory, W[bestFactory]);
        }

        System.out.println("Best tot cost: " + totalCost);
        System.out.printf("Best avg cost: %.6f", ((double) totalCost) / Z.length);
    }

    private void generateQueues() {
        // for every factory
        for (int f = 0; f < Z[0].length; f++) {

            // make a sorted list of item indices
            LinkedList<Integer> list = new LinkedList<>();
            list.add(0);

            for (int i = 1; i < Z.length; i++) {
                int pos = 0;
                while (pos < list.size()) {
                    if (Z[list.get(pos)][f] < Z[i][f])
                        pos++;
                    else
                        break;
                }
                list.add(pos, i);
            }

            // and put the list in the array of lists
            bestItems[f] = list;
        }
    }

    private void updateBestItems() {
        for (LinkedList<Integer> itemsQueue : bestItems)
            while (added[itemsQueue.get(0)])
                itemsQueue.pop();
    }

    private int getBestFactory() {
        int leastCost = Integer.MAX_VALUE;
        int indexLeastCost = 0;

        // check every factory to pick the factory who
        // can make the next item the fastest
        for (int f = 0; f < bestItems.length; f++) {
            if (Z[bestItems[f].get(0)][f] + W[f] < leastCost) {
                leastCost = Z[bestItems[f].get(0)][f] + W[f];
                indexLeastCost = f;
            }
        }

        return indexLeastCost;
    }


    private class IO {
        private List<String> lines;
        private List<Integer> testHead = new ArrayList<>();

        IO (String filename) throws IOException {
            lines = Files.readAllLines(Paths.get(filename));

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).isBlank())
                    testHead.add(i+1);
            }
        }

        int getTestCases() {
            return Integer.parseInt(lines.get(0));
        }

        int getItemNumber(int testCase) {
            return Integer.parseInt(
                    lines.get(testHead.get(testCase)) // line containing "3 4"
                            .split(" ")[0] // get the first number
            );
        }

        int getFactoryNumber(int testCase) {
            return Integer.parseInt(
                    lines.get(testHead.get(testCase)) // line containing "3 4"
                            .split(" ")[1] // get the second number
            );
        }

        int[][] getTestMatrix(int testCase) {
            int items = getItemNumber(testCase);
            int factories = getFactoryNumber(testCase);
            int[][] Z = new int[items][factories];

            for (int i = 0; i < items; i++) {
                String[] costs = lines.get(testHead.get(testCase)+1 + i).split(" ");

                for (int c = 0; c < costs.length; c++) {
                    Z[i][c] = Integer.parseInt(costs[c]);
                }
            }

            return Z;
        }
    }
}
