package io.hhplus.tdd.point;

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
import org.springframework.test.web.servlet.MockMvc;

class PointServiceTest {

    @InjectMocks
    private PointServiceImpl pointService;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * <p>{@code PointService.searchUserPoint} 메서드에 대한 테스트입니다.</p>
     */
    @Test
    public void 포인트_조회_테스트() {
        when(userPointTable.selectById(1L)).thenReturn(UserPoint.empty(1));

        UserPoint actualUserPoint = pointService.searchUserPoint(1);

        Assertions.assertEquals(1, actualUserPoint.id());
        Assertions.assertEquals(0, actualUserPoint.point());
    }

    /**
     * <p>{@code PointService.searchUserHistories} 메서드에 대한 테스트입니다.</p>
     */
    @Test
    public void 포인트_히스토리_목록_조회_테스트() {
        when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(List.of());

        List<PointHistory> actualPointHistories = pointService.searchPointHistories(1);

        Assertions.assertEquals(0, actualPointHistories.size());
    }

    /**
     * <p>{@code PointService.chargeUserPoint} 메서드에 대한 테스트입니다.</p>
     *
     * <pre>사용자가 충전할 때, 10000 포인트를 초과하여 충전할 수 없습니다. </pre>
     */
    @Test
    public void 포인트_충전은_최대_잔고량_제한을_넘으면_안된다() {
        long amountToCharge = 10001, userId = 1;

        when(userPointTable.selectById(userId)).thenReturn(UserPoint.empty(userId));
        when(userPointTable.insertOrUpdate(userId, amountToCharge)).thenReturn(
            new UserPoint(userId, amountToCharge, System.currentTimeMillis()));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.chargeUserPoint(userId, amountToCharge);
        });
    }

    /**
     * <p>{@code PointService.chargeUserPoint} 메서드에 대한 테스트입니다.</p>
     *
     * <pre>사용자가 충전할 때, 0 포인트 이하는 충전할 수 없습니다. </pre>
     */
    @Test
    public void 충전하려는_포인트가_0_이하면_안된다() {
        long amountToCharge = 0, userId = 1;

        when(userPointTable.selectById(userId)).thenReturn(UserPoint.empty(userId));
        when(userPointTable.insertOrUpdate(userId, amountToCharge)).thenReturn(
            new UserPoint(userId, amountToCharge, System.currentTimeMillis()));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.chargeUserPoint(userId, amountToCharge);
        });
    }

    /**
     * <p>{@code PointService.chargeUserPoint} 메서드에 대한 테스트입니다.</p>
     *
     * <pre>사용자가 충전할 때, 0을 초과하고 10000 포인트 이하의 충전은 정상동작 해야 합니다 </pre>
     */
    @Test
    public void 포인트_충전은_최대_잔고량_제한_이하면_정상으로_동작해야한다() {
        long amountToCharge = 10000, userId = 1;

        when(userPointTable.selectById(userId)).thenReturn(UserPoint.empty(userId));
        when(userPointTable.insertOrUpdate(userId, amountToCharge)).thenReturn(
            new UserPoint(userId, amountToCharge, System.currentTimeMillis()));

        UserPoint chargedUserPoint = pointService.chargeUserPoint(userId, amountToCharge);

        Assertions.assertEquals(chargedUserPoint.id(), userId);
        Assertions.assertEquals(chargedUserPoint.point(), amountToCharge);
    }


}