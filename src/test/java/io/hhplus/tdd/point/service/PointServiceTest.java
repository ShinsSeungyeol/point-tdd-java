package io.hhplus.tdd.point.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.point.PolicyChecker;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PointServiceTest {

    @InjectMocks
    private PointServiceImpl pointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PolicyChecker policyChecker;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * <p>포인트 조회 정상 동작 단위 테스트</p>
     */
    @Test
    public void 포인트_조회_테스트() {
        long userId = 1L;

        when(userPointRepository.findByUserId(userId)).thenReturn(UserPoint.empty(1));

        UserPoint actualUserPoint = pointService.searchUserPoint(1);

        verify(userPointRepository).findByUserId(userId);

        Assertions.assertEquals(userId, actualUserPoint.id());
        Assertions.assertEquals(0, actualUserPoint.point());
    }

    /**
     * 포인트 히스토리 목록 조회 정상 동작 단위 테스트
     */
    @Test
    public void 포인트_히스토리_목록_조회_테스트() {
        long userId = 1L;

        when(pointHistoryRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<PointHistory> actualPointHistories = pointService.searchPointHistories(1);

        verify(pointHistoryRepository).findAllByUserId(userId);

        Assertions.assertEquals(0, actualPointHistories.size());
    }


    /**
     * 포인트 충전 정상 동작 단위 테스트
     */
    @Test
    public void 포인트_충전_테스트() {
        long userId = 1L;
        long amountToCharge = 300L;
        long remainingAmount = 400L;

        UserPoint remainingUserPoint = new UserPoint(userId, remainingAmount,
            System.currentTimeMillis());

        when(userPointRepository.findByUserId(userId)).thenReturn(remainingUserPoint);
        when(userPointRepository.save(userId, remainingAmount + amountToCharge)).thenReturn(
            new UserPoint(userId, remainingAmount + amountToCharge, System.currentTimeMillis())
        );

        UserPoint chargedUserPoint = pointService.chargeUserPoint(userId, amountToCharge);

        verify(policyChecker).checkChargePolicy(remainingAmount, amountToCharge);
        verify(pointHistoryRepository).save(eq(userId), eq(amountToCharge),
            eq(TransactionType.CHARGE));
        verify(userPointRepository).save(userId, remainingAmount + amountToCharge);

        Assertions.assertEquals(userId, chargedUserPoint.id());
        Assertions.assertEquals(remainingAmount + amountToCharge, chargedUserPoint.point());
    }

    /**
     * 포인트 사용 정상 동작 단위 테스트
     */
    @Test
    public void 포인트_사용_테스트() {
        long amountToUse = 300, userId = 1;
        long remainingAmount = 100;

        when(userPointRepository.findByUserId(userId)).thenReturn(
            new UserPoint(userId, remainingAmount, System.currentTimeMillis()));

        when(userPointRepository.save(userId, remainingAmount - amountToUse)).thenReturn(
            new UserPoint(userId, remainingAmount - amountToUse, System.currentTimeMillis()));

        UserPoint usedUserPoint = pointService.useUserPoint(userId, amountToUse);

        verify(policyChecker).checkUsePolicy(remainingAmount, amountToUse);
        verify(pointHistoryRepository).save(eq(userId), eq(amountToUse), eq(TransactionType.USE));
        verify(userPointRepository).save(userId, remainingAmount - amountToUse);

        Assertions.assertEquals(userId, usedUserPoint.id());
        Assertions.assertEquals(remainingAmount - amountToUse, usedUserPoint.point());
    }
}