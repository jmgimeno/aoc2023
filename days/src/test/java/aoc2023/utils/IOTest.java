package aoc2023.utils;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOTest {

    @Test
    void readResource() {
        var contents = IO.getResourceAsImmutableList("resource_as_immutable_list.txt");
        var expected = ImmutableList.of("alpha", "beta", "gamma");
        assertEquals(expected, contents);
    }
}