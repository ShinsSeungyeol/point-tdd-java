package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PolicyChecker;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PolicyChecker policyChecker;
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 사용자 포인트 조회
     *
     * @param userId
     * @return
     */
    @Override
    public UserPoint searchUserPoint(long userId) {
        return userPointRepository.findByUserId(userId);
    }

    /**
     * 사용자 포인트 이력 목록 조회
     *
     * @param userId
     * @return
     */
    @Override
    public List<PointHistory> searchPointHistories(long userId) {
        return pointHistoryRepository.findAllByUserId(userId);
    }

    /**
     * 사용자 포인트 충전
     *
     * @param userId
     * @param amountToCharge
     * @return
     */
    @Override
    public UserPoint chargeUserPoint(long userId, long amountToCharge) {
        UserPoint userPoint = searchUserPoint(userId);
        long remainingAmount = userPoint.point();

        policyChecker.checkChargePolicy(remainingAmount, amountToCharge);

        pointHistoryRepository.save(userId, amountToCharge, TransactionType.CHARGE);

        return userPointRepository.save(userId, remainingAmount + amountToCharge);
    }

    /**
     * 사용자 포인트를 사용
     *
     * @param userId
     * @param amountToUse
     * @return
     */
    @Override
    public UserPoint useUserPoint(long userId, long amountToUse) {
        UserPoint userPoint = searchUserPoint(userId);
        long remainingAmount = userPoint.point();

        policyChecker.checkUsePolicy(remainingAmount, amountToUse);

        pointHistoryRepository.save(userId, amountToUse, TransactionType.USE);

        return userPointRepository.save(userId, remainingAmount - amountToUse);
    }

}
