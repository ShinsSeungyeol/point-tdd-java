package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PolicyChecker policyChecker;
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
        long remainingAmount = userPoint.point();

        policyChecker.checkChargePolicy(remainingAmount, amountToCharge);

        pointHistoryTable.insert(userId, amountToCharge, TransactionType.CHARGE,
            System.currentTimeMillis());

        return userPointTable.insertOrUpdate(userId, remainingAmount + amountToCharge);
    }

    @Override
    public UserPoint useUserPoint(long userId, long amountToUse) {
        UserPoint userPoint = searchUserPoint(userId);
        long remainingAmount = userPoint.point();

        policyChecker.checkUsePolicy(remainingAmount, amountToUse);

        pointHistoryTable.insert(userId, amountToUse, TransactionType.USE,
            System.currentTimeMillis());

        return userPointTable.insertOrUpdate(userId, remainingAmount - amountToUse);
    }

}
