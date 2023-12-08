package aoc2023.day8;

import aoc2023.utils.IO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

public class Day8 {

    /*
        --- Day 8: Haunted Wasteland ---
    You're still riding a camel across Desert Island when you spot a sandstorm quickly approaching.
    When you turn to warn the Elf, she disappears before your eyes! To be fair, she had just
    finished warning you about ghosts a few minutes ago.

    One of the camel's pouches is labeled "maps" - sure enough, it's full of documents (your puzzle
    input) about how to navigate the desert. At least, you're pretty sure that's what they are; one
    of the documents contains a list of left/right instructions, and the rest of the documents seem
    to describe some kind of network of labeled nodes.

    It seems like you're meant to use the left/right instructions to navigate the network. Perhaps
    if you have the camel follow the same instructions, you can escape the haunted wasteland!

    After examining the maps for a bit, two nodes stick out: AAA and ZZZ. You feel like AAA is where
    you are now, and you have to follow the left/right instructions until you reach ZZZ.

    This format defines each node of the network individually. For example:

        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)

    Starting with AAA, you need to look up the next element based on the next left/right instruction
    in your input. In this example, start with AAA and go right (R) by choosing the right element of
    AAA, CCC. Then, L means to choose the left element of CCC, ZZZ. By following the left/right
    instructions, you reach ZZZ in 2 steps.

    Of course, you might not find ZZZ right away. If you run out of left/right instructions, repeat
    the whole sequence of instructions as necessary: RL really means RLRLRLRLRLRLRLRL... and so on.
    For example, here is a situation that takes 6 steps to reach ZZZ:

        LLR

        AAA = (BBB, BBB)
        BBB = (AAA, ZZZ)
        ZZZ = (ZZZ, ZZZ)

    Starting at AAA, follow the left/right instructions. How many steps are required to reach ZZZ?

    Your puzzle answer was 20659.
    */

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
            private final Map<String, String> visited;  // start -> end
            private long cycleLength;

            SimpleWalker(String current, Predicate<String> isFinished) {
                this.current = current;
                this.isFinished = isFinished;
                this.visited = new HashMap<>();
                this.cycleLength = 0L;
            }

            void next(String step) {
                var node = nodes.get(current);
                if (step.equals("L")) {
                    current = node.left();
                } else {
                    current = node.right();
                }
            }

            void walkOncePart1(InputPath path) {
                for (var step : path.steps().split("")) {
                    next(step);
                }
            }

            void walkOncePart2(InputPath path) {
                var start = current;
                if (visited.containsKey(start)) {
                    System.out.printf("%s -> %s%n", start, visited.get(start));
                    cycleLength = path(visited, start).size();
                }
                for (var step : path.steps().split("")) {
                    next(step);
                }
                visited.put(start, current);
            }

            private static List<String> path(Map<String, String> visited, String start) {
                var path = new ArrayList<String>();
                var current = start;
                do {
                    path.add(current);
                    current = visited.get(current);
                } while (!current.equals(start));
                return path;
            }

            long part1(InputPath path) {
                var count = 0L;
                while (!isFinished()) {
                    walkOncePart1(path);
                    count += path.steps().length();
                }
                return count;
            }

            boolean isFinished() {
                return isFinished.test(current);
            }

            boolean hasCycle() {
                return cycleLength > 0;
            }

