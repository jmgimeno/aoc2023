package aoc2023.day14;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.HashMap;
import java.util.List;

public class Day14 {

    static class Platform extends CharGrid {
        Platform(List<String> data) {
            super(data, false);
        }

        void tiltNorth() {
            for (int x = 0; x < width; x++)
                for (int y = 1; y < height; y++) {
                    char c = points[y][x];
                    if (c == 'O') {
                        for (int yy = y - 1; yy >= 0; yy--) {
                            char cc = points[yy][x];
                            if (cc == '.') {
                                points[yy][x] = 'O';
                                points[yy + 1][x] = '.';
                            } else break;
                        }
                    }
                }
        }

        void tiltWest() {
            for (int y = 0; y < height; y++)
                for (int x = 1; x < width; x++) {
                    char c = points[y][x];
                    if (c == 'O') {
                        for (int xx = x - 1; xx >= 0; xx--) {
                            char cc = points[y][xx];
                            if (cc == '.') {
                                points[y][xx] = 'O';
                                points[y][xx + 1] = '.';
                            } else break;
                        }
                    }
                }
        }

        void tiltSouth() {
            for (int x = 0; x < width; x++)
                for (int y = height - 2; y >= 0; y--) {
                    char c = points[y][x];
                    if (c == 'O') {
                        for (int yy = y + 1; yy < height; yy++) {
                            char cc = points[yy][x];
                            if (cc == '.') {
                                points[yy][x] = 'O';
                                points[yy - 1][x] = '.';
                            } else break;
                        }
                    }
                }
        }

        void tiltEast() {
            for (int y = 0; y < height; y++)
                for (int x = width - 2; x >= 0; x--) {
                    char c = points[y][x];
                    if (c == 'O') {
                        for (int xx = x + 1; xx < width; xx++) {
                            char cc = points[y][xx];
                            if (cc == '.') {
                                points[y][xx] = 'O';
                                points[y][xx - 1] = '.';
                            } else break;
                        }
                    }
                }
        }

        void cycle() {
            tiltNorth();
            tiltWest();
            tiltSouth();
            tiltEast();
        }

        int totalLoadNorthBeam() {
            int total = 0;
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    if (points[y][x] == 'O')
                        total += height - y;
            return total;
        }
    }

    int part1(List<String> data) {
        var platform = new Platform(data);
        platform.tiltNorth();
        return platform.totalLoadNorthBeam();
    }

    int part2(List<String> data) {
        var platform = new Platform(data);
        int cycles = 1_000_000_000;
        var visited = new HashMap<String, Integer>();
        visited.put(platform.toString(), 0);
        boolean repeated = false;
        for (int i = 1; i <= cycles; i++) {
            platform.cycle();
            if (repeated) continue;
            if (visited.containsKey(platform.toString())) {
                repeated = true;
                var first = visited.get(platform.toString());
                var period = i - first;
                var restPeriods = (cycles - i) / period;
                // we position at the end of the last full cycle
                i += restPeriods * period;
                // i = cycles - (cycles - i) % period;
            }
            visited.put(platform.toString(), i);
        }
        return platform.totalLoadNorthBeam();
    }

    public static void main(String[] args) {
        var day14 = new Day14();
        var data = IO.getResourceAsList("day14.txt");
        var part1 = day14.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day14.part2(data);
        System.out.println("part2 = " + part2);
    }
}
