package aoc2023.day15;

import aoc2023.utils.IO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Day15 {

    int part1(List<String> data) {
        return Arrays.stream(data.getFirst().split(",")).mapToInt(Day15::hash).sum();
    }

    static int hash(String str) {
        int hash = 0;
        int mask = (1 << 8) - 1; // mod 256
        for (int i = 0; i < str.length(); i++) {
            hash += str.charAt(i);
            hash *= 17;
            hash &= mask;
        }
        return hash;
    }

    sealed interface Instruction permits Put, Remove {
        static Instruction parse(String line) {
            if (line.endsWith("-")) {
                return new Remove(line.substring(0, line.length() - 1));
            } else {
                var parts = line.split("=");
                return new Put(parts[0], Integer.parseInt(parts[1]));
            }
        }
    }

    record Put(String key, int value) implements Instruction {
    }

    record Remove(String key) implements Instruction {
    }

    static class Boxes {

        static class Entry {
            final String key;
            int value;

            Entry(String key, int value) {
                this.key = key;
                this.value = value;
            }

            void setValue(int value) {
                this.value = value;
            }
        }

        record ByKey(String key) {
            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Entry entry)) return false;
                return key.equals(entry.key);
            }
        }

        List<LinkedList<Entry>> boxes = IntStream.range(0, 256).mapToObj(i -> new LinkedList<Entry>()).toList();

        public int totalFocusingPower() {
            int total = 0;
            for (int i = 0; i < boxes.size(); i++) {
                var box = boxes.get(i);
                for (int j = 0; j < box.size(); j++) {
                    var entry = box.get(j);
                    total += (i + 1) * (j + 1) * entry.value;
                }
            }
            return total;
        }

        void put(String key, int value) {
            var entry = new Entry(key, value);
            var box = boxes.get(hash(key));
            var index = box.indexOf(new ByKey(key));
            if (index == -1) {
                box.addLast(entry);
            } else {
                box.get(index).setValue(value);
            }
        }

        void remove(String key) {
            var box = boxes.get(hash(key));
            var index = box.indexOf(new ByKey(key));
            if (index != -1) {
                box.remove(index);
            }
        }

        void execute(Instruction instruction) {
            switch (instruction) {
                case Put(String key, int value) -> put(key, value);
                case Remove(String key) -> remove(key);
            }
        }
    }

    int part2(List<String> data) {
        var program = Arrays.stream(data.getFirst().split(",")).map(Instruction::parse).toList();
        var boxes = new Boxes();
        program.forEach(boxes::execute);
        return boxes.totalFocusingPower();
    }

    public static void main(String[] args) {
        var day15 = new Day15();
        var data = IO.getResourceAsList("day15.txt");
        var part1 = day15.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day15.part2(data);
        System.out.println("part2 = " + part2);
    }
}
