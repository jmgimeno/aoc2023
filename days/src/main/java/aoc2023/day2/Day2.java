package aoc2023.day2;

import aoc2023.utils.IO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2 {

    enum Color {
        RED, GREEN, BLUE
    }

    record Game(int id, Map<Color, Integer> cubes) {

        static final int MAX_RED = 12;
        static final int MAX_GREEN = 13;
        static final int MAX_BLUE = 14;

        boolean isValid() {
            return cubes.getOrDefault(Color.RED, 0) <= MAX_RED
                    && cubes.getOrDefault(Color.GREEN, 0) <= MAX_GREEN
                    && cubes.getOrDefault(Color.BLUE, 0) <= MAX_BLUE;
        }

        int power() {
            return cubes.getOrDefault(Color.RED, 0)
                    * cubes.getOrDefault(Color.GREEN, 0)
                    * cubes.getOrDefault(Color.BLUE, 0);
        }

        static Game parse(String line) {
            // parses using a regular expression the line and returns the maximum number of cubes if
            // each color that are present in the line
            // Lines have the format:
            //    Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            //    Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            //    Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            //    Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            //    Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
            // the result, for the first one, would be:
            //    RED -> 4, GREEN -> 2, BLUE -> 6

            Map<Color, Integer> counters = new HashMap<>();
            String regex = "Game (\\d+):|((\\d+)\\s+(blue|red|green))";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);

            int gameId = 0;
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    gameId = Integer.parseInt(matcher.group(1));
                } else {
                    int number = Integer.parseInt(matcher.group(3));
                    Color color = Color.valueOf(matcher.group(4).toUpperCase());
                    counters.put(color, Math.max(counters.getOrDefault(color, 0), number));
                }
            }
            return new Game(gameId, counters);
        }
    }

    int part1(List<String> data) {
        return data.stream()
                .map(Game::parse)
                .filter(Game::isValid)
                .mapToInt(Game::id)
                .sum();
    }

    int part2(List<String> data) {
        return data.stream()
                .map(Game::parse)
                .mapToInt(Game::power)
                .sum();
    }

    public static void main(String[] args) {
        var day2 = new Day2();
        var data = IO.getResourceAsList("day2.txt");
        var part1 = day2.part1(data);
        System.out.println("part1 = " + part1);
        var part2 = day2.part2(data);
        System.out.println("part2 = " + part2);
    }
}
