package aoc2023.day5;

import aoc2023.utils.IO;

import java.util.*;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;


public class Day5 {

    record Segment(long destinationStart, long sourceStart, long length) {

        boolean isInRange(long value) {
            return sourceStart <= value && value < sourceStart + length;
        }

        public long transformCovered(long input) {
            var delta = destinationStart - sourceStart;
            return input + delta;
        }

        public Coverage transform(Range input) {
            var sourceEnd = sourceStart + length;
            var inputEnd = input.start + input.length;
            if (inputEnd <= sourceStart || sourceEnd <= input.start) {
                // No overlap
                return new Coverage(List.of(), List.of(input));
            } else if (sourceStart <= input.start && inputEnd <= sourceEnd) {
                // Input is contained in the segment
                return new Coverage(List.of(transformCovered(input)), List.of());
            } else if (input.start <= sourceStart && sourceEnd <= inputEnd) {
                // Segment is contained in the input
                var uncovered = new ArrayList<Range>();
                var left = new Range(input.start, sourceStart - input.start);
                if (left.length != 0) uncovered.add(left);
                var covered = new Range(sourceStart, length);
                var right = new Range(sourceEnd, inputEnd - sourceEnd);
                if (right.length != 0) uncovered.add(right);
                return new Coverage(List.of(transformCovered(covered)), uncovered);
            } else if (input.start <= sourceStart) {
                // Segment overlaps on the right side of the input
                var uncovered = new ArrayList<Range>();
                var left = new Range(input.start, sourceStart - input.start);
                if (left.length != 0) uncovered.add(left);
                var covered = new Range(sourceStart, inputEnd - sourceStart);
                return new Coverage(List.of(transformCovered(covered)), uncovered);
            } else {
                // Segment overlaps on the left side of the input
                var uncovered = new ArrayList<Range>();
                var covered = new Range(input.start, sourceEnd - input.start);
                var right = new Range(sourceEnd, inputEnd - sourceEnd);
                if (right.length != 0) uncovered.add(right);
                return new Coverage(List.of(transformCovered(covered)), uncovered);
            }
        }

        public Range transformCovered(Range input) {
            assert input.length != 0;
            var delta = destinationStart - sourceStart;
            return new Range(input.start + delta, input.length);
        }
    }

    record Map(String name, List<Segment> segments) {

        public long transform(long value) {
            return segments.stream()
                    .filter(r -> r.isInRange(value))
                    .mapToLong(r -> r.transformCovered(value))
                    .findFirst()
                    .orElse(value);
        }

        public List<Range> transform(List<Range> input) {
            var ranges = new ArrayList<>(input);
            var result = new ArrayList<Range>();
            for (var segment : segments) {
                var nextRanges = new ArrayList<Range>();
                for (var range : ranges) {
                    var coverage = segment.transform(range);
                    result.addAll(coverage.covered());
                    nextRanges.addAll(coverage.uncovered());
                }
                ranges = nextRanges;
            }
            result.addAll(ranges);
            assert result.stream().allMatch(r -> r.length != 0);
            return result;
        }

        static Map parseMap(String name, List<String> data) {
            var ranges = data.stream()
                    .map(line -> {
                        var parts = Arrays.stream(line.split(" "))
                                .mapToLong(Long::parseLong)
                                .toArray();
                        return new Segment(parts[0], parts[1], parts[2]);
                    })
                    .toList();
            return new Map(name, ranges);
        }

        LongUnaryOperator asLongUnaryOperator() {
            return this::transform;
        }

        UnaryOperator<List<Range>> asUnaryOperator() {
            return this::transform;
        }
    }

    record Almanac(List<Map> maps) {

        public long minLocation(long input) {
            var composition = maps.stream()
                    .map(Map::asLongUnaryOperator)
                    .reduce((long l) -> l, LongUnaryOperator::andThen);
            return composition.applyAsLong(input);
        }

