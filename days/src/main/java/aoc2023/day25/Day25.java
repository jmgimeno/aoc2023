package aoc2023.day25;

import aoc2023.utils.IO;
import aoc2023.utils.UnionFind;

import java.util.*;
import java.util.stream.IntStream;

public class Day25 {

    public static class Graph {

        public record Edge(int src, int dst) {
        }

        private final Map<String, Integer> ids;
        private final ArrayList<Edge> edges;

        public Graph() {
            ids = new HashMap<>();
            edges = new ArrayList<>();
        }

        public int numVertices() {
            return ids.size();
        }

        public int numEdges() {
            return edges.size();
        }

        public void addEdge(String from, String to) {
            ids.putIfAbsent(from, ids.size());
            ids.putIfAbsent(to, ids.size());
            edges.add(new Edge(ids.get(from), ids.get(to)));
        }

        public void update(String line) {
            var parts = line.split(":");
            var from = parts[0].trim();
            var tos = parts[1].trim().split(" ");
            for (var to : tos) {
                addEdge(from, to.trim());
            }
        }
    }

    public record Cut(long mincut, long part1) {
    }

    public static Cut kargerMinCut(Graph graph) {
        Random random = new Random();
        int vertices = graph.numVertices();
        UnionFind subsets = new UnionFind(vertices);

        while (vertices > 2) {
            var edge = graph.edges.get(random.nextInt(graph.numEdges()));
            int subset1 = subsets.find(edge.src);
            int subset2 = subsets.find(edge.dst);
            if (subset1 != subset2) {
                subsets.union(subset1, subset2);
                vertices--;
            }
        }

        long cutedges =
                graph.edges.stream()
                        .filter(edge -> subsets.find(edge.src) != subsets.find(edge.dst))
                        .count();

        long count0 =
                IntStream.range(0, graph.numVertices())
                        .filter(vertice -> subsets.find(vertice) == subsets.find(0))
                        .count();

        return new Cut(cutedges, count0 * (graph.ids.size() - count0));
    }

    // The idea will be to use different runs of Karger's algorithm
    // to find a mincut of size three (that the problem statement
    // guarantees exists).  Then we can use the fact that the
    // mincut is unique to find the product of the partition sizes

    long part1(List<String> data) {
        var graph = new Graph();
        data.forEach(graph::update);
        Cut cut;
        do {
            cut = kargerMinCut(graph);
        } while (cut.mincut() != 3L);
        return cut.part1();
    }

    public static void main(String[] args) {
        var day25 = new Day25();
        var data = IO.getResourceAsList("day25.txt");
        var part1 = day25.part1(data);
        System.out.println("part1 = " + part1);
    }
}
