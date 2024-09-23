package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class PolicyCheckerImpl implements PolicyChecker {

    @Override
    public void checkChargePolicy(long remainingAmount, long amountToCharge) {
        checkAmountIsGreaterThanZero(amountToCharge);
        checkBalanceAmountNotExceedsLimit(remainingAmount + amountToCharge);
    }

    @Override
    public void checkUsePolicy(long remainingAmount, long amountToUse) {
        checkAmountIsGreaterThanZero(amountToUse);
        checkSufficientRemainingAmount(remainingAmount, amountToUse);
    }

    private void checkAmountIsGreaterThanZero(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("0 이하의 포인트를 충전할 수 없습니다.");
        }
    }

    private void checkBalanceAmountNotExceedsLimit(long balanceAmount) {
        if (balanceAmount > PointConstant.MAX_BALANCE_AMOUNT_LIMIT) {
            throw new IllegalStateException("최대 잔고를 초과하여, 충전할 수 없습니다.");
        }
    }

    private void checkSufficientRemainingAmount(long remainingAmount, long amountToUse) {
        if (remainingAmount < amountToUse) {
            throw new IllegalStateException("잔고가 부족합니다.");
        }
    }

}
