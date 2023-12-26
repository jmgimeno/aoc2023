package aoc2023.day20;

import aoc2023.utils.GCRT;
import aoc2023.utils.IO;

import java.util.*;

public class Day20 {

    /*
        --- Day 20: Pulse Propagation ---
    With your help, the Elves manage to find the right parts and fix all of the machines. Now, they
    just need to send the command to boot up the machines and get the sand flowing again.

    The machines are far apart and wired together with long cables. The cables don't connect to the
    machines directly, but rather to communication modules attached to the machines that perform
    various initialization tasks and also act as communication relays.

    Modules communicate using pulses. Each pulse is either a high pulse or a low pulse. When a
    module sends a pulse, it sends that type of pulse to each module in its list of destination
    modules.

    There are several different types of modules:

    Flip-flop modules (prefix %) are either on or off; they are initially off. If a flip-flop module
    receives a high pulse, it is ignored and nothing happens. However, if a flip-flop module
    receives a low pulse, it flips between on and off. If it was off, it turns on and sends a high
    pulse. If it was on, it turns off and sends a low pulse.

    Conjunction modules (prefix &) remember the type of the most recent pulse received from each of
    their connected input modules; they initially default to remembering a low pulse for each input.
    When a pulse is received, the conjunction module first updates its memory for that input. Then,
    if it remembers high pulses for all inputs, it sends a low pulse; otherwise, it sends a high
    pulse.

    There is a single broadcast module (named broadcaster). When it receives a pulse, it sends the
    same pulse to all of its destination modules.

    Here at Desert Machine Headquarters, there is a module with a single button on it called, aptly,
    the button module. When you push the button, a single low pulse is sent directly to the
    broadcaster module.

    After pushing the button, you must wait until all pulses have been delivered and fully handled
    before pushing it again. Never push the button if modules are still processing pulses.

    Pulses are always processed in the order they are sent. So, if a pulse is sent to modules a, b,
    and c, and then module a processes its pulse and sends more pulses, the pulses sent to modules b
    and c would have to be handled first.

    The module configuration (your puzzle input) lists each module. The name of the module is
    preceded by a symbol identifying its type, if any. The name is then followed by an arrow and a
    list of its destination modules. For example:

    broadcaster -> a, b, c
    %a -> b
    %b -> c
    %c -> inv
    &inv -> a
    In this module configuration, the broadcaster has three destination modules named a, b, and c.
    Each of these modules is a flip-flop module (as indicated by the % prefix). a outputs to b which
    outputs to c which outputs to another module named inv. inv is a conjunction module (as
    indicated by the & prefix) which, because it has only one input, acts like an inverter (it sends
    the opposite of the pulse type it receives); it outputs to a.

    By pushing the button once, the following pulses are sent:

    button -low-> broadcaster
    broadcaster -low-> a
    broadcaster -low-> b
    broadcaster -low-> c
    a -high-> b
    b -high-> c
    c -high-> inv
    inv -low-> a
    a -low-> b
    b -low-> c
    c -low-> inv
    inv -high-> a
    After this sequence, the flip-flop modules all end up off, so pushing the button again repeats
    the same sequence.

    Here's a more interesting example:

    broadcaster -> a
    %a -> inv, con
    &inv -> b
    %b -> con
    &con -> output
    This module configuration includes the broadcaster, two flip-flops (named a and b), a
    single-input conjunction module (inv), a multi-input conjunction module (con), and an untyped
    module named output (for testing purposes). The multi-input conjunction module con watches the
    two flip-flop modules and, if they're both on, sends a low pulse to the output module.

    Here's what happens if you push the button once:

    button -low-> broadcaster
    broadcaster -low-> a
    a -high-> inv
    a -high-> con
    inv -low-> b
    con -high-> output
    b -high-> con
    con -low-> output
    Both flip-flops turn on and a low pulse is sent to output! However, now that both flip-flops are
    on and con remembers a high pulse from each of its two inputs, pushing the button a second time
    does something different:

    button -low-> broadcaster
    broadcaster -low-> a
    a -low-> inv
    a -low-> con
    inv -high-> b
    con -high-> output
    Flip-flop a turns off! Now, con remembers a low pulse from module a, and so it sends only a high
    pulse to output.

    Push the button a third time:

    button -low-> broadcaster
    broadcaster -low-> a
    a -high-> inv
    a -high-> con
    inv -low-> b
    con -low-> output
    b -low-> con
    con -high-> output
    This time, flip-flop a turns on, then flip-flop b turns off. However, before b can turn off, the
    pulse sent to con is handled first, so it briefly remembers all high pulses for its inputs and
    sends a low pulse to output. After that, flip-flop b turns off, which causes con to update its
    state and send a high pulse to output.

    Finally, with a on and b off, push the button a fourth time:

    button -low-> broadcaster
    broadcaster -low-> a
    a -low-> inv
    a -low-> con
    inv -high-> b
    con -high-> output
    This completes the cycle: a turns off, causing con to remember only low pulses and restoring all
    modules to their original states.

    To get the cables warmed up, the Elves have pushed the button 1000 times. How many pulses got
    sent as a result (including the pulses sent by the button itself)?

    In the first example, the same thing happens every time the button is pushed: 8 low pulses and 4
    high pulses are sent. So, after pushing the button 1000 times, 8000 low pulses and 4000 high
    pulses are sent. Multiplying these together gives 32000000.

    In the second example, after pushing the button 1000 times, 4250 low pulses and 2750 high pulses
    are sent. Multiplying these together gives 11687500.

    Consult your module configuration; determine the number of low pulses and high pulses that would
    be sent after pushing the button 1000 times, waiting for all pulses to be fully handled after
    each push of the button. What do you get if you multiply the total number of low pulses sent by
    the total number of high pulses sent?

    Your puzzle answer was 666795063.
    */

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

    /*
    --- Part Two ---
    The final machine responsible for moving the sand down to Island Island has a module attached
    named rx. The machine turns on when a single low pulse is sent to rx.

    Reset all modules to their default states. Waiting for all pulses to be fully handled after each button
    press, what is the fewest number of button presses required to deliver a single low pulse to the module
    named rx?
     */

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
