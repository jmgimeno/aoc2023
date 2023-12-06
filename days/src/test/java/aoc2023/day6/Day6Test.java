package aoc2023.day6;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static aoc2023.day6.Day6.Race;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day6Test {

    /*
        Time:      7  15   30
        Distance:  9  40  200
    */

    static final List<Race> exampleData1 = List.of(
            new Race(7L, 9L),
            new Race(15L, 40L),
            new Race(30L, 200L));

    static final Race exampleData2 = new Race(71530L, 940200L);

    static final Day6 day6 = new Day6();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        assertEquals(288, day6.part1(exampleData1));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        assertEquals(1155175, day6.part1(day6.data1));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        assertEquals(71503, day6.part2(exampleData2));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        assertEquals(35961505, day6.part2(day6.data2));
    }
}
