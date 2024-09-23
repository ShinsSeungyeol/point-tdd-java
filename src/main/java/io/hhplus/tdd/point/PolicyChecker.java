package io.hhplus.tdd.point;

public interface PolicyChecker {

    void checkChargePolicy(long remainingAmount, long amountToCharge);

    void checkUsePolicy(long remainingAmount, long amountToUse);
}
