package aoc2023.day7;

import aoc2023.day7.Day7.Hand;
import aoc2023.utils.IO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day7Test {

    static final String example = """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
            """;

    static final Day7 day7 = new Day7();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsList(example);
        assertEquals(6440, day7.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsList("day7.txt");
        assertEquals(248422077, day7.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    @Disabled("part2 - not implemented")
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(-1, day7.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day7.txt");
        assertEquals(-1, day7.part2(data));
    }

    @Test
    @DisplayName("sort hands")
    void test5() {
        var h_32T3K = Hand.parse("32T3K 765");
        var h_T55J5 = Hand.parse("T55J5 684");
        var h_KK677 = Hand.parse("KK677 28");
        var h_KTJJT = Hand.parse("KTJJT 220");
        var h_QQQJA = Hand.parse("QQQJA 483");
        var list = new ArrayList<>(List.of(h_32T3K, h_T55J5, h_KK677, h_KTJJT, h_QQQJA));
        list.sort(Hand::compareTo);
        assertEquals(List.of(h_32T3K, h_KTJJT, h_KK677, h_T55J5, h_QQQJA), list);
    }
}
