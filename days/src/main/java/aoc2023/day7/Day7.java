package aoc2023.day7;

import aoc2023.utils.IO;
import com.google.common.collect.Streams;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Day7 {

    /*
        --- Day 7: Camel Cards ---
    Your all-expenses-paid trip turns out to be a one-way, five-minute ride in an airship. (At least
    it's a cool airship!) It drops you off at the edge of a vast desert and descends back to Island
    Island.

    "Did you bring the parts?"

    You turn around to see an Elf completely covered in white clothing, wearing goggles, and riding
    a large camel.

    "Did you bring the parts?" she asks again, louder this time. You aren't sure what parts she's
    looking for; you're here to figure out why the sand stopped.

    "The parts! For the sand, yes! Come with me; I will show you." She beckons you onto the camel.

    After riding a bit across the sands of Desert Island, you can see what look like very large
    rocks covering half of the horizon. The Elf explains that the rocks are all along the part of
    Desert Island that is directly above Island Island, making it hard to even get there. Normally,
    they use big machines to move the rocks and filter the sand, but the machines have broken down
    because Desert Island recently stopped receiving the parts they need to fix the machines.

    You've already assumed it'll be your job to figure out why the parts stopped when she asks if
    you can help. You agree automatically.

    Because the journey will take a few days, she offers to teach you the game of Camel Cards. Camel
    Cards is sort of similar to poker except it's designed to be easier to play while riding a
    camel.

    In Camel Cards, you get a list of hands, and your goal is to order them based on the strength of
    each cards. A cards consists of five cards labeled one of A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or
    2. The relative strength of each card follows this order, where A is the highest and 2 is the
    lowest.

    Every cards is exactly one type. From strongest to weakest, they are:

    Five of a kind, where all five cards have the same label: AAAAA Four of a kind, where four cards
    have the same label and one card has a different label: AA8AA Full house, where three cards have
    the same label, and the remaining two cards share a different label: 23332 Three of a kind,
    where three cards have the same label, and the remaining two cards are each different from any
    other card in the cards: TTT98 Two pair, where two cards share one label, two other cards share a
    second label, and the remaining card has a third label: 23432 One pair, where two cards share
    one label, and the other three cards have a different label from the pair and each other: A23A4
    High card, where all cards' labels are distinct: 23456
    Hands are primarily ordered based on type; for example, every full house is stronger than any
    three of a kind.

    If two hands have the same type, a second ordering rule takes effect. Start by comparing the
    first card in each cards. If these cards are different, the cards with the stronger first card is
    considered stronger. If the first card in each cards have the same label, however, then move on
    to considering the second card in each cards. If they differ, the cards with the higher second
    card wins; otherwise, continue with the third card in each cards, then the fourth, then the
    fifth.

    So, 33332 and 2AAAA are both four of a kind hands, but 33332 is stronger because its first card
    is stronger. Similarly, 77888 and 77788 are both a full house, but 77888 is stronger because its
    third card is stronger (and both hands have the same first and second card).

    To play Camel Cards, you are given a list of hands and their corresponding bid (your puzzle
    input). For example:

    32T3K 765
    T55J5 684
    KK677 28
    KTJJT 220
    QQQJA 483
    This example shows five hands; each cards is followed by its bid amount. Each cards wins an amount
    equal to its bid multiplied by its rank, where the weakest cards gets rank 1, the second-weakest
    cards gets rank 2, and so on up to the strongest cards. Because there are five hands in this
    example, the strongest cards will have rank 5 and its bid will be multiplied by 5.

    So, the first step is to put the hands in order of strength:

    32T3K is the only one pair and the other hands are all a stronger type, so it gets rank 1. KK677
    and KTJJT are both two pair. Their first cards both have the same label, but the second card of
    KK677 is stronger (K vs T), so KTJJT gets rank 2 and KK677 gets rank 3. T55J5 and QQQJA are both
    three of a kind. QQQJA has a stronger first card, so it gets rank 5 and T55J5 gets rank 4.
    Now, you can determine the total winnings of this set of hands by adding up the result of
    multiplying each cards's bid with its rank (765 * 1 + 220 * 2 + 28 * 3 + 684 * 4 + 483 * 5). So
    the total winnings in this example are 6440.

    Find the rank of every cards in your set. What are the total winnings?

    Your puzzle answer was 248422077.
    */

    record Hand(String cards, int bid, HandType type) {
        static Hand parse1(String line) {
            var parts = line.split(" ");
            return new Hand(parts[0], Integer.parseInt(parts[1]), classifyWithoutJokers(parts[0]));
        }

        static Hand parse2(String line) {
            var parts = line.split(" ");
            return new Hand(parts[0], Integer.parseInt(parts[1]), classifyWithJokers(parts[0]));
        }

        static HandType classifyWithoutJokers(String cards) {
            return classify(cards, false);
        }

        static HandType classifyWithJokers(String cards) {
            return classify(cards, true);
        }

        private static HandType classify(String cards, boolean withJokers) {
            var counts = countCards(cards);
            var jokerCount = counts.getOrDefault('J', 0L);
            if (!withJokers || jokerCount == 0L || jokerCount == 5L) {
                return getRawHandType(counts.values(), 5);
            } else {
                counts.remove('J');
                return getJokerizedHandType(counts.values(), jokerCount.intValue());
            }
        }
        private static Map<Character, Long> countCards(String cards) {
            return cards.chars()
                    .boxed()
                    .collect(groupingBy(i -> (char) i.intValue(), counting()));
        }

        private static HandType getRawHandType(Collection<Long> counters, int maxCards) {
            if (counters.contains(5L)) {
                return HandType.FIVE_OF_A_KIND;
            } else if (counters.contains(4L)) {
                return HandType.FOUR_OF_A_KIND;
            } else if (counters.contains(3L) && counters.contains(2L)) {
                return HandType.FULL_HOUSE;
            } else if (counters.contains(3L)) {
                return HandType.THREE_OF_A_KIND;
            } else if (counters.contains(2L) && counters.size() == maxCards - 2) {
                return HandType.TWO_PAIR;
            } else if (counters.contains(2L)) {
                return HandType.ONE_PAIR;
            } else {
                return HandType.HIGH_CARD;
            }
        }

        private static HandType getJokerizedHandType(Collection<Long> counters, int jokerCount) {
            HandType typeWithoutJokerCards = getRawHandType(counters, 5 - jokerCount);
            return switch (typeWithoutJokerCards) {
                case HIGH_CARD -> {
                    if (jokerCount == 1)
                        yield HandType.ONE_PAIR;
                    else if (jokerCount == 2)
                        yield HandType.THREE_OF_A_KIND;
                    else if (jokerCount == 3)
                        yield HandType.FOUR_OF_A_KIND;
                    else if (jokerCount == 4)
                        yield HandType.FIVE_OF_A_KIND;
                    throw new IllegalStateException("More than four jokers in high card");
                }
                case ONE_PAIR -> {
                    if (jokerCount == 1)
                        yield HandType.THREE_OF_A_KIND;
                    else if (jokerCount == 2)
                        yield HandType.FOUR_OF_A_KIND;
                    else if (jokerCount == 3)
                        yield HandType.FIVE_OF_A_KIND;
                    throw new IllegalStateException("More than three jokers in one pair");
                }
                case TWO_PAIR -> {
                    if (jokerCount == 1)
                        yield HandType.FULL_HOUSE;
                    throw new IllegalStateException("More than two jokers in two pair");
                }
                case THREE_OF_A_KIND -> {
                    if (jokerCount == 1)
                        yield HandType.FOUR_OF_A_KIND;
                    else if (jokerCount == 2)
                        yield HandType.FIVE_OF_A_KIND;
                    throw new IllegalStateException("More than two jokers in three of a kind");
                }
                case FULL_HOUSE -> throw new IllegalStateException("Joker in full house");
                case FOUR_OF_A_KIND -> {
                    if (jokerCount == 1)
                        yield HandType.FIVE_OF_A_KIND;
                    throw new IllegalStateException("More than two jokers in three of a kind");
                }
                case FIVE_OF_A_KIND -> throw new IllegalStateException("Joker in five of a kind");
            };
        }

        private static int compareCards(String cardOrder, String cards1, String cards2) {
            int i = 0;
            while (i < cards1.length() && i < cards2.length()) {
                var c1 = cards1.charAt(i);
                var c2 = cards2.charAt(i);
                var cmp = Integer.compare(cardOrder.indexOf(c1), cardOrder.indexOf(c2));
                if (cmp != 0) {
                    return cmp;
                }
                i++;
            }
            return 0;
        }

        static class HandComparatorPart1 implements Comparator<Hand> {

            private final String cardOrder;

            public HandComparatorPart1() {
                this("23456789TJQKA");
            }

            protected HandComparatorPart1(String cardOrder) {
                this.cardOrder = cardOrder;
            }

            @Override
            public int compare(Hand o1, Hand o2) {
                var byHandType = o1.type.compareTo(o2.type);
                if (byHandType != 0)
                    return byHandType;
                return compareCards(cardOrder, o1.cards, o2.cards);
            }
        }

        static class HandComparatorPart2 extends HandComparatorPart1 {
            public HandComparatorPart2() {
                super("J23456789TQKA");
            }
        }
    }

    enum HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND
    }

    long part1(List<String> data) {

        var hands = data.stream()
                .map(Hand::parse1)
                .sorted(new Hand.HandComparatorPart1())
                .toList();

        return Streams.mapWithIndex(hands.stream(), (hand, index) -> hand.bid() * (index + 1))
                .mapToLong(Long::longValue)
                .sum();
    }

    /*
    --- Part Two ---
    To make things a little more interesting, the Elf introduces one additional rule. Now, J cards
    are jokers - wildcards that can act like whatever card would make the hand the strongest type
    possible.

    To balance this, J cards are now the weakest individual cards, weaker even than 2. The other
    cards stay in the same order: A, K, Q, T, 9, 8, 7, 6, 5, 4, 3, 2, J.

    J cards can pretend to be whatever card is best for the purpose of determining hand type; for
    example, QJJQ2 is now considered four of a kind. However, for the purpose of breaking ties
    between two hands of the same type, J is always treated as J, not the card it's pretending
    to be: JKKK2 is weaker than QQQQ2 because J is weaker than Q.

    Now, the above example goes very differently:

    32T3K 765
    T55J5 684
    KK677 28
    KTJJT 220
    QQQJA 483

    32T3K is still the only one pair; it doesn't contain any jokers, so its strength doesn't
    increase.

    KK677 is now the only two pair, making it the second-weakest hand.

    T55J5, KTJJT, and QQQJA are now all four of a kind! T55J5 gets rank 3, QQQJA gets rank 4,
    and KTJJT gets rank 5.

    With the new joker rule, the total winnings in this example are 5905.

    Using the new joker rule, find the rank of every hand in your set. What are the new total
    winnings?
     */

    long part2(List<String> data) {

        var hands = data.stream()
                .map(Hand::parse2)
                .sorted(new Hand.HandComparatorPart2())
                .toList();

        return Streams.mapWithIndex(hands.stream(), (hand, index) -> hand.bid() * (index + 1))
                .mapToLong(Long::longValue)
                .sum();
    }

    public static void main(String[] args) {
        var day7 = new Day7();
        var data = IO.getResourceAsList("day7.txt");
        var part1 = day7.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day7.part2(data);
        System.out.println("part2 = " + part2);
    }
}
