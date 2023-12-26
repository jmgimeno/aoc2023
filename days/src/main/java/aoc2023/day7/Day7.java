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
