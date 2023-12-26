package aoc2023.day19;

import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {

    public record Part(int x, int m, int a, int s) {
        public static List<Part> parse(List<String> data) {
            return data.stream()
                    .map(Part::parse)
                    .toList();
        }

        public static Part parse(String data) {
            // {x=172,m=2372,a=1091,s=1657}
            var parts = data.substring(1, data.length() - 1).split(",");
            var x = Integer.parseInt(parts[0].substring(2));
            var m = Integer.parseInt(parts[1].substring(2));
            var a = Integer.parseInt(parts[2].substring(2));
            var s = Integer.parseInt(parts[3].substring(2));
            return new Part(x, m, a, s);
        }
    }

    @FunctionalInterface
    public interface PartChecker {
        boolean check(Part part);
    }

    public record Work(MultiPart multiPart, Destination destination) {
    }

    public interface TotalExecutor {
        Destination executePart(Part part);

        List<Work> executeMultiPart(MultiPart multiPart);
    }

    public interface PartialExecutor {
        Optional<Destination> executePart(Part part);
    }

    static class RuleBook implements PartChecker {
        Map<String, Workflow> workflows;

        public RuleBook(Map<String, Workflow> workflows) {
            this.workflows = workflows;
        }

        public static RuleBook parse(ArrayList<String> data) {
            var workflows = data.stream()
                    .map(Workflow::parse)
                    .collect(Collectors.toMap(Workflow::name, Function.identity()));
            return new RuleBook(workflows);
        }

        @Override
        public boolean check(Part part) {
            var workflowName = "in";
            do {
                var workflow = workflows.get(workflowName);
                switch (workflow.executePart(part)) {
                    case Destination.GoTo(String next) -> workflowName = next;
                    case Destination.Accept() -> {
                        return true;
                    }
                    case Destination.Reject() -> {
                        return false;
                    }
                }
            } while (true);
        }

        public List<MultiPart> check(MultiPart multiPart) {
            var accepted = new ArrayList<MultiPart>();
            var work = new ArrayList<Work>();
            work.add(new Work(multiPart, new Destination.GoTo("in")));
            while (!work.isEmpty()) {
                var next = work.removeFirst();
                var multiPart1 = next.multiPart;
                var destination = next.destination;
                if (destination instanceof Destination.GoTo(String name)) {
                    var workflow = workflows.get(name);
                    var works = workflow.executeMultiPart(multiPart1);
                    work.addAll(works);
                } else if (destination instanceof Destination.Accept) {
                    accepted.add(multiPart1);
                } else if (destination instanceof Destination.Reject) {
                    // do nothing
                }
            }
            return accepted;
        }
    }

    record Workflow(String name, List<Rule> rules) implements TotalExecutor {
        @Override
        public Destination executePart(Part part) {
            return rules.stream()
                    .map(rule -> rule.executePart(part))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElseThrow();
        }

        @Override
        public List<Work> executeMultiPart(MultiPart multiPart) {
            assert multiPart.notEmpty();
            var work = new ArrayList<Work>();
            for (var rule : rules) {
                if (rule instanceof Rule.IfThen ifThen) {
                    var split = multiPart.split(ifThen.condition);
                    if (split.ifTrue().notEmpty()) {
                        work.add(new Work(split.ifTrue(), ifThen.destination));
                    }
                    if (split.ifFalse().notEmpty()) {
                        multiPart = split.ifFalse();
                    }
                } else if (rule instanceof Rule.Else elseRule) {
                    work.add(new Work(multiPart, elseRule.destination));
                }
            }
            return work;
        }

        public static Workflow parse(String data) {
            // px{a<2006:qkq,m>2090:A,rfg}
            var parts = data.split("\\{");
            var name = parts[0];
            var rules = parts[1].substring(0, parts[1].length() - 1).split(",");
            return new Workflow(
                    name,
                    Stream.of(rules).map(Rule::parse).toList());
        }
    }

    sealed interface Condition extends PartChecker {
        static Condition parse(String part) {
            if (part.contains("<")) {
                var parts = part.split("<");
                var value = Integer.parseInt(parts[1]);
                switch (parts[0]) {
                    case "x" -> {
                        return new xLt(value);
                    }
                    case "m" -> {
                        return new mLt(value);
                    }
                    case "a" -> {
                        return new aLt(value);
                    }
                    case "s" -> {
                        return new sLt(value);
                    }
                }
            } else if (part.contains(">")) {
                var parts = part.split(">");
                var value = Integer.parseInt(parts[1]);
                switch (parts[0]) {
                    case "x" -> {
                        return new xGt(value);
                    }
                    case "m" -> {
                        return new lGt(value);
                    }
                    case "a" -> {
                        return new aGt(value);
                    }
                    case "s" -> {
                        return new sGt(value);
                    }
                }
            }
            throw new IllegalArgumentException("Invalid condition: " + part);
        }

        record xLt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.x() < value;
            }
        }

        record mLt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.m() < value;
            }
        }

        record aLt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.a() < value;
            }
        }

        record sLt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.s() < value;
            }
        }

        record xGt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.x() > value;
            }
        }

        record lGt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.m() > value;
            }
        }

        record aGt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.a() > value;
            }
        }

        record sGt(int value) implements Condition {
            @Override
            public boolean check(Part part) {
                return part.s() > value;
            }
        }
    }

    sealed interface Rule extends PartialExecutor {

        static Rule parse(String data) {
            // a>1716:R
            // A
            if (data.contains(":")) {
                return IfThen.parse(data);
            } else {
                return Else.parse(data);
            }
        }

        record IfThen(Condition condition, Destination destination) implements Rule {

            static IfThen parse(String data) {
                var parts = data.split(":");
                var condition = Condition.parse(parts[0]);
                var destination = Destination.parse(parts[1]);
                return new IfThen(condition, destination);
            }

            @Override
            public Optional<Destination> executePart(Part part) {
                return condition.check(part) ? Optional.of(destination) : Optional.empty();
            }
        }

        record Else(Destination destination) implements Rule {

            static Else parse(String data) {
                var destination = Destination.parse(data);
                return new Else(destination);
            }

            @Override
            public Optional<Destination> executePart(Part part) {
                return Optional.of(destination);
            }
        }
    }

    public sealed interface Destination {
        static Destination parse(String part) {
            if (part.equals("A")) {
                return new Accept();
            } else if (part.equals("R")) {
                return new Reject();
            } else {
                return new GoTo(part);
            }
        }

        record GoTo(String name) implements Destination {
        }

        record Accept() implements Destination {
        }

        record Reject() implements Destination {
        }

    }

    record Parsed(RuleBook ruleBook, List<Part> parts) {
        static Parsed parse(List<String> data) {
            var first = new ArrayList<String>();
            var second = new ArrayList<String>();
            var firstDone = false;
            for (var line : data) {
                if (line.isBlank()) {
                    firstDone = true;
                } else if (firstDone) {
                    second.add(line);
                } else {
                    first.add(line);
                }
            }
            return new Parsed(RuleBook.parse(first), Part.parse(second));
        }
    }

    int part1(List<String> data) {
        var parsed = Parsed.parse(data);
        return parsed.parts().stream()
                .filter(parsed.ruleBook()::check)
                .mapToInt(part -> part.x() + part.m() + part.a() + part.s())
                .sum();
    }

    public record Split(MultiPart ifTrue, MultiPart ifFalse) {
    }

    public record MultiPart(int minX, int maxX, int minM, int maxM, int minA, int maxA, int minS,
                            int maxS) {

        long range() {
            return (long) (maxX - minX + 1) * (maxM - minM + 1) * (maxA - minA + 1) * (maxS - minS + 1);
        }

        boolean notEmpty() {
            return minX <= maxX && minM <= maxM && minA <= maxA && minS <= maxS;
        }

        static MultiPart make() {
            return new MultiPart(1, 4000, 1, 4000, 1, 4000, 1, 4000);
        }

        Split split(Condition condition) {
            MultiPart ifTrue = null, ifFalse = null;
            if (condition instanceof Condition.xLt xLt) {
                ifTrue = new MultiPart(minX, xLt.value - 1, minM, maxM, minA, maxA, minS, maxS);
                ifFalse = new MultiPart(xLt.value, maxX, minM, maxM, minA, maxA, minS, maxS);
            } else if (condition instanceof Condition.mLt mLt) {
                ifTrue = new MultiPart(minX, maxX, minM, mLt.value - 1, minA, maxA, minS, maxS);
                ifFalse = new MultiPart(minX, maxX, mLt.value, maxM, minA, maxA, minS, maxS);
            } else if (condition instanceof Condition.aLt aLt) {
                ifTrue = new MultiPart(minX, maxX, minM, maxM, minA, aLt.value - 1, minS, maxS);
                ifFalse = new MultiPart(minX, maxX, minM, maxM, aLt.value, maxA, minS, maxS);
            } else if (condition instanceof Condition.sLt sLt) {
                ifTrue = new MultiPart(minX, maxX, minM, maxM, minA, maxA, minS, sLt.value - 1);
                ifFalse = new MultiPart(minX, maxX, minM, maxM, minA, maxA, sLt.value, maxS);
            } else if (condition instanceof Condition.xGt xGt) {
                ifTrue = new MultiPart(xGt.value + 1, maxX, minM, maxM, minA, maxA, minS, maxS);
                ifFalse = new MultiPart(minX, xGt.value, minM, maxM, minA, maxA, minS, maxS);
            } else if (condition instanceof Condition.lGt lGt) {
                ifTrue = new MultiPart(minX, maxX, lGt.value + 1, maxM, minA, maxA, minS, maxS);
                ifFalse = new MultiPart(minX, maxX, minM, lGt.value, minA, maxA, minS, maxS);
            } else if (condition instanceof Condition.aGt aGt) {
                ifTrue = new MultiPart(minX, maxX, minM, maxM, aGt.value + 1, maxA, minS, maxS);
                ifFalse = new MultiPart(minX, maxX, minM, maxM, minA, aGt.value, minS, maxS);
            } else if (condition instanceof Condition.sGt sGt) {
                ifTrue = new MultiPart(minX, maxX, minM, maxM, minA, maxA, sGt.value + 1, maxS);
                ifFalse = new MultiPart(minX, maxX, minM, maxM, minA, maxA, minS, sGt.value);
            }
            return new Split(ifTrue, ifFalse);
        }
    }

    long part2(List<String> data) {
        var ruleBoolk = Parsed.parse(data).ruleBook();
        var multiPart = MultiPart.make();
        var accepted = ruleBoolk.check(multiPart);
        return accepted.stream()
                .mapToLong(MultiPart::range)
                .sum();
    }

    public static void main(String[] args) {
        var day19 = new Day19();
        var data = IO.getResourceAsList("day19.txt");
        var part1 = day19.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day19.part2(data);
        System.out.println("part2 = " + part2);
    }
}
