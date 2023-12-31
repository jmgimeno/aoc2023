package aoc2023.day25;

import aoc2023.utils.IO;

import java.util.*;

public class Day25 {

    public static class Graph {

        public void update(String line) {
            var parts = line.split(":");
            var from = parts[0].trim();
            var tos = parts[1].trim().split(" ");
            for (var to : tos) {
                addEdge(from, to.trim());
            }
        }

        public record Edge(int src, int dst) {
        }

        public Map<String, Integer> ids;
        public ArrayList<Edge> edges;

        public Graph() {
            ids = new HashMap<>();
            edges = new ArrayList<>();
        }

        public void addEdge(String from, String to) {
            ids.putIfAbsent(from, ids.size());
            ids.putIfAbsent(to, ids.size());
            edges.add(new Edge(ids.get(from), ids.get(to)));
        }

        public Graph copy() {
            var copy = new Graph();
            copy.ids = new HashMap<>(ids);
            copy.edges = new ArrayList<>(edges);
            return copy;
        }
    }

    public static class Subset {
        int parent;
        int rank;

        public Subset(int parent, int rank) {
            this.parent = parent;
            this.rank = rank;
        }
    }

    public static int find(Subset[] subsets, int i) {
        if (subsets[i].parent != i)
            subsets[i].parent = find(subsets, subsets[i].parent);
        return subsets[i].parent;
    }

    public static void union(Subset[] subsets, int x, int y) {
        int xroot = find(subsets, x);
        int yroot = find(subsets, y);
        if (subsets[xroot].rank < subsets[yroot].rank) {
            subsets[xroot].parent = yroot;
        } else if (subsets[xroot].rank > subsets[yroot].rank) {
            subsets[yroot].parent = xroot;
        } else {
            subsets[yroot].parent = xroot;
            subsets[xroot].rank++;
        }
    }

    public record Cut(int mincut, int part1) {
    }

    public static Cut kargerMinCut(Graph graph) {
        Random random = new Random();
        int V = graph.ids.size();
        int E = graph.edges.size();
        Graph.Edge[] edge = new Graph.Edge[E];
        graph.edges.toArray(edge);

        Subset[] subsets = new Subset[V];
        for (int v = 0; v < V; ++v) {
            subsets[v] = new Subset(v, 0);
        }

        int vertices = V;

        while (vertices > 2) {
            int i = random.nextInt(E);
            int subset1 = find(subsets, edge[i].src);
            int subset2 = find(subsets, edge[i].dst);
            if (subset1 != subset2) {
                union(subsets, subset1, subset2);
                vertices--;
            }
        }

        int cutedges = 0;
        for (int i = 0; i < E; i++) {
            int subset1 = find(subsets, edge[i].src);
            int subset2 = find(subsets, edge[i].dst);
            if (subset1 != subset2) {
                cutedges++;
            }
        }

        int class0 = find(subsets, 0); // subset of vertex 0
        int count0 = 1;
        for (int i = 1; i < V; i++) {
            if (find(subsets, i) == class0) {
                count0++;
            }
        }

        return new Cut(cutedges, count0 * (V - count0));
    }

    // The idea will be to use different runs of Karger's algorithm
    // to find a mincut of size three (that the problem statement
    // guarantees exists).  Then we can use the fact that the
    // mincut is unique to find the product of the partition sizes
    int part1(List<String> data) {
        var graph = new Graph();
        data.forEach(graph::update);
        Cut cut;
        do {
            var g = graph.copy();
            cut = kargerMinCut(g);
        } while (cut.mincut() != 3);
        return cut.part1();
    }

    public static void main(String[] args) {
        var day25 = new Day25();
        var data = IO.getResourceAsList("day25.txt");
        var part1 = day25.part1(data);
        System.out.println("part1 = " + part1);
    }
}
