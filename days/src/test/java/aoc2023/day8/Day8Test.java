package aoc2023.day8;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day8Test {

    static final String example1a = """
            RL
                        
            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)
            """;

    static final String example2b = """
            LLR
            
            AAA = (BBB, BBB)
            BBB = (AAA, ZZZ)
            ZZZ = (ZZZ, ZZZ)
            """;

    static final String example2 = """
            LR
            
            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)
            """;

    static final Day8 day8 = new Day8();

    @Test
    @DisplayName("part1 - example1a")
    void test1a() {
        var data = IO.splitLinesAsList(example1a);
        assertEquals(2, day8.part1(data));
    }

    @Test
    @DisplayName("part1 - example2b")
    void test1b() {
        var data = IO.splitLinesAsList(example2b);
        assertEquals(6, day8.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    @Disabled("part1 - not implemented")
    void test2() {
        var data = IO.getResourceAsList("day8.txt");
        assertEquals(-1, day8.part1(data));
    }

    @Test
    @DisplayName("part2 - example2")
    @Disabled("part2 - not implemented")
    void test3a() {
        var data = IO.splitLinesAsList(example2);
        assertEquals(-1, day8.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day8.txt");
        assertEquals(-1, day8.part2(data));
    }
}
