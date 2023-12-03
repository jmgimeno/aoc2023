package aoc2023.day3;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day3Test {

    static final String example = """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...$.*....
            .664.598..
            """;

    static final Day3 day3 = new Day3();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsImmutableList(example);
        assertEquals(4361, day3.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsImmutableList("day3.txt");
        assertEquals(553825, day3.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        var data = IO.splitLinesAsImmutableList(example);
        assertEquals(467835, day3.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsImmutableList("day3.txt");
        assertEquals(93994191, day3.part2(data));
    }

    @Test
    @DisplayName("part2 - test data - PartNumbers in line 1")
    void test5() {
        var data = IO.splitLinesAsImmutableList(example);
        var schematic = new Day3.Schematic(data);
        var partNumbers = schematic.partNumbersInLine(1);
        var expected = List.of(
                new Day3.PartNumber(467, 1, 1, 3),
                new Day3.PartNumber(114, 1, 6, 8)
        );
        assertEquals(expected, partNumbers);
    }

    @Test
    @DisplayName("part2 - test data - 467 is surrounded by symbol")
    void test6() {
        var data = IO.splitLinesAsImmutableList(example);
        var schematic = new Day3.Schematic(data);
        var partNumber = new Day3.PartNumber(467, 1, 1, 3);
        assertTrue(schematic.surroundedBySymbol(partNumber));
    }

    @Test
    @DisplayName("part2 - test data - 114 is surrounded by symbol")
    void test7() {
        var data = IO.splitLinesAsImmutableList(example);
        var schematic = new Day3.Schematic(data);
        var partNumber = new Day3.PartNumber(114, 1, 6, 8);
        assertFalse(schematic.surroundedBySymbol(partNumber));
    }

    @Test
    @DisplayName("part2 - test data - all numbers surrounded by symbol")
    void test8() {
        var data = IO.splitLinesAsImmutableList(example);
        var schematic = new Day3.Schematic(data);
        var expected = List.of(467, 35, 633, 617, 592, 755, 664, 598);
        assertEquals(expected, schematic.allSurroundedBySymbols());
    }

    @Test
    @DisplayName("reads correctly the data")
    void test9() {
        var expected = """
                ............
                .467..114...
                ....*.......
                ...35..633..
                .......#....
                .617*.......
                ......+.58..
                ...592......
                .......755..
                ....$.*.....
                ..664.598...
                ............
                """;
        var data = IO.splitLinesAsImmutableList(example);
        var schematic = new Day3.Schematic(data);
        assertEquals(expected, schematic.toString());
    }

    @Test
    @DisplayName("part2 - test data - get all gears")
    void test10() {
        var data = IO.splitLinesAsImmutableList(example);
        var schematic = new Day3.Schematic(data);
        var expected = List.of(
                new Day3.Position(4, 2),
                new Day3.Position(4, 5),
                new Day3.Position(6, 9)
        );
        assertEquals(expected, schematic.gearPositions());
    }
}
