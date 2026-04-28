package com.sosim.server.event;

import com.sosim.server.event.dto.req.EventFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryDsl {

    Page<Event> searchAll(long groupId, EventFilterRequest request, Pageable pageable);
}