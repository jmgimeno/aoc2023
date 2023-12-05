package aoc2023.meta;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.StringTemplate.STR;

public class DayGenerator {

    private static final int MAX_LINE_LENGTH = 100;
    private static final int INDENT = 4;

    static String generateProduction(int day, String instructions) {
        return STR
                . """
            package aoc2023.day\{ day };

            import aoc2023.utils.IO;
            import java.util.List;

            public class Day\{ day } {
                        
                /*
                \{ formatInstructions(instructions) }
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
                    var data = IO.getResourceAsList("day\{ day }.txt");
                    var part1 = day\{ day }.part1(data);
                    System.out.println("part1 = " + part1);
            //        var part2 = day\{ day }.part2(data);
            //        System.out.println("part2 = " + part2);
                }
            }
            """ ;
    }


    static String generateTest(int day) {
        return STR
                . """
                package aoc2023.day\{ day };
                
                import aoc2023.utils.IO;
                import org.junit.jupiter.api.Disabled;
                import org.junit.jupiter.api.DisplayName;
                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertEquals;

                class Day\{ day }Test {
                                
                    static final String example = \"""
                            \""";

                    static final Day\{ day } day\{ day } = new Day\{ day }();

                    @Test
                    @DisplayName("part1 - example data")
                    @Disabled("part1 - not implemented")
                    void test1() {
                        var data = IO.splitLinesAsList(example);
                        assertEquals(-1, day\{ day }.part1(data));
                    }

                    @Test
                    @DisplayName("part1 - input data")
                    @Disabled("part1 - not implemented")
                    void test2() {
                        var data = IO.getResourceAsList("day\{ day }.txt");
                        assertEquals(-1, day\{ day }.part1(data));
                    }

                    @Test
                    @DisplayName("part2 - example data")
                    @Disabled("part2 - not implemented")
                    void test3() {
                        var data = IO.splitLinesAsList(example);
                        assertEquals(-1, day\{ day }.part2(data));
                    }

                    @Test
                    @DisplayName("part2 - input data")
                    @Disabled("part2 - not implemented")
                    void test4() {
                        var data = IO.getResourceAsList("day\{ day }.txt");
                        assertEquals(-1, day\{ day }.part2(data));
                    }
                }
                """ ;
    }

    static String getInstructionsForPart1(int day) {
        // Hi, given the url https://adventofcode.com/2023/day/{day} for a given day,
        // I'd want to scrape the instructions (the article tag) using jsoup and return
        // them as a string without tags.
        try {
            String url = "https://adventofcode.com/2023/day/" + day;
            Document doc = Jsoup.connect(url).get();
            Elements articleElements = doc.select("article");
            List<String> elements = new ArrayList<>();
            for (Element element : articleElements.first().children()) {
                elements.add(element.text() + (element.tagName().equals("p") ? "\n" : ""));
            }
            return String.join("\n", elements);
        } catch (IOException e) {
            return "Sorry, no instructions found for day %d".formatted(day);
        }
    }

    static String formatInstructions(String instructions) {
        // Given the instructions as a string, I'd like to format them so that each line
        // starts fours spaces to the right and its max length is 80 characters.
        String[] lines = instructions.split("\n");
        List<String> formattedLines = new ArrayList<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            String formattedLine = "";
            List<String> formattedWords = getFormattedWords(words, formattedLine);
            formattedLines.add(formattedWords.stream()
                    .map(s -> "    " + s)
                    .collect(Collectors.joining("\n")));
        }
        return String.join("\n", formattedLines);
    }

    private static List<String> getFormattedWords(String[] words, String formattedLine) {
        List<String> formattedWords = new ArrayList<>();
        StringBuilder formattedLineBuilder = new StringBuilder(formattedLine);
        for (String word : words) {
            if (formattedLineBuilder.length() + word.length() + 1 <= MAX_LINE_LENGTH - INDENT) {
                formattedLineBuilder.append((formattedLineBuilder.isEmpty()) ? "" : " ").append(word);
            } else {
                formattedWords.add(formattedLineBuilder.toString());
                formattedLineBuilder = new StringBuilder(word);
            }
        }
        formattedLine = formattedLineBuilder.toString();
        if (!formattedLine.isEmpty()) {
            formattedWords.add(formattedLine);
        }
        return formattedWords;
    }

    static void generate(int day) throws IOException {
        var mainPackage = Path.of("days/src/main/java/aoc2023/day%d".formatted(day));
        var testPackage = Path.of("days/src/test/java/aoc2023/day%d".formatted(day));
        if (mainPackage.toFile().mkdir() && testPackage.toFile().mkdir()) {
            var instructions = getInstructionsForPart1(day);
            var dayClass = mainPackage.resolve(Path.of("Day%d.java".formatted(day)));
            var testClass = testPackage.resolve(Path.of("Day%dTest.java".formatted(day)));
            Files.writeString(dayClass, generateProduction(day, instructions));
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
