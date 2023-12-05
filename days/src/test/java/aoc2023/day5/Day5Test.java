package aoc2023.day5;

import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
        var expected = new Day5.Seeds(Set.of(79L, 14L, 55L, 13L));
        assertEquals(expected, Day5.Seeds.parse(seeds));
    }

    @Test
    @DisplayName("coverage disjoint input to the left")
    void test6() {
        var input = new Day5.Range(1, 3);
        var segment = new Day5.Range(4, 5);
        var expected = new Day5.Coverage(Set.of(), Set.of(new Day5.Range(1, 3)));
        assertEquals(expected, segment.coverage(input));
    }

    @Test
    @DisplayName("coverage disjoint input to the right")
    void test7() {
        var input = new Day5.Range(4, 5);
        var segment = new Day5.Range(1, 3);
        var expected = new Day5.Coverage(Set.of(), Set.of(new Day5.Range(4, 5)));
        assertEquals(expected, segment.coverage(input));
    }

    @Test
    @DisplayName("coverage input to the left")
    void test8() {
        var input = new Day5.Range(1, 6);
        var segment = new Day5.Range(4, 5);
        var expected = new Day5.Coverage(Set.of(new Day5.Range(4, 3)), Set.of(new Day5.Range(1,
                3)));
        assertEquals(expected, segment.coverage(input));
    }

    @Test
    @DisplayName("coverage input to the right")
    void test9() {
        var input = new Day5.Range(4, 5);
        var segment = new Day5.Range(1, 6);
        var expected = new Day5.Coverage(Set.of(new Day5.Range(4, 3)), Set.of(new Day5.Range(7,
                2)));
        assertEquals(expected, segment.coverage(input));
    }

    @Test
    @DisplayName("coverage input contained")
    void test10() {
        var input = new Day5.Range(2, 3);
        var segment = new Day5.Range(1, 6);
        var expected = new Day5.Coverage(Set.of(new Day5.Range(2, 3)), Set.of());
        assertEquals(expected, segment.coverage(input));
    }

    @Test
    @DisplayName("transform a covered range")
    void test11() {
        var input = new Day5.Range(2, 3);
        var segment = new Day5.SegmentMap(4, 1, 8);
        var expected = new Day5.Range(5, 3);
        assertEquals(expected, segment.transformCovered(input));
    }

    @Test
    @DisplayName("transform a range")
    void test12() {
        var input = new Day5.Range(1, 11);
        var segment = new Day5.SegmentMap(20, 5, 3);
        var expected = new Day5.Coverage(Set.of(new Day5.Range(20, 3)), Set.of(new Day5.Range(1, 4), new Day5.Range(8, 4)));
        assertEquals(expected, segment.transform(input));
    }
}
