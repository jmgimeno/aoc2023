package aoc2023.day22;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day22Test {

    static final String example = """
            """;

    static final Day22 day22 = new Day22();

    @Test
    @DisplayName("part1 - example data")
    @Disabled("part1 - not implemented")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(-1, day22.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    @Disabled("part1 - not implemented")
    void test2() {
        var data = IO.getResourceAsList("day22.txt");
        assertEquals(-1, day22.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    @Disabled("part2 - not implemented")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(-1, day22.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day22.txt");
        assertEquals(-1, day22.part2(data));
    }
}
