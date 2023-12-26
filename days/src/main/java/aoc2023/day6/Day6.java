package aoc2023.day6;

import java.util.List;

public class Day6 {

    final List<Race> data1 = List.of(
            new Race(60L, 601L),
            new Race(80L, 1163L),
            new Race(86L, 1559L),
            new Race(76L, 1300L));

    record Race(long maxTime, long recordDistance) {

        long distance(long pushTime) {
            return pushTime * (maxTime - pushTime);
        }

        long numRecords() {
            return maxTime - 2 * firstRecord() + 1;
        }

        private long firstRecord() {
            long left = 0L;
            long right = maxTime / 2L + 1L;
            while (left < right) {
                long mid = left + (right - left) / 2L;
                if (distance(mid) <= recordDistance) {
                    left = mid + 1L;
                } else {
                    right = mid;
                }
            }
            return left;
        }
    }

    long part1(List<Race> data) {
        return data.stream().map(Race::numRecords).reduce(1L, (a, b) -> a * b);
    }

    final Race data2 = new Race(60808676L, 601116315591300L);

    long part2(Race data) {
        return data.numRecords();
    }

    public static void main(String[] args) {
        var day6 = new Day6();
        var part1 = day6.part1(day6.data1);
        System.out.println("part1 = " + part1);
        var part2 = day6.part2(day6.data2);
        System.out.println("part2 = " + part2);
    }
}
