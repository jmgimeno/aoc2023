package aoc2023.day12;

import aoc2023.utils.IO;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Day12 {

    /*
        --- Day 12: Hot Springs ---
    You finally reach the hot springs! You can see steam rising from secluded areas attached to the
    primary, ornate building.

    As you turn to enter, the researcher stops you. "Wait - I thought you were looking for the hot
    springs, weren't you?" You indicate that this definitely looks like hot springs to you.

    "Oh, sorry, common mistake! This is actually the onsen! The hot springs are next door."

    You look in the direction the researcher is pointing and suddenly notice the massive metal
    helixes towering overhead. "This way!"

    It only takes you a few more steps to reach the main gate of the massive fenced-off area
    containing the springs. You go through the gate and into a small administrative building.

    "Hello! What brings you to the hot springs today? Sorry they're not very hot right now; we're
    having a lava shortage at the moment." You ask about the missing machine parts for Desert
    Island.

    "Oh, all of Gear Island is currently offline! Nothing is being manufactured at the moment, not
    until we get more lava to heat our forges. And our springs. The springs aren't very springy
    unless they're hot!"

    "Say, could you go up and see why the lava stopped flowing? The springs are too cold for normal
    operation, but we should be able to find one springy enough to launch you up there!"

    There's just one problem - many of the springs have fallen into disrepair, so they're not
    actually sure which springs would even be safe to use! Worse yet, their condition records of
    which springs are damaged (your puzzle input) are also damaged! You'll need to help them repair
    the damaged records.

    In the giant field just outside, the springs are arranged into rows. For each row, the condition
    records show every spring and whether it is operational (.) or damaged (#). This is the part of
    the condition records that is itself damaged; for some springs, it is simply unknown (?) whether
    the spring is operational or damaged.

    However, the engineer that produced the condition records also duplicated some of this
    information in a different format! After the list of springs for a given row, the size of each
    contiguous group of damaged springs is listed in the order those groups appear in the row. This
    list always accounts for every damaged spring, and each number is the entire size of its
    contiguous group (that is, groups are always separated by at least one operational spring: ####
    would always be 4, never 2,2).

    So, condition records with no unknown spring conditions might look like this:

    #.#.### 1,1,3
    .#...#....###. 1,1,3
    .#.###.#.###### 1,3,1,6
    ####.#...#... 4,1,1
    #....######..#####. 1,6,5
    .###.##....# 3,2,1
    However, the condition records are partially damaged; some of the springs' conditions are
    actually unknown (?). For example:

    ???.### 1,1,3
    .??..??...?##. 1,1,3
    ?#?#?#?#?#?#?#? 1,3,1,6
    ????.#...#... 4,1,1
    ????.######..#####. 1,6,5
    ?###???????? 3,2,1
    Equipped with this information, it is your job to figure out how many different arrangements of
    operational and broken springs fit the given criteria in each row.

    In the first line (???.### 1,1,3), there is exactly one way separate groups of one, one, and
    three broken springs (in that order) can appear in that row: the first three unknown springs
    must be broken, then operational, then broken (#.#), making the whole row #.#.###.

    The second line is more interesting: .??..??...?##. 1,1,3 could be a total of four different
    arrangements. The last ? must always be broken (to satisfy the final contiguous group of three
    broken springs), and each ?? must hide exactly one of the two broken springs. (Neither ?? could
    be both broken springs or they would form a single contiguous group of two; if that were true,
    the numbers afterward would have been 2,3 instead.) Since each ?? can either be #. or .#, there
    are four possible arrangements of springs.

    The last line is actually consistent with ten different arrangements! Because the first number
    is 3, the first and second ? must both be . (if either were #, the first number would have to be
    4 or higher). However, the remaining run of unknown spring conditions have many different ways
    they could hold groups of two and one broken springs:

    ?###???????? 3,2,1
    .###.##.#...
    .###.##..#..
    .###.##...#.
    .###.##....#
    .###..##.#..
    .###..##..#.
    .###..##...#
    .###...##.#.
    .###...##..#
    .###....##.#
    In this example, the number of possible arrangements for each row is:

    ???.### 1,1,3 - 1 arrangement .??..??...?##. 1,1,3 - 4 arrangements ?#?#?#?#?#?#?#? 1,3,1,6 - 1
    arrangement ????.#...#... 4,1,1 - 1 arrangement ????.######..#####. 1,6,5 - 4 arrangements
    ?###???????? 3,2,1 - 10 arrangements
    Adding all of the possible arrangement counts together produces a total of 21 arrangements.

    For each row, count all of the different arrangements of operational and broken springs that
    meet the given criteria. What is the sum of those counts?

    Your puzzle answer was 7090.
    */

    record Split(Row left, Row right) {

    }

    record Row(String condition, List<Integer> lengths) {

        static Row parse(String line) {
            String[] parts = line.split(" ");
            String condition = parts[0];
            List<Integer> lengths = Arrays.stream(parts[1].split(","))
                    .map(Integer::parseInt)
                    .toList();
            return new Row(condition, lengths);
        }

        Row unfold(int copies) {
            var newCondition =
                    IntStream.range(0, copies).boxed().map(i -> condition).collect(Collectors.joining("?"));
            var newLengths = new ArrayList<Integer>();
            for (int i = 0; i < copies; i++)
                newLengths.addAll(lengths);
            return new Row(newCondition, newLengths);
        }

        boolean isFinal() {
            return condition.chars().allMatch(c -> c == '.' || c == '#');
        }

        boolean isOK() {
            assert isFinal();
            var groups =
                    Arrays.stream(condition.split("\\.+")).filter(s -> !s.isEmpty()).map(String::length).toList();
            return groups.equals(lengths);
        }

        Split split() {
            assert !isFinal();
            var p = condition.indexOf('?');
            var left = condition.substring(0, p) + "." + condition.substring(p + 1);
            var right = condition.substring(0, p) + "#" + condition.substring(p + 1);
            return new Split(new Row(left, lengths), new Row(right, lengths));
        }

        int countArrangements() {
            // brute force !!
            if (isFinal()) {
                return isOK() ? 1 : 0;
            } else {
                Split split = split();
                return split.left().countArrangements() + split.right().countArrangements();
            }
        }
    }

    static int countArrangements(String conditions, List<Integer> lengths) {
        if (conditions.isEmpty())
            return lengths.isEmpty() ? 1 : 0;
        if (lengths.isEmpty())
            return conditions.chars().noneMatch(c -> c == '#') ? 1 : 0;
        var knownBlocks = conditions.chars().filter(c -> c == '#').count();
        var unknowns = conditions.chars().filter(c -> c == '?').count();
        var totalLength = lengths.stream().mapToInt(l -> l).sum();
        if (knownBlocks > totalLength)
            return 0;
        if (knownBlocks + unknowns < totalLength)
            return 0;
        char c = conditions.charAt(0);
        switch (c) {
            case '.' -> {
                return countArrangements(conditions.substring(1), lengths);
            }
            case '#' -> {
                if (lengths.isEmpty()) return 0;
                var f = lengths.get(0);
                if (f > 1) {
                    if (conditions.length() > 1) {
                        var cc = conditions.charAt(1);
                        if (cc == '#' || cc == '?') {
                            var ll = new ArrayList<>(lengths);
                            ll.set(0, f - 1);
                            return countArrangements("#" + conditions.substring(2), ll);
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                } else if (f == 1) {
                    if (conditions.length() > 1) {
                        var cc = conditions.charAt(1);
                        if (cc == '.' || cc == '?')
                            return countArrangements(conditions.substring(2), lengths.subList(1,
                                    lengths.size()));
                        else return 0;
                    } else {
                        return countArrangements("", lengths.subList(1, lengths.size()));
                    }
                } else {
                    return 0;
                }
            }
            case '?' -> {
                var cond = conditions.substring(1);
                return countArrangements(cond, lengths) + countArrangements("#" + cond,
                        lengths);
            }
            default -> throw new IllegalStateException("impossible");
        }
    }

    int part1(List<String> data) {
        return data.stream()
                .map(Row::parse)
                .mapToInt(r -> countArrangements(r.condition(), r.lengths()))
                .sum();
    }

    /*
    --- Part Two ---
    As you look out at the field of springs, you feel like there are way more springs than the
    condition records list. When you examine the records, you discover that they were actually
    folded up this whole time!

    To unfold the records, on each row, replace the list of spring conditions with five copies of
     itself (separated by ?) and replace the list of contiguous groups of damaged springs with
     five copies of itself (separated by ,).

    So, this row:

    .# 1
    Would become:

    .#?.#?.#?.#?.# 1,1,1,1,1
    The first line of the above example would become:

    ???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3
    In the above example, after unfolding, the number of possible arrangements for some rows is
    now much larger:

    ???.### 1,1,3 - 1 arrangement
    .??..??...?##. 1,1,3 - 16384 arrangements
    ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
    ????.#...#... 4,1,1 - 16 arrangements
    ????.######..#####. 1,6,5 - 2500 arrangements
    ?###???????? 3,2,1 - 506250 arrangements
    After unfolding, adding all of the possible arrangement counts together produces 525152.
     */

    int part2(List<String> data) {
        return data.stream()
                .map(Row::parse)
                .map(r -> r.unfold(5))
                .mapToInt(r -> countArrangements(r.condition(), r.lengths()))
                .sum();
    }

    public static void main(String[] args) {
        var day12 = new Day12();
        var data = IO.getResourceAsList("day12.txt");
        var part1 = day12.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day12.part2(data);
        System.out.println("part2 = " + part2);
    }
}
