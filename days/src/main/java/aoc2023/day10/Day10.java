package aoc2023.day10;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.*;
import java.util.function.Predicate;

public class Day10 {

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

        private boolean isInside(int x, int y, Loop loop) {
            // As I don't know how to deal with the start position, as I only need to count in
            // one direction, I count
            // tp the east but, if I find the start in this direction, I use the counter going to
            // the west (which will
            // not have the start node)
            return insideToTheEast(x, y, loop)
                    .orElseGet(() -> insideToTheWest(x, y, loop));
        }

        public Optional<Boolean> insideToTheEast(int x, int y, Loop loop) {
            var crossings = 0;
            var fromNorth = false;
            var fromSouth = false;
            for (int xx = x + 1; xx < width + 1; xx++) {
                // I only need to count the crossings with tiles in the loop
                if (!loop.contains(xx, y)) continue;
                switch (at(xx, y)) {
                    case 'S' -> {
                        // I do not know how to deal with it, so I fail
                        return Optional.empty();
                    }
                    case '|' -> {
                        // If it is a | is a crossing
                        assert !fromNorth && !fromSouth;
                        crossings++;
                    }
                    case 'L' -> {
                        // If it is an J it will be a crossing depending on how it ends
                        assert !fromNorth && !fromSouth;
                        fromNorth = true;
                    }
                    case 'F' -> {
                        // If it is an F it will be a crossing depending on how it ends
                        assert !fromNorth && !fromSouth;
                        fromSouth = true;
                    }
                    case 'J' -> {
                        // If it is a J is only a crossing if, on this line, we have found an F
                        // (both are equivalent
                        // to a | as if we pull them from the north and south extremes)
                        // If we come from an L this is not a crossing (we pull both ends from
                        // the north)
                        // In either case we clear the flags
                        assert fromNorth || fromSouth;
                        if (fromSouth) crossings++;
                        fromNorth = fromSouth = false;
                    }
                    case '7' -> {
                        // If it is a 7 is only a crossing if, on this line, we have found an L
                        // (both are equivalent
                        // to a | as if we pull them from the north and south extremes)
                        // If we come from an F this is not a crossing (we pull both ends from
                        // the south)
                        // In either case we clear the flags;
                        assert fromNorth || fromSouth;
                        if (fromNorth) crossings++;
                        fromNorth = fromSouth = false;
                    }
                }
            }
            return Optional.of(crossings % 2 == 1);
        }

        public boolean insideToTheWest(int x, int y, Loop loop) {
            var crossings = 0;
            var fromNorth = false;
            var fromSouth = false;
            for (int xx = x - 1; xx >= 0; xx--) {
                // I only need to count the crossings with tiles in the loop
                if (!loop.contains(xx, y)) continue;
                switch (at(xx, y)) {
                    case 'S' ->
                        // Cannot happen !!
                            throw new IllegalStateException("start should not be on this path");
                    case '|' ->
                        // If it is a | is a crossing
                            crossings++;
                    case 'J' ->
                        // If it is an J it will be a crossing depending on how it ends
                            fromNorth = true;
                    case '7' ->
                        // If it is an 7 it will be a crossing depending on how it ends
                            fromSouth = true;
                    case 'F' -> {
                        // If it is an F is only a crossing if, on this line, we have found a J
                        // (both
                        // are equivalent
                        // to a | as if we pull them from the north and south extremes)
                        // If we come from a 7 this is not a crossing (we pull both ends from the
                        // south)
                        // In either case we clear the flags
                        if (fromNorth) crossings++;
                        fromNorth = fromSouth = false;
                    }
                    case 'L' -> {
                        // If it is an L is only a crossing if, on this line, we have found a 7
                        // (both
                        // are equivalent
                        // to a | as if we pull them from the north and south extremes)
                        // If we come from an J this is not a crossing (we pull both ends from the
                        // north)
                        // In either case we clear the flags;
                        if (fromSouth) crossings++;
                        fromNorth = fromSouth = false;
                    }
                }
            }
            return crossings % 2 == 1;
        }

        public int countInsideNodes(Loop loop) {
            var inside = 0;
            for (int y = 1; y < height + 1; y++) {
                for (int x = 1; x < width + 1; x++) {
                    if (!loop.contains(x, y) && isInside(x, y, loop)) {
                        inside++;
                    }
                }
            }
            return inside;
        }

        record Loop(Set<Position> positions) {

            boolean contains(Position position) {
                return positions.contains(position);
            }

            boolean contains(int x, int y) {
                return contains(new Position(x, y));
            }

            int length() {
                return positions.size() / 2;
            }
        }

        private Loop bfs(Position start) {
            Queue<Position> queue = new LinkedList<>();
            Map<Position, Integer> distance = new HashMap<>();

            queue.add(start);
            distance.put(start, 0);

            while (!queue.isEmpty()) {
                Position position = queue.poll();
                int dist = distance.get(position);

                for (var nextPos : validNextPositions(position)) {
                    if (!distance.containsKey(nextPos)) {
                        distance.put(nextPos, dist + 1);
                        queue.add(nextPos);
                    }
                }
            }
            return new Loop(distance.keySet());
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
        return pipeGrid.bfs(pipeGrid.start).length();
    }

    int part2(List<String> data) {
        var pipeGrid = new PipeGrid(data);
        var loop = pipeGrid.bfs(pipeGrid.start);
        return pipeGrid.countInsideNodes(loop);
    }

    public static void main(String[] args) {
        var day10 = new Day10();
        var data = IO.getResourceAsList("day10.txt");
        var part1 = day10.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day10.part2(data);
        System.out.println("part2 = " + part2);
    }
}
