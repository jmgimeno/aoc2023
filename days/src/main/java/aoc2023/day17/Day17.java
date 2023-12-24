package aoc2023.day17;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class Day17 {

    /*
        --- Day 17: Clumsy Crucible ---
    The lava starts flowing rapidly once the Lava Production Facility is operational. As you leave,
    the reindeer offers you a parachute, allowing you to quickly reach Gear Island.

    As you descend, your bird's-eye view of Gear Island reveals why you had trouble finding anyone
    on your way up: half of Gear Island is empty, but the half below you is a giant factory city!

    You land near the gradually-filling pool of lava at the base of your new lavafall. Lavaducts
    will eventually carry the lava throughout the city, but to make use of it immediately, Elves are
    loading it into large crucibles on wheels.

    The crucibles are top-heavy and pushed by hand. Unfortunately, the crucibles become very
    difficult to steer at high speeds, and so it can be hard to go in a straight line for very long.

    To get Desert Island the machine parts it needs as soon as possible, you'll need to find the
    best way to get the crucible from the lava pool to the machine parts factory. To do this, you
    need to minimize heat loss while choosing a route that doesn't require the crucible to go in a
    straight line for too long.

    Fortunately, the Elves here have a map (your puzzle input) that uses traffic patterns, ambient
    temperature, and hundreds of other parameters to calculate exactly how much heat loss can be
    expected for a crucible entering any particular city block.

    For example:

    2413432311323
    3215453535623
    3255245654254
    3446585845452
    4546657867536
    1438598798454
    4457876987766
    3637877979653
    4654967986887
    4564679986453
    1224686865563
    2546548887735
    4322674655533
    Each city block is marked by a single digit that represents the amount of heat loss if the
    crucible enters that block. The starting point, the lava pool, is the top-left city block; the
    destination, the machine parts factory, is the bottom-right city block. (Because you already
    start in the top-left block, you don't incur that block's heat loss unless you leave that block
    and then return to it.)

    Because it is difficult to keep the top-heavy crucible going in a straight line for very long,
    it can move at most three blocks in a single direction before it must turn 90 degrees left or
    right. The crucible also can't reverse direction; after entering each city block, it may only
    turn left, continue straight, or turn right.

    One way to minimize heat loss is this path:

    2>>34^>>>1323
    32v>>>35v5623
    32552456v>>54
    3446585845v52
    4546657867v>6
    14385987984v4
    44578769877v6
    36378779796v>
    465496798688v
    456467998645v
    12246868655<v
    25465488877v5
    43226746555v>
    This path never moves more than three consecutive blocks in the same direction and incurs a heat
    loss of only 102.

    Directing the crucible from the lava pool to the machine parts factory, but not moving more than
    three consecutive blocks in the same direction, what is the least heat loss it can incur?
    */

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

    record State(int length, Bobby bobby, int counter) implements Comparable<State> {
        @Override
        public int compareTo(State o) {
            return Integer.compare(length, o.length); // swapped cause min length first
        }
    }

    static class Map extends CharGrid {
        public Map(List<String> data) {
            super(data, '#');
        }

        int heatLoss(Position p) {
            return points[p.y()][p.x()] - '0';
        }

        int part1() {
            var end = new Position(width, height);
            var queue = new PriorityQueue<State>();
            var visited = new HashSet<State>();
            queue.add(new State(heatLoss(new Position(2, 1)), new Bobby(new Position(2, 1), Direction.LEFT), 1));
            queue.add(new State(heatLoss(new Position(1, 2)), new Bobby(new Position(1, 2), Direction.DOWN), 1));
            while (!queue.isEmpty()) {
                var current = queue.remove();
                if (!visited.add(current))
                    continue;
                if (current.bobby().position().equals(end))
                    return current.length;
                queue.addAll(expand(current));
            }
            throw new IllegalStateException("no path found");
        }

        List<State> expand(State state) {
            var straight = straight(state);
            var left = left(state);
            var right = right(state);
            return Stream.of(straight, left, right)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        Optional<State> straight(State state) {
            if (state.counter() == 3)
                return Optional.empty();
            var next = state.bobby().straight();
            if (points[next.position().y()][next.position().x()] == '#')
                return Optional.empty();
            var heatLoss = heatLoss(next.position());
            return Optional.of(new State(state.length() + heatLoss, next, state.counter() + 1));
        }

        Optional<State> left(State state) {
            var next = state.bobby().left();
            if (points[next.position().y()][next.position().x()] == '#')
                return Optional.empty();
            var heatLoss = heatLoss(next.position());
            return Optional.of(new State(state.length() + heatLoss, next, 1));
        }

        Optional<State> right(State state) {
            var next = state.bobby().right();
            if (points[next.position().y()][next.position().x()] == '#')
                return Optional.empty();
            var heatLoss = heatLoss(next.position());
            return Optional.of(new State(state.length() + heatLoss, next, 1));
        }
    }

    int part1(List<String> data) {
        var map = new Map(data);
        return map.part1();
    }

    /*

     */

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day17 = new Day17();
        var data = IO.getResourceAsList("day17.txt");
        var part1 = day17.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day17.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
