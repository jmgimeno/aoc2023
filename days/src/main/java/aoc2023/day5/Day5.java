package aoc2023.day5;

import aoc2023.utils.IO;

import java.util.*;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;


public class Day5 {

    /*
        --- Day 5: If You Give A Seed A Fertilizer ---
    You take the boat and find the gardener right where you were told he would be: managing a giant
    "garden" that looks more to you like a farm.

    "A water source? Island Island is the water source!" You point out that Snow Island isn't
    receiving any water.

    "Oh, we had to stop the water because we ran out of sand to filter it with! Can't make snow with
    dirty water. Don't worry, I'm sure we'll get more sand soon; we only turned off the water a few
    days... weeks... oh no." His face sinks into a look of horrified realization.

    "I've been so busy making sure everyone here has food that I completely forgot to check why we
    stopped getting more sand! There's a ferry leaving soon that is headed over in that direction -
    it's much faster than your boat. Could you please go check it out?"

    You barely have time to agree to this request when he brings up another. "While you wait for the
    ferry, maybe you can help us with our food production problem. The latest Island Island Almanac
    just arrived and we're having trouble making sense of it."

    The almanac (your puzzle input) lists all of the seeds that need to be planted. It also lists
    what type of soil to use with each kind of seed, what type of fertilizer to use with each kind
    of soil, what type of water to use with each kind of fertilizer, and so on. Every type of seed,
    soil, fertilizer and so on is identified with a number, but numbers are reused by each category
    - that is, soil 123 and fertilizer 123 aren't necessarily related to each other.

    For example:

    seeds: 79 14 55 13

    seed-to-soil map:
    50 98 2
    52 50 48

    soil-to-fertilizer map:
    0 15 37
    37 52 2
    39 0 15

    fertilizer-to-water map:
    49 53 8
    0 11 42
    42 0 7
    57 7 4

    water-to-light map:
    88 18 7
    18 25 70

    light-to-temperature map:
    45 77 23
    81 45 19
    68 64 13

    temperature-to-humidity map:
    0 69 1
    1 0 69

    humidity-to-location map:
    60 56 37
    56 93 4
    The almanac starts by listing which seeds need to be planted: seeds 79, 14, 55, and 13.

    The rest of the almanac contains a list of maps which describe how to convert numbers from a
    source category into numbers in a destination category. That is, the section that starts with
    seed-to-soil map: describes how to convert a seed number (the source) to a soil number (the
    destination). This lets the gardener and his team know which soil to use with which seeds, which
    water to use with which fertilizer, and so on.

    Rather than list every source number and its corresponding destination number one by one, the
    maps describe entire segmentMaps of numbers that can be converted. Each line within a map
    contains
    three numbers: the destination range start, the source range start, and the range length.

    Consider again the example seed-to-soil map:

    50 98 2
    52 50 48
    The first line has a destination range start of 50, a source range start of 98, and a range
    length of 2. This line means that the source range starts at 98 and contains two values: 98 and
    99. The destination range is the same length, but it starts at 50, so its two values are 50 and
    51. With this information, you know that seed number 98 corresponds to soil number 50 and that
    seed number 99 corresponds to soil number 51.

    The second line means that the source range starts at 50 and contains 48 values: 50, 51, ...,
    96, 97. This corresponds to a destination range starting at 52 and also containing 48 values:
    52, 53, ..., 98, 99. So, seed number 53 corresponds to soil number 55.

    Any source numbers that aren't mapped correspond to the same destination number. So, seed number
    10 corresponds to soil number 10.

    So, the entire list of seed numbers and their corresponding soil numbers looks like this:

    seed soil
    0 0
    1 1
    ... ...
    48 48
    49 49
    50 52
    51 53
    ... ...
    96 98
    97 99
    98 50
    99 51
    With this map, you can look up the soil number required for each initial seed number:

    Seed number 79 corresponds to soil number 81. Seed number 14 corresponds to soil number 14. Seed
    number 55 corresponds to soil number 57. Seed number 13 corresponds to soil number 13.
    The gardener and his team want to get started as soon as possible, so they'd like to know the
    closest location that needs a seed. Using these maps, find the lowest location number that
    corresponds to any of the initial seeds. To do this, you'll need to convert each seed number
    through other categories until you can find its corresponding location number. In this example,
    the corresponding types are:

    Seed 79, soil 81, fertilizer 81, water 81, light 74, temperature 78, humidity 78, location 82.
    Seed 14, soil 14, fertilizer 53, water 49, light 42, temperature 42, humidity 43, location 43.
    Seed 55, soil 57, fertilizer 57, water 53, light 46, temperature 82, humidity 82, location 86.
    Seed 13, soil 13, fertilizer 52, water 41, light 34, temperature 34, humidity 35, location 35.

    So, the lowest location number in this example is 35.

    What is the lowest location number that corresponds to any of the initial seed numbers?

    Your puzzle answer was 825516882.
    */

