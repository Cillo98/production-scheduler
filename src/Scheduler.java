import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Scheduler {
    // INPUT RELATED DATA
    private int[][] Z;      // cost matrix
    private int tests;      // number of tests

    // ALGORITHM DATA
    private boolean[] added;                    // list of produced products
    private LinkedList<Integer>[] bestItems;    // best item to produce for each factory
    private int[] W;                            // waiting time for each factory

    private IO io;          // file reader

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: invalid arguments. Must include 1 argument: the filename");
            return;
        }
        new Scheduler(args[0]);
    }

    private Scheduler(String filename) {
        try {
            init(filename);

            for (int test=0; test < tests; test++) {
                // load the data of this test
                Z = io.getTestMatrix(test);
                W = new int[Z[0].length];
                added = new boolean[Z.length];

                // initialize best items
                bestItems = new LinkedList[Z[0].length];
                generateQueues();

                // run the test
                double avg = test();
                System.out.printf("Test: %d\tAverage Cost: %.6f\n", test+1, avg);
            }

        } catch (IOException e) {
            System.out.printf("Error loading file. Make sure it is '%s'", filename);
        }
    }

    private void init(String filename) throws IOException {
        // instantiate the file reader
        io = new IO(filename);

        // get number of tests
        tests = io.getTestCases();
    }

    private double test() {
        int totalCost = 0;

        // loop once per item
        for (int[] ignored : Z) {
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
        }

        return ((double) totalCost) / Z.length;
    }

    private void generateQueues() {
        // for every factory
        for (int f = 0; f < Z[0].length; f++) {
            // make a sorted list of item indices
            LinkedList<Integer> list = new LinkedList<>();
            list.add(0);

            // add all items
            for (int i = 1; i < Z.length; i++) {
                insertInList(list, 0, list.size()-1, i, f);
            }

            // and put the list in the array of lists
            bestItems[f] = list;
        }
    }

    private void insertInList(LinkedList<Integer> list, int from, int to, int i, int f) {
        int mid = (from + to) / 2;

        if (list.size() == 1) {
            if (Z[list.get(mid)][f] < Z[i][f])
                list.addLast(i);
            else
                list.addFirst(i);
        }

        else if (from == to) {
            if (Z[list.get(mid)][f] < Z[i][f])
                list.add(mid+1, i);

            // if less or equal to the number pointed, insert there
            else
                list.add(mid, i);
        }

        // mid item is smaller, add to second half of the list
        else if (Z[list.get(mid)][f] < Z[i][f])
            insertInList(list, mid+1, to, i, f);
        
        // mid item is larger, add to first half of the list
        else if (Z[list.get(mid)][f] > Z[i][f])
            insertInList(list, from, mid, i, f);
        
        // item costs are the same, add here
        else 
            list.add(mid, i);
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
                if (lines.get(i).isEmpty())
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
