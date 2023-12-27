package aoc2023.day13;

import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class Day13 {

    record Grid(List<String> data) {

        int part1() {
            return verticalReflections(0) + 100 * transpose().verticalReflections(0);
        }

        int part2() {
            return verticalReflections(1) + 100 * transpose().verticalReflections(1);
        }

        int verticalReflections(int maxDiffs) {
            var length = data.getFirst().length();
            var mirrors = new HashSet<>(IntStream.range(1, length).boxed().toList());
            var it = mirrors.iterator();
            while (it.hasNext()) {
                var diffs = 0;
                var mirror = it.next();
                for (var row : data) {
                    diffs += diffs(row, mirror);
                    if (diffs > maxDiffs) {
                        it.remove();
                        break;
                    }
                }
                if (diffs == 0 && maxDiffs > 0)
                    // for the second part I reject the case where there are no diffs
                    it.remove();
            }
            return mirrors.stream().reduce(0, Integer::sum);
        }

        private int diffs(String row, int mirror) {
            var length = row.length();
            var left = row.substring(0, mirror);
            var right = reversed(row).substring(0, length - mirror);
            if (left.length() > right.length())
                return diffs(left, right);
            else
                return diffs(right, left);
        }

        private int diffs(String longer, String shorter) {
            var length = shorter.length();
            var startLonger = longer.length() - length;
            var diffs = 0;
            for (int i = 0; i < length; i++) {
                if (longer.charAt(startLonger + i) != shorter.charAt(i))
                    diffs++;
            }
            return diffs;
        }

        private String reversed(String string) {
            return new StringBuilder(string).reverse().toString();
        }

        Grid transpose() {
            var result = new ArrayList<String>();
            for (int i = 0; i < data.getFirst().length(); i++) {
                var builder = new StringBuilder();
                for (var row : data) {
                    builder.append(row.charAt(i));
                }
                result.add(builder.toString());
            }
            return new Grid(result);
        }
    }

    private List<Grid> parse(List<String> data) {
        var result = new ArrayList<Grid>();
        var grid = new ArrayList<String>();
        for (var line : data) {
            if (line.isEmpty()) {
                result.add(new Grid(grid));
                grid = new ArrayList<>();
            } else {
                grid.add(line);
            }
        }
        if (!grid.isEmpty())
            result.add(new Grid(grid));
        return result;
    }

    int part1(List<String> data) {
        var grids = parse(data);
        return grids.stream().mapToInt(Grid::part1).sum();
    }

    int part2(List<String> data) {
        var grids = parse(data);
        return grids.stream().mapToInt(Grid::part2).sum();
    }

    public static void main(String[] args) {
        var day13 = new Day13();
        var data = IO.getResourceAsList("day13.txt");
        var part1 = day13.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day13.part2(data);
        System.out.println("part2 = " + part2);
    }
}
