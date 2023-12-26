package aoc2023.day1;

import aoc2023.utils.IO;
import com.google.common.collect.Comparators;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day1 {

    final Finder digitsFinder = new Finder("1", "2", "3", "4", "5", "6", "7", "8", "9");
    final Finder namesFinder = new Finder("one", "two", "three", "four", "five", "six", "seven", "eight", "nine");

    record Appearance(int digit, int position) implements Comparable<Appearance> {
        @Override
        public int compareTo(Appearance o) {
            return Integer.compare(position, o.position);
        }

        static int toIntValue(Appearance first, Appearance last) {
            return 10 * first.digit() + last.digit();
        }
    }

    static class Finder {
        final List<String> names;
        final Pattern pattern;

        Finder(String... names) {
            this.names = Arrays.asList(names);
            var regexp = this.names.stream().reduce((a, b) -> a + "|" + b).orElseThrow();
            this.pattern = Pattern.compile(regexp);
        }

        Appearance findFirst(String line) {
            var firstMatch = pattern.matcher(line).results().findFirst();
            int firstWordPosition = firstMatch.map(MatchResult::start).orElse(Integer.MAX_VALUE);
            var firstWordString = firstMatch.map(MatchResult::group).orElse("");
            return new Appearance(names.indexOf(firstWordString) + 1, firstWordPosition);
        }

        Optional<MatchResult> lastMatch(String line) {
            var matcher = pattern.matcher(line);
            MatchResult last = null;
            var start = 0;
            while (matcher.find(start)) {
                last = matcher.toMatchResult();
                start = last.start() + 1;
            }
            return Optional.ofNullable(last);
        }

        Appearance findLast(String line) {
            var lastMatch = lastMatch(line);
            int lastWordPosition = lastMatch.map(MatchResult::start).orElse(Integer.MIN_VALUE);
            var lastWordString = lastMatch.map(MatchResult::group).orElse("");
            return new Appearance(names.indexOf(lastWordString) + 1, lastWordPosition);
        }
    }

    int calibrationValue1(String line) {
        var first = digitsFinder.findFirst(line);
        var last = digitsFinder.findLast(line);
        return Appearance.toIntValue(first, last);
    }

    int part1(List<String> data) {
        return data.stream().mapToInt(this::calibrationValue1).sum();
    }

    int calibrationValue2(String line) {
        var firstDigit = digitsFinder.findFirst(line);
        var lastDigit = digitsFinder.findLast(line);
        var firstName = namesFinder.findFirst(line);
        var lastName = namesFinder.findLast(line);
        var first = Comparators.min(firstDigit, firstName);
        var last = Comparators.max(lastDigit, lastName);
        return Appearance.toIntValue(first, last);
    }

    int part2(List<String> data) {
        return data.stream().mapToInt(this::calibrationValue2).sum();
    }

    public static void main(String[] args) {
        var day1 = new Day1();
        var data = IO.getResourceAsList("day1.txt");
        var part1 = day1.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day1.part2(data);
        System.out.println("part2 = " + part2);
    }
}
