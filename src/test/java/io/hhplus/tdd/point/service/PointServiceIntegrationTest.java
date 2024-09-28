package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.UserPoint;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    /**
     * 동시성 테스트로 10에서 1씩 10 번을 동시에 포인트 사용을 시도하여, 결과 값이 0이라면 이상이 없다.
     *
     * @throws InterruptedException
     */
    @Test
    void 동시성_테스트() throws InterruptedException {
        long userId = 1L;
        long initialAmount = 30;

        pointService.chargeUserPoint(userId, initialAmount);

        int numberOfThreads = 30;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    // 포인트 사용 또는 충전
                    pointService.useUserPoint(userId, 1);
                } finally {
                    latch.countDown(); // 스레드 종료 시 카운트 감소
                }
            });
            threads[i].start();
        }
        // 모든 스레드가 종료될 때까지 대기
        latch.await();

        // 최종 포인트 조회
        UserPoint finalUserPoint = pointService.searchUserPoint(userId);
        Assertions.assertEquals(0, finalUserPoint.point());
    }

    @Test
    void 동시성_사용_충전_테스트() throws InterruptedException {
        long userId = 1L;
        long initialAmount = 10;

        pointService.chargeUserPoint(userId, initialAmount);

        int numberOfThreads = 30;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    // 포인트 사용 또는 충전
                    pointService.useUserPoint(userId, 1);
                } finally {
                    latch.countDown(); // 스레드 종료 시 카운트 감소
                }
            });
            threads[i].start();
        }
        // 모든 스레드가 종료될 때까지 대기
        latch.await();

        // 최종 포인트 조회
        UserPoint finalUserPoint = pointService.searchUserPoint(userId);
        Assertions.assertEquals(0, finalUserPoint.point());
    }
}
