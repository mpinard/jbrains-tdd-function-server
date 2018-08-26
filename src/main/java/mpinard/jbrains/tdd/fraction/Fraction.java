package mpinard.jbrains.tdd.fraction;

import lombok.Value;

import java.beans.ConstructorProperties;

@Value
public class Fraction {
    private int numerator;
    private int denominator;

    @ConstructorProperties({"numerator", "denominator"})
    private Fraction(final int numerator, final int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("The denominator cannot be zero");
        }
        
        final SimpleFraction reduced =
            SimpleFraction.of(
                isPositive(numerator, denominator) ? Math.abs(numerator) : Math.negateExact(Math.abs(numerator)),
                Math.abs(denominator)).reduce();

        this.numerator = reduced.numerator;
        this.denominator = reduced.denominator;
    }

    public static Fraction of(final int wholeNumber) {
        return Fraction.of(wholeNumber, 1);
    }

    public static Fraction of(final int theNumerator, final int theDenominator) {
        return new Fraction(theNumerator, theDenominator);
    }

    private static Fraction from(final SimpleFraction simpleFraction) {
        return Fraction.of(simpleFraction.numerator, simpleFraction.denominator);
    }

    public Fraction plus(final Fraction addend) {
        return Fraction.from(this.toSimpleFraction().plus(addend.toSimpleFraction()));
    }

    @Override
    public String toString() {
        return denominator == 1
            ? String.valueOf(numerator)
            : String.format("%s/%s", numerator, denominator);
    }

    private boolean isPositive(final int theNumerator, final int theDenominator) {
        return theNumerator >= 0
            ? theDenominator > 0
            : theDenominator < 0;
    }
    
    private SimpleFraction toSimpleFraction() {
        return SimpleFraction.of(numerator, denominator);
    }

    /**
     * Simpler version of {@link Fraction} that allows unreduced values for use in calculations, while {@code Fraction} will always reduce as
     * as much as possible during creation.
     */
    @Value(staticConstructor = "of")
    private static class SimpleFraction {
        private int numerator;
        private int denominator;
        
        private SimpleFraction plus(final SimpleFraction addend) {
            final SimpleFraction resolvedAugend;
            final SimpleFraction resolvedAddend;

            if (denominator == addend.denominator) {
                resolvedAugend = this;
                resolvedAddend = addend;
            } else {
                final int commonDenominator = this.denominator * addend.denominator;

                resolvedAugend =
                    SimpleFraction.of(
                        this.numerator * addend.denominator,
                        commonDenominator);
                resolvedAddend =
                    SimpleFraction.of(
                        addend.numerator * this.denominator,
                        commonDenominator);
            }

            return SimpleFraction.of(resolvedAugend.numerator + resolvedAddend.numerator, resolvedAugend.denominator);
        }
        
        private SimpleFraction reduce() {
            if (this.numerator % 2 == 0 && this.denominator % 2 == 0) {
                return SimpleFraction.of(this.numerator / 2, this.denominator / 2);
            }
            
            return this;
        }
    }
    
}

