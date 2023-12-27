package aoc2023.day22;

import aoc2023.utils.IO;

import java.util.*;

public class Day22 {

    // x axis goes from left (L) to right (R
    // y axis goes from front (F) to back (B)
    // z axis goes from down (D) to up (U

    record Point3D(int x, int y, int z) implements Comparable<Point3D> {
        Point3D {
            if (x < 0 || y < 0 || z < 0) {
                throw new IllegalArgumentException("x, y and z must be positive");
            }
        }

        static Point3D parse(String s) {
            var parts = s.split(",");
            var x = Integer.parseInt(parts[0]);
            var y = Integer.parseInt(parts[1]);
            var z = Integer.parseInt(parts[2]);
            return new Point3D(x, y, z);
        }

        @Override
        public int compareTo(Point3D o) {
            return Comparator.comparing(Point3D::z)
                    .compare(this, o);
        }
    }

    record Brick(Point3D lfd, Point3D rbu) implements Comparable<Brick> {
        Brick {
            if (lfd.x() > rbu.x() || lfd.y() > rbu.y() || lfd.z() > rbu.z()) {
                throw new IllegalArgumentException("lfd must be less than or equal to rbu");
            }
        }

        static Brick parse(String s) {
            var parts = s.split("~");
            var lfd = Point3D.parse(parts[0]);
            var rbu = Point3D.parse(parts[1]);
            return new Brick(lfd, rbu);
        }

        @Override
        public int compareTo(Brick o) {
            return lfd.compareTo(o.lfd);
        }

        public Base base() {
            return new Base(new Point2D(lfd.x(), lfd.y()), new Point2D(rbu.x(), rbu.y()));
        }

        public int height() {
            int height = rbu.z() - lfd.z() + 1;
            assert height > 0 : "height must be positive";
            return height;
        }

        boolean overlaps(Brick other) {
            return base().overlaps(other.base());
        }
    }

    record Point2D(int x, int y) {
        Point2D {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("x and y must be positive");
            }
        }
    }

    record Base(Point2D lf, Point2D rb) {
        Base {
            if (lf.x() > rb.x() || lf.y() > rb.y()) {
                throw new IllegalArgumentException("lf must be less than or equal to rb");
            }
        }

        boolean overlaps(Base other) {
            return !(lf.x() > other.rb.x() || other.lf.x() > rb.x() || lf.y() > other.rb.y() || other.lf.y() > rb.y());
        }
    }

    static class Pile {
        private final Map<Integer, List<Brick>> bottoms;
        private final Map<Integer, List<Brick>> tops;
        private final Map<Point2D, Integer> heights;
        private int bricksAdded;

        public Pile() {
            bottoms = new HashMap<>();
            tops = new HashMap<>();
            heights = new HashMap<>();
            bricksAdded = 0;
        }

        public void add(Brick brick) {
            var base = brick.base();
            var bottom = height(base);
            var top = bottom + brick.height();
            bottoms.putIfAbsent(bottom, new ArrayList<>());
            bottoms.get(bottom).add(brick);
            tops.putIfAbsent(top, new ArrayList<>());
            tops.get(top).add(brick);
            updateHeight(base, top);
            bricksAdded++;
        }

        private void updateHeight(Base base, int newHeight) {
            for (var x = base.lf().x(); x <= base.rb().x(); x++) {
                for (var y = base.lf().y(); y <= base.rb().y(); y++) {
                    heights.put(new Point2D(x, y), newHeight);
                }
            }
        }

        public int height(Base base) {
            var max = 0;
            for (var x = base.lf().x(); x <= base.rb().x(); x++) {
                for (var y = base.lf().y(); y <= base.rb().y(); y++) {
                    var h = heights.getOrDefault(new Point2D(x, y), 0);
                    if (h > max) {
                        max = h;
                    }
                }
            }
            return max;
        }

        @Override
        public String toString() {
            return "Pile{" +
                    "bottoms=" + bottoms +
                    ", tops=" + tops +
                    ", height=" + heights +
                    ", bricksAdded=" + bricksAdded +
                    '}';
        }

        public int safeToDisintegrate() {
            int maxHeight = bottoms.keySet().stream().max(Integer::compareTo).orElseThrow();
            var unsafeToDisintegrate = new HashSet<Brick>();
            // all the last layer (maxHeight) are safe to disintegrate
            for (int height = 1; height <= maxHeight; height++) {
                var supporting = tops.getOrDefault(height, Collections.emptyList());
                var supported = bottoms.getOrDefault(height, Collections.emptyList());
                for (var brick : supported) {
                    var supportingTheBrick = supporting.stream().filter(brick::overlaps).toList();
                    assert !supportingTheBrick.isEmpty() : "a brick must be supported by at least one other brick";
                    if (supportingTheBrick.size() == 1) {
                        unsafeToDisintegrate.add(supportingTheBrick.getFirst());
                    }
                }
            }
            return bricksAdded - unsafeToDisintegrate.size();
        }
    }

    int part1(List<String> data) {
        var bricks = data.stream().map(Brick::parse).sorted().toList();
        var pile = new Pile();
        bricks.forEach(pile::add);
        return pile.safeToDisintegrate();
    }

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day22 = new Day22();
        var data = IO.getResourceAsList("day22.txt");
        var part1 = day22.part1(data);
        System.out.println("part1 = " + part1);
//        var part2 = day22.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
