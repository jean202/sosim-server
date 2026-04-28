package com.sosim.server.event;

import com.sosim.server.event.dto.info.DayInfo;
import com.sosim.server.event.dto.info.EventInfo;
import com.sosim.server.event.dto.info.EventListInfo;
import com.sosim.server.event.dto.info.EventSingleInfo;
import com.sosim.server.event.dto.info.ListInfo;
import com.sosim.server.event.dto.req.EventCreateReq;
import com.sosim.server.event.dto.req.EventFilterRequest;
import com.sosim.server.event.dto.req.EventModifyReq;
import com.sosim.server.event.dto.req.MonthlyDayPaymentTypeReq;
import com.sosim.server.event.dto.req.PaymentTypeReq;
import com.sosim.server.security.AuthUser;
import java.util.List;

public interface EventService {

    EventSingleInfo getEvent(long id);

    Long createEvent(AuthUser authUser, EventCreateReq eventCreateReq);

    EventInfo updateEvent(AuthUser authUser, long id, EventModifyReq eventModifyReq);

    void deleteEvent(AuthUser authUser, long id);

    EventInfo changePaymentType(AuthUser authUser, long id, PaymentTypeReq paymentTypeReq);

    ListInfo<EventListInfo> getEventList(long groupId, EventFilterRequest request);

    List<DayInfo> getMonthlyDayPaymentType(long groupId, MonthlyDayPaymentTypeReq mdpTreq);
}
