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

    public static void main(String[] args) {
        new Scheduler();
    }

    private Scheduler() {
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
            added[bestItem[bestFactory]] = true;
            totalCost += Z[bestItem[bestFactory]][bestFactory];
            updateBestItem(bestFactory);

            // one more item added
            count++;
        }

        System.out.println("Best tot cost: " + totalCost);
        System.out.println("Best avg cost: " + ((double) totalCost)/count);
    }

    private void updateBestItem(int factory) {
        int leastCost = 0;

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
}
