package com.sosim.server.participant;

import com.sosim.server.common.auditing.BaseTimeEntity;
import com.sosim.server.group.Group;
import com.sosim.server.type.StatusType;
import com.sosim.server.user.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "PARTICIPANT")
public class Participant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @Column(name = "NICKNAME")
    private String nickname;

    // User 관련 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        user.getParticipants().add(this);
    }

    // Group 관련 연관관계 편의 메서드
    public void setGroup(Group group) {
        this.group = group;
        group.getParticipants().add(this);
    }

    @Builder (access = AccessLevel.PRIVATE)
    private Participant(User user, Group group, String nickname) {
        this.user = user;
        this.group = group;
        this.nickname = nickname;
        statusType = StatusType.ACTIVE;
    }

    public static Participant create(User user, Group group, String nickname) {
        return Participant.builder()
                .user(user)
                .group(group)
                .nickname(nickname)
                .build();
    }

    public void modifyNickname(String nickname) {
        this.nickname = nickname;
    }
}
