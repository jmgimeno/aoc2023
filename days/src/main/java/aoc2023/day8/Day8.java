package aoc2023.day8;

import aoc2023.utils.IO;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

public class Day8 {

    record InputPath(String steps) {
        static InputPath parse(String line) {
            return new InputPath(line);
        }
    }

    static final class Tree {

        private final Map<String, Node> nodes;

        record Node(String root, String left, String right) {
            static Node parse(String line) {
                var parts = line.split(" = ");
                var root = parts[0];
                var children = parts[1].substring(1, parts[1].length() - 1).split(", ");
                return new Node(root, children[0], children[1]);
            }
        }

        Tree(Map<String, Node> nodes) {
            this.nodes = nodes;
        }

        static Tree parse(List<String> data) {
            var nodes = data.stream()
                    .map(Node::parse)
                    .collect(Collectors.toMap(Node::root, identity()));
            return new Tree(nodes);
        }

        SimpleWalker simpleWalker() {
            return new SimpleWalker("AAA", s -> s.endsWith("ZZZ"));
        }

        MultipleWalker multipleWalker() {
            var starts = nodes.keySet().stream()
                    .filter(s -> s.endsWith("A"))
                    .collect(Collectors.toList());
            return new MultipleWalker(starts);
        }

        class SimpleWalker {
            private String current;
            private final Predicate<String> isFinished;

            SimpleWalker(String current, Predicate<String> isFinished) {
                this.current = current;
                this.isFinished = isFinished;
            }

            void next(String step) {
                var node = nodes.get(current);
                if (step.equals("L")) {
                    current = node.left();
                } else {
                    current = node.right();
                }
            }

            void walkPath(InputPath path) {
                for (var step : path.steps().split("")) {
                    next(step);
                }
            }

            long numPathsToFinish(InputPath path) {
                var count = 0L;
                while (!isFinished()) {
                    walkPath(path);
                    count += 1;
                }
                return count;
            }

            boolean isFinished() {
                return isFinished.test(current);
            }

            long part1(InputPath path) {
                return numPathsToFinish(path) * path.steps().length();
            }
        }

        class MultipleWalker {

            private final List<SimpleWalker> walkers;

            MultipleWalker(List<String> starts) {
                walkers = starts.stream()
                        .map(s -> new SimpleWalker(s, node -> node.endsWith("Z")))
                        .collect(Collectors.toList());
            }

            long numPathsToFinish(InputPath path) {
                return walkers.stream()
                        .mapToLong(w -> w.numPathsToFinish(path))
                        .reduce(1L, (a, b) -> a * b);
            }

            long part2(InputPath path) {
                return numPathsToFinish(path) * path.steps().length();
            }
        }
    }

    long part1(List<String> data) {
        var path = InputPath.parse(data.getFirst());
        var tree = Tree.parse(data.subList(2, data.size()));
        return tree.simpleWalker().part1(path);
    }

    long part2(List<String> data) {
        var path = InputPath.parse(data.getFirst());
        var tree = Tree.parse(data.subList(2, data.size()));
        return tree.multipleWalker().part2(path);
    }

    public static void main(String[] args) {
        var day8 = new Day8();
        var data = IO.getResourceAsList("day8.txt");
        var part1 = day8.part1(data);
        System.out.printf("part1 = %s%n", part1);
        var part2 = day8.part2(data);
        System.out.printf("part2 = %s%n", part2);
    }
}
