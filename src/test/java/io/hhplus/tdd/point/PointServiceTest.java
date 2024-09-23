package io.hhplus.tdd.point;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
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
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

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
        when(userPointTable.selectById(1L)).thenReturn(UserPoint.empty(1));

        UserPoint actualUserPoint = pointService.searchUserPoint(1);

        Assertions.assertEquals(1, actualUserPoint.id());
        Assertions.assertEquals(0, actualUserPoint.point());
    }

    /**
     * 포인트 히스토리 목록 조회 정상 동작 단위 테스트
     */
    @Test
    public void 포인트_히스토리_목록_조회_테스트() {
        when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(List.of());

        List<PointHistory> actualPointHistories = pointService.searchPointHistories(1);

        Assertions.assertEquals(0, actualPointHistories.size());
    }


    /**
     * 포인트 충전 정상 동작 단위 테스트
     */
    @Test
    public void 포인트_충전_테스트() {
        long amountToCharge = PointConstant.MAX_BALANCE_AMOUNT_LIMIT, userId = 1;

        when(userPointTable.selectById(userId)).thenReturn(UserPoint.empty(userId));
        when(userPointTable.insertOrUpdate(userId, amountToCharge)).thenReturn(
            new UserPoint(userId, amountToCharge, System.currentTimeMillis()));

        UserPoint chargedUserPoint = pointService.chargeUserPoint(userId, amountToCharge);

        Assertions.assertEquals(chargedUserPoint.id(), userId);
        Assertions.assertEquals(chargedUserPoint.point(), amountToCharge);
    }

    /**
     * 포인트 사용 정상 동작 단위 테스트
     */
    @Test
    public void 포인트_사용_테스트() {
        long amountToUse = 300, userId = 1;
        long remainingAmount = 100;

        when(userPointTable.selectById(userId)).thenReturn(
            new UserPoint(userId, remainingAmount, System.currentTimeMillis()));

        when(userPointTable.insertOrUpdate(userId, remainingAmount - amountToUse)).thenReturn(
            new UserPoint(userId, remainingAmount - amountToUse, System.currentTimeMillis()));

        UserPoint usedUserPoint = pointService.useUserPoint(userId, amountToUse);

        // policyChecker.checkUsePolicy가 호출되었는지 확인
        verify(policyChecker).checkUsePolicy(remainingAmount, amountToUse);

        // pointHistoryTable.insert가 호출되었는지 확인
        verify(pointHistoryTable).insert(userId, amountToUse, TransactionType.USE,
            anyLong());

        // insertOrUpdate가 호출되었는지 확인
        verify(userPointTable).insertOrUpdate(userId, remainingAmount - amountToUse);

        // 결과 검증: 반환된 UserPoint가 예상한 결과와 일치하는지 확인
        Assertions.assertEquals(userId, usedUserPoint.id());
        Assertions.assertEquals(remainingAmount - amountToUse, usedUserPoint.point());
    }
}