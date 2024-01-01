package aoc2023.utils;

import java.math.BigInteger;

public record BigRational(BigInteger numerator, BigInteger denominator)
        implements Comparable<BigRational> {

    public static BigRational ZERO = new BigRational(BigInteger.ZERO);
    public static BigRational ONE = new BigRational(BigInteger.ONE);

    public BigRational {
        if (denominator.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("denominator is zero");
        }
        if (denominator.compareTo(BigInteger.ZERO) < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
        var gcd = numerator.gcd(denominator);
        if (!gcd.equals(BigInteger.ONE)) {
            numerator = numerator.divide(gcd);
            denominator = denominator.divide(gcd);
        }
    }

    public BigRational(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    public BigRational(long numerator, long denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public BigRational(long numerator) {
        this(BigInteger.valueOf(numerator), BigInteger.ONE);
    }

    public BigRational add(BigRational o) {
        return new BigRational(
                numerator.multiply(o.denominator).add(denominator.multiply(o.numerator)),
                denominator.multiply(o.denominator));
    }

    public BigRational subtract(BigRational o) {
        return new BigRational(numerator.multiply(
                o.denominator).subtract(denominator.multiply(o.numerator)),
                denominator.multiply(o.denominator));
    }

    public BigRational multiply(BigRational o) {
        return new BigRational(
                numerator.multiply(o.numerator),
                denominator.multiply(o.denominator));
    }

    public BigRational divide(BigRational o) {
        return new BigRational(
                numerator.multiply(o.denominator),
                denominator.multiply(o.numerator));
    }

    @Override
    public int compareTo(BigRational o) {
        return numerator.multiply(o.denominator)
                .compareTo(denominator.multiply(o.numerator));
    }
}
