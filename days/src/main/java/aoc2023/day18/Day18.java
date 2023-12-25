package aoc2023.day18;

import aoc2023.utils.IO;

import java.util.List;
import java.util.function.UnaryOperator;

public class Day18 {

    /*
        --- Day 18: Lavaduct Lagoon ---
    Thanks to your efforts, the machine parts factory is one of the first factories up and running
    since the lavafall came back. However, to catch up with the large backlog of parts requests, the
    factory will also need a large supply of lava for a while; the Elves have already started
    creating a large lagoon nearby for this purpose.

    However, they aren't sure the lagoon will be big enough; they've asked you to take a look at the
    dig plan (your puzzle input). For example:

    R 6 (#70c710)
    D 5 (#0dc571)
    L 2 (#5713f0)
    D 2 (#d2c081)
    R 2 (#59c680)
    D 2 (#411b91)
    L 5 (#8ceee2)
    U 2 (#caa173)
    L 1 (#1b58a2)
    U 2 (#caa171)
    R 2 (#7807d2)
    U 3 (#a77fa3)
    L 2 (#015232)
    U 2 (#7a21e3)
    The digger starts in a 1 meter cube hole in the ground. They then dig the specified number of
    meters up (U), down (D), left (L), or right (R), clearing full 1 meter cubes as they go. The
    directions are given as seen from above, so if "up" were north, then "right" would be east, and
    so on. Each trench is also listed with the color that the edge of the trench should be painted
    as an RGB hexadecimal color code.

    When viewed from above, the above example dig plan would result in the following loop of trench
    (#) having been dug out from otherwise ground-level terrain (.):

    #######
    #.....#
    ###...#
    ..#...#
    ..#...#
    ###.###
    #...#..
    ##..###
    .#....#
    .######
    At this point, the trench could contain 38 cubic meters of lava. However, this is just the edge
    of the lagoon; the next step is to dig out the interior so that it is one meter deep as well:

    #######
    #######
    #######
    ..#####
    ..#####
    #######
    #####..
    #######
    .######
    .######
    Now, the lagoon can contain a much more respectable 62 cubic meters of lava. While the interior
    is dug out, the edges are also painted according to the color codes in the dig plan.

    The Elves are concerned the lagoon won't be large enough; if they follow their dig plan, how
    many cubic meters of lava could it hold?

    Your puzzle answer was 47527.

    */

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

    /*
    --- Part Two ---
    The Elves were right to be concerned; the planned lagoon would be much too small.

    After a few minutes, someone realizes what happened; someone swapped the color and
    instruction parameters when producing the dig plan. They don't have time to fix the bug;
    one of them asks if you can extract the correct instructions from the hexadecimal codes.

    Each hexadecimal code is six hexadecimal digits long. The first five hexadecimal digits encode
    the distance in meters as a five-digit hexadecimal number. The last hexadecimal digit encodes
    the direction to dig: 0 means R, 1 means D, 2 means L, and 3 means U.

    So, in the above example, the hexadecimal codes can be converted into the true instructions:

    #70c710 = R 461937
    #0dc571 = D 56407
    #5713f0 = R 356671
    #d2c081 = D 863240
    #59c680 = R 367720
    #411b91 = D 266681
    #8ceee2 = L 577262
    #caa173 = U 829975
    #1b58a2 = L 112010
    #caa171 = D 829975
    #7807d2 = L 491645
    #a77fa3 = U 686074
    #015232 = L 5411
    #7a21e3 = U 500254

    Digging out this loop and its interior produces a lagoon that can hold an impressive 952408144115
    cubic meters of lava.

    Convert the hexadecimal color codes into the correct instructions; if the Elves follow this new dig
    plan, how many cubic meters of lava could the lagoon hold?

    */

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
