package com.sosim.server.event;

import com.sosim.server.group.Group;
import com.sosim.server.type.StatusType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryDsl {

    Optional<Event> findByIdAndStatusType(Long id, StatusType statusType);

    List<Event> findByGroupAndStatusTypeAndGroundsDateBetween(
        Group group, StatusType statusType, LocalDateTime startDate, LocalDateTime endDate);
}
