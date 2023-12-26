package aoc2023.day11;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day11 {

    record Galaxy(int x, int y) {
        int distance(Galaxy other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }
    }

    static class Image extends CharGrid {
        List<Galaxy> galaxies;
        Set<Integer> emptyRows;
        Set<Integer> emptyCols;

        public Image(List<String> data) {
            super(data);
            findGalaxies();
        }

        private void findGalaxies() {
            galaxies = new ArrayList<>();
            emptyRows = new HashSet<>(IntStream.rangeClosed(1, width).boxed().toList());
            emptyCols = new HashSet<>(IntStream.rangeClosed(1, height).boxed().toList());
            for (int y = 1; y < height + 1; y++)
                for (int x = 1; x < width + 1; x++)
                    if (points[y][x] == '#') {
                        galaxies.add(new Galaxy(x, y));
                        emptyCols.remove(x);
                        emptyRows.remove(y);
                    }
        }

        public long distance(Galaxy g1, Galaxy g2, int factor) {
            var min = new Galaxy(Math.min(g1.x, g2.x), Math.min(g1.y, g2.y));
            var max = new Galaxy(Math.max(g1.x, g2.x), Math.max(g1.y, g2.y));
            var expandedCols = expandedBetween(min.x, max.x, emptyCols);
            var expandedRows = expandedBetween(min.y, max.y, emptyRows);
            var expandedMax = new Galaxy(
                    max.x + (factor - 1) * expandedCols,
                    max.y + (factor - 1) * expandedRows);
            return min.distance(expandedMax);
        }

        private int expandedBetween(int min, int max, Set<Integer> empty) {
            return (int) IntStream.range(min + 1, max).filter(empty::contains).count();
        }

        public long allDistances(int factor) {
            long total = 0;
            for (int i = 0; i < galaxies.size(); i++) {
                for (int j = i + 1; j < galaxies.size(); j++) {
                    total += distance(galaxies.get(i), galaxies.get(j), factor);
                }
            }
            return total;
        }
    }

    long part1(List<String> data) {
        return new Image(data).allDistances(2);
    }

    long part2(List<String> data) {
        return new Image(data).allDistances(1_000_000);
    }

    public static void main(String[] args) {
        var day11 = new Day11();
        var data = IO.getResourceAsList("day11.txt");
        var part1 = day11.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day11.part2(data);
        System.out.println("part2 = " + part2);
    }
}
