package aoc2023.day17;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day17Test {

    static final String example = """
            2413432311323
            3215453535623
            3255245654254
            3446585845452
            4546657867536
            1438598798454
            4457876987766
            3637877979653
            4654967986887
            4564679986453
            1224686865563
            2546548887735
            4322674655533
            """;

    static final Day17 day17 = new Day17();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(102, day17.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day17.txt");
        assertEquals(694, day17.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(94, day17.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsList("day17.txt");
        assertEquals(829, day17.part2(data));
    }
}
