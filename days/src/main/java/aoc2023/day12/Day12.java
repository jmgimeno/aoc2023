package aoc2023.day12;

import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Day12 {

    record Row(String condition, List<Integer> lengths) {

        static Row parse(String line) {
            String[] parts = line.split(" ");
            String condition = parts[0] + "$"; // We add a marker
            List<Integer> lengths = Arrays.stream(parts[1].split(","))
                    .map(Integer::parseInt)
                    .toList();
            return new Row(condition, lengths);
        }

        Row unfold(int copies) {
            var newCondition =
                    IntStream.range(0, copies).boxed().map(i -> condition.substring(0,
                            condition.length() - 1)).collect(Collectors.joining("?")) + "$";
            var newLengths = new ArrayList<Integer>();
            for (int i = 0; i < copies; i++)
                newLengths.addAll(lengths);
            return new Row(newCondition, newLengths);
        }

        long countArrangements() {
            var memo = new HashMap<String, Long>();
            return countArrangements(condition, lengths, 0, memo);
        }

        static long countArrangements(String conditions, List<Integer> lengths, int currentBlock,
                                      HashMap<String, Long> memo) {
            var key = conditions + lengths.toString() + currentBlock;
            if (memo.containsKey(key))
                return memo.get(key);
            // Conditions is never empty
            char c = conditions.charAt(0);
            long result = switch (c) {
                case '$' -> {
                    if (currentBlock == 0) {
                        yield lengths.isEmpty() ? 1L : 0L;
                    } else {
                        yield List.of(currentBlock).equals(lengths) ? 1L : 0L;
                    }
                }
                case '#' -> {
                    var blocks = count(conditions, '#');
                    if (!lengths.isEmpty() && currentBlock + blocks <= lengths.getFirst()) {
                        yield countArrangements(conditions.substring(blocks), lengths,
                                currentBlock + blocks, memo);
                    } else {
                        yield 0L;
                    }
                }
                case '.' -> {
                    int dots = count(conditions, '.');
                    if (currentBlock == 0) {
                        yield countArrangements(conditions.substring(dots), lengths,
                                currentBlock, memo);
                    } else if (!lengths.isEmpty() && currentBlock == lengths.getFirst()) {
                        yield countArrangements(conditions.substring(dots), lengths.subList(1
                                , lengths.size()), 0, memo);
                    } else {
                        yield 0L;
                    }
                }
                case '?' -> countArrangements(conditions.substring(1), lengths,
                        currentBlock + 1, memo)
                        + countArrangements("." + conditions.substring(1), lengths,
                        currentBlock, memo);
                default -> throw new AssertionError("cannot happen");
            };
            memo.put(key, result);
            return result;
        }
    }

    static int count(String conditions, char c) {
        assert conditions.charAt(0) == c;
        int i = 0;
        while (conditions.charAt(i) == c)
            i++;
        return i;
    }

    long part1(List<String> data) {
        return data.stream()
                .map(Row::parse)
                .mapToLong(Row::countArrangements)
                .sum();
    }

    long part2(List<String> data) {
        return data.stream()
                .map(Row::parse)
                .map(r -> r.unfold(5))
                .mapToLong(Row::countArrangements)
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
