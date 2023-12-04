package aoc2023.day1;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day1Test {

    static final String part1 = """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet""";

    static final String part2 = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen""";

    static final Day1 day1 = new Day1();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(part1);
        assertEquals(142, day1.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day1.txt");
        assertEquals(55621, day1.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        var data = IO.splitLinesAsList(part2);
        assertEquals(281, day1.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsList("day1.txt");
        assertEquals(53592, day1.part2(data));
    }

    // Thanks reddit !!

    @Test
    @DisplayName("part2 - overlapped patterns - eighthree")
    void overlapped1() {
        assertEquals(83, day1.calibrationValue2("eighthree"));
    }

    @Test
    @DisplayName("part2 - calibrate sevenine")
    void overlapped2() {
        assertEquals(79, day1.calibrationValue2("sevenine"));
    }
}
