package aoc2023.day7;

import aoc2023.day7.Day7.Hand;
import aoc2023.day7.Day7.HandType;
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
    void test3() {
        var data = IO.splitLinesAsList(example);
        assertEquals(5905, day7.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    @Disabled("part2 - not implemented")
    void test4() {
        var data = IO.getResourceAsList("day7.txt");
        assertEquals(-1, day7.part2(data));
    }

    @Test
    @DisplayName("sort hands part 1")
    void test5() {
        var h_32T3K = Hand.parse("32T3K 765");
        var h_T55J5 = Hand.parse("T55J5 684");
        var h_KK677 = Hand.parse("KK677 28");
        var h_KTJJT = Hand.parse("KTJJT 220");
        var h_QQQJA = Hand.parse("QQQJA 483");
        var list = new ArrayList<>(List.of(h_32T3K, h_T55J5, h_KK677, h_KTJJT, h_QQQJA));
        list.sort(new Hand.HandComparatorPart1());
        assertEquals(List.of(h_32T3K, h_KTJJT, h_KK677, h_T55J5, h_QQQJA), list);
    }

    @Test
    @DisplayName("jokerize 32T3K")
    void test6() {
        var h_32T3K = new Hand("32T3K", 765, HandType.HIGH_CARD);
        var j_32T3K = new Hand("32T3K", 765, HandType.HIGH_CARD);
        assertEquals(j_32T3K, h_32T3K.jokerize());
    }

    @Test
    @DisplayName("jokerize T55J5")
    void test7() {
        var h_T55J5 = new Hand("T55J5", 684, HandType.THREE_OF_A_KIND);
        var j_T55J5 = new Hand("T55J5", 684, HandType.FOUR_OF_A_KIND);
        assertEquals(j_T55J5, h_T55J5.jokerize());
    }

    @Test
    @DisplayName("jokerize KK677")
    void test8() {
        var h_KK677 = new Hand("KK677", 28, HandType.TWO_PAIR);
        var j_KK677 = new Hand("KK677", 28, HandType.TWO_PAIR);
        assertEquals(j_KK677, h_KK677.jokerize());
    }

    @Test
    @DisplayName("jokerize KTJJT")
    void test9() {
        var h_KTJJT = new Hand("KTJJT", 220, HandType.TWO_PAIR);
        var j_KTJJT = new Hand("KTJJT", 220, HandType.FOUR_OF_A_KIND);
        assertEquals(j_KTJJT, h_KTJJT.jokerize());
    }

    @Test
    @DisplayName("jokerize QQQJA")
    void test10() {
        var h_QQQJA = new Hand("QQQJA", 483, HandType.THREE_OF_A_KIND);
        var j_QQQJA = new Hand("QQQJA", 483, HandType.FOUR_OF_A_KIND);
        assertEquals(j_QQQJA, h_QQQJA.jokerize());
    }

    @Test
    @DisplayName("sort hands part 2")
    void test11() {
        var h_32T3K = Hand.parse("32T3K 765");
        var h_T55J5 = Hand.parse("T55J5 684");
        var h_KK677 = Hand.parse("KK677 28");
        var h_KTJJT = Hand.parse("KTJJT 220");
        var h_QQQJA = Hand.parse("QQQJA 483");
        var input = List.of(h_32T3K, h_T55J5, h_KK677, h_KTJJT, h_QQQJA);
        var jokerized = new ArrayList<>(input.stream().map(Hand::jokerize).toList());
        jokerized.sort(new Hand.HandComparatorPart2());
        var cards = jokerized.stream().map(Hand::cards).toList();
        assertEquals(List.of("32T3K", "KK677", "T55J5", "QQQJA", "KTJJT"), cards);
    }

    @Test
    @DisplayName("jokerize JK336")
    void test12() {
        var h_JK336 = new Hand("JK336", 894, HandType.ONE_PAIR);
        var j_JK336 = new Hand("JK336", 894, HandType.THREE_OF_A_KIND);
        assertEquals(j_JK336, h_JK336.jokerize());
    }
}
