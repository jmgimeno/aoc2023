package aoc2023.day2;

import aoc2023.utils.IO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day2Test {

    static final String example = """
            Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
            """;

    static final Day2 day2 = new Day2();

    @Test
    @DisplayName("part1 - example data")
    void test1() {
        var data = IO.splitLinesAsImmutableList(example);
        assertEquals(8, day2.part1(data));
    }

    @Test
    @DisplayName("part1 - input data")
    void test2() {
        var data = IO.getResourceAsImmutableList("day2.txt");
        assertEquals(3099, day2.part1(data));
    }

    @Test
    @DisplayName("part2 - example data")
    void test3() {
        var data = IO.splitLinesAsImmutableList(example);
        assertEquals(2286, day2.part2(data));
    }

    @Test
    @DisplayName("part2 - input data")
    void test4() {
        var data = IO.getResourceAsImmutableList("day2.txt");
        assertEquals(72970, day2.part2(data));
    }

    @Test
    @DisplayName("parse - example data - game 1")
    void test5() {
        var game = Day2.Game.parse("Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green");
        assertEquals(1, game.id());
        assertEquals(4, game.cubes().get(Day2.Color.RED));
        assertEquals(2, game.cubes().get(Day2.Color.GREEN));
        assertEquals(6, game.cubes().get(Day2.Color.BLUE));
    }

    @Test
    @DisplayName("parse - example data - game 2")
    void test6() {
        var game = Day2.Game.parse("Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue");
        assertEquals(2, game.id());
        assertEquals(1, game.cubes().get(Day2.Color.RED));
        assertEquals(3, game.cubes().get(Day2.Color.GREEN));
        assertEquals(4, game.cubes().get(Day2.Color.BLUE));
    }

    @Test
    @DisplayName("power - example data - game 1")
    void test7() {
        var game = Day2.Game.parse("Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green");
        assertEquals(48, game.power());
    }
}
