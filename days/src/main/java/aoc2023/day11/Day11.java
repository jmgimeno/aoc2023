package aoc2023.day11;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day11 {

    /*
        --- Day 11: Cosmic Expansion ---
    You continue following signs for "Hot Springs" and eventually come across an observatory. The
    Elf within turns out to be a researcher studying cosmic expansion using the giant telescope
    here.

    He doesn't know anything about the missing machine parts; he's only visiting for this research
    project. However, he confirms that the hot springs are the next-closest area likely to have
    people; he'll even take you straight there once he's done with today's observation analysis.

    Maybe you can help him with the analysis to speed things up?

    The researcher has collected a bunch of data and compiled the data into a single giant image
    (your puzzle input). The image includes empty space (.) and galaxies (#). For example:

    ...#......
    .......#..
    #.........
    ..........
    ......#...
    .#........
    .........#
    ..........
    .......#..
    #...#.....
    The researcher is trying to figure out the sum of the lengths of the shortest path between every
    pair of galaxies. However, there's a catch: the universe expanded in the time it took the light
    from those galaxies to reach the observatory.

    Due to something involving gravitational effects, only some space expands. In fact, the result
    is that any rows or columns that contain no galaxies should all actually be twice as big.

    In the above example, three columns and two rows contain no galaxies:

    v v v
    ...#......
    .......#..
    #.........
    >..........<
    ......#...
    .#........
    .........#
    >..........<
    .......#..
    #...#.....
    ^ ^ ^
    These rows and columns need to be twice as big; the result of cosmic expansion therefore looks
    like this:

    ....#........
    .........#...
    #............
    .............
    .............
    ........#....
    .#...........
    ............#
    .............
    .............
    .........#...
    #....#.......
    Equipped with this expanded universe, the shortest path between every pair of galaxies can be
    found. It can help to assign every galaxy a unique number:

    ....1........
    .........2...
    3............
    .............
    .............
    ........4....
    .5...........
    ............6
    .............
    .............
    .........7...
    8....9.......
    In these 9 galaxies, there are 36 pairs. Only count each pair once; order within the pair
    doesn't matter. For each pair, find any shortest path between the two galaxies using only steps
    that move up, down, left, or right exactly one . or # at a time. (The shortest path between two
    galaxies is allowed to pass through another galaxy.)

    For example, here is one of the shortest paths between galaxies 5 and 9:

    ....1........
    .........2...
    3............
    .............
    .............
    ........4....
    .5...........
    .##.........6
    ..##.........
    ...##........
    ....##...7...
    8....9.......
    This path has length 9 because it takes a minimum of nine steps to get from galaxy 5 to galaxy 9
    (the eight locations marked # plus the step onto galaxy 9 itself). Here are some other example
    shortest path lengths:

    Between galaxy 1 and galaxy 7: 15 Between galaxy 3 and galaxy 6: 17 Between galaxy 8 and galaxy
    9: 5
    In this example, after expanding the universe, the sum of the shortest path between all 36 pairs
    of galaxies is 374.

    Expand the universe, then find the length of the shortest path between every pair of galaxies.
    What is the sum of these lengths?

    Your puzzle answer was 9177603.
    */

    record Galaxy(int x, int y) {
        int distance(Galaxy other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }
    }
    static class Image extends CharGrid {
        List<Galaxy> galaxies;
        Set<Integer> emptyRows;
        Set<Integer> emptyCols;

        public Image(List<String> data) {
            super(data);
            findGalaxies();
        }

        private void findGalaxies() {
            galaxies = new ArrayList<>();
            emptyRows = new HashSet<>(IntStream.rangeClosed(1, width).boxed().toList());
            emptyCols = new HashSet<>(IntStream.rangeClosed(1, height).boxed().toList());
            for (int y = 1 ; y < height+ 1; y++)
                for (int x = 1; x < width + 1; x++)
                    if (points[y][x] == '#') {
                        galaxies.add(new Galaxy(x, y));
                        emptyCols.remove(x);
                        emptyRows.remove(y);
                    }
        }

        public int distance(Galaxy g1, Galaxy g2) {
            return g1.distance(g2) + expandedBetween(g1, g2);
        }

        private int expandedBetween(Galaxy g1, Galaxy g2) {
            return expandedBetween(Math.min(g1.x, g2.x), Math.max(g1.x, g2.x), emptyCols)
                    + expandedBetween(Math.min(g1.y, g2.y), Math.max(g1.y, g2.y), emptyRows);
        }

        private int expandedBetween(int min, int max, Set<Integer> empty) {
            int between = 0;
            for (int i = min + 1; i < max; i++)
                if (empty.contains(i))
                    between++;
            return between;
        }

        private int allDistances() {
            int total = 0;
            for (int i = 0; i < galaxies.size(); i++) {
                for (int j = i + 1; j < galaxies.size(); j++) {
                    total += distance(galaxies.get(i), galaxies.get(j));
                }
            }
            return total;
        }
    }
    int part1(List<String> data) {
        var image = new Image(data);
        return image.allDistances();
    }

    /*
    --- Part Two ---
    The galaxies are much older (and thus much farther apart) than the researcher initially estimated.

    Now, instead of the expansion you did before, make each empty row or column one million times larger.
    That is, each empty row should be replaced with 1000000 empty rows, and each empty column should be
    replaced with 1000000 empty columns.

    (In the example above, if each empty row or column were merely 10 times larger, the sum of the shortest
    paths between every pair of galaxies would be 1030. If each empty row or column were merely 100 times
    larger, the sum of the shortest paths between every pair of galaxies would be 8410. However, your
    universe will need to expand far beyond these values.)

    Starting with the same initial image, expand the universe according to these new rules, then find the
    length of the shortest path between every pair of galaxies. What is the sum of these lengths?
    */

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day11 = new Day11();
        var data = IO.getResourceAsList("day11.txt");
        var part1 = day11.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day11.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
