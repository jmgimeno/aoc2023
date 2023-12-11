package aoc2023.day11;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {

    static final String example = """
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
            """;

    static final Day11 day11 = new Day11();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(374L, day11.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day11.txt");
        assertEquals(9177603L, day11.part1(data));
    }

    @Test
    @DisplayName("part2 - example data expanded 10")
    void test3() {
        var data = IO.splitLinesAsList(example);
        var image = new Day11.Image(data);
        assertEquals(1030L, image.allDistances(10));
    }

    @Test
    @DisplayName("part2 - example data expanded 100")
    void test4() {
        var data = IO.splitLinesAsList(example);
        var image = new Day11.Image(data);
        assertEquals(8410L, image.allDistances(100));
    }

    @Test
    @DisplayName("part2 - input data")
    void test5() {
        var data = IO.getResourceAsList("day11.txt");
        assertEquals(632003913611L, day11.part2(data));
    }
}