    record SegmentMap(long destinationStart, long sourceStart, long length) {

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
                var left = new Range(input.start, sourceStart - input.start);
                var covered = new Range(sourceStart, length);
                var right = new Range(sourceEnd, inputEnd - sourceEnd);
                return new Coverage(List.of(transformCovered(covered)), List.of(left, right));
            } else if (input.start <= sourceStart) {
                // Segment overlaps on the right side of the input
                var left = new Range(input.start, sourceStart - input.start);
                var covered = new Range(sourceStart, inputEnd - sourceStart);
                return new Coverage(List.of(transformCovered(covered)), List.of(left));
            } else {
                // Segment overlaps on the left side of the input
                var covered = new Range(input.start, sourceEnd - input.start);
                var right = new Range(sourceEnd, inputEnd - sourceEnd);
                return new Coverage(List.of(transformCovered(covered)), List.of(right));
            }
        }

        public Range transformCovered(Range input) {
            var delta = destinationStart - sourceStart;
            return new Range(input.start + delta, input.length);
        }
    }

    record Map(String name, List<SegmentMap> segmentMaps) {

        public long transform(long value) {
            return segmentMaps.stream()
                    .filter(r -> r.isInRange(value))
                    .mapToLong(r -> r.transformCovered(value))
                    .findFirst()
                    .orElse(value);
        }

        public List<Range> transform(List<Range> input) {
            var ranges = new ArrayList<>(input);
            var result = new ArrayList<Range>();
            for (var segment : segmentMaps) {
                var nextRanges = new ArrayList<Range>();
                for (var range : ranges) {
                    var coverage = segment.transform(range);
                    result.addAll(coverage.covered());
                    nextRanges.addAll(coverage.uncovered());
                }
                ranges = nextRanges;
            }
            result.addAll(ranges);
            return result;
        }

        static Map parseMap(String name, List<String> data) {
            var ranges = data.stream()
                    .map(line -> {
                        var parts = Arrays.stream(line.split(" "))
                                .mapToLong(Long::parseLong)
                                .toArray();
                        return new SegmentMap(parts[0], parts[1], parts[2]);
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

    /*
    --- Part Two ---
    Everyone will starve if you only plant such a small number of seeds. Re-reading the almanac,
    it looks like the seeds: line actually describes segmentMaps of seed numbers.

    The values on the initial seeds: line come in pairs. Within each pair, the first value is the
    start of the range and the second value is the length of the range. So, in the first line of
    the example above:

    seeds: 79 14 55 13

    This line describes two segmentMaps of seed numbers to be planted in the garden. The first range
    starts with seed number 79 and contains 14 values: 79, 80, ..., 91, 92. The second range
    starts with seed number 55 and contains 13 values: 55, 56, ..., 66, 67.

    Now, rather than considering four seed numbers, you need to consider a total of 27 seed numbers.

    In the above example, the lowest location number can be obtained from seed number 82, which
    corresponds to soil 84, fertilizer 84, water 84, light 77, temperature 45, humidity 46, and
    location 46. So, the lowest location number is 46.

    Consider all of the initial seed numbers listed in the segmentMaps on the first line of the
    almanac. What is the lowest location number that corresponds to any of the initial seed numbers?
     */

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
