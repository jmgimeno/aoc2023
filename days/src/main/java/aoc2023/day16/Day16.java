package aoc2023.day16;

import aoc2023.utils.CharGrid;
import aoc2023.utils.IO;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;

public class Day16 {

    /*
        --- Day 16: The Floor Will Be Lava ---
    With the beam of light completely focused somewhere, the reindeer leads you deeper still into
    the Lava Production Facility. At some point, you realize that the steel facility walls have been
    replaced with cave, and the doorways are just cave, and the floor is cave, and you're pretty
    sure this is actually just a giant cave.

    Finally, as you approach what must be the heart of the mountain, you see a bright light in a
    cavern up ahead. There, you discover that the beam of light you so carefully focused is emerging
    from the cavern wall closest to the facility and pouring all of its energy into a contraption on
    the opposite side.

    Upon closer inspection, the contraption appears to be a flat, two-dimensional square grid
    containing empty space (.), mirrors (/ and \), and splitters (| and -).

    The contraption is aligned so that most of the beam bounces around the grid, but each tile on
    the grid converts some of the beam's light into heat to melt the rock in the cavern.

    You note the layout of the contraption (your puzzle input). For example:

    .|...\....
    |.-.\.....
    .....|-...
    ........|.
    ..........
    .........\
    ..../.\\..
    .-.-/..|..
    .|....-|.\
    ..//.|....
    The beam enters in the top-left corner from the left and heading to the right. Then, its
    behavior depends on what it encounters as it moves:

    If the beam encounters empty space (.), it continues in the same direction. If the beam
    encounters a mirror (/ or \), the beam is reflected 90 degrees depending on the angle of the
    mirror. For instance, a rightward-moving beam that encounters a / mirror would continue upward
    in the mirror's column, while a rightward-moving beam that encounters a \ mirror would continue
    downward from the mirror's column. If the beam encounters the pointy end of a splitter (| or -),
    the beam passes through the splitter as if the splitter were empty space. For instance, a
    rightward-moving beam that encounters a - splitter would continue in the same direction. If the
    beam encounters the flat side of a splitter (| or -), the beam is split into two beams going in
    each of the two directions the splitter's pointy ends are pointing. For instance, a
    rightward-moving beam that encounters a | splitter would split into two beams: one that
    continues upward from the splitter's column and one that continues downward from the splitter's
    column.
    Beams do not interact with other beams; a tile can have many beams passing through it at the
    same time. A tile is energized if that tile has at least one beam pass through it, reflect in
    it, or split in it.

    In the above example, here is how the beam of light bounces around the contraption:

    >|<<<\....
    |v-.\^....
    .v...|->>>
    .v...v^.|.
    .v...v^...
    .v...v^..\
    .v../2\\..
    <->-/vv|..
    .|<<<2-|.\
    .v//.|.v..
    Beams are only shown on empty tiles; arrows indicate the direction of the beams. If a tile
    contains beams moving in multiple directions, the number of distinct directions is shown
    instead. Here is the same diagram but instead only showing whether a tile is energized (#) or
    not (.):

    ######....
    .#...#....
    .#...#####
    .#...##...
    .#...##...
    .#...##...
    .#..####..
    ########..
    .#######..
    .#...#.#..
    Ultimately, in this example, 46 tiles become energized.

    The light isn't energizing enough tiles to produce lava; to debug the contraption, you need to
    start by analyzing the current situation. With the beam starting in the top-left heading right,
    how many tiles end up being energized?

    Your puzzle answer was 7046.

    */


    enum Direction {
        UP, LEFT, DOWN, RiGHT

    }

    record Split(Beam first, Beam second) {
    }

    record Position(int x, int y) {
    }

    record Beam(int x, int y, Direction heading) {

        Beam ahead() {
            return switch (heading) {
                case UP -> new Beam(x, y - 1, heading);
                case LEFT -> new Beam(x - 1, y, heading);
                case DOWN -> new Beam(x, y + 1, heading);
                case RiGHT -> new Beam(x + 1, y, heading);
            };
        }

        Beam slash() { // /
            return switch (heading) {
                case UP -> new Beam(x, y - 1, Direction.RiGHT);
                case LEFT -> new Beam(x - 1, y, Direction.DOWN);
                case DOWN -> new Beam(x, y + 1, Direction.LEFT);
                case RiGHT -> new Beam(x + 1, y, Direction.UP);
            };
        }

