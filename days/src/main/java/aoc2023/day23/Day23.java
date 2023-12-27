package aoc2023.day23;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Day23 {

    record Position(int x, int y) {
    }

    static class HikingTrailsMap extends CharGrid {
        HikingTrailsMap(List<String> data) {
            super(data, false);
        }

        List<Position> neighbours(int x, int y) {
            var neighbours = new ArrayList<Position>();
            if ((points[y][x] == '.' || points[y][x] == '^') && y > 0 && points[y - 1][x] != '#')
                neighbours.add(new Position(x, y - 1));
            if ((points[y][x] == '.' || points[y][x] == 'v') && y < height -1 && points[y + 1][x] != '#')
                neighbours.add(new Position(x, y + 1));
            if ((points[y][x] == '.' || points[y][x] == '<') && x > 0 && points[y][x - 1] != '#')
                neighbours.add(new Position(x - 1, y));
            if ((points[y][x] == '.' || points[y][x] == '>') && x < width -1 && points[y][x + 1] != '#')
                neighbours.add(new Position(x + 1, y));
            return neighbours;
        }

        public Graph<Position> pathCompress(Position start, Position end) {
            var vertices = findVertices(start, end);
            var edges = findEdges(vertices);
            return new Graph<>(vertices, edges);
        }

        private List<Graph.Edge<Position>> findEdges(List<Position> vertices) {

            record State(Position position, int distance) {
            }

            var edges = new ArrayList<Graph.Edge<Position>>();
            // do a btf from the first vertex to the next and finish when all vertices are visited
            // if there is a vertex in the way, add an edge
            for (var vertex : vertices) {
                // do a bfs from current to the next vertex
                // if there is a vertex in the way, add an edge
                var queue = new ArrayList<State>();
                var visited = new HashSet<Position>();
                queue.add(new State(vertex, 0));
                while (!queue.isEmpty()) {
                    var state = queue.removeFirst();
                    var next = state.position;
                    int distance = state.distance;
                    if (!visited.add(next)) continue;
                    if (vertices.contains(next) && !next.equals(vertex)) {
                        edges.add(new Graph.Edge<>(vertex, next, distance));
                        continue;
                    }
                    for (var neighbour : neighbours(next.x, next.y)) {
                        queue.add(new State(neighbour, distance + 1));
                    }
                }
            }
            return edges;
        }

        private List<Position> findVertices(Position start, Position end) {
            // I do not consider the start and end positions
            var vertices = new ArrayList<>(List.of(start, end));
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (points[y][x] != '#') {
                        if (neighbours(x, y).size() > 2) {
                            vertices.add(new Position(x, y));
                        }
                    }
                }
            }
            return vertices;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }
    }

    static class Graph<V> {

        private final List<V> vertices;
        private final List<Edge<V>> edges;

        record Edge<V>(V from, V to, int weight) {
        }

        public Graph(List<V> vertices, List<Edge<V>> edges) {
            this.vertices = vertices;
            this.edges = edges;
        }

        int longestPathLength(V start, V end) {
            var distances = new int[vertices.size()];
            for (int i = 0; i < vertices.size(); i++) {
                distances[i] = Integer.MIN_VALUE;
            }
            distances[vertices.indexOf(start)] = 0;
            var queue = new ArrayList<V>();
            queue.add(start);
            while (!queue.isEmpty()) {
                var current = queue.removeFirst();
                var currentDistance = distances[vertices.indexOf(current)];
                for (var edge : edges) {
                    if (edge.from.equals(current)) {
                        var newDistance = currentDistance + edge.weight;
                        var toIndex = vertices.indexOf(edge.to);
                        if (newDistance > distances[toIndex]) {
                            distances[toIndex] = newDistance;
                            queue.add(edge.to);
                        }
                    }
                }
            }
            return distances[vertices.indexOf(end)];
        }
    }

    int part1(List<String> data) {
        var map = new HikingTrailsMap(data);
        var start = new Position(1, 0);
        var end = new Position(map.width() - 2, map.height() - 1);
        // The map does not give many options (in most positions the movement
        // is restricted to one direction) so I can use a simple path compression
        // to create the graph pf decission points and then use the longest path
        var graph = map.pathCompress(start, end);
        return graph.longestPathLength(start, end);
    }

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day23 = new Day23();
        var data = IO.getResourceAsList("day23.txt");
        var part1 = day23.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day23.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
