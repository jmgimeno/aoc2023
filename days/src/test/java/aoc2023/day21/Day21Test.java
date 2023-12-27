package aoc2023.day21;

import aoc2023.utils.IO;
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

    // Example data does nor work for part2

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsList("day21.txt");
        assertEquals(609012263058042L, day21.part2(data, 26501365));
    }
}
