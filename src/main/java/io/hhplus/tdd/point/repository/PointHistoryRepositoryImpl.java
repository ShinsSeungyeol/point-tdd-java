package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public PointHistory save(long userId, long amount, TransactionType transactionType) {
        return pointHistoryTable.insert(userId, amount, transactionType,
            System.currentTimeMillis());
    }

    @Override
    public List<PointHistory> findAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
