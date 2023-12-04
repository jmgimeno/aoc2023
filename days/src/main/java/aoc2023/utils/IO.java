package aoc2023.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class IO {
    public static List<String> getResourceAsList(String name) {
        var loader = IO.class.getClassLoader();
        try (var inputStream = loader.getResourceAsStream(name);
             var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().toList();
        } catch (IOException e) {
            // Handle or log the exception appropriately
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<String> splitLinesAsList(String lines) {
        return Arrays.stream(lines.split("\n"))
                .toList();
    }
}
