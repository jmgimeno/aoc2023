package aoc2023.day16;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class Day16 {

    enum Direction {
        UP, LEFT, DOWN, RiGHT

    }

    record Split(Beam first, Beam second) {
    }

    record Position(int x, int y) {
    }

    record Beam(int x, int y, Direction heading) {

        Beam ahead() {
            return switch (heading) {
                case UP -> new Beam(x, y - 1, heading);
                case LEFT -> new Beam(x - 1, y, heading);
                case DOWN -> new Beam(x, y + 1, heading);
                case RiGHT -> new Beam(x + 1, y, heading);
            };
        }

        Beam slash() { // /
            return switch (heading) {
                case UP -> new Beam(x, y - 1, Direction.RiGHT);
                case LEFT -> new Beam(x - 1, y, Direction.DOWN);
                case DOWN -> new Beam(x, y + 1, Direction.LEFT);
                case RiGHT -> new Beam(x + 1, y, Direction.UP);
            };
        }

        Beam backslash() {
            return switch (heading) {
                case UP -> new Beam(x, y - 1, Direction.LEFT);
                case LEFT -> new Beam(x - 1, y, Direction.UP);
                case DOWN -> new Beam(x, y + 1, Direction.RiGHT);
                case RiGHT -> new Beam(x + 1, y, Direction.DOWN);
            };
        }

        Split split() {
            return switch (heading) {
                case UP ->
                        new Split(new Beam(x, y - 1, Direction.LEFT), new Beam(x, y - 1, Direction.RiGHT));
                case LEFT ->
                        new Split(new Beam(x - 1, y, Direction.UP), new Beam(x - 1, y, Direction.DOWN));
                case DOWN ->
                        new Split(new Beam(x, y + 1, Direction.LEFT), new Beam(x, y + 1, Direction.RiGHT));
                case RiGHT ->
                        new Split(new Beam(x + 1, y, Direction.UP), new Beam(x + 1, y, Direction.DOWN));
            };
        }

        Position next() {
            return switch (heading) {
                case UP -> new Position(x, y - 1);
                case LEFT -> new Position(x - 1, y);
                case DOWN -> new Position(x, y + 1);
                case RiGHT -> new Position(x + 1, y);
            };
        }

    }

    static class Layout extends CharGrid {

        Layout(List<String> data) {
            super(data, true, '$');
        }

        int part1() {
            var start = new Beam(0, 1, Direction.RiGHT);
            return energiseFrom(start);
        }

        private int energiseFrom(Beam start) {
            var beams = new ArrayDeque<Beam>();
            beams.addLast(start);
            var visited = new HashSet<Beam>();
            while (!beams.isEmpty()) {
                var beam = beams.removeFirst();
                if (!visited.add(beam)) continue;
                var next = beam.next();
                var on = points[next.y()][next.x()];
                switch (on) {
                    case '$' -> {
                    }
                    case '.' -> beams.addLast(beam.ahead());
                    case '/' -> beams.addLast(beam.slash());
                    case '\\' -> beams.addLast(beam.backslash());
                    case '|' -> {
                        if (beam.heading() == Direction.LEFT || beam.heading() == Direction.RiGHT) {
                            var split = beam.split();
                            beams.addLast(split.first());
                            beams.addLast(split.second());
                        } else {
                            beams.addLast(beam.ahead());
                        }
                    }
                    case '-' -> {
                        if (beam.heading() == Direction.UP || beam.heading() == Direction.DOWN) {
                            var split = beam.split();
                            beams.addLast(split.first());
                            beams.addLast(split.second());
                        } else {
                            beams.addLast(beam.ahead());
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + on);
                }
            }
            return energised(visited);
        }

        private int energised(HashSet<Beam> visited) {
            var result = new HashSet<Position>();
            for (var beam : visited) {
                if (points[beam.y()][beam.x()] != '$')
                    result.add(new Position(beam.x(), beam.y()));
            }
            return result.size();
        }

        int part2() {
            var top = IntStream.rangeClosed(1, width)
                    .map(x -> energiseFrom(new Beam(x, 0, Direction.DOWN))).max().orElseThrow();
            var right = IntStream.rangeClosed(1, height)
                    .map(y -> energiseFrom(new Beam(width, y, Direction.LEFT))).max().orElseThrow();
            var bottom = IntStream.rangeClosed(1, width)
                    .map(x -> energiseFrom(new Beam(x, height, Direction.UP))).max().orElseThrow();
            var left = IntStream.rangeClosed(1, height)
                    .map(y -> energiseFrom(new Beam(0, y, Direction.RiGHT))).max().orElseThrow();
            return Collections.max(List.of(top, right, bottom, left));
        }
    }

    int part1(List<String> data) {
        var layout = new Layout(data);
        return layout.part1();
    }

    int part2(List<String> data) {
        var layout = new Layout(data);
        return layout.part2();
    }

    public static void main(String[] args) {
        var day16 = new Day16();
        var data = IO.getResourceAsList("day16.txt");
        var part1 = day16.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day16.part2(data);
        System.out.println("part2 = " + part2);
    }
}

