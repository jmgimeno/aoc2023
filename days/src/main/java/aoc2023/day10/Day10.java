package aoc2023.day10;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.*;
import java.util.function.Predicate;

public class Day10 {

    /*
        --- Day 10: Pipe Maze ---
    You use the hang glider to ride the hot air from Desert Island all the way up to the floating
    metal island. This island is surprisingly cold and there definitely aren't any thermals to glide
    on, so you leave your hang glider behind.

    You wander around for a while, but you don't find any people or animals. However, you do
    occasionally find signposts labeled "Hot Springs" pointing in a seemingly consistent direction;
    maybe you can find someone at the hot springs and ask them where the desert-machine parts are
    made.

    The landscape here is alien; even the flowers and trees are made of metal. As you stop to admire
    some metal grass, you notice something metallic scurry away in your peripheral vision and jump
    into a big pipe! It didn't look like any animal you've ever seen; if you want a better look,
    you'll need to get ahead of it.

    Scanning the area, you discover that the entire field you're standing on is densely packed with
    pipes; it was hard to tell at first because they're the same metallic silver color as the
    "ground". You make a quick sketch of all of the surface pipes you can see (your puzzle input).

    The pipes are arranged in a two-dimensional grid of tiles:

    | is a vertical pipe connecting north and south. - is a horizontal pipe connecting east and
    west. L is a 90-degree bend connecting north and east. J is a 90-degree bend connecting north
    and west. 7 is a 90-degree bend connecting south and west. F is a 90-degree bend connecting
    south and east. . is ground; there is no pipe in this tile. S is the starting position of the
    animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
    Based on the acoustics of the animal's scurrying, you're confident the pipe that contains the
    animal is one large, continuous loop.

    For example, here is a square loop of pipe:

    .....
    .F-7.
    .|.|.
    .L-J.
    .....
    If the animal had entered this loop in the northwest corner, the sketch would instead look like
    this:

    .....
    .S-7.
    .|.|.
    .L-J.
    .....
    In the above diagram, the S tile is still a 90-degree F bend: you can tell because of how the
    adjacent pipes connect to it.

    Unfortunately, there are also many pipes that aren't connected to the loop! This sketch shows
    the same loop as above:

    -L|F7
    7S-7|
    L|7||
    -L-J|
    L|-JF
    In the above diagram, you can still figure out which pipes form the main loop: they're the ones
    connected to S, pipes those pipes connect to, pipes those pipes connect to, and so on. Every
    pipe in the main loop connects to its two neighbors (including S, which will have exactly two
    pipes connecting to it, and which is assumed to connect back to those two pipes).

    Here is a sketch that contains a slightly more complex main loop:

    ..F7.
    .FJ|.
    SJ.L7
    |F--J
    LJ...
    Here's the same example sketch with the extra, non-main-loop pipe tiles also shown:

    7-F7-
    .FJ|7
    SJLL7
    |F--J
    LJ.LJ
    If you want to get out ahead of the animal, you should find the tile in the loop that is
    farthest from the starting position. Because the animal is in the pipe, it doesn't make sense to
    measure this by direct distance. Instead, you need to find the tile that would take the longest
    number of steps along the loop to reach from the starting point - regardless of which way around
    the loop the animal went.

    In the first example with the square loop:

    .....
    .S-7.
    .|.|.
    .L-J.
    .....
    You can count the distance each tile in the loop is from the starting point like this:

    .....
    .012.
    .1.3.
    .234.
    .....
    In this example, the farthest point from the start is 4 steps away.

    Here's the more complex loop again:

    ..F7.
    .FJ|.
    SJ.L7
    |F--J
    LJ...
    Here are the distances for each tile on that loop:

    ..45.
    .236.
    01.78
    14567
    23...
    Find the single giant loop starting at S. How many steps along the loop does it take to get from
    the starting position to the point farthest from the starting position?

    Your puzzle answer was 6951.
    */

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

    static class PipeTyper {

        static boolean isNS(char c) {
            return c == '|';
        }

        static boolean isEW(char c) {
            return c == '-';
        }

        static boolean isNE(char c) {
            return c == 'L';
        }

        static boolean isNW(char c) {
            return c == 'J';
        }

        static boolean isSW(char c) {
            return c == '7';
        }

        static boolean isSE(char c) {
            return c == 'F';
        }

        static boolean isGround(char c) {
            return c == '.';
        }

        static boolean isStart(char c) {
            return c == 'S';
        }

        static boolean canGoNorth(char c) {
            return isSW(c) || isNS(c) || isSE(c);
        }

        static boolean canGoEast(char c) {
            return isNW(c) || isEW(c) || isSW(c);
        }

        static boolean canGoSouth(char c) {
            return isNW(c) || isNS(c) || isNE(c);
        }

        static boolean canGoWest(char c) {
            return isNE(c) || isEW(c) || isSE(c);
        }
    }

    static class PipeGrid extends CharGrid {

        private final Position start;

        public PipeGrid(List<String> data) {
            super(data);
            start = findStart();
        }

        private char at(Position position) {
            return points[position.y()][position.x()];
        }

        private Position findStart() {
            for (int y = 0; y < height + 2; y++) {
                for (int x = 0; x < width + 2; x++) {
                    if (PipeTyper.isStart(points[y][x])) {
                        return new Position(x, y);
                    }
                }
            }
            throw new IllegalStateException("No start found");
        }

        private int bfs(Position start) {
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
            return Collections.max(distance.values());
        }

        private void addIfValid(Collection<Position> positions, Predicate<Position> isValid, Position current, Position next) {
            if (isValid.test(next)) {
                positions.add(next);
            }
        }
        private List<Position> validNextPositions(Position position) {
            var current = at(position);
            var nextPositions = new ArrayList<Position>();
            if (PipeTyper.isStart(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoNorth(at(p)), position, position.north());
                addIfValid(nextPositions, p -> PipeTyper.canGoEast(at(p)), position, position.east());
                addIfValid(nextPositions, p -> PipeTyper.canGoSouth(at(p)), position, position.south());
                addIfValid(nextPositions, p -> PipeTyper.canGoWest(at(p)), position, position.west());
            } else if (PipeTyper.isNE(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoNorth(at(p)), position, position.north());
                addIfValid(nextPositions, p -> PipeTyper.canGoEast(at(p)), position, position.east());
            } else if (PipeTyper.isNW(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoNorth(at(p)), position, position.north());
                addIfValid(nextPositions, p -> PipeTyper.canGoWest(at(p)), position, position.west());
            } else if (PipeTyper.isSE(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoEast(at(p)), position, position.east());
                addIfValid(nextPositions, p -> PipeTyper.canGoSouth(at(p)), position, position.south());
            } else if (PipeTyper.isSW(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoSouth(at(p)), position, position.south());
                addIfValid(nextPositions, p -> PipeTyper.canGoWest(at(p)), position, position.west());
            } else if (PipeTyper.isNS(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoNorth(at(p)), position, position.north());
                addIfValid(nextPositions, p -> PipeTyper.canGoSouth(at(p)), position, position.south());
            } else if (PipeTyper.isEW(current)) {
                addIfValid(nextPositions, p -> PipeTyper.canGoEast(at(p)), position, position.east());
                addIfValid(nextPositions, p -> PipeTyper.canGoWest(at(p)), position, position.west());
            }
            return nextPositions;
        }
    }

    int part1(List<String> data) {
        var pipeGrid = new PipeGrid(data);
        return pipeGrid.bfs(pipeGrid.start);
    }

    /*

     */

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day10 = new Day10();
        var data = IO.getResourceAsList("day10.txt");
        var part1 = day10.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day10.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
