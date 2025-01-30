package studio.fantasyit.ars_botania.utils;

public class DoubleAccumulator {
    private double value;
    private final double fraction;

    public DoubleAccumulator(double oneInnerToXOuter) {
        this.value = 0;
        this.fraction = oneInnerToXOuter;
    }

    public int accumulateAndGet(double outerMana) {
        if (outerMana < 0) return takeAndGetCost(-outerMana);
        this.value += outerMana / fraction;
        int iMana = (int) Math.floor(this.value);
        this.value -= iMana;
        return iMana;
    }

    public int takeAndGetCost(double outerMana) {
        if (outerMana < 0) return accumulateAndGet(-outerMana);
        double innerRequest = outerMana / fraction;
        innerRequest -= this.value;
        int cost = (int) Math.ceil(innerRequest);
        this.value = innerRequest - cost;
        return cost;
    }

    public int calcCost(int outerMana) {
        double total = outerMana / fraction;
        total -= this.value;
        return (int) Math.ceil(total);
    }

    public int inner2outer(int innerMana) {
        return (int) Math.floor(innerMana * fraction);
    }

    public int outer2inner(int outerMana) {
        return (int) Math.floor(outerMana / fraction);
    }

    public double inner2outer(double innerMana) {
        return innerMana * fraction;
    }

    public double outer2inner(double outerMana) {
        return outerMana / fraction;
    }
}
