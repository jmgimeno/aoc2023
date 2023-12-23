package aoc2023.day14;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {

    static final String example = """
            O....#....
            O.OO#....#
            .....##...
            OO.#O....O
            .O.....O#.
            O.#..O.#.#
            ..O..#O..O
            .......O..
            #....###..
            #OO..#....
            """;

    static final Day14 day14 = new Day14();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(136, day14.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day14.txt");
        assertEquals(106378, day14.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    @Disabled("part2 - not implemented")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(-1, day14.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day14.txt");
        assertEquals(-1, day14.part2(data));
    }
}
