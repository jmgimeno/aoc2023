package aoc2023.utils;

public class UnionFind {

    private static class Subset {
        int parent;
        int rank;

        public Subset(int parent, int rank) {
            this.parent = parent;
            this.rank = rank;
        }
    }

    final Subset[] subsets;

    public UnionFind(int size) {
        subsets = new Subset[size];
        for (int i = 0; i < size; i++) {
            subsets[i] = new Subset(i, 0);
        }
    }

    public int find(int i) {
        if (subsets[i].parent != i)
            subsets[i].parent = find(subsets[i].parent);
        return subsets[i].parent;
    }

    public void union(int x, int y) {
        int xroot = find(x);
        int yroot = find(y);
        if (subsets[xroot].rank < subsets[yroot].rank) {
            subsets[xroot].parent = yroot;
        } else if (subsets[xroot].rank > subsets[yroot].rank) {
            subsets[yroot].parent = xroot;
        } else {
            subsets[yroot].parent = xroot;
            subsets[xroot].rank++;
        }
    }
}


