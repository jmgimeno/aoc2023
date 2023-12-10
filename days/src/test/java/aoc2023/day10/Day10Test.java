package aoc2023.day10;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {

    static final String exampleA = """
            .....
            .S-7.
            .|.|.
            .L-J.
            .....
            """;

    static final String exampleB = """
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
            """;

    static final Day10 day10 = new Day10();

    @Test
    @DisplayName("part1 - example data A")
    void test1a() {
        var data = IO.splitLinesAsList(exampleA);
        assertEquals(4, day10.part1(data));
    }

    @Test
    @DisplayName("part1 - example data B")
    void test1b() {
        var data = IO.splitLinesAsList(exampleB);
        assertEquals(8, day10.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day10.txt");
        assertEquals(6951, day10.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    @Disabled("part2 - not implemented")
    void test3() {
        var data = IO.splitLinesAsList(exampleA);
        assertEquals(-1, day10.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day10.txt");
        assertEquals(-1, day10.part2(data));
    }
}
