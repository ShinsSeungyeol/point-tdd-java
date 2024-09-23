package io.hhplus.tdd.point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PolicyCheckerTest {

    private final PolicyChecker policyChecker = new PolicyCheckerImpl();

    /**
     * 충전 정책 중, 포인트 충전은 최대 잔고량 제한을 넘으면 안되는 정책 단위 테스트 함수
     */
    @Test
    public void 포인트_충전은_최대_잔고량_제한을_넘으면_안된다() {
        long amountToCharge = PointConstant.MAX_BALANCE_AMOUNT_LIMIT + 1, userId = 1;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            policyChecker.checkChargePolicy(0, amountToCharge);

        });
    }

    /**
     * 충전 정책 중, 충전 하려는 포인트가 0 이하면 안되는 정책 단위 테스트 함수
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