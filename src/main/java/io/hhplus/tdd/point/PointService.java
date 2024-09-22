package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {

    UserPoint searchUserPoint(long userId);

    List<PointHistory> searchPointHistories(long userId);
}
