package aoc2023.day24;

import aoc2023.utils.BigRational;
import aoc2023.utils.IO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


public class Day24 {

    enum Axis {
        X, Y, Z
    }

    record Vector3D(long x, long y, long z) {
        public static Vector3D parse(String s) {
            var parts = s.split(",");
            return new Vector3D(
                    Long.parseLong(parts[0].trim()),
                    Long.parseLong(parts[1].trim()),
                    Long.parseLong(parts[2].trim()));
        }
    }

    record Vector2D(long ax1, long ax2) {
    }

    record Vector2DR(BigRational ax1, BigRational ax2) {
    }

    static final class Hailstone2D {
        private final Vector2D pos;
        private final Vector2D vel;

        private final BigRational a;
        private final BigRational b;

        public Hailstone2D(Vector2D pos, Vector2D vel) {
            this.pos = pos;
            this.vel = vel;
            // x = x0 + vx * t  => t = (x - x0) / vx
            // y = y0 + vy * t =  y0 + vy * (x - x0) / vx = y0 - x0 * (vy / vx) + (vy / vx) * x
            a = new BigRational(vel.ax2(), vel.ax1());
            b = new BigRational(pos.ax2()).subtract(new BigRational(pos.ax1()).multiply(a));
        }

        public Optional<Vector2DR> crossingR(Hailstone2D other) {
            // ax2 = aa1 * ax1 + bb1
            // ax2 = aa2 * ax1 + bb2
            // => aa1 * ax1 + bb1 = aa2 * ax1 + bb2
            // => ax1 = (bb2 - bb1) / (aa1 - aa2)
            if (a.compareTo(other.a) == 0)
                return Optional.empty();
            var ax1 = other.b.subtract(b).divide(a.subtract(other.a));
            var ax2 = a.multiply(ax1).add(b);
            return Optional.of(new Vector2DR(ax1, ax2)).filter(p -> inFuture(p) && other.inFuture(p));
        }

        public BigRational time(Vector2DR cross) {
            // x = x0 + vx * t => t = (x - x0) / vx
            return cross.ax1().subtract(new BigRational(pos.ax1())).divide(new BigRational(vel.ax1()));
        }

        public boolean inFuture(Vector2DR cross) {
            return time(cross).compareTo(BigRational.ZERO) >= 0;
        }

        public Hailstone2D relativeTo(Vector2D p) {
            return new Hailstone2D(pos, new Vector2D(vel.ax1() - p.ax1(), vel.ax2() - p.ax2()));
        }
    }

    static final class Hailstone3D {
        private final Vector3D pos;
        private final Vector3D vel;

        Hailstone3D(Vector3D pos, Vector3D vel) {
            this.pos = pos;
            this.vel = vel;
        }

        public static Hailstone3D parse(String line) {
            // 19, 13, 30 @ -2,  1, -2
            var parts = line.split("@");
            return new Hailstone3D(Vector3D.parse(parts[0].trim()), Vector3D.parse(parts[1].trim()));
        }

        public Hailstone2D project(Axis axis) {
            return switch (axis) {
                case X -> new Hailstone2D(new Vector2D(pos.y, pos.z), new Vector2D(vel.y, vel.z));
                case Y -> new Hailstone2D(new Vector2D(pos.x, pos.z), new Vector2D(vel.x, vel.z));
                case Z -> new Hailstone2D(new Vector2D(pos.x, pos.y), new Vector2D(vel.x, vel.y));
            };
        }
    }

    record Boundary(BigRational min, BigRational max) {
        public Boundary(long min, long max) {
            this(new BigRational(min), new BigRational(max));
        }

        public boolean contains(Vector2DR cross) {
            return min.compareTo(cross.ax1()) <= 0
                    && cross.ax1().compareTo(max) <= 0
                    && min.compareTo(cross.ax2()) <= 0
                    && cross.ax2().compareTo(max) <= 0;
        }
    }

    int part11(List<String> data, long min, long max) {
        var bound = new Boundary(min, max);
        var lines = data.stream().map(Hailstone3D::parse).toList();
        var crossings = 0;
        for (int i = 0; i < lines.size(); i++) {
            var line1 = lines.get(i).project(Axis.Z);
            for (int j = i + 1; j < lines.size(); j++) {
                var line2 = lines.get(j).project(Axis.Z);
                var crossPoint = line1.crossingR(line2).filter(bound::contains);
                if (crossPoint.isPresent())
                    crossings++;
            }
        }
        return crossings;
    }

    record Hailstone2DR(Vector2DR pos, Vector2D vel) {
    }

    Optional<Hailstone2DR> tryVelocityR(List<Hailstone3D> hailstones, Axis axis, int security, Vector2D rockV) {
        int count = 0;
        var positions = new HashSet<Vector2DR>();
        for (int i = 0; i < hailstones.size(); i++) {
            var line1 = hailstones.get(i).project(axis).relativeTo(rockV);
            for (int j = i + 1; j < hailstones.size(); j++) {
                var line2 = hailstones.get(j).project(axis).relativeTo(rockV);
                var crossPoint = line1.crossingR(line2);
                if (crossPoint.isPresent() && positions.size() <= 1) {
                    positions.add(crossPoint.get());
                    count++;
                    if (count >= security && positions.size() == 1)
                        return Optional.of(new Hailstone2DR(positions.iterator().next(), rockV));
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    Optional<Hailstone2DR> possibleRockR(List<Hailstone3D> hailstones, Axis axis, long range, int security) {
        for (long v1 = -range; v1 <= range; v1++) {
            for (long v2 = -range; v2 <= range; v2++) {
                var rockV = new Vector2D(v1, v2); // velocity for the rock
                var tryV = tryVelocityR(hailstones, axis, security, rockV);
                if (tryV.isPresent())
                    return tryV;
            }
        }
        return Optional.empty();
    }

    long part22(List<String> data, long range, int security) {
        var lines = data.stream().map(Hailstone3D::parse).toList();
        var rXY = possibleRockR(lines, Axis.Z, range, security).orElseThrow();
        var rXZ = possibleRockR(lines, Axis.Y, range, security).orElseThrow();
        assert rXY.pos.ax1().compareTo(rXZ.pos.ax1()) == 0 : "rocks are not in the same position";
        assert rXY.vel.ax1() == rXZ.vel.ax1() : "rocks are not moving in the same direction";
        // I assume the final coordinates for the position are long
        return rXY.pos.ax1().add(rXY.pos.ax2()).add(rXZ.pos.ax2()).numerator().longValue();
    }

    public static void main(String[] args) {
        var day24 = new Day24();
        var data = IO.getResourceAsList("day24.txt");
        var part1 = day24.part11(data, 200000000000000L, 400000000000000L);
        System.out.println("part1 = " + part1);
        var part2 = day24.part22(data, 300L, 5);
        System.out.println("part2 = " + part2);
    }
}
