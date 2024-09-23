package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {

    UserPoint searchUserPoint(long userId);

    List<PointHistory> searchPointHistories(long userId);

    UserPoint chargeUserPoint(long userId, long amountToCharge);

    UserPoint useUserPoint(long userId, long amountToUse);
}
