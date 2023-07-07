package com.sosim.server.event;

import com.sosim.server.common.auditing.BaseTimeEntity;
import com.sosim.server.event.dto.req.EventModifyReq;
import com.sosim.server.event.dto.req.PaymentTypeReq;
import com.sosim.server.group.Group;
import com.sosim.server.type.EventType;
import com.sosim.server.type.PaymentType;
import com.sosim.server.type.StatusType;
import com.sosim.server.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(name = "EVENT")
@AllArgsConstructor
@DynamicInsert
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @NotNull
    @Column(name = "GROUNDS_DATE")
    private LocalDateTime groundsDate;

    @NotNull
    @Column(name = "PAYMENT")
    private Long payment;

    @Size(max=65)
    @Column(name = "GROUNDS")
    private String grounds;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_TYPE")
    private PaymentType paymentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_TYPE")
    private StatusType statusType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "EVENT_TYPE")
    private EventType eventType;

    @Setter
    @ColumnDefault("0")
    @Column(name = "ADMIN_NON_TO_FULL")
    private Integer adminNonToFull;

    @Setter
    @ColumnDefault("0")
    @Column(name = "ADMIN_CON_TO_FULL")
    private Integer adminConToFull;

    @Setter
    @ColumnDefault("0")
    @Column(name = "USER_NON_TO_CON")
    private Integer userNonToCon;

    // Group 관련 연관관계 편의 메서드
    public void setGroup(Group group) {
        this.group = group;
        group.getEvents().add(this);
    }

    // User 관련 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        user.getEvents().add(this);
    }

    public void updateEvent(EventModifyReq eventModifyReq) {

        this.user = eventModifyReq.getUser();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate localDate = LocalDate.parse(eventModifyReq.getGroundsDate(), dateTimeFormatter);
        LocalDateTime groundsDatetime = LocalDateTime.of(localDate, LocalTime.of(0,0,0));
        this.groundsDate = groundsDatetime;
        this.payment = eventModifyReq.getPayment();
        this.grounds = eventModifyReq.getGrounds();

        if (eventModifyReq.getPaymentType().equals("full")) {
            if (this.paymentType.equals(PaymentType.NON_PAYMENT)) {
                this.adminNonToFull++;
            } else if (this.paymentType.equals(PaymentType.CONFIRMING)) {
                this.adminConToFull++;
            }
        }
        this.paymentType = PaymentType.getType(eventModifyReq.getPaymentType());
    }

    public void deleteEvent(){
        this.statusType = StatusType.DELETED;
    }

    public void changePaymentType(PaymentTypeReq paymentTypeReq) {
        this.paymentType = PaymentType.getType(paymentTypeReq.getPaymentType());
    }
}
