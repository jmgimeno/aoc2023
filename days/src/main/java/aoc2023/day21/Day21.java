package aoc2023.day21;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 {

    /*
        --- Day 21: Step Counter ---
    You manage to catch the airship right as it's dropping someone else off on their
    all-expenses-paid trip to Desert Island! It even helpfully drops you off near the gardener and
    his massive farm.

    "You got the sand flowing again! Great work! Now we just need to wait until we have enough sand
    to filter the water for Snow Island and we'll have snow again in no time."

    While you wait, one of the Elves that works with the gardener heard how good you are at solving
    problems and would like your help. He needs to get his steps in for the day, and so he'd like to
    know which garden plots he can reach with exactly his remaining 64 steps.

    He gives you an up-to-date map (your puzzle input) of his starting position (S), garden plots
    (.), and rocks (#). For example:

    ...........
    .....###.#.
    .###.##..#.
    ..#.#...#..
    ....#.#....
    .##..S####.
    .##..#...#.
    .......##..
    .##.#.####.
    .##..##.##.
    ...........
    The Elf starts at the starting position (S) which also counts as a garden plot. Then, he can
    take one step north, south, east, or west, but only onto tiles that are garden plots. This would
    allow him to reach any of the tiles marked O:

    ...........
    .....###.#.
    .###.##..#.
    ..#.#...#..
    ....#O#....
    .##.OS####.
    .##..#...#.
    .......##..
    .##.#.####.
    .##..##.##.
    ...........
    Then, he takes a second step. Since at this point he could be at either tile marked O, his
    second step would allow him to reach any garden plot that is one step north, south, east, or
    west of any tile that he could have reached after the first step:

    ...........
    .....###.#.
    .###.##..#.
    ..#.#O..#..
    ....#.#....
    .##O.O####.
    .##.O#...#.
    .......##..
    .##.#.####.
    .##..##.##.
    ...........
    After two steps, he could be at any of the tiles marked O above, including the starting position
    (either by going north-then-south or by going west-then-east).

    A single third step leads to even more possibilities:

    ...........
    .....###.#.
    .###.##..#.
    ..#.#.O.#..
    ...O#O#....
    .##.OS####.
    .##O.#...#.
    ....O..##..
    .##.#.####.
    .##..##.##.
    ...........
    He will continue like this until his steps for the day have been exhausted. After a total of 6
    steps, he could reach any of the garden plots marked O:

    ...........
    .....###.#.
    .###.##.O#.
    .O#O#O.O#..
    O.O.#.#.O..
    .##O.O####.
    .##.O#O..#.
    .O.O.O.##..
    .##.#.####.
    .##O.##.##.
    ...........
    In this example, if the Elf's goal was to get exactly 6 more steps today, he could use them to
    reach any of 16 garden plots.

    However, the Elf actually needs to get 64 steps today, and the map he's handed you is much
    larger than the example map.

    Starting from the garden plot marked S on your map, how many garden plots could the Elf reach in
    exactly 64 steps?

    Your puzzle answer was 3682.
    */

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

        private boolean isAllowed(Position p) {
            return inBounds(p) && points[p.y][p.x] == '.'  || points[p.y][p.x] == 'S';
        }

        private boolean inBounds(Position p) {
            return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
        }

        private int walk(int steps) {
            var points = Set.of(start);
            var stepsTaken = 0;
            while (stepsTaken < steps) {
                var nextPoints = points.stream()
                        .flatMap(p1 -> p1.candidates(p1).stream())
                        .filter(this::isAllowed)
                        .collect(Collectors.toSet());
                if (nextPoints.isEmpty()) {
                    break;
                }
                points = nextPoints;
                stepsTaken++;
            }
            return points.size();
        }
    }

    int part1(List<String> data, int numSteps) {
        var map = new Map(data);
        return map.walk(numSteps);
    }

    /*
    --- Part Two ---
    The Elf seems confused by your answer until he realizes his mistake: he was reading from a list of his favorite numbers that are both
    perfect squares and perfect cubes, not his step counter.

    The actual number of steps he needs to get today is exactly 26501365.

    He also points out that the garden plots and rocks are set up so that the map repeats infinitely in every direction.

    So, if you were to look one additional map-width or map-height out from the edge of the example map above, you would find that i
    t keeps repeating:

    .................................
    .....###.#......###.#......###.#.
    .###.##..#..###.##..#..###.##..#.
    ..#.#...#....#.#...#....#.#...#..
    ....#.#........#.#........#.#....
    .##...####..##...####..##...####.
    .##..#...#..##..#...#..##..#...#.
    .......##.........##.........##..
    .##.#.####..##.#.####..##.#.####.
    .##..##.##..##..##.##..##..##.##.
    .................................
    .................................
    .....###.#......###.#......###.#.
    .###.##..#..###.##..#..###.##..#.
    ..#.#...#....#.#...#....#.#...#..
    ....#.#........#.#........#.#....
    .##...####..##..S####..##...####.
    .##..#...#..##..#...#..##..#...#.
    .......##.........##.........##..
    .##.#.####..##.#.####..##.#.####.
    .##..##.##..##..##.##..##..##.##.
    .................................
    .................................
    .....###.#......###.#......###.#.
    .###.##..#..###.##..#..###.##..#.
    ..#.#...#....#.#...#....#.#...#..
    ....#.#........#.#........#.#....
    .##...####..##...####..##...####.
    .##..#...#..##..#...#..##..#...#.
    .......##.........##.........##..
    .##.#.####..##.#.####..##.#.####.
    .##..##.##..##..##.##..##..##.##.
    .................................
    This is just a tiny three-map-by-three-map slice of the inexplicably-infinite farm layout; garden plots and rocks repeat as far
    as you can see. The Elf still starts on the one middle tile marked S, though - every other repeated S is replaced with a normal
    garden plot (.).

    Here are the number of reachable garden plots in this new infinite version of the example map for different numbers of steps:

    In exactly 6 steps, he can still reach 16 garden plots.
    In exactly 10 steps, he can reach any of 50 garden plots.
    In exactly 50 steps, he can reach 1594 garden plots.
    In exactly 100 steps, he can reach 6536 garden plots.
    In exactly 500 steps, he can reach 167004 garden plots.
    In exactly 1000 steps, he can reach 668697 garden plots.
    In exactly 5000 steps, he can reach 16733044 garden plots.

    However, the step count the Elf needs is much larger! Starting from the garden plot marked S on your infinite map, how many
    garden plots could the Elf reach in exactly 26501365 steps?
     */

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day21 = new Day21();
        var data = IO.getResourceAsList("day21.txt");
        var part1 = day21.part1(data, 64);
        System.out.println("part1 = " + part1);
//        var part2 = day21.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
