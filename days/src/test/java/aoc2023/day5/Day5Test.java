package aoc2023.day5;

import aoc2023.day5.Day5.Range;
import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static aoc2023.day5.Day5.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day5Test {

    static final String example = """
            seeds: 79 14 55 13
                        
            seed-to-soil map:
            50 98 2
            52 50 48
                        
            soil-to-fertilizer map:
            0 15 37
            37 52 2
            39 0 15
                        
            fertilizer-to-water map:
            49 53 8
            0 11 42
            42 0 7
            57 7 4
                        
            water-to-light map:
            88 18 7
            18 25 70
                        
            light-to-temperature map:
            45 77 23
            81 45 19
            68 64 13
                        
            temperature-to-humidity map:
            0 69 1
            1 0 69
                        
            humidity-to-location map:
            60 56 37
            56 93 4
            """;

    static final Day5 day5 = new Day5();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(35, day5.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day5.txt");
        assertEquals(825516882, day5.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(46, day5.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day5.txt");
        assertEquals(-1, day5.part2(data));
    }

    @Test
    @DisplayName("parse seeds")
    void test5() {
        var seeds = "seeds: 79 14 55 13";
        var expected = new Seeds(List.of(79L, 14L, 55L, 13L));
        assertEquals(expected, Seeds.parse(seeds));
    }

    @Test
    @DisplayName("transformCovered a range")
    void test11() {
        var input = new Range(2, 3);
        var segment = new Segment(4, 1, 8);
        var expected = new Range(5, 3);
        assertEquals(expected, segment.transformCovered(input));
    }

    @Test
    @DisplayName("segment.transform - a range containning the segment")
    void test12() {
        var input = new Range(1, 11);
        var segment = new Segment(20, 5, 3);
        var expected = new Coverage(List.of(new Range(20, 3)),
                List.of(new Range(1, 4), new Range(8, 4)));
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range to the left")
    void test13() {
        var input = new Range(1, 3);
        var segment = new Segment(20, 5, 3);
        var expected = new Coverage(List.of(), List.of(new Range(1, 3)));
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range touching the left")
    void test14() {
        var input = new Range(1, 4);
        var segment = new Segment(20, 5, 3);
        var expected = new Coverage(List.of(), List.of(new Range(1, 4)));
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range touching the right")
    void test15() {
        var input = new Range(8, 4);
        var segment = new Segment(20, 5, 3);
        var expected = new Coverage(List.of(), List.of(new Range(8, 4)));
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range to the right")
    void test16() {
        var input = new Range(10, 4);
        var segment = new Segment(20, 5, 3);
        var expected = new Coverage(List.of(), List.of(new Range(10, 4)));
        assertEquals(expected, segment.transform(input));
    }
    @Test
    @DisplayName("segment.transform - a range fully contained at beginning")
    void test17() {
        var input = new Range(5, 3);
        var segment = new Segment(20, 5, 8);
        var expected = new Coverage(List.of(new Range(20, 3)), List.of());
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range fully contained in the middle")
    void test18() {
        var input = new Range(8, 3);
        var segment = new Segment(20, 5, 8);
        var expected = new Coverage(List.of(new Range(23, 3)), List.of());
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range fully contained at end")
    void test19() {
        var input = new Range(10, 3);
        var segment = new Segment(20, 5, 8);
        var expected = new Coverage(List.of(new Range(25, 3)), List.of());
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range intersecting to the left")
    void test20() {
        var input = new Range(1, 8);
        var segment = new Segment(20, 5, 8);
        var expected = new Coverage(List.of(new Range(20, 4)), List.of(new Range(1, 4)) );
        assertEquals(expected, segment.transform(input));
    }

    @Test
    @DisplayName("segment.transform - a range intersecting to the right")
    void test21() {
        var input = new Range(12, 10);
        var segment = new Segment(20, 5, 15);
        var expected = new Coverage(List.of(new Range(27, 8)), List.of(new Range(20, 2)) );
        assertEquals(expected, segment.transform(input));
    }
}
