package aoc2023.day4;

import aoc2023.utils.IO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Day4 {

    record Card(int id, Set<Integer> winning, List<Integer> yours) {

        static Card parse(String line) {
            Pattern pattern = Pattern.compile("Card (\\s*\\d+): (.+)\\| (.+)");
            Matcher matcher = pattern.matcher(line);

            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1).trim());
                Set<Integer> winning = Arrays.stream(matcher.group(2).trim().split("\\s+"))
                        .map(Integer::parseInt)
                        .collect(toSet());
                List<Integer> yours = Arrays.stream(matcher.group(3).trim().split("\\s+"))
                        .map(Integer::parseInt)
                        .collect(toList());

                return new Card(id, winning, yours);
            } else {
                throw new IllegalArgumentException("Invalid card format: " + line);
            }
        }

        int count() {
            return (int) yours.stream().filter(winning::contains).count();
        }

        int score() {
            var count = count();
            return count == 0 ? 0 : twoPower(count() - 1);
        }

        private int twoPower(int expo) {
            return 1 << expo;
        }
    }

    int part1(List<String> data) {
        return data.stream().map(Card::parse).mapToInt(Card::score).sum();
    }

    record CardCounter(Map<Integer, Integer> counters) {
        void addOne(int cardId) {
            addMany(cardId, 1);
        }

        void addMany(int cardId, int increment) {
            counters.merge(cardId, increment, Integer::sum);
        }

        int count() {
            return counters.values().stream().mapToInt(Integer::intValue).sum();
        }

        int get(int cardId) {
            return counters.getOrDefault(cardId, 0);
        }
    }

    int part2(List<String> data) {
        var cards = data.stream().map(Card::parse).toList();
        var cardCounter = new CardCounter(new HashMap<>());
        for (var card : cards) {
            cardCounter.addOne(card.id());
            int numCopies = cardCounter.get(card.id());
            for (int i = 1; i <= card.count(); i++) {
                cardCounter.addMany(card.id() + i, numCopies);
            }
        }
        return cardCounter.count();
    }

    public static void main(String[] args) {
        var day4 = new Day4();
        var data = IO.getResourceAsList("day4.txt");
        var part1 = day4.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day4.part2(data);
        System.out.println("part2 = " + part2);
    }
}
