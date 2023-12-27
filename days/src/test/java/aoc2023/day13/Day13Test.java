package aoc2023.day13;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day13Test {

    static final String example1 = """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
            """;

    static final String example2 = """
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
            """;

    static final String example = """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
                    
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
            """;

    static final Day13 day13 = new Day13();

    @Test
    @DisplayName("part1 - example1 data")
    void test1a() {
        var data = IO.splitLinesAsList(example1);
        assertEquals(5, day13.part1(data));
    }

    @Test
    @DisplayName("part1 - example2 data")
    void test1b() {
        var data = IO.splitLinesAsList(example2);
        assertEquals(400, day13.part1(data));
    }

    @Test
    @DisplayName("part1 - example data")
    void test1c() {
        var data = IO.splitLinesAsList(example);
        assertEquals(405, day13.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day13.txt");
        assertEquals(33195, day13.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(400, day13.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsList("day13.txt");
        assertEquals(31836, day13.part2(data));
    }
}
