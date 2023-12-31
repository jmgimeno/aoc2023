package aoc2023.day24;

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

    record Vector2D(double x, double y) {
    }

    record Crossing(Vector2D p, double time1, double time2) {
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
            double dx = pos0.x();
            double dy = pos0.y();
            double vx = vel.x();
            double vy = vel.y();
            double a = vy / vx;
            double b = dy - a * dx;
            return new Line2D(a, b);
        }

        public Optional<Crossing> crosses(Hailstone3D o) {
            var line1 = projectXY();
            var line2 = o.projectXY();
            var crossPoint = line1.crosses(line2);
            return crossPoint.map(p -> new Crossing(p, time(p), o.time(p)));
        }

        public double time(Vector2D p) {
            // x = dx + vx * t => t = (x - dx) / vx
            return (p.x() - (double) pos0.x()) / vel.x();
        }
    }

    record Line2D(double a, double b) { // y = a * x + b
        Optional<Vector2D> crosses(Line2D o) {
            if (Double.compare(a, o.a) == 0)
                return Optional.empty();
            // a1 x + b1 = a2 x + b2 => a1 x - a2 x = b2 - b1 => x = (b2 - b1) / (a1 - a2)
            // y = a1 * x + b1
            double x = (o.b - b) / (a - o.a);
            double y = a * x + b;
            return Optional.of(new Vector2D(x, y));
        }
    }

    record TestArea(long min, long max) {
        public boolean containsInTheFuture(Crossing c) {
            var mind = (double) min;
            var maxd = (double) max;
            return mind <= c.p().x() && c.p().x() <= maxd
                    && mind <= c.p().y() && c.p().y() <= maxd
                    && c.time1() >= 0
                    && c.time2() >= 0;
        }
    }

    int part1(List<String> data, long min, long max) {
        var area = new TestArea(min, max);
        var lines = data.stream().map(Hailstone3D::parse).toList();
        var crossings = 0;
        for (int i = 0; i < lines.size(); i++)
            for (int j = i + 1; j < lines.size(); j++) {
                var cross = lines.get(i).crosses(lines.get(j)).filter(area::containsInTheFuture);
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
