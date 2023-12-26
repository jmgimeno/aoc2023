package aoc2023.day18;

import aoc2023.utils.IO;

import java.util.List;
import java.util.function.UnaryOperator;

public class Day18 {

    enum Direction {

        UP('U'), DOWN('D'), LEFT('L'), RIGHT('R');
        final char code;

        Direction(char code) {
            this.code = code;
        }

        static Direction of(char code) {
            return switch (code) {
                case 'U' -> UP;
                case 'D' -> DOWN;
                case 'L' -> LEFT;
                case 'R' -> RIGHT;
                default -> throw new IllegalArgumentException("Unknown direction: " + code);
            };
        }
    }

    record Position(long x, long y) {

        Position apply(Direction direction, long distance) {
            return switch (direction) {
                case UP -> new Position(x, y - distance );
                case DOWN -> new Position(x, y + distance );
                case LEFT -> new Position(x - distance, y);
                case RIGHT -> new Position(x + distance, y);
            };
        }

        long product(Position p) {
            return x * p.y - y * p.x;
        }
    }

    record Instruction(Direction direction, long distance) implements UnaryOperator<Position> {
        static Instruction parse1(String data) {
            var parts = data.split(" ");
            var direction = Direction.of(parts[0].charAt(0));
            var distance = Long.parseLong(parts[1]);
            return new Instruction(direction, distance);
        }

        public static Instruction parse2(String data) {
            var directions = "RDLU";
            var parts = data.split(" ");
            var encodedDistance = parts[2].substring(2, 7);
            var encodedDirection = parts[2].charAt(7);
            var direction = Direction.of(directions.charAt(encodedDirection - '0'));
            var distance = Long.parseLong(encodedDistance, 16);
            return new Instruction(direction, distance);
        }

        @Override
        public Position apply(Position position) {
            return position.apply(direction, distance);
        }
    }

    record Plan(List<Instruction> instructions) {

        static Plan parse1(List<String> data) {
            return new Plan(data.stream().map(Instruction::parse1).toList());
        }

        static Plan parse2(List<String> data) {
            return new Plan(data.stream().map(Instruction::parse2).toList());
        }

        long part() {
            // Shoelace uses cartesian coordinates, problem uses integer coordinates.
            // This causes a difference between problem's area and shoelace area.
            // For example, digging from (0,0) to (0,6) gives a Cartesian length of 6,
            // but for the sake of the problem that should count as 7,
            // since both endpoints are included: #######
            // So we can calculate the perimeter, add its half, plus 1 (because off by 1).
            return area() + perimeter() / 2 + 1;
        }

        private long perimeter() {
            return instructions.stream().mapToLong(i -> i.distance).sum();
        }

        private long area() {
            long area = 0;
            var position = new Position(0, 0);
            for (var instruction : instructions) {
                var next = instruction.apply(position);
                area += position.product(next);
                position = next;
            }
            return Math.abs(area / 2);
        }
    }

    long part1(List<String> data) {
        var plan = Plan.parse1(data);
        return plan.part();
    }

    long part2(List<String> data) {
        var plan = Plan.parse2(data);
        return plan.part();
    }

    public static void main(String[] args) {
        var day18 = new Day18();
        var data = IO.getResourceAsList("day18.txt");
        var part1 = day18.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day18.part2(data);
        System.out.println("part2 = " + part2);
    }
}
