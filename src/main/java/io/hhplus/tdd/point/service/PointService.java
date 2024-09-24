package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import java.util.List;

public interface PointService {

    UserPoint searchUserPoint(long userId);

    List<PointHistory> searchPointHistories(long userId);

    UserPoint chargeUserPoint(long userId, long amountToCharge);

    UserPoint useUserPoint(long userId, long amountToUse);
}
