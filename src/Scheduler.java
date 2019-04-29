import java.util.*;

public class Scheduler {
    private int[][] Z = {
            {3, 5},
            {4, 7},
            {3, 3},
            {3, 4}
    };
    private boolean added[] = new boolean[Z.length];
    private int bestItem[] = new int[Z[0].length];
    private int count = 0;
    private int totalCost = 0;
    private LinkedList<Integer>[] bestItems;

    public static void main(String[] args) {
        new Scheduler();
    }

    private Scheduler() {
        generateQueues();

        // at the beginning no items are added
        for (int i=0; i<added.length; i++)
            added[i] = false;

        // initialize the first best items per factory
        for (int i=0; i<Z[0].length; i++)
            updateBestItem(i);

        while (count < Z.length) {
            // get the best factory to produce the next item
            int bestFactory = getBestFactory();

            // produce the item. to produce it, do:
            //  1. change the 'added' flag
            //  2. add its cost to the total cost
            //  3. update the cost of other items in the same factory
            System.out.printf("Picked item %d with cost %d\n", bestItem[bestFactory], Z[bestItem[bestFactory]][bestFactory]);
            added[bestItem[bestFactory]] = true;
            totalCost += Z[bestItem[bestFactory]][bestFactory];
            updateCosts(bestFactory, Z[bestItem[bestFactory]][bestFactory]);

            // must review which one is the best item to produce now
            updateBestItem(bestFactory);

            // one more item added
            count++;
        }

        System.out.println("Best tot cost: " + totalCost);
        System.out.println("Best avg cost: " + ((double) totalCost)/count);
    }

    private void generateQueues() {
        // for every factory
        for (int f=0; f<Z[0].length; f++) {

            // make a sorted list of item indices
            LinkedList<Integer> list = new LinkedList<>();
            list.add(0);

            for (int i=1; i<Z.length; i++)
                for (int pos=0; i<list.size(); pos++)
                    if (Z[list.get(pos)][f] > Z[i][f])
                        list.add(pos, i);

            // and put the list in the array of lists
            bestItems[f] = list;
        }
    }

    private void updateBestItem(int factory) {
        int leastCost = 0;
        while (added[leastCost])
            leastCost++;

        for (int item=1; item<Z.length; item++)
            if (!added[item])
                if (Z[item][factory] < Z[leastCost][factory])
                    leastCost = item;

        bestItem[factory] = leastCost;
    }

    private int getBestFactory() {
        int leastCost = Z[bestItem[0]][0];
        int indexLeastCost = 0;

        // check every factory to pick the factory who
        // can make the next item the fastest
        for (int i=0; i<bestItem.length; i++) {
            if (Z[bestItem[i]][i] < leastCost) {
                leastCost = Z[bestItem[i]][i];
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
