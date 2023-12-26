package aoc2023.day9;

import aoc2023.utils.IO;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day9 {

    record FirstLast(int first, int last) {
    }

    record Line(List<Integer> line) {
        static Line parse(String line) {
            // Line is of the form "0 3 6 9 12 15"
            var split = line.split(" ");
            var history = Stream.of(split).map(Integer::parseInt).toList();
            return new Line(history);
        }

        static boolean allZero(List<Integer> line) {
            return line.stream().allMatch(i -> i == 0);
        }

        List<FirstLast> extrems() {
            var differences = Stream.iterate(
                    line,
                    line -> !allZero(line),
                    line -> IntStream.range(0, line.size() - 1)
                            .map(i -> line.get(i + 1) - line.get(i))
                            .boxed()
                            .toList()
            );
            return differences
                    .map(l -> new FirstLast(l.getFirst(), l.getLast()))
                    .toList();
        }

        int nextValue() {
            return extrems().stream().mapToInt(FirstLast::last).sum();
        }

        int previousValue() {
            /*
            I can use reduce with these parameters:
                - identity = 0
                - accumulator = (a, b) -> b - a
                - combiner = (a, b) -> b - a
            Because:
                - forall u:
                    - combiner.apply(identity, u) = u
                    - u - 0 = u
                - forall u, t:
                    - combiner.apply(u, accumulator.apply(identity, t)) == accumulator.apply(u, t)
                    - (t - 0) - u  = t - u
             */

            return extrems().reversed().stream().
                    reduce(0, (value, line) -> line.first() - value, (a, b) -> b - a);
        }
    }

    int part1(List<String> data) {
        return data.stream().map(Line::parse).mapToInt(Line::nextValue).sum();
    }

    int part2(List<String> data) {
        return data.stream().map(Line::parse).mapToInt(Line::previousValue).sum();
    }

    public static void main(String[] args) {
        var day9 = new Day9();
        var data = IO.getResourceAsList("day9.txt");
        var part1 = day9.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day9.part2(data);
        System.out.println("part2 = " + part2);
    }
}
