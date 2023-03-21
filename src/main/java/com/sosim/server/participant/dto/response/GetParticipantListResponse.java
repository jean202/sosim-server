package com.sosim.server.participant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sosim.server.group.Group;
import com.sosim.server.participant.Participant;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetParticipantListResponse {
    @JsonProperty("admin_id")
    private Long adminId;

    @JsonProperty("admin_nickname")
    private String adminNickname;

    @JsonProperty("nickname_list")
    private List<String> nicknameList;

    public static GetParticipantListResponse create(Group group, List<Participant> participants) {
        return GetParticipantListResponse.builder()
                .adminId(group.getAdminId())
                .adminNickname(group.getAdminNickname())
                .nicknameList(participants.stream()
                        .map(Participant::getNickname)
                        .filter(nickname -> !nickname.equals(group.getAdminNickname()))
                        .collect(Collectors.toList()))
                .build();
    }
}