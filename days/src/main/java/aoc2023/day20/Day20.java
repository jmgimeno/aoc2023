package aoc2023.day20;

import aoc2023.utils.GCRT;
import aoc2023.utils.IO;

import java.util.*;

public class Day20 {

    enum Pulse {
        HIGH, LOW
    }

    static class QueueCounter {
        long countLow = 0L;
        long countHight = 0L;

        final Queue<Configuration.Message> queue = new LinkedList<>();

        void add(Configuration.Message message) {
            if (message.pulse == Pulse.LOW) {
                countLow++;
            } else {
                countHight++;
            }
            queue.add(message);
        }

        Configuration.Message poll() {
            return queue.poll();
        }

        boolean isEmpty() {
            return queue.isEmpty();
        }

        long part1() {
            return countLow * countHight;
        }
    }

    static abstract class Module {
        final protected String name;
        final protected List<String> destinations;

        Module(String name, List<String> destinations) {
            this.name = name;
            this.destinations = destinations;
        }

        abstract List<Configuration.Message> process(String from, Pulse pulse);
    }

    static class Broadcaster extends Module {
        Broadcaster(String name, List<String> destinations) {
            super(name, destinations);
        }

        @Override
        List<Configuration.Message> process(String from, Pulse pulse) {
            return destinations.stream().map(d -> new Configuration.Message(name, d, pulse)).toList();
        }

        @Override
        public String toString() {
            return "Broadcaster{" +
                    "name='" + name + '\'' +
                    ", destinations=" + destinations +
                    '}';
        }
    }

    static class FlipFlop extends Module {
        Pulse state;

        FlipFlop(String name, List<String> destinations) {
            super(name, destinations);
            state = Pulse.LOW;
        }

        @Override
        List<Configuration.Message> process(String from, Pulse pulse) {
            if (pulse == Pulse.LOW) {
                state = state == Pulse.LOW ? Pulse.HIGH : Pulse.LOW;
                return destinations.stream().map(d -> new Configuration.Message(name, d, state)).toList();
            } else {
                return List.of();
            }
        }

        @Override
        public String toString() {
            return "FlipFlop{" +
                    "name='" + name + '\'' +
                    ", destinations=" + destinations +
                    ", state=" + state +
                    '}';
        }
    }

    static class Conjunction extends Module {
        Map<String, Pulse> state;

        Conjunction(String name, List<String> destinations) {
            super(name, destinations);
            state = new HashMap<>();
        }

        void addInput(String input) {
            state.put(input, Pulse.LOW);
        }

        @Override
        List<Configuration.Message> process(String from, Pulse pulse) {
            state.put(from, pulse);
            if (state.values().stream().allMatch(p -> p == Pulse.HIGH)) {
                return destinations.stream().map(d -> new Configuration.Message(name, d, Pulse.LOW)).toList();
            } else {
                return destinations.stream().map(d -> new Configuration.Message(name, d, Pulse.HIGH)).toList();
            }
        }

        @Override
        public String toString() {
            return "Conjunction{" +
                    "name='" + name + '\'' +
                    ", destinations=" + destinations +
                    ", state=" + state +
                    '}';
        }

        static class Untyped extends Module {
            Untyped(String name) {
                super(name, List.of());
            }

            @Override
            List<Configuration.Message> process(String from, Pulse pulse) {
                return List.of();
            }

            @Override
            public String toString() {
                return "Untyped{" +
                        "name='" + name +
                        '}';
            }
        }
    }

    static class Button {
        final Module broadcaster;

        Button(Module broadcaster) {
            this.broadcaster = broadcaster;
        }

        Configuration.Message push() {
            return new Configuration.Message("button", "broadcaster", Pulse.LOW);
        }

        @Override
        public String toString() {
            return "Button{" +
                    "broadcaster=" + broadcaster +
                    '}';
        }
    }

    static class Configuration {
        final Map<String, Module> modules;
        final Button button;

        Configuration(Map<String, Module> modules, Button button) {
            this.modules = modules;
            this.button = button;
        }

        static Configuration parse(List<String> data) {
            var modules = new HashMap<String, Module>();
            for (var line : data) {
                var parts = line.split("->");
                var name = parts[0].trim();
                var destinations = Arrays.stream(parts[1].split(",")).map(String::trim).toList();
                var module = parseModule(name, destinations);
                modules.put(module.name, module);
            }
            var button = new Button(modules.get("broadcaster"));
            addInputs(modules);
            return new Configuration(modules, button);
        }

        private static void addInputs(HashMap<String, Module> modules) {
            for (var module : modules.values()) {
                for (var destination : module.destinations) {
                    var destinationModule = modules.get(destination);
                    if (destinationModule instanceof Conjunction conjunction) {
                        conjunction.addInput(module.name);
                    }
                }
            }
        }

        private static Module parseModule(String name, List<String> destinations) {
            if (name.startsWith("%")) {
                return new FlipFlop(name.substring(1), destinations);
            } else if (name.startsWith("&")) {
                return new Conjunction(name.substring(1), destinations);
            } else if (name.equals("broadcaster")) {
                return new Broadcaster(name, destinations);
            } else {
                throw new IllegalArgumentException("Unknown module type: " + name);
            }
        }

        record Message(String origin, String destination, Pulse pulse) {
        }

        final QueueCounter ongoing = new QueueCounter();

        void pushButtonOnce() {
            ongoing.add(button.push());
            while (!ongoing.isEmpty()) {
                var message = ongoing.poll();
                var module = modules.getOrDefault(message.destination, new Conjunction.Untyped(message.destination));
                var messages = module.process(message.origin, message.pulse);
                messages.forEach(ongoing::add);
            }
        }

        void pushButtonMany(int times) {
            for (int i = 0; i < times; i++) {
                pushButtonOnce();
            }
        }

        @Override
        public String toString() {
            return "Configuration{" +
                    "modules=" + modules +
                    ", button=" + button +
                    '}';
        }

        List<Long> whenHigh(Set<String> names) {
            names = new HashSet<>(names);
            var counters = new ArrayList<Long>();
            long counter = 0;
            do {
                ongoing.add(button.push());
                counter++;
                while (!ongoing.isEmpty()) {
                    var message = ongoing.poll();
                    if (names.contains(message.origin) && message.pulse == Pulse.HIGH) {
                        names.remove(message.origin);
                        counters.add(counter);
                    }
                    var module = modules.getOrDefault(message.destination, new Conjunction.Untyped(message.destination));
                    var messages = module.process(message.origin, message.pulse);
                    messages.forEach(ongoing::add);
                }
            } while (!names.isEmpty());
            return counters;
        }
    }

    long part1(List<String> data) {
        var configuration = Configuration.parse(data);
        configuration.pushButtonMany(1000);
        return configuration.ongoing.part1();
    }

    long part2(List<String> data) {
        // &df -> rx
        // rx will receive a low pulse when all inputs of df are high
        // &xl -> df
        // &ln -> df
        // &xp -> df
        // &gp -> df
        // so we need to know when all of them are high -> lcm of their frequencies
        // NOTE: I'm not sure why the first appearance of the HIGH puls in each module is exactly
        // its frequency, but it works !!!
        var configuration = Configuration.parse(data);
        return GCRT.lcm(configuration.whenHigh(Set.of("xl", "ln", "xp", "gp")));
    }

    public static void main(String[] args) {
        var day20 = new Day20();
        var data = IO.getResourceAsList("day20.txt");
        var part1 = day20.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day20.part2(data);
        System.out.println("part2 = " + part2);
    }
}
