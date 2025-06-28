package com.tranqilo.repository;

import com.tranqilo.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    /**
     * Finds all check-ins for a specific user created after a given timestamp,
     * ordered by the creation date.
     * @param userId The ID of the user.
     * @param startDate The start date to filter check-ins from.
     * @return A list of check-ins.
     */
    List<CheckIn> findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(Long userId, LocalDateTime startDate);

}