        Beam backslash() {
            return switch (heading) {
                case UP -> new Beam(x, y - 1, Direction.LEFT);
                case LEFT -> new Beam(x - 1, y, Direction.UP);
                case DOWN -> new Beam(x, y + 1, Direction.RiGHT);
                case RiGHT -> new Beam(x + 1, y, Direction.DOWN);
            };
        }

        Split split() {
            return switch (heading) {
                case UP ->
                        new Split(new Beam(x, y - 1, Direction.LEFT), new Beam(x, y - 1, Direction.RiGHT));
                case LEFT ->
                        new Split(new Beam(x - 1, y, Direction.UP), new Beam(x - 1, y, Direction.DOWN));
                case DOWN ->
                        new Split(new Beam(x, y + 1, Direction.LEFT), new Beam(x, y + 1, Direction.RiGHT));
                case RiGHT ->
                        new Split(new Beam(x + 1, y, Direction.UP), new Beam(x + 1, y, Direction.DOWN));
            };
        }

        Position next() {
            return switch (heading) {
                case UP -> new Position(x, y - 1);
                case LEFT -> new Position(x - 1, y);
                case DOWN -> new Position(x, y + 1);
                case RiGHT -> new Position(x + 1, y);
            };
        }

    }

    static class Layout extends CharGrid {

        Layout(List<String> data) {
            super(data, true, '$');
        }

        int part1() {
            var start = new Beam(0, 1, Direction.RiGHT);
            var beams = new ArrayDeque<Beam>();
            beams.addLast(start);
            var visited = new HashSet<Beam>();
            while (!beams.isEmpty()) {
                var beam = beams.removeFirst();
                if (!visited.add(beam)) continue;
                var next = beam.next();
                var on = points[next.y()][next.x()];
                switch (on) {
                    case '$' -> {
                    }
                    case '.' -> beams.addLast(beam.ahead());
                    case '/' -> beams.addLast(beam.slash());
                    case '\\' -> beams.addLast(beam.backslash());
                    case '|' -> {
                        if (beam.heading() == Direction.LEFT || beam.heading() == Direction.RiGHT) {
                            var split = beam.split();
                            beams.addLast(split.first());
                            beams.addLast(split.second());
                        } else {
                            beams.addLast(beam.ahead());
                        }
                    }
                    case '-' -> {
                        if (beam.heading() == Direction.UP || beam.heading() == Direction.DOWN) {
                            var split = beam.split();
                            beams.addLast(split.first());
                            beams.addLast(split.second());
                        } else {
                            beams.addLast(beam.ahead());
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + on);
                }
            }
            return energised(visited);
        }

        private int energised(HashSet<Beam> visited) {
            var result = new HashSet<Position>();
            for (var beam : visited) {
                if (points[beam.y()][beam.x()] != '$')
                    result.add(new Position(beam.x(), beam.y()));
            }
            return result.size();
        }

    }

    int part1(List<String> data) {
        var layout = new Layout(data);
        return layout.part1();
    }

    /*
--- Part Two ---
As you try to work out what might be wrong, the reindeer tugs on your shirt and leads you to a nearby control panel.
There, a collection of buttons lets you align the contraption so that the beam enters from any edge tile and heading
away from that edge. (You can choose either of two directions for the beam if it starts on a corner; for instance, if the
beam starts in the bottom-right corner, it can start heading either left or upward.)

So, the beam could start on any tile in the top row (heading downward), any tile in the bottom row (heading upward),
 any tile in the leftmost column (heading right), or any tile in the rightmost column (heading left). To produce lava, you
 need to find the configuration that energizes as many tiles as possible.

In the above example, this can be achieved by starting the beam in the fourth tile from the left in the top row:

.|<2<\....
|v-v\^....
.v.v.|->>>
.v.v.v^.|.
.v.v.v^...
.v.v.v^..\
.v.v/2\\..
<-2-/vv|..
.|<<<2-|.\
.v//.|.v..
Using this configuration, 51 tiles are energized:

.#####....
.#.#.#....
.#.#.#####
.#.#.##...
.#.#.##...
.#.#.##...
.#.#####..
########..
.#######..
.#...#.#..

Find the initial beam configuration that energizes the largest number of tiles; how many tiles are energized in
that configuration?


     */

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day16 = new Day16();
        var data = IO.getResourceAsList("day16.txt");
        var part1 = day16.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day16.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
