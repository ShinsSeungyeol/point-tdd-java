package io.hhplus.tdd.point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PolicyCheckerTest {

    private final PolicyChecker policyChecker = new PolicyCheckerImpl();

    /**
     * <p>{@code PointChecker.checkChargePolicy} 메서드에 대한 테스트입니다.</p>
     *
     * <pre>사용자가 충전할 때, 10000 포인트를 초과하여 충전할 수 없습니다. </pre>
     */
    @Test
    public void 포인트_충전은_최대_잔고량_제한을_넘으면_안된다() {
        long amountToCharge = PointConstant.MAX_BALANCE_AMOUNT_LIMIT + 1, userId = 1;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            policyChecker.checkChargePolicy(0, amountToCharge);

        });
    }

    /**
     * <p>{@code ointChecker.checkChargePolicy} 메서드에 대한 테스트입니다.</p>
     *
     * <pre>사용자가 충전할 때, 0 포인트 이하는 충전할 수 없습니다. </pre>
     */
    @Test
    public void 충전하려는_포인트가_0_이하면_안된다() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            policyChecker.checkChargePolicy(0, 0);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            policyChecker.checkChargePolicy(-1, 0);
        });
    }
}