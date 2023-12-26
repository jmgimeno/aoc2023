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

        String key(Set<Bobby> bobbies) {
            var positions = bobbies.stream().map(b -> b.position).collect(Collectors.toSet());
            var builder = new StringBuilder();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    var p = new Position(x, y);
                    if (positions.contains(p)) {
                        builder.append('b');
                    } else {
                        builder.append('.');
                    }
                }
            }
            return builder.toString();
        }

        record Photo(int steps, java.util.Map<Position, Integer> counters) {
            Photo(int steps, Set<Bobby> bobbies) {
                this(steps, bobbies.stream()
                        .collect(Collectors.toMap(
                                b -> b.position,
                                b -> b.quadrants.size())));
            }
        }


        int walk(int steps) {
            var bob = new Bobby(start, Set.of(new Quadrant(0, 0)));
            var bobbies = Set.of(bob);
            var photos = new HashMap<String, Photo>();
            for (int stepsTaken = 0; stepsTaken < steps; stepsTaken++) {
                var key = key(bobbies);
                if (photos.containsKey(key)) {
                    var old = photos.get(key);
                    System.out.println("loop find at = " + stepsTaken + " with previous at  " + old.steps);
                    System.out.println("old counters = " + old.counters);
                    System.out.println("old sum counters = " + old.counters.values().stream().mapToInt(i -> i).sum());
                    System.out.println("new counters = " + new Photo(stepsTaken, bobbies).counters);
                    System.out.println("new sum counters = " + new Photo(stepsTaken, bobbies).counters.values().stream().mapToInt(i -> i).sum());
                    System.out.println("-----------------------");
                }
                photos.put(key(bobbies), new Photo(stepsTaken, bobbies));
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
            return bobbies.stream().mapToInt(b -> b.quadrants.size()).sum();
        }
    }

    int part2(List<String> data, int numSteps) {
        var map = new InfiniteMap(data);
        return map.walk(numSteps);
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
