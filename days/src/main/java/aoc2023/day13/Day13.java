package aoc2023.day13;

import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class Day13 {

    record Grid(List<String> data) {

        int part1() {
            return verticalReflections() + 100 * transpose().verticalReflections();
        }

        int verticalReflections() {
            var length = data.getFirst().length();
            var mirrors = new HashSet<>(IntStream.range(1, length).boxed().toList());
            data.forEach(row -> mirrors.removeIf(mirror -> !reflects(row, mirror)));
            return mirrors.stream().reduce(0, Integer::sum);
        }

        private boolean reflects(String row, int mirror) {
            var length = row.length();
            var left = row.substring(0, mirror);
            var right = reversed(row).substring(0, length - mirror);
            return right.endsWith(left) || left.endsWith(right);
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
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day13 = new Day13();
        var data = IO.getResourceAsList("day13.txt");
        var part1 = day13.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day13.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
