package mineSweeping;

// Data Structures & Algorithms
// Spring 2018
// HW2: bfs/dfs traversal
// text-based comparison of bfs, dfs, and random walk us


import java.util.Random;


public class TextTraverse {

    private static Random randomizer = new Random();

    private static final int DEFAULT_COLUMNS = 16;
    private static final int DEFAULT_ROWS = 12;
    
    public static void main(String[] args) {
        boolean verbose = false;
        int columns = DEFAULT_COLUMNS;
        int rows = DEFAULT_ROWS;
        for (String arg : args) {
            if (arg.equals("-verbose")) {
                verbose = true;
            } else if (arg.startsWith("-r")) {
                String s = arg.substring(2);
                try {
                    rows = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.err.println("bad row value; using default");
                }
            } else if (arg.startsWith("-c")) {
                String s = arg.substring(2);
                try {
                    columns = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.err.println("bad column value; using default");
                }
            } else {
                System.err.println("ignoring unknown command argument: " + arg);
            }
        }
        int r = randomizer.nextInt(rows);
        int c = randomizer.nextInt(columns);
        Pair<Integer, Integer> p = new Pair<Integer, Integer>(c, r);
        TraverserInterface walker = new RandomWalk(columns, rows, p);
        traverse(walker, columns, rows, p, "random walk", verbose);
        walker = new Bfs(columns, rows, p);
        traverse(walker, columns, rows, p, "breadth first", verbose);
        walker = new Dfs(columns, rows, p);
        traverse(walker, columns, rows, p, "depth first", verbose);
    }

    public static void traverse(TraverserInterface walker,
                               int columns,
                               int rows,
                               Pair<Integer, Integer> origin,
                               String name,
                               boolean verbose) {
        GridCounter grid = new GridCounter(columns, rows);
        if (verbose) {
            System.out.println("\ntraversal method: " + name);
            System.out.println("starting at: " + origin);
        }
        grid.increment(origin.first(), origin.second());
        int steps = 0;
        while (grid.zeros() > 0 && walker.hasNext()) {
            Pair<Integer, Integer> p = walker.next();
            int n = grid.get(p.first(), p.second());
            if (verbose) {
                System.out.print("visiting: " + p);
                if (n > 0) {
                    System.out.print(" (previous visits: " + n + ")");
                }
                System.out.println();
            }
            grid.increment(p.first(), p.second());
            steps++;
        }
        if (grid.zeros() > 0) {
            System.out.println("unreached cells: " + grid.zeros());
        }
        System.out.println("total steps for " + name + ": " + steps);
    }

}
