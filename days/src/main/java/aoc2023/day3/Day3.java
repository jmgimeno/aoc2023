package aoc2023.day3;

import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day3 {

    static boolean isSymbol(char c) {
        return !Character.isDigit(c) && c != '.';
    }

    static boolean isGear(char c) {
        return c == '*';
    }

    record Position(int x, int y) {
    }

    record PartNumber(int number, int row, int colStart, int colEnd) {
        public boolean touches(Position position) {
            return row - 1 <= position.y() && position.y() <= row + 1
                    && colStart - 1 <= position.x() && position.x() <= colEnd + 1;
        }
    }

    static class Schematic extends aoc2023.utils.CharGrid {

        Schematic(List<String> data) {
            super(data);
        }

        List<PartNumber> partNumbersInLine(int y) {
            List<PartNumber> partNumbers = new ArrayList<>();
            StringBuilder currentPart = new StringBuilder();
            int start = -1;

            for (int x = 1; x < width + 1; x++) {
                char currentChar = points[y][x];

                if (Character.isDigit(currentChar)) {
                    currentPart.append(currentChar);
                    if (start == -1) {
                        start = x;
                    }
                } else {
                    if (!currentPart.isEmpty()) {
                        partNumbers.add(new PartNumber(Integer.parseInt(currentPart.toString()), y, start, x - 1));
                        currentPart = new StringBuilder();
                        start = -1;
                    }
                }
            }

            if (!currentPart.isEmpty()) {
                partNumbers.add(new PartNumber(Integer.parseInt(currentPart.toString()), y, start, width - 1));
            }

            return partNumbers;
        }

        boolean surroundedBySymbol(PartNumber partNumber) {

            int row = partNumber.row();
            int start = partNumber.colStart();
            int end = partNumber.colEnd();

            // check the characters above
            for (int x = start; x <= end; x++) {
                if (isSymbol(points[row - 1][x])) {
                    return true;
                }
            }

            // check the characters below
            for (int x = start; x <= end; x++) {
                if (isSymbol(points[row + 1][x])) {
                    return true;
                }
            }

            // check the characters to the left
            for (int y = row - 1; y <= row + 1; y++) {
                if (isSymbol(points[y][start - 1])) {
                    return true;
                }
            }

            // check the characters to the right
            for (int y = row - 1; y <= row + 1; y++) {
                if (isSymbol(points[y][end + 1])) {
                    return true;
                }
            }

            return false;
        }

        List<Integer> allSurroundedBySymbols() {
            return IntStream.rangeClosed(1, height)
                    .mapToObj(this::partNumbersInLine)
                    .flatMap(List::stream)
                    .filter(this::surroundedBySymbol)
                    .map(PartNumber::number)
                    .toList();
        }

        int sumPartNumbers() {
            return IntStream.rangeClosed(1, height)
                    .mapToObj(this::partNumbersInLine)
                    .flatMap(List::stream)
                    .filter(this::surroundedBySymbol)
                    .mapToInt(PartNumber::number)
                    .sum();
        }

        List<Position> gearPositions() {
            return IntStream.rangeClosed(1, height).boxed()
                    .flatMap(y -> IntStream.rangeClosed(1, width)
                            .filter(x -> isGear(points[y][x]))
                            .mapToObj(x -> new Position(x, y))).toList();
        }

        int gearFactor(Position gear) {
            assert isGear(points[gear.y()][gear.x()]);
            var y = gear.y();
            var candidates = IntStream.rangeClosed(y - 1, y + 1)
                    .boxed()
                    .flatMap(row -> partNumbersInLine(row).stream())
                    .filter(p -> p.touches(gear))
                    .toList();
            if (candidates.size() != 2) {
                return 0;
            }
            return candidates.get(0).number() * candidates.get(1).number();
        }

    }

    int part1(List<String> data) {
        var schematic = new Schematic(data);
        return schematic.sumPartNumbers();
    }

    int part2(List<String> data) {
        var schematic = new Schematic(data);
        return schematic.gearPositions().stream()
                .mapToInt(schematic::gearFactor)
                .sum();
    }

    public static void main(String[] args) {
        var day3 = new Day3();
        var data = IO.getResourceAsList("day3.txt");
        var part1 = day3.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day3.part2(data);
        System.out.println("part2 = " + part2);
    }
}