            long cycleLength() {
                return cycleLength;
            }
        }

        class MultipleWalker {

            private final List<SimpleWalker> walkers;

            MultipleWalker(List<String> starts) {
                walkers = starts.stream()
                        .map(s -> new SimpleWalker(s, node -> node.endsWith("Z")))
                        .collect(Collectors.toList());
            }

            long part2(InputPath path) {
                List<SimpleWalker> yetWalking = new ArrayList<>(walkers);
                while (!yetWalking.isEmpty()) {
                    yetWalking.forEach(w -> w.walkOncePart2(path));
                    yetWalking = yetWalking.stream()
                            .filter(w -> !w.hasCycle())
                            .toList();
                }
                return walkers.stream()
                        .mapToLong(SimpleWalker::cycleLength)
                        .reduce(1L, (a, b) -> a * b) * path.steps().length();
            }
        }
    }

    long part1(List<String> data) {
        var path = InputPath.parse(data.getFirst());
        var tree = Tree.parse(data.subList(2, data.size()));
        return tree.simpleWalker().part1(path);
    }

    /*
    --- Part Two ---
    The sandstorm is upon you and you aren't any closer to escaping the wasteland. You had the
    camel follow the instructions, but you've barely left your starting position. It's going to
    take significantly more steps to escape!

    What if the map isn't for people - what if the map is for ghosts? Are ghosts even bound by
    the laws of spacetime? Only one way to find out.

    After examining the maps a bit longer, your attention is drawn to a curious fact: the number
    of nodes with names ending in A is equal to the number ending in Z! If you were a ghost, you'd
    probably just start at every node that ends with A and follow all of the paths at the same time
    until they all simultaneously end up at nodes that end with Z.

    For example:

    LR

    11A = (11B, XXX)
    11B = (XXX, 11Z)
    11Z = (11B, XXX)
    22A = (22B, XXX)
    22B = (22C, 22C)
    22C = (22Z, 22Z)
    22Z = (22B, 22B)
    XXX = (XXX, XXX)

    Here, there are two starting nodes, 11A and 22A (because they both end with A). As you follow
    each left/right instruction, use that instruction to simultaneously navigate away from both
    nodes you're currently on. Repeat this process until all of the nodes you're currently on end
    with Z. (If only some of the nodes you're on end with Z, they act like any other node and you
    continue as normal.) In this example, you would proceed as follows:

    Step 0: You are at 11A and 22A.
    Step 1: You choose all of the left paths, leading you to 11B and 22B.
    Step 2: You choose all of the right paths, leading you to 11Z and 22C.
    Step 3: You choose all of the left paths, leading you to 11B and 22Z.
    Step 4: You choose all of the right paths, leading you to 11Z and 22B.
    Step 5: You choose all of the left paths, leading you to 11B and 22C.
    Step 6: You choose all of the right paths, leading you to 11Z and 22Z.

    So, in this example, you end up entirely on nodes that end in Z after 6 steps.

    Simultaneously start on every node that ends with A. How many steps does it take before you're
    only on nodes that end with Z?

    Your puzzle answer was 15690466351717.
     */

    /*

    Thanks reddit.com for the posts analyzing this problem:

    https://www.reddit.com/r/adventofcode/comments/18dfpub/2023_day_8_part_2_why_is_spoiler_correct/

    The puzzle data is constructed such that:

    - A -(h)-> X -(m)-> Z
    -          ^        |
    -          |--(t)---|
    -
    - A needs h steps to reach the cycle
    - there is only one end node Z in the cycle
    - the time from the head of the cycle X to Z is m
    - the time from Z to the head of the cycle X is t
    - the time h is the same as t

    => the cycleLength = m + t = m + h
    => at each cycleLength we are in the end node Z

    => all cycles will be synchronized after the mcm of all cycle lengths
    => as the lengths are prime => multiply them all together

    - BTW: I noticed that the cycle is 1 step longer than the path to detect it, but
    - I didn't fully grasp why it mattered

    For instance, in the input data:

            LR

            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)

    - 11A -(LR)-> 11Z -(LR)-> 11Z
        => the cycle has length 1
        => the length from 11A to the unique finishing node 11Z is 1

    - 22A -(LR)-> 22C -(LR)-> 22B -(LR)-> 22Z -(LR)-> 22C
        => the cycle has length 3
        => the path from 22A to the unique finishing node 22Z is 3

    - the first one will be on a finishing node after every path
    - the second one will be on a finishing node after every 3 paths
        => both will be on an end node after every mcm(1, 3) = 3 paths
        => so the num of steps will be 3 * 2 = 6

     */

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