        public long minLocation(Range input) {
            var composition = maps.stream()
                    .map(Map::asUnaryOperator)
                    .reduce(UnaryOperator.identity(),
                            (op1, op2) -> ranges -> op2.apply(op1.apply(ranges)));
            return composition.apply(List.of(input)).stream().mapToLong(Range::start).min().orElseThrow();
        }
    }

    record Seeds(List<Long> values) {
        static Seeds parse(String line) {
            var numbers = line.replace("seeds: ", "").split(" ");
            var values = Arrays.stream(numbers)
                    .map(Long::parseLong)
                    .toList();
            return new Seeds(values);
        }
    }

    record Day5InputPart1(Seeds seeds, Almanac almanac) {
        static Day5InputPart1 parse(List<String> data) {
            var seedsLine = data.get(0);
            var seeds = Seeds.parse(seedsLine);

            var mapsData = data.subList(2, data.size()); // Skip the first two lines ("seeds:"
            // and blank line)
            var maps = new ArrayList<Map>();
            var currentMapData = new ArrayList<String>();
            var currentMapName = "";

            for (var line : mapsData) {
                if (line.isBlank()) {
                    maps.add(Map.parseMap(currentMapName, currentMapData));
                    currentMapData.clear();
                } else if (line.endsWith("map:")) {
                    currentMapName = line.replace(" map:", "");
                } else {
                    currentMapData.add(line);
                }
            }
            // Don't forget to parse the last map
            if (!currentMapData.isEmpty()) {
                maps.add(Map.parseMap(currentMapName, currentMapData));
            }

            return new Day5InputPart1(seeds, new Almanac(maps));
        }
    }

    long part1(List<String> data) {
        var input = Day5InputPart1.parse(data);
        return input.seeds().values().stream()
                .mapToLong(input.almanac()::minLocation)
                .min()
                .orElseThrow();
    }

    record Coverage(List<Range> covered, List<Range> uncovered) {
    }

    record Range(long start, long length) {
    }

    record SeedRanges(List<Range> ranges) {
        static SeedRanges parse(String line) {
            var numbers = line.replace("seeds: ", "").split(" ");
            var ranges = new ArrayList<Range>();
            for (int i = 0; i < numbers.length; i += 2) {
                var start = Long.parseLong(numbers[i]);
                var length = Long.parseLong(numbers[i + 1]);
                ranges.add(new Range(start, length));
            }
            return new SeedRanges(ranges);
        }
    }

    record Day5InputPart2(SeedRanges seeds, Almanac almanac) {
        static Day5InputPart2 parse(List<String> data) {
            var seedsLine = data.get(0);
            var seeds = SeedRanges.parse(seedsLine);

            var mapsData = data.subList(2, data.size()); // Skip the first two lines ("seeds:"
            // and blank line)
            var maps = new ArrayList<Map>();
            var currentMapData = new ArrayList<String>();
            var currentMapName = "";

            for (var line : mapsData) {
                if (line.isBlank()) {
                    maps.add(Map.parseMap(currentMapName, currentMapData));
                    currentMapData.clear();
                } else if (line.endsWith("map:")) {
                    currentMapName = line.replace(" map:", "");
                } else {
                    currentMapData.add(line);
                }
            }
            // Don't forget to parse the last map
            if (!currentMapData.isEmpty()) {
                maps.add(Map.parseMap(currentMapName, currentMapData));
            }

            return new Day5InputPart2(seeds, new Almanac(maps));
        }
    }

    long part2(List<String> data) {
        var input = Day5InputPart2.parse(data);
        return input.seeds().ranges().stream()
                .mapToLong(input.almanac()::minLocation)
                .min()
                .orElseThrow();
    }

    public static void main(String[] args) {
        var day5 = new Day5();
        var data = IO.getResourceAsList("day5.txt");
        var part1 = day5.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day5.part2(data);
        System.out.println("part2 = " + part2);
    }
}
