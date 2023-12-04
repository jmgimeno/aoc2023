package aoc2023.day1;

import aoc2023.utils.IO;
import com.google.common.collect.Comparators;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day1 {

    /*
    --- Day 1: Trebuchet?! ---
    Something is wrong with global snow production, and you've been selected to take a look. The
    Elves have even given you a map; on it, they've used stars to mark the top fifty locations
    that are likely to be having problems.

    You've been doing this long enough to know that to restore snow operations, you need to check
    all fifty stars by December 25th.

    Collect stars by solving puzzles. Two puzzles will be made available on each day in the
    Advent calendar; the second puzzle is unlocked when you complete the first. Each puzzle
    grants one star. Good luck!

    You try to ask why they can't just use a weather machine ("not powerful enough") and where
    they're even sending you ("the sky") and why your map looks mostly blank ("you sure ask a lot
    of questions") and hang on did you just say the sky ("of course, where do you think snow
    comes from") when you realize that the Elves are already loading you into a trebuchet
    ("please hold still, we need to strap you in").

    As they're making the final adjustments, they discover that their calibration document (your
    puzzle input) has been amended by a very young Elf who was apparently just excited to show
    off her art skills. Consequently, the Elves are having trouble reading the values on the
    document.

    The newly-improved calibration document consists of lines of text; each line originally
    contained a specific calibration value that the Elves now need to recover. On each line, the
    calibration value can be found by combining the first digit and the last digit (in that
    order) to form a single two-digit number.

    For example:

    1abc2
    pqr3stu8vwx
    a1b2c3d4e5f
    treb7uchet
    In this example, the calibration values of these four lines are 12, 38, 15, and 77. Adding
    these together produces 142.

    Consider your entire calibration document. What is the sum of all of the calibration values?

    Your puzzle answer was 55621.
     */

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

    /*
    --- Part Two ---
    Your calculation isn't quite right. It looks like some of the digits are actually spelled out
    with letters: one, two, three, four, five, six, seven, eight, and nine also count as valid
    "digits".

    Equipped with this new information, you now need to find the real first and last digit on
    each line. For example:

    two1nine
    eightwothree
    abcone2threexyz
    xtwone3four
    4nineeightseven2
    zoneight234
    7pqrstsixteen
    In this example, the calibration values are 29, 83, 13, 24, 42, 14, and 76. Adding these
    together produces 281.

    What is the sum of all of the calibration values?

    Your puzzle answer was 53592.
    */

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
