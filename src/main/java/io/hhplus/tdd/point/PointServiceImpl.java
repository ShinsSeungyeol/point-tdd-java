package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final static long MAX_TOTAL_AMOUNT_LIMIT = 10000;
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Override
    public UserPoint searchUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    @Override
    public List<PointHistory> searchPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public UserPoint chargeUserPoint(long userId, long amountToCharge) {
        UserPoint userPoint = searchUserPoint(userId);
        long remainingPoint = userPoint.point();

        if (amountToCharge <= 0) {
            throw new IllegalArgumentException("0 이하의 포인트를 충전할 수 없습니다.");
        }

        if (remainingPoint + amountToCharge > MAX_TOTAL_AMOUNT_LIMIT) {
            throw new IllegalArgumentException("최대 잔고를 초과하여 충전할 수 없습니다.");
        }

        pointHistoryTable.insert(userId, amountToCharge, TransactionType.CHARGE,
            System.currentTimeMillis());

        return userPointTable.insertOrUpdate(userId, amountToCharge);
    }
}
