package aoc2023.day12;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12Test {

    static final String example = """
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1
            """;

    static final Day12 day12 = new Day12();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(21, day12.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    @Disabled("part1 - not implemented")
    void test2() {
        var data = IO.getResourceAsList("day12.txt");
        assertEquals(-1, day12.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    @Disabled("part2 - not implemented")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(-1, day12.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day12.txt");
        assertEquals(-1, day12.part2(data));
    }

    @Test
    @DisplayName("count arrangements no ?")
    void test5() {
        var noQuestions = """
                #.#.### 1,1,3
                .#...#....###. 1,1,3
                .#.###.#.###### 1,3,1,6
                ####.#...#... 4,1,1
                #....######..#####. 1,6,5
                .###.##....# 3,2,1
                """;
        var data = IO.splitLinesAsList(noQuestions);
        assertEquals(data.size(), day12.part1(data));
    }
}