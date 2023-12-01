package aoc2023.utils;

import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class IO {
    public static List<String> getResourceAsImmutableList(String name) {
        var loader = IO.class.getClassLoader();
        try (var inputStream = loader.getResourceAsStream(name);
             var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(ImmutableList.toImmutableList());
        } catch (IOException e) {
            // Handle or log the exception appropriately
            e.printStackTrace();
            return ImmutableList.of();
        }
    }

    public static List<String> splitLinesAsImmutableList(String lines) {
        return Arrays.stream(lines.split("\n"))
                .collect(ImmutableList.toImmutableList());
    }
}
