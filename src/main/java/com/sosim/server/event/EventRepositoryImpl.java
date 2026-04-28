package com.sosim.server.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sosim.server.event.dto.req.EventFilterRequest;
import com.sosim.server.participant.QParticipant;
import com.sosim.server.type.PaymentType;
import com.sosim.server.type.StatusType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryDsl {

    private final JPAQueryFactory queryFactory;

    private final QEvent event = QEvent.event;
    private final QParticipant participant = QParticipant.participant;

    @Override
    public Page<Event> searchAll(long groupId, EventFilterRequest request, Pageable pageable) {
        BooleanBuilder where = buildWhere(groupId, request);

        List<Event> content = queryFactory
            .selectFrom(event)
            .leftJoin(event.user).fetchJoin()
            .where(where)
            .orderBy(event.groundsDate.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory.select(event.count()).from(event).where(where).fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder buildWhere(long groupId, EventFilterRequest request) {
        return new BooleanBuilder()
            .and(event.group.id.eq(groupId))
            .and(event.statusType.eq(StatusType.USING))
            .and(betweenTime(request.toDateRange()))
            .and(equalsUser(request.getNickname(), groupId))
            .and(equalsPaymentType(request.toPaymentType()));
    }

    private BooleanExpression betweenTime(DateRange dateRange) {
        return dateRange == null ? null : event.groundsDate.between(dateRange.getStart(), dateRange.getEnd());
    }

    private BooleanExpression equalsUser(String nickname, long groupId) {
        if (!StringUtils.hasText(nickname)) return null;
        return event.user.id.in(
            JPAExpressions.select(participant.user.id)
                .from(participant)
                .where(
                    participant.nickname.eq(nickname),
                    participant.group.id.eq(groupId),
                    participant.status.eq(StatusType.USING)
                )
        );
    }

    private BooleanExpression equalsPaymentType(PaymentType paymentType) {
        return paymentType == null ? null : event.paymentType.eq(paymentType);
    }
}
