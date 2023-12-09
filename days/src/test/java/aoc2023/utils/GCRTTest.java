package aoc2023.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GCRTTest {

    @Test
    @DisplayName("Extended Euclidean")
    void test1() {
        var result = GCRT.extendedEuclidean(240, 46);
        var expected = new GCRT.ExtendedGcd(2, -9, 47);
        assertEquals(expected, result);
        assertEquals(result.gcd(), 240 * result.s() + 46 * result.t());
    }

    @Test
    @DisplayName("Generalized Chinese (two equations)")
    void test2() {
        var r = new long[]{5, 3};
        var m = new long[]{6, 8};
        var gcrt = new GCRT(r, m);
        assertEquals(new GCRT.Result(11, 24), gcrt.solve());
    }

    @Test
    @DisplayName("Generalized Chinese (five)")
    void test3() {
        var r = new long[]{1, 1, 1, 1, 1};
        var m = new long[]{2, 3, 4, 5, 6};
        var gcrt = new GCRT(r, m);
        assertEquals(new GCRT.Result(1, 60), gcrt.solve());
    }

    @Test
    @DisplayName("Generalized Chinese with coprime moduli")
    void test4() {
        var r = new long[]{2, 3, 2};
        var m = new long[]{3, 5, 7};
        var gcrt = new GCRT(r, m);
        assertEquals(new GCRT.Result(23, 105), gcrt.solve());
    }
}
