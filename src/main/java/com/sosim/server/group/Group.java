package com.sosim.server.group;

import com.sosim.server.common.auditing.BaseTimeEntity;
import com.sosim.server.event.Event;
import com.sosim.server.group.dto.request.CreateGroupRequest;
import com.sosim.server.group.dto.request.UpdateGroupRequest;
import com.sosim.server.participant.Participant;
import com.sosim.server.type.CoverColorType;
import com.sosim.server.type.GroupType;
import com.sosim.server.type.StatusType;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "`GROUP`")
public class Group extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_ID")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ADMIN_ID")
    private Long adminId;

    @Column(name = "ADMIN_NICKNAME")
    private String adminNickname;

    @Column(name = "COVER_COLOR_TYPE")
    @Enumerated(EnumType.STRING)
    private CoverColorType coverColorType;

    @Column(name = "GROUP_TYPE")
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<Event> events = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Group(String title, Long adminId, String adminNickname,
                  CoverColorType coverColorType, GroupType groupType) {
        this.title = title;
        this.adminId = adminId;
        this.adminNickname = adminNickname;
        this.coverColorType = coverColorType;
        this.groupType = groupType;
        statusType = StatusType.ACTIVE;
    }

    public static Group create(Long adminId, CreateGroupRequest createGroupRequest) {
        return Group.builder()
                .title(createGroupRequest.getTitle())
                .adminId(adminId)
                .adminNickname(createGroupRequest.getNickname())
                .groupType(createGroupRequest.getGroupType())
                .coverColorType(createGroupRequest.getCoverColorType())
                .build();
    }

    public void update(UpdateGroupRequest updateGroupRequest) {
        this.title = updateGroupRequest.getTitle();
        this.groupType = updateGroupRequest.getGroupType();
        this.coverColorType = updateGroupRequest.getCoverColorType();
    }

    public void modifyAdmin(Participant participant) {
        adminId = participant.getUser().getId();
        adminNickname = participant.getNickname();
    }
}
