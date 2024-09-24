package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.entity.UserPoint;
import org.springframework.stereotype.Component;

@Component
public interface UserPointRepository {

    UserPoint save(long id, long amount);

    UserPoint findByUserId(Long id);
}
