package aoc2023.day10;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {

    static final String example1A = """
            .....
            .S-7.
            .|.|.
            .L-J.
            .....
            """;

    static final String example1B = """
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
            """;

    static final String example2A = """
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
            """;

    static final String example2B = """
            ..........
            .S------7.
            .|F----7|.
            .||....||.
            .||....||.
            .|L-7F-J|.
            .|II||II|.
            .L--JL--J.
            ..........
            """;

    static final String example2C = """
            .F----7F7F7F7F-7....
            .|F--7||||||||FJ....
            .||.FJ||||||||L7....
            FJL7L7LJLJ||LJ.L-7..
            L--J.L7...LJS7F-7L7.
            ....F-J..F7FJ|L7L7L7
            ....L7.F7||L7|.L7L7|
            .....|FJLJ|FJ|F7|.LJ
            ....FJL-7.||.||||...
            ....L---J.LJ.LJLJ...
            """;

    static final String example2D = """
            FF7FSF7F7F7F7F7F---7
            L|LJ||||||||||||F--J
            FL-7LJLJ||||||LJL-77
            F--JF--7||LJLJ7F7FJ-
            L---JF-JLJ.||-FJLJJ7
            |F|F-JF---7F7-L7L|7|
            |FFJF7L7F-JF7|JL---7
            7-L-JL7||F7|L7F-7F7|
            L.L7LFJ|||||FJL7||LJ
            L7JLJL-JLJLJL--JLJ.L
            """;

    static final Day10 day10 = new Day10();

    @Test
    @DisplayName("part1 - example data A")
    void test1a() {
        var data = IO.splitLinesAsList(example1A);
        assertEquals(4, day10.part1(data));
    }

    @Test
    @DisplayName("part1 - example data B")
    void test1b() {
        var data = IO.splitLinesAsList(example1B);
        assertEquals(8, day10.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test1i() {
        var data = IO.getResourceAsList("day10.txt");
        assertEquals(6951, day10.part1(data));
    }

    @Test
    @DisplayName("part2 - example data A")
    void test2a() {
        var data = IO.splitLinesAsList(example2A);
        assertEquals(4, day10.part2(data));
    }

    @Test
    @DisplayName("part2 - example data B")
    void test2b() {
        var data = IO.splitLinesAsList(example2B);
        assertEquals(4, day10.part2(data));
    }

    @Test
    @DisplayName("part2 - example data C")
    void test2c() {
        var data = IO.splitLinesAsList(example2C);
        assertEquals(8, day10.part2(data));
    }

    @Test
    @DisplayName("part2 - example data D")
    void test2d() {
        var data = IO.splitLinesAsList(example2D);
        assertEquals(10, day10.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsList("day10.txt");
        assertEquals(563, day10.part2(data));
    }
}
