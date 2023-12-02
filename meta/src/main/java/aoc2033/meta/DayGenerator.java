package aoc2033.meta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.StringTemplate.STR;

public class DayGenerator {

    static String generateProduction(int day) {
        return STR
                . """
            package aoc2023.day\{ day };

            import aoc2023.utils.IO;
            import java.util.List;

            public class Day\{ day } {
                        
                /*

                */

                int part1(List<String> data) {
                    throw new UnsupportedOperationException("part1");
                }

                /*

                */

                int part2(List<String> data) {
                    throw new UnsupportedOperationException("part2");
                }

                public static void main(String[] args) {
                    var day\{ day } = new Day\{ day }();
                    var data = IO.getResourceAsImmutableList("day\{ day }.txt");
                    var part1 = day\{ day }.part1(data);
                    System.out.println("part1 = " + part1);
                    var part2 = day\{ day }.part2(data);
                    System.out.println("part2 = " + part2);
                }
            }
            """ ;
    }


    static String generateTest(int day) {
        return STR
                . """
                package aoc2023.day\{ day };
                
                import aoc2023.utils.IO;
                import org.junit.jupiter.api.DisplayName;
                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertEquals;

                class Day\{ day }Test {
                                
                    static final String example = \"""
                            \""";

                    static final Day\{ day } day\{ day } = new Day\{ day }();

                    @Test
                    @DisplayName("part1 - example data")
                    void test1() {
                        var data = IO.splitLinesAsImmutableList(example);
                        assertEquals(-1, day\{ day }.part1(data));
                    }

                    @Test
                    @DisplayName("part1 - input data")
                    void test2() {
                        var data = IO.getResourceAsImmutableList("day\{ day }.txt");
                        assertEquals(-1, day\{ day }.part1(data));
                    }

                    @Test
                    @DisplayName("part2 - example data")
                    void test3() {
                        var data = IO.splitLinesAsImmutableList(example);
                        assertEquals(-1, day\{ day }.part2(data));
                    }

                    @Test
                    @DisplayName("part2 - input data")
                    void test4() {
                        var data = IO.getResourceAsImmutableList("day\{ day }.txt");
                        assertEquals(-1, day\{ day }.part2(data));
                    }
                }
                """ ;
    }

    static void generate(int day) throws IOException {
        var mainPackage = Path.of("days/src/main/java/aoc2023/day%d".formatted(day));
        var testPackage = Path.of("days/src/test/java/aoc2023/day%d".formatted(day));
        if (mainPackage.toFile().mkdir() && testPackage.toFile().mkdir()) {
            var dayClass = mainPackage.resolve(Path.of("Day%d.java".formatted(day)));
            var testClass = testPackage.resolve(Path.of("Day%dTest.java".formatted(day)));
            Files.writeString(dayClass, generateProduction(day));
            Files.writeString(testClass, generateTest(day));
            System.out.printf("Enjoy your newly created day %d%n", day);
        } else {
            System.out.printf("Sorry, day %d already exists%n", day);
        }
    }

    public static void main(String[] args) throws IOException {
        var scanner = new Scanner(System.in);
        System.out.print("Which day do you want to generate? ");
        var day = scanner.nextInt();
        generate(day);
    }
}
