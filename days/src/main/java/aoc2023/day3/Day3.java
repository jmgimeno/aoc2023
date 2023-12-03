package aoc2023.day3;

import aoc2023.utils.IO;
import org.checkerframework.common.value.qual.IntRange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day3 {

    /*
        --- Day 3: Gear Ratios ---
    You and the Elf eventually reach a gondola lift station; he says the gondola lift will take you
    up to the water source, but this is as far as he can bring you. You go inside.

    It doesn't take long to find the gondolas, but there seems to be a problem: they're not moving.

    "Aaah!"

    You turn around to see a slightly-greasy Elf with a wrench and a look of surprise. "Sorry, I
    wasn't expecting anyone! The gondola lift isn't working right now; it'll still be a while before
    I can fix it." You offer to help.

    The engineer explains that an engine part seems to be missing from the engine, but nobody can
    figure out which one. If you can add up all the part numbers in the engine schematic, it should
    be easy to work out which part is missing.

    The engine schematic (your puzzle input) consists of a visual representation of the engine.
    There are lots of numbers and symbols you don't really understand, but apparently any number
    adjacent to a symbol, even diagonally, is a "part number" and should be included in your sum.
    (Periods (.) do not count as a symbol.)

    Here is an example engine schematic:

    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...$.*....
    .664.598..

    In this schematic, two numbers are not part numbers because they are not adjacent to a symbol:
    114 (top right) and 58 (middle right). Every other number is adjacent to a symbol and so is a
    part number; their sum is 4361.

    Of course, the actual engine schematic is much larger. What is the sum of all of the part
    numbers in the engine schematic?

    Your puzzle answer was 553825.
    */

    record Position(int x, int y) {
        boolean isAjacent(Position other) {
            return Math.abs(x - other.x) <= 1 && Math.abs(y - other.y) <= 1;
        }
    }

    record PartNumber(int number, Position start, Position end) {
        public boolean touches(Position position) {
            // return truew if the position of the gear touches
            // any of the positions occupied by the part number
            return IntStream.rangeClosed(start.x(), end.x())
                    .anyMatch(x -> position.isAjacent(new Position(x, start.y())));
        }
    }

    static class Schematic {

        final int height;
        final int width;
        final char[][] points;

        Schematic(List<String> data) {
            height = data.size();
            width = data.get(0).length();
            points = new char[height + 2][width + 2];
            // fill the matrix of points with the contents of the data
            // and surround it by '.' to avoid out of bounds checks
            fillPoints(data);
        }

        private void fillPoints(List<String> data) {
            for (int y = 0; y < height + 2; y++) {
                for (int x = 0; x < width + 2; x++) {
                    if (x == 0 || y == 0 || x == width + 1 || y == height + 1) {
                        points[y][x] = '.';
                    } else {
                        points[y][x] = data.get(y - 1).charAt(x - 1);
                    }
                }
            }
        }

        List<PartNumber> getPartNumbersInLine(int y) {
            // Using the char matrix, return a list of part numbers in the given line
            // a part number is a sequence of digits that are adjacent to a symbol
            // a symbol is a character that is not a digit or a period
            // you are given the y coordinate of the line

            // 1. Initialize an empty list to store the part numbers.
            // 2. Iterate over the characters in the line at the given y coordinate.
            // 3. If a character is a digit, append it to the current part number string.
            // 4. If a character is a symbol (not a digit or a period), check the surrounding characters. If any of them are digits, add the current part number string to the list and start a new part number string.
            // 5. At the end of the line, if the current part number string is not empty, add it to the list.
            // 6. Return the list of part numbers.

            // The first solution didn't took into account the fact that a number ennds when a symbol is found (the problem was with my prompt)

            List<PartNumber> partNumbers = new ArrayList<>();
            StringBuilder currentPartNumber = new StringBuilder();
            Position start = null;

            for (int x = 1; x < width + 1; x++) {
                char currentChar = points[y][x];

                if (Character.isDigit(currentChar)) {
                    currentPartNumber.append(currentChar);
                    if (start == null) {
                        start = new Position(x, y);
                    }
                } else {
                    if (currentPartNumber.length() > 0) {
                        partNumbers.add(new PartNumber(Integer.parseInt(currentPartNumber.toString()), start, new Position(x - 1, y)));
                        currentPartNumber = new StringBuilder();
                        start = null;
                    }
                }
            }

            if (currentPartNumber.length() > 0) {
                partNumbers.add(new PartNumber(Integer.parseInt(currentPartNumber.toString()), start, new Position(width - 1, y)));
            }

            return partNumbers;
        }

        static boolean isSymbol(char c) {
            return !Character.isDigit(c) && c != '.';
        }

        boolean surroundedBySymbol(PartNumber partNumber) {
            // Given a part number, return true if it is surrounded by symbols, false otherwise.
            // A symbol is a character that is not a digit or a period.
            // The part number is represented by a start and end position.
            // The start and end positions are inclusive.
            // The part number is surrounded by symbols if any of the characters around it are symbols.
            // The characters around it are the characters above, below, to the left, and to the right of the start and end positions.

            Position start = partNumber.start();
            Position end = partNumber.end();

            // check the characters above
            for (int x = start.x(); x <= end.x(); x++) {
                if (isSymbol(points[start.y() - 1][x])) {
                    return true;
                }
            }

            // check the characters below
            for (int x = start.x(); x <= end.x(); x++) {
                if (isSymbol(points[end.y() + 1][x])) {
                    return true;
                }
            }

            // check the characters to the left
            for (int y = start.y() - 1; y <= start.y() + 1; y++) {
                if (isSymbol(points[y][start.x() - 1])) {
                    return true;
                }
            }

            // check the characters to the right
            for (int y = start.y() - 1; y <= start.y() + 1; y++) {
                if (isSymbol(points[y][end.x() + 1])) {
                    return true;
                }
            }

            return false;
        }

        List<Integer> allSurroundedBySymbols() {
            return IntStream.rangeClosed(1, height)
                    .mapToObj(this::getPartNumbersInLine)
                    .flatMap(List::stream)
                    .filter(this::surroundedBySymbol)
                    .map(PartNumber::number)
                    .toList();
        }

        int sumPartNumbers() {
            return IntStream.rangeClosed(1, height)
                    .mapToObj(this::getPartNumbersInLine)
                    .flatMap(List::stream)
                    .filter(this::surroundedBySymbol)
                    .mapToInt(PartNumber::number)
                    .sum();
        }

        List<Gear> getALlGears() {
            // 1. Initialize an empty list to store the gears.
            // 2. Iterate over the characters in the schematic.
            // 3. If a character is a *, add it to the list.
            // 4. Return the list of gears.

            List<Gear> gears = new ArrayList<>();

            for (int y = 1; y < height + 1; y++) {
                for (int x = 1; x < width + 1; x++) {
                    if (points[y][x] == '*') {
                        gears.add(new Gear(new Position(x, y)));
                    }
                }
            }

            return gears;
        }

        int adjacentProduct(Gear gear) {
            var y = gear.position().y();
            var candidates = IntStream.rangeClosed(y - 1, y + 1)
                    .boxed()
                    .flatMap(row -> getPartNumbersInLine(row).stream())
                    .filter(p -> p.touches(gear.position))
                    .toList();
            if (candidates.size() != 2) {
                return 0;
            }
            return candidates.get(0).number() * candidates.get(1).number();
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            for (int y = 0; y < height + 2; y++) {
                for (int x = 0; x < width + 2; x++) {
                    sb.append(points[y][x]);
                }
                sb.append("\n");
            }
            return sb.toString();
        }

    }

    int part1(List<String> data) {
        var schematic = new Schematic(data);
        return schematic.sumPartNumbers();
    }

    /*
    --- Part Two ---
    The engineer finds the missing part and installs it in the engine! As the engine springs to
    life, you jump in the closest gondola, finally ready to ascend to the water source.

    You don't seem to be going very fast, though. Maybe something is still wrong? Fortunately, the
    gondola has a phone labeled "help", so you pick it up and the engineer answers.

    Before you can explain the situation, she suggests that you look out the window. There stands
    the engineer, holding a phone in one hand and waving with the other. You're going so slowly
    that you haven't even left the station. You exit the gondola.

    The missing part wasn't the only issue - one of the gears in the engine is wrong. A gear is
    any * symbol that is adjacent to exactly two part numbers. Its gear ratio is the result of
    multiplying those two numbers together.

    This time, you need to find the gear ratio of every gear and add them all up so that the
    engineer can figure out which gear needs to be replaced.

    Consider the same engine schematic again:

    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...$.*....
    .664.598..

    In this schematic, there are two gears. The first is in the top left; it has part numbers 467
    and 35, so its gear ratio is 16345. The second gear is in the lower right; its gear ratio is
    451490. (The * adjacent to 617 is not a gear because it is only adjacent to one part number.)
    Adding up all of the gear ratios produces 467835.

    What is the sum of all of the gear ratios in your engine schematic?

    Your puzzle answer was 93994191.
     */

    record Gear(Position position) {
    }


    int part2(List<String> data) {
        var schematic = new Schematic(data);
        return schematic.getALlGears().stream()
                .mapToInt(schematic::adjacentProduct)
                .sum();
    }

    public static void main(String[] args) {
        var day3 = new Day3();
        var data = IO.getResourceAsImmutableList("day3.txt");
        var part1 = day3.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day3.part2(data);
        System.out.println("part2 = " + part2);
    }
}
