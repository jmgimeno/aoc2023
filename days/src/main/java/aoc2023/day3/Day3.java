package aoc2023.day3;

import aoc2023.utils.IO;

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
