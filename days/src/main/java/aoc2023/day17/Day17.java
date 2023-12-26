package aoc2023.day17;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class Day17 {

    record Position(int x, int y) {
        Position up() {
            return new Position(x, y - 1);
        }

        Position right() {
            return new Position(x + 1, y);
        }

        Position down() {
            return new Position(x, y + 1);
        }

        Position left() {
            return new Position(x - 1, y);
        }
    }

    enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    record Bobby(Position position, Direction direction) {
        Bobby straight() {
            return switch (direction) {
                case UP -> new Bobby(position.up(), Direction.UP);
                case RIGHT -> new Bobby(position.right(), Direction.RIGHT);
                case DOWN -> new Bobby(position.down(), Direction.DOWN);
                case LEFT -> new Bobby(position.left(), Direction.LEFT);
            };
        }

        Bobby left() {
            return switch (direction) {
                case UP -> new Bobby(position.left(), Direction.LEFT);
                case RIGHT -> new Bobby(position.up(), Direction.UP);
                case DOWN -> new Bobby(position.right(), Direction.RIGHT);
                case LEFT -> new Bobby(position.down(), Direction.DOWN);
            };
        }

        Bobby right() {
            return switch (direction) {
                case UP -> new Bobby(position.right(), Direction.RIGHT);
                case RIGHT -> new Bobby(position.down(), Direction.DOWN);
                case DOWN -> new Bobby(position.left(), Direction.LEFT);
                case LEFT -> new Bobby(position.up(), Direction.UP);
            };
        }
    }

    record State(int heatLoss, int heuristic, Bobby bobby,
                 int counter) implements Comparable<State> {

        @Override
        public int compareTo(State o) {
            return Integer.compare(heatLoss + heuristic, o.heatLoss + o.heuristic);
        }

        record Key(Bobby bobby, int counter) {
        }

        Key key() {
            return new Key(bobby, counter); // to not repeat states the counter is needed
        }
    }

    static class Map extends CharGrid {
        public Map(List<String> data) {
            super(data, '#');
        }

        int heatLoss(Position p) {
            return points[p.y()][p.x()] - '0';
        }

        int heuristic(Position p) { // We know the end is the bottom right corner
            return Math.abs(p.x() - width) + Math.abs(p.y() - height);
        }

        int part(int minStraight, int maxStraight) {
            var end = new Position(width, height);
            var queue = new PriorityQueue<State>();
            var visited = new HashSet<State.Key>();
            var p1 = new Position(2, 1);
            queue.add(new State(heatLoss(p1), heuristic(p1), new Bobby(p1, Direction.RIGHT), 1));
            var p2 = new Position(1, 2);
            queue.add(new State(heatLoss(p2), heuristic(p2), new Bobby(p2, Direction.DOWN), 1));
            while (!queue.isEmpty()) {
                var current = queue.remove();
                if (!visited.add(current.key()))
                    continue;
                if (current.bobby().position().equals(end) && minStraight <= current.counter() && current.counter() <= maxStraight)
                    return current.heatLoss;
                queue.addAll(expand(current, minStraight, maxStraight));
            }
            throw new IllegalStateException("no path found");
        }

        List<State> expand(State state, int minStraight, int maxStraight) {
            var straight = straight(state, maxStraight);
            var left = left(state, minStraight);
            var right = right(state, minStraight);
            return Stream.of(straight, left, right)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        Optional<State> straight(State state, int maxStraight) {
            if (state.counter() >= maxStraight)
                return Optional.empty();
            var next = state.bobby().straight();
            if (points[next.position().y()][next.position().x()] == '#')
                return Optional.empty();
            var heatLoss = heatLoss(next.position());
            return Optional.of(new State(state.heatLoss() + heatLoss, heuristic(next.position()), next, state.counter() + 1));
        }

        Optional<State> left(State state, int minStraight) {
            if (state.counter() < minStraight)
                return Optional.empty();
            var next = state.bobby().left();
            if (points[next.position().y()][next.position().x()] == '#')
                return Optional.empty();
            var heatLoss = heatLoss(next.position());
            return Optional.of(new State(state.heatLoss() + heatLoss, heuristic(next.position()), next, 1));
        }

        Optional<State> right(State state, int minStraight) {
            if (state.counter() < minStraight)
                return Optional.empty();
            var next = state.bobby().right();
            if (points[next.position().y()][next.position().x()] == '#')
                return Optional.empty();
            var heatLoss = heatLoss(next.position());
            return Optional.of(new State(state.heatLoss() + heatLoss, heuristic(next.position()), next, 1));
        }

        int part1() {
            return part(0, 3);
        }

        int part2() {
            return part(4, 10);
        }
    }

    int part1(List<String> data) {
        var map = new Map(data);
        return map.part1();
    }

    int part2(List<String> data) {
        var map = new Map(data);
        return map.part2();
    }

    public static void main(String[] args) {
        var day17 = new Day17();
        var data = IO.getResourceAsList("day17.txt");
        var part1 = day17.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day17.part2(data);
        System.out.println("part2 = " + part2);
    }
}
