package aoc2023.day24;

import aoc2023.utils.GCRT;
import aoc2023.utils.IO;

import java.util.List;
import java.util.Optional;


public class Day24 {

    record Vector3D(long x, long y, long z) {
        public static Vector3D parse(String s) {
            var parts = s.split(",");
            return new Vector3D(Long.parseLong(parts[0].trim()), Long.parseLong(parts[1].trim()), Long.parseLong(parts[2].trim()));
        }
    }

    static final class Hailstone3D {
        private final Vector3D pos0;
        private final Vector3D vel;

        Hailstone3D(Vector3D p, Vector3D vel) {
            this.pos0 = p;
            this.vel = vel;
        }

        public static Hailstone3D parse(String line) {
            // 19, 13, 30 @ -2,  1, -2
            var parts = line.split("@");
            return new Hailstone3D(Vector3D.parse(parts[0].trim()), Vector3D.parse(parts[1].trim()));
        }

        public Line2D projectXY() {
            // x = dx + vx * t => t = (x - dx) / vx
            // y = dy + vy * t => y = dy + vy *  (x - dx) / vx = (vy / vx) * x + dy - (vy / vx) * dx
            var a = new Rational(vel.y(), vel.x());
            var b = new Rational(pos0.y()).sub(a.mul(new Rational(pos0.x())));
            return new Line2D(a, b);
        }

        record Crossing(Vector2D p, Rational time1, Rational time2) {
        }

        public Optional<Crossing> crosses(Hailstone3D o) {
            var line1 = projectXY();
            var line2 = o.projectXY();
            var crossPoint = line1.crosses(line2);
            return crossPoint.map(p -> {
                var t1 = time(p);
                var t2 = o.time(p);
                return new Crossing(p, t1, t2);
            });
        }

        public Rational time(Vector2D p) {
            // x = dx + vx * t => t = (x - dx) / vx
            return p.x().sub(new Rational(this.pos0.x())).div(new Rational(vel.x()));
        }
    }

    record Rational(long num, long den) implements Comparable<Rational> {
        Rational {
            if (den == 0)
                throw new IllegalArgumentException("den cannot be zero");
            var gcd = GCRT.gcd(Math.abs(num), Math.abs(den));
            if (den < 0) {
                num = -num;
                den = -den;
            }
            num = num / gcd;
            den = den / gcd;
        }

        Rational(long numerator) {
            this(numerator, 1L);
        }

        Rational add(Rational o) {
            return new Rational(num * o.den + den * o.num, den * o.den);
        }

        Rational sub(Rational o) {
            return new Rational(num * o.den - den * o.num, den * o.den);
        }

        Rational mul(Rational o) {
            return new Rational(num * o.num, den * o.den);
        }

        Rational div(Rational o) {
            return new Rational(num * o.den, den * o.num);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Rational rational = (Rational) o;

            if (num != rational.num) return false;
            return den == rational.den;
        }

        @Override
        public int compareTo(Rational o) {
            return Long.compare(num * o.den, den * o.num);
        }
    }

    record Line2D(Rational a, Rational b) { // y = a * x + b
        Optional<Vector2D> crosses(Line2D o) {
            if (a.equals(o.a)) return Optional.empty();
            // a1 x + b1 = a2 x + b2 => a1 x - a2 x = b2 - b1 => x = (b2 - b1) / (a1 - a2)
            // y = a1 * x + b1
            var x = o.b.sub(b).div(a.sub(o.a));
            var y = a.mul(x).add(b);
            return Optional.of(new Vector2D(x, y));
        }
    }

    record Vector2D(Rational x, Rational y) {
    }

    record TestArea(long min, long max) {
        public boolean contains(Vector2D p) {
            var minr = new Rational(min);
            var maxr = new Rational(max);
            return minr.compareTo(p.x()) <= 0 && p.x().compareTo(maxr) <= 0 &&
                    minr.compareTo(p.y()) <= 0 && p.y().compareTo(maxr) <= 0;
        }
    }

    int part1(List<String> data, long min, long max) {
        var area = new TestArea(min, max);
        var lines = data.stream().map(Hailstone3D::parse).toList();
        var crossings = 0;
        for (int i = 0; i < lines.size(); i++)
            for (int j = i + 1; j < lines.size(); j++) {
                var cross = lines.get(i).crosses(lines.get(j)).filter(crossing -> {
                    var t1 = crossing.time1;
                    var t2 = crossing.time2;
                    return area.contains(crossing.p) && t1.compareTo(new Rational(0)) >= 0 && t2.compareTo(new Rational(0)) >= 0;
                });
                if (cross.isPresent())
                    crossings++;
            }
        return crossings;
    }

    int part2(List<String> data) {
        throw new UnsupportedOperationException("part2");
    }

    public static void main(String[] args) {
        var day24 = new Day24();
        var data = IO.getResourceAsList("day24.txt");
        var part1 = day24.part1(data, 200000000000000L, 400000000000000L);
        System.out.println("part1 = " + part1);
//        var part2 = day24.part2(data);
//        System.out.println("part2 = " + part2);
    }
}
