import java.util.*;

public class Scheduler {
    private int[][] Z = {
            {3, 5},
            {4, 7},
            {2, 3},
            {3, 4}
    };
    private boolean[] added = new boolean[Z.length];
    private LinkedList<Integer>[] bestItems;

    public static void main(String[] args) {
        new Scheduler();
    }

    private Scheduler() {
        // initialize best items
        bestItems = (LinkedList<Integer>[]) new LinkedList[Z[0].length];
        generateQueues();

        // at the beginning no items are added
        for (int i=0; i<added.length; i++)
            added[i] = false;

        //private int bestItem[] = new int[Z[0].length];
        int count = 0;
        int totalCost = 0;
        while (count < Z.length) {
            // review which one is the best item to produce for each factory
            updateBestItems();

            // get the best factory to produce the next item
            int bestFactory = getBestFactory();
            int item = bestItems[bestFactory].get(0);

            // produce the item. to produce it, do:
            //  1. change the 'added' flag
            //  2. add its cost to the total cost
            //  3. update the cost of other items in the same factory
            added[item] = true;
            totalCost += Z[item][bestFactory];
            updateCosts(bestFactory, Z[item][bestFactory]);

            // one more item added
            count++;

            // log
            System.out.printf("Picked item %d with cost %d\n", item, Z[item][bestFactory]);
        }

        System.out.println("Best tot cost: " + totalCost);
        System.out.println("Best avg cost: " + ((double) totalCost)/ count);
    }

    private void generateQueues() {
        // for every factory
        for (int f=0; f<Z[0].length; f++) {

            // make a sorted list of item indices
            LinkedList<Integer> list = new LinkedList<>();
            list.add(0);

            for (int i=1; i<Z.length; i++) {
                int pos = 0;
                while (pos<list.size()) {
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
                itemsQueue.remove(0);
    }

    private int getBestFactory() {
        int leastCost = Integer.MAX_VALUE;
        int indexLeastCost = 0;

        // check every factory to pick the factory who
        // can make the next item the fastest
        for (int i=0; i<bestItems.length; i++) {
            if (Z[bestItems[i].get(0)][i] < leastCost) {
                leastCost = Z[bestItems[i].get(0)][i];
                indexLeastCost = i;
            }
        }

        return indexLeastCost;
    }

    private void updateCosts(int factory, int increment) {
        for (int i=0; i<Z.length; i++)
            Z[i][factory] += increment;
    }
}
