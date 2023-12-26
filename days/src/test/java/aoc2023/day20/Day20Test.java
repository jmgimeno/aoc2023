package aoc2023.day20;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day20Test {

    static final String exampleA = """
            broadcaster -> a, b, c
            %a -> b
            %b -> c
            %c -> inv
            &inv -> a
            """;

    static final String exampleB = """
            broadcaster -> a
            %a -> inv, con
            &inv -> b
            %b -> con
            &con -> output
            """;

    static final Day20 day20 = new Day20();

    @Test
    @DisplayName("part1 - example data A")
    void test1a() {
        var data = IO.splitLinesAsList(exampleA);
        assertEquals(32000000L, day20.part1(data));
    }


    @Test
    @DisplayName("part1 - example data B")
    void test1b() {
        var data = IO.splitLinesAsList(exampleB);
        assertEquals(11687500L, day20.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day20.txt");
        assertEquals(666795063L, day20.part1(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsList("day20.txt");
        assertEquals(253302889093151L, day20.part2(data));
    }
}
