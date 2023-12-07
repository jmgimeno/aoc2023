package aoc2023.day7;

import aoc2023.day7.Day7.Hand;
import aoc2023.day7.Day7.HandType;
import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static aoc2023.day7.Day7.Hand.classifyWithJokers;
import static aoc2023.day7.Day7.Hand.classifyWithoutJokers;
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
    void test4() {
        var data = IO.getResourceAsList("day7.txt");
        assertEquals(249817836, day7.part2(data));
    }

    @Test
    @DisplayName("sort hands part 1")
    void test5() {
        var h_32T3K = Hand.parse1("32T3K 765");
        var h_T55J5 = Hand.parse1("T55J5 684");
        var h_KK677 = Hand.parse1("KK677 28");
        var h_KTJJT = Hand.parse1("KTJJT 220");
        var h_QQQJA = Hand.parse1("QQQJA 483");
        var hands = new ArrayList<>(List.of(h_32T3K, h_T55J5, h_KK677, h_KTJJT, h_QQQJA));
        hands.sort(new Hand.HandComparatorPart1());
        assertEquals(List.of(h_32T3K, h_KTJJT, h_KK677, h_T55J5, h_QQQJA), hands);
    }

    @Test
    @DisplayName("classify 32T3K")
    void test6() {
        assertEquals(HandType.ONE_PAIR, classifyWithoutJokers("32T3K"));
        assertEquals(HandType.ONE_PAIR, classifyWithoutJokers("32T3K"));
    }

    @Test
    @DisplayName("classify T55J5")
    void test7() {
        assertEquals(HandType.THREE_OF_A_KIND, classifyWithoutJokers("T55J5"));
        assertEquals(HandType.FOUR_OF_A_KIND, classifyWithJokers("T55J5"));
    }

    @Test
    @DisplayName("classify KK677")
    void test8() {
        assertEquals(HandType.TWO_PAIR, classifyWithoutJokers("KK677"));
        assertEquals(HandType.TWO_PAIR, classifyWithJokers("KK677"));
    }

    @Test
    @DisplayName("classify KTJJT")
    void test9() {
        assertEquals(HandType.TWO_PAIR, classifyWithoutJokers("KTJJT"));
        assertEquals(HandType.FOUR_OF_A_KIND, classifyWithJokers("KTJJT"));
    }

    @Test
    @DisplayName("jokerize QQQJA")
    void test10() {
        assertEquals(HandType.THREE_OF_A_KIND, classifyWithoutJokers("QQQJA"));
        assertEquals(HandType.FOUR_OF_A_KIND, classifyWithJokers("QQQJA"));
    }

    @Test
    @DisplayName("sort hands part 2")
    void test11() {
        var h_32T3K = Hand.parse2("32T3K 765");
        var h_T55J5 = Hand.parse2("T55J5 684");
        var h_KK677 = Hand.parse2("KK677 28");
        var h_KTJJT = Hand.parse2("KTJJT 220");
        var h_QQQJA = Hand.parse2("QQQJA 483");
        var hands = new ArrayList<>(List.of(h_32T3K, h_T55J5, h_KK677, h_KTJJT, h_QQQJA));
        hands.sort(new Hand.HandComparatorPart2());
        assertEquals(List.of(h_32T3K, h_KK677, h_T55J5, h_QQQJA, h_KTJJT), hands);
    }

    @Test
    @DisplayName("classify JK336")
    void test12() {
        assertEquals(HandType.ONE_PAIR, classifyWithoutJokers("JK336"));
        assertEquals(HandType.THREE_OF_A_KIND, classifyWithJokers("JK336"));
    }
}
