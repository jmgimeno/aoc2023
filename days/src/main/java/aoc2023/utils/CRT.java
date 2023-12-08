package aoc2023.utils;

import java.util.Arrays;

public class CRT {

    // Chinese Remainder Theorem
    // - https://en.wikipedia.org/wiki/Chinese_remainder_theorem
    // - https://www.geeksforgeeks.org/implementation-of-chinese-remainder-theorem-inverse-modulo-based-implementation/
    // - https://www.youtube.com/watch?v=ru7mWZJlRQg

    // Extended Euclidean algorithm
    // - https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm

    // How to find modular inverse of a number
    // - https://www.geeksforgeeks.org/multiplicative-inverse-under-modulo-m/

    // number of equations
    final int k;

    // moduli
    long[] m;

    // remainders
    long[] r;

    public CRT(long[] r, long[] m) {
        assert m.length == r.length;
        this.k = m.length;
        this.m = Arrays.copyOf(m, k);
        this.r = Arrays.copyOf(r, k);
        sortByDecreasingModuli();
    }

    private void sortByDecreasingModuli() {
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                if (m[i] < m[j]) {
                    long tmp = m[i];
                    m[i] = m[j];
                    m[j] = tmp;
                    tmp = r[i];
                    r[i] = r[j];
                    r[j] = tmp;
                }
            }
        }
    }

    public long solveByBruteForce() {
        long M = Arrays.stream(m).reduce(1L, (a, b) -> a * b);
        for (long x = 0; x < M; x++) {
            boolean ok = true;
            for (int i = 0; i < k; i++) {
                if (x % m[i] != r[i]) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return x;
            }
        }
        throw new RuntimeException("No solution found");
    }

    public long solveBySieving() {
        long x = r[0];
        long n = m[0];
        int i = 1;
        while (i < k) {
            if (x % m[i] == r[i]) {
                n *= m[i];
                i++;
            } else {
                x += n;
            }
        }
        return x;
    }

    // Returns modulo inverse of a
    // with respect to m using extended
    // Euclid Algorithm.
    // https://www.geeksforgeeks.org/multiplicative-inverse-under-modulo-m/
    private static long modInverse(long r, long m) {
        long m0 = m, t, q;
        long x0 = 0L, x1 = 1L;
        if (m == 1L) return 0L;
        // Apply extended Euclid Algorithm
        while (r > 1) {
            q = r / m;
            t = m;
            // m is remainder now, process
            // same as euclid's algo
            m = r % m;
            r = t;

            t = x0;
            x0 = x1 - q * x0;
            x1 = t;
        }
        return x1 < 0 ? x1 + m0 : x1;
    }

    // k is size of num[] and rem[].
    // Returns the smallest number
    // x such that:
    // x % num[0] = rem[0],
    // x % num[1] = rem[1],
    // ..................
    // x % num[k-2] = rem[k-1]
    // Assumption: Numbers in num[] are pairwise
    // coprime (gcd for every pair is 1)
    // https://www.geeksforgeeks.org/implementation-of-chinese-remainder-theorem-inverse-modulo-based-implementation/

    public long solveByInverseModulo() {
        long M = Arrays.stream(m).reduce(1L, (a, b) -> a * b);
        long[] M_i = new long[k];
        for (int i = 0; i < k; i++) {
            M_i[i] = M / m[i];
        }
        long[] y_i = new long[k];
        for (int i = 0; i < k; i++) {
            y_i[i] = modInverse(M_i[i], m[i]);
        }
        long x = 0;
        for (int i = 0; i < k; i++) {
            x += r[i] * M_i[i] * y_i[i];
        }
        return x % M;
    }

}
