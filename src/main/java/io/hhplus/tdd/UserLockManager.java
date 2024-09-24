package io.hhplus.tdd;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class UserLockManager {

    private final ConcurrentHashMap<Long, Lock> userLocks = new ConcurrentHashMap<>();

    /**
     * 사용자 userId 에 Lock 을 반환
     *
     * @param userId
     * @return
     */
    public AutoCloseableLock createOrGetUserLock(long userId) {
        Lock lock = userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
        return new AutoCloseableLock(lock);
    }
}
