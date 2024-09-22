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

        if (remainingPoint + amountToCharge > MAX_TOTAL_AMOUNT_LIMIT) {
            throw new IllegalArgumentException("최대 잔고를 초과하여 충전할 수 없습니다.");
        }

        return userPointTable.insertOrUpdate(userId, amountToCharge);
    }
}
