package aoc2023.day10;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Day10Alt {

    record Position(int x, int y) {

        Position north() {
            return new Position(x, y - 1);
        }

        Position south() {
            return new Position(x, y + 1);
        }

        Position east() {
            return new Position(x + 1, y);
        }

        Position west() {
            return new Position(x - 1, y);
        }

        int product(Position o) {
            return x * o.y - y * o.x;
        }
    }

    static class PipeGrid extends CharGrid {

        private final Position start;

        public PipeGrid(List<String> data) {
            super(data);
            start = findStart();
        }

        boolean canGoNorth(Position p) {
            return "7|F".indexOf(at(p)) != -1;
        }

        private char at(Position p) {
            return at(p.x(), p.y());
        }

        private char at(int x, int y) {
            return points[y][x];
        }

        boolean canGoEast(Position p) {
            return "J-7".indexOf(at(p)) != -1;
        }

        boolean canGoSouth(Position p) {
            return "J|L".indexOf(at(p)) != -1;
        }

        boolean canGoWest(Position p) {
            return "L-F".indexOf(at(p)) != -1;
        }

        private Position findStart() {
            for (int y = 0; y < height + 2; y++) {
                for (int x = 0; x < width + 2; x++) {
                    if (at(x, y) == 'S') {
                        return new Position(x, y);
                    }
                }
            }
            throw new IllegalStateException("No start found");
        }


        record Loop(List<Position> positions) {
            int length() {
                return positions.size() / 2;
            }

            int area() {
                // Shoelace formula
                var doubleArea = IntStream.range(0, positions.size() - 1)
                        .map(i -> positions.get(i).product(positions.get(i + 1)))
                        .sum()
                        + positions.getLast().product(positions.getFirst());
                return Math.abs(doubleArea / 2);
            }

            int inner() {
                // Pick's theorem
                return area() - length() + 1;
            }
        }

        private Loop traverseLoop(Position start) {
            // We don't need much logic here because we have the security
            // that a loop exists, and we have not to branch to find it.
            var path = new ArrayList<Position>();
            path.add(start);
            while (true) {
                var current = path.getLast();
                var nextCandidates = validNextPositions(current);
                nextCandidates.removeIf(p -> path.size() >= 2 && p.equals(path.get(path.size() - 2)));
                if (nextCandidates.isEmpty()) {
                    return new Loop(path);
                } else {
                    path.add(nextCandidates.getFirst());
                }
            }
        }

        private void addIfValid(Collection<Position> positions, Predicate<Position> isValid,
                                Position next) {
            if (isValid.test(next)) {
                positions.add(next);
            }
        }

        private List<Position> validNextPositions(Position position) {
            var nextPositions = new ArrayList<Position>();
            switch (at(position)) {
                case 'S' -> {
                    addIfValid(nextPositions, this::canGoNorth, position.north());
                    addIfValid(nextPositions, this::canGoEast, position.east());
                    addIfValid(nextPositions, this::canGoSouth, position.south());
                    addIfValid(nextPositions, this::canGoWest, position.west());
                }
                case 'L' -> {
                    addIfValid(nextPositions, this::canGoNorth, position.north());
                    addIfValid(nextPositions, this::canGoEast, position.east());
                }
                case 'J' -> {
                    addIfValid(nextPositions, this::canGoNorth, position.north());
                    addIfValid(nextPositions, this::canGoWest, position.west());
                }
                case 'F' -> {
                    addIfValid(nextPositions, this::canGoEast, position.east());
                    addIfValid(nextPositions, this::canGoSouth, position.south());
                }
                case '7' -> {
                    addIfValid(nextPositions, this::canGoSouth, position.south());
                    addIfValid(nextPositions, this::canGoWest, position.west());
                }
                case '|' -> {
                    addIfValid(nextPositions, this::canGoNorth, position.north());
                    addIfValid(nextPositions, this::canGoSouth, position.south());
                }
                case '-' -> {
                    addIfValid(nextPositions, this::canGoEast, position.east());
                    addIfValid(nextPositions, this::canGoWest, position.west());
                }
            }
            return nextPositions;
        }
    }

    int part1(List<String> data) {
        var pipeGrid = new PipeGrid(data);
        return pipeGrid.traverseLoop(pipeGrid.start).length();
    }

    int part2(List<String> data) {
        var pipeGrid = new PipeGrid(data);
        var loop = pipeGrid.traverseLoop(pipeGrid.start);
        return loop.inner();
    }

    public static void main(String[] args) {
        var day10 = new Day10Alt();
        var data = IO.getResourceAsList("day10.txt");
        var part1 = day10.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day10.part2(data);
        System.out.println("part2 = " + part2);
    }
}
