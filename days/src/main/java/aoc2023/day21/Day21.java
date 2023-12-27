package aoc2023.day21;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 {

    record Direction(int dx, int dy) {

        static final Direction[] directions = {
                new Direction(0, -1),
                new Direction(0, 1),
                new Direction(-1, 0),
                new Direction(1, 0),
        };

        Position apply(Position p) {
            return new Position(p.x + dx, p.y + dy);
        }

    }

    record Position(int x, int y) {
        List<Position> candidates(Position p) {
            return Arrays.stream(Direction.directions)
                    .map(d -> d.apply(p))
                    .toList();
        }
    }

    static class Map extends CharGrid {

        final Position start;

        Map(List<String> data) {
            // The input is already embedded in dots
            super(data, false);
            start = find('S');
        }

        private Position find(char s) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (points[y][x] == s) {
                        return new Position(x, y);
                    }
                }
            }
            throw new IllegalArgumentException("Not found: " + s);
        }

        protected boolean isAllowed(Position p) {
            return inBounds(p) && points[p.y][p.x] == '.' || points[p.y][p.x] == 'S';
        }

        protected boolean inBounds(Position p) {
            return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
        }

        int walk(int steps) {
            var points = Set.of(start);
            for (int stepsTaken = 0; stepsTaken < steps; stepsTaken++) {
                points = points.stream()
                        .flatMap(p1 -> p1.candidates(p1).stream())
                        .filter(this::isAllowed)
                        .collect(Collectors.toSet());
            }
            return points.size();
        }
    }

    int part1(List<String> data, int numSteps) {
        var map = new Map(data);
        return map.walk(numSteps);
    }

    static class InfiniteMap extends Map {

        record Quadrant(int x, int y) {
            Quadrant move(int dx, int dy) {
                return new Quadrant(x + dx, y + dy);
            }
        }

        class Bobby {
            Position position;
            Set<Quadrant> quadrants;

            Bobby(Position position, Set<Quadrant> quadrants) {
                this.position = position;
                this.quadrants = quadrants;
            }

            @Override
            public String toString() {
                return "Bobby{" +
                        "position=" + position +
                        ", quadrants=" + quadrants +
                        '}';
            }

            Optional<Bobby> move(Direction direction) {
                var newPosition = direction.apply(position);
                if (inBounds(newPosition)) {
                    if (isAllowed(newPosition)) {
                        return Optional.of(new Bobby(newPosition, quadrants));
                    } else {
                        return Optional.empty();
                    }
                } else {
                    var normalized = normalize(newPosition);
                    if (!isAllowed(normalized)) {
                        return Optional.empty();
                    }
                    int dx = getDifference(newPosition.x, normalized.x);
                    int dy = getDifference(newPosition.y, normalized.y);
                    var newQuadrants = quadrants.stream()
                            .map(q -> q.move(dx, dy))
                            .collect(Collectors.toSet());
                    return Optional.of(new Bobby(normalized, newQuadrants));
                }
            }

            private static int getDifference(int newPosition, int normalized) {
                int diff = 0;
                if (newPosition < normalized) {
                    diff = -1;
                } else if (newPosition > normalized) {
                    diff = 1;
                }
                return diff;
            }
        }

        InfiniteMap(List<String> data) {
            super(data);
        }

        protected Position normalize(Position p) {
            var x = p.x % width;
            x = x < 0 ? x + width : x;
            var y = p.y % height;
            y = y < 0 ? y + height : y;
            return new Position(x, y);
        }

        @Override
        protected boolean isAllowed(Position p) {
            return super.isAllowed(normalize(p));
        }

        // Thanks for the reddit forum for this hint (I tried to analyze the plot of the first 500 steps,
        // but I couldn't find a pattern).
        // The idea workd because of the special structure of the map:
        // - The map is a square
        // - S is in the middle of the map
        // - The row and column of S are free to go
        // - So, at time 65 the bobby reaches the limits of the map
        // - Traversing the limit of  the next map needs 131 steps
        // - Each "expansion" of the map generates a quadratic number of new positions
        // - The number of steps that we are asked for are 26501365 = 131 * 202300 + 65
        long walkQuadratic(int steps) {
            var bob = new Bobby(start, Set.of(new Quadrant(0, 0)));
            var bobbies = Set.of(bob);
            var counters = new ArrayList<Integer>();
            for (int stepsTaken = 0; stepsTaken < steps; stepsTaken++) {
                if (stepsTaken % 131 == 65)
                    counters.add(count(bobbies));
                if (counters.size() == 3)
                    break;
                var newPositionsAndQuadrants = bobbies.stream()
                        .flatMap(b -> Arrays.stream(Direction.directions)
                                .map(b::move)
                                .filter(Optional::isPresent)
                                .map(Optional::get))
                        .collect(Collectors.groupingBy(
                                b -> b.position,
                                Collectors.reducing(
                                        Set.<Quadrant>of(),
                                        b -> b.quadrants,
                                        (s1, s2) -> {
                                            Set<Quadrant> set = new HashSet<>();
                                            set.addAll(s1);
                                            set.addAll(s2);
                                            return set;
                                        })));
                bobbies = newPositionsAndQuadrants.entrySet().stream()
                        .map(e -> new Bobby(e.getKey(), e.getValue()))
                        .collect(Collectors.toSet());
            }
            var quadratic = Quadratic.solve(counters.get(0), counters.get(1), counters.get(2));
            return quadratic.apply(steps / 131);
        }

        private static int count(Set<Bobby> bobbies) {
            return bobbies.stream().mapToInt(b -> b.quadrants.size()).sum();
        }
    }

    record Quadratic(long a, long b, long c) {
        static Quadratic solve(long p0, long p1, long p2) {
            long a = (p2 - 2 * p1 + p0) / 2;
            long b = p1 -p0 - a;
            long c = p0;
            return new Quadratic(a, b, c);
        }

        long apply(long x) {
            return a * x * x + b * x + c;
        }
    }

    long part2(List<String> data, int numSteps) {
        var map = new InfiniteMap(data);
        return map.walkQuadratic(numSteps);
    }

    public static void main(String[] args) {
        var day21 = new Day21();
        var data = IO.getResourceAsList("day21.txt");
        var part1 = day21.part1(data, 64);
        System.out.println("part1 = " + part1);
        var part2 = day21.part2(data, 26501365);
        System.out.println("part2 = " + part2);
    }
}
