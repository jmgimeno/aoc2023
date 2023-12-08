package aoc2023.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CRTTest {

    final long[] r1 = {0, 3, 4};
    final long[] m1 = {3, 4, 5};

    final long[] r2 = {2, 3, 1};
    final long[] m2 = {3, 4, 5};

    @Test
    @DisplayName("Test brute force on problem 1")
    void bruteforce1() {
        CRT crt = new CRT(r1, m1);
        assertEquals(39, crt.solveByBruteForce());
    }

    @Test
    @DisplayName("Test brute force on problem 2")
    void bruteforce2() {
        CRT crt = new CRT(r2, m2);
        assertEquals(11, crt.solveByBruteForce());
    }

    @Test
    @DisplayName("Test sieving on problem 1")
    void sieving1() {
        CRT crt = new CRT(r1, m1);
        assertEquals(39, crt.solveBySieving());
    }

    @Test
    @DisplayName("Test sieving on problem 2")
    void sieving2() {
        CRT crt = new CRT(r2, m2);
        assertEquals(11, crt.solveBySieving());
    }

    @Test
    @DisplayName("Test solver on problem 1")
    void solve1() {
        CRT crt = new CRT(r1, m1);
        assertEquals(39, crt.solveByInverseModulo());
    }

    @Test
    @DisplayName("Test solver on problem 2")
    void solve2() {
        CRT crt = new CRT(r2, m2);
        assertEquals(11, crt.solveByInverseModulo());
    }
}
