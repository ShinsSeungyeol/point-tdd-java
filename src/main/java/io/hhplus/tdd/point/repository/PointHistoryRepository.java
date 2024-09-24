package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import java.util.List;

public interface PointHistoryRepository {

    PointHistory save(long userId, long amount, TransactionType transactionType);

    List<PointHistory> findAllByUserId(long userId);
}
