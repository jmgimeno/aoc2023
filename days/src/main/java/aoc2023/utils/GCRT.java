package aoc2023.utils;

import java.util.Arrays;

public class GCRT {
    // Generalized Chinese Remainder Theorem
    // - https://en.wikipedia.org/wiki/Chinese_remainder_theorem#Generalization_to_non-coprime_moduli
    // - https://math.stackexchange.com/questions/1644677/what-to-do-if-the-modulus-is-not-coprime-in-the-chinese-remainder-theorem


    // number of equations
    final int k;

    // moduli (not necessarily coprime)
    long[] m;

    // remainders
    long[] r;

    public GCRT(long[] r, long[] m) {
        assert m.length == r.length;
        this.k = m.length;
        this.m = Arrays.copyOf(m, k);
        this.r = Arrays.copyOf(r, k);
    }

    public record Result(long r, long m) {}

    public Result solve() {
        long r0 = r[0];
        long m0 = m[0];
        for (int i = 1; i < k; i++) {
            var res = extendedEuclidean(m0, m[i]);
            assert (r0 - r[i]) % res.gcd() == 0: "not soluble";
            var lambda = (r0 - r[i]) / res.gcd();
            var sigma = r[i] + m[i] * lambda * res.t();
            m0 = (m0 * m[i]) / res.gcd();
            r0 = sigma < 0 ? sigma + m0 : sigma;
        }
        return new Result(r0, m0);
    }

    // Extended Euclidean Algorithm
    // - https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm

    public record ExtendedGcd(long gcd, long s, long t) {}

    public static ExtendedGcd extendedEuclidean(long a, long b) {
        long r0 = a;
        long r1 = b;
        long s0 = 1L;
        long s1 = 0L;
        long t0 = 0L;
        long t1 = 1L;
        while (r1 != 0) {
            long q = r0 / r1;
            long r2 = r0 - q * r1;
            long s2 = s0 - q * s1;
            long t2 = t0 - q * t1;
            r0 = r1;
            r1 = r2;
            s0 = s1;
            s1 = s2;
            t0 = t1;
            t1 = t2;
        }
        return new ExtendedGcd(r0, s0, t0);
    }
}
