package aoc2023.day21;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day21Test {

    static final String example = """
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
            """;

    static final Day21 day21 = new Day21();

    @Test
    @DisplayName("part1 - example data 1 step")
    void test1a() {
        var data = IO.splitLinesAsList(example);
        assertEquals(2, day21.part1(data, 1));
    }

    @Test
    @DisplayName("part1 - example data 2 steps")
    void test1b() {
        var data = IO.splitLinesAsList(example);
        assertEquals(4, day21.part1(data, 2));
    }

    @Test
    @DisplayName("part1 - example data 6 steps")
    void test1c() {
        var data = IO.splitLinesAsList(example);
        assertEquals(16, day21.part1(data, 6));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day21.txt");
        assertEquals(3682, day21.part1(data, 64));
    }

    @Test
    @DisplayName("part2 - example data 6 steps")
    void test3a() {
        var data = IO.splitLinesAsList(example);
        assertEquals(16, day21.part2(data, 6));
    }

    @Test
    @DisplayName("part2 - example data 10 steps")
    void test3b() {
        var data = IO.splitLinesAsList(example);
        assertEquals(50, day21.part2(data, 10));
    }

    @Test
    @DisplayName("part2 - example data 50 steps")
    void test3c() {
        var data = IO.splitLinesAsList(example);
        assertEquals(1594, day21.part2(data, 50));
    }

    @Test
    @DisplayName("part2 - example data 100 steps")
    void test3d() {
        var data = IO.splitLinesAsList(example);
        assertEquals(6536, day21.part2(data, 100));
    }

    @Test
    @DisplayName("part2 - example data 500 steps")
    void test3e() {
        var data = IO.splitLinesAsList(example);
        assertEquals(167004, day21.part2(data, 500));
    }

    @Test
    @DisplayName("part2 - example data 1000 steps")
    @Disabled("too slow")
    void test3f() {
        var data = IO.splitLinesAsList(example);
        assertEquals(668697, day21.part2(data, 1000));
    }

    @Test
    @DisplayName("part2 - example data 5000 steps")
    @Disabled("too slow")
    void test3g() {
        var data = IO.splitLinesAsList(example);
        assertEquals(16733044, day21.part2(data, 5000));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day21.txt");
        assertEquals(-1, day21.part2(data, 26501365));
    }
}
