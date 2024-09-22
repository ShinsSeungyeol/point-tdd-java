package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

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
}
