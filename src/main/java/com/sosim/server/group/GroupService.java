package com.sosim.server.group;

import com.sosim.server.config.exception.CustomException;
import com.sosim.server.group.dto.request.CreateGroupRequest;
import com.sosim.server.group.dto.request.UpdateGroupRequest;
import com.sosim.server.group.dto.response.CreateGroupResponse;
import com.sosim.server.group.dto.response.GetGroupResponse;
import com.sosim.server.group.dto.response.GetGroupListResponse;
import com.sosim.server.participant.Participant;
import com.sosim.server.participant.ParticipantService;
import com.sosim.server.participant.dto.response.GetNicknameResponse;
import com.sosim.server.participant.dto.response.GetParticipantListResponse;
import com.sosim.server.participant.dto.request.ParticipantNicknameRequest;
import com.sosim.server.type.CodeType;
import com.sosim.server.type.StatusType;
import com.sosim.server.user.User;
import com.sosim.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final ParticipantService participantService;
    private final UserService userService;

    public CreateGroupResponse createGroup(Long userId, CreateGroupRequest createGroupRequest) {
        User userEntity = userService.getUser(userId);
        Group groupEntity = saveGroupEntity(Group.create(userId, createGroupRequest));

        participantService.createParticipant(userEntity, groupEntity, createGroupRequest.getNickname());

        return CreateGroupResponse.create(groupEntity);
    }

    public GetGroupResponse getGroup(Long userId, Long groupId) {
        Group groupEntity = getGroupEntity(groupId);
        boolean isInto = false;

        try {
            if (userId != 0) isInto = participantService.
                    getParticipantEntity(userService.getUser(userId), groupEntity) != null;

        } catch (CustomException e) {}

        return GetGroupResponse.create(groupEntity, groupEntity.getAdminId().equals(userId),
                (int) groupEntity.getParticipants().stream()
                        .filter(p -> p.getStatusType().equals(StatusType.ACTIVE)).count(), isInto);
    }

    public GetParticipantListResponse getGroupParticipant(Long userId, Long groupId) {
        Group groupEntity = getGroupEntity(groupId);
        List<String> nicknameList = groupEntity.getParticipants().stream()
                .filter(p -> p.getStatusType().equals(StatusType.ACTIVE) &&
                        !p.getNickname().equals(groupEntity.getAdminNickname()))
                .map(Participant::getNickname)
                .collect(Collectors.toList());

        List<Long> userIdList = participantService.getUserIdList(nicknameList, groupId);

        List<GetParticipantListResponse.Member> memberList = new ArrayList<>();

        for (int i = 0; i < nicknameList.size(); i++) {
            memberList.add(new GetParticipantListResponse.Member(userIdList.get(i), nicknameList.get(i)));
        }

        if (!groupEntity.getAdminId().equals(userId)) Collections.swap(memberList, 0, userIdList.indexOf(userId));
        if (memberList.size() > 0) memberList.subList(1, memberList.size())
                .sort(Comparator.comparing(GetParticipantListResponse.Member::getNickname));

        return GetParticipantListResponse.create(groupEntity, memberList);
    }

    public CreateGroupResponse updateGroup(Long userId, Long groupId, UpdateGroupRequest updateGroupRequest) {
        Group groupEntity = getGroupEntity(groupId);

        if (!groupEntity.getAdminId().equals(userId)) {
            throw new CustomException(CodeType.NONE_ADMIN);
        }
        groupEntity.update(updateGroupRequest);

        if (updateGroupRequest.getNickname() != null) {
            modifyNickname(userId, groupId, new ParticipantNicknameRequest(updateGroupRequest.getNickname()));
        }

        return CreateGroupResponse.create(groupEntity);
    }

    public void deleteGroup(Long userId, Long groupId) {
        Group groupEntity = getGroupEntity(groupId);

        if (!groupEntity.getAdminId().equals(userId)) {
            throw new CustomException(CodeType.NONE_ADMIN);
        }

        if (groupEntity.getParticipants().stream()
                .filter(p -> p.getStatusType().equals(StatusType.ACTIVE)).count() > 1) {
            throw new CustomException(CodeType.NONE_ZERO_PARTICIPANT);
        }

        participantService.deleteParticipantEntity(userService.getUser(userId), getGroupEntity(groupId));
        groupEntity.delete();
    }

    public void intoGroup(Long userId, Long groupId, ParticipantNicknameRequest participantNicknameRequest) {
        User userEntity = userService.getUser(userId);
        Group groupEntity = getGroupEntity(groupId);

        participantService.createParticipant(userEntity, groupEntity, participantNicknameRequest.getNickname());
    }

    public void modifyAdmin(Long userId, Long groupId, ParticipantNicknameRequest participantNicknameRequest) {
        Group groupEntity = getGroupEntity(groupId);

        if (!groupEntity.getAdminId().equals(userId)) {
            throw new CustomException(CodeType.NONE_ADMIN);
        }

        Participant participantEntity = participantService
                .getParticipantEntity(participantNicknameRequest.getNickname(), groupEntity);

        if (!groupEntity.getParticipants().contains(participantEntity)) {
            throw new CustomException(CodeType.NONE_PARTICIPANT);
        }

        groupEntity.modifyAdmin(participantEntity);
    }

    public void withdrawGroup(Long userId, Long groupId) {
        Group groupEntity = getGroupEntity(groupId);
        participantService.deleteParticipantEntity(userService.getUser(userId), groupEntity);
        if (groupEntity.getParticipants().stream().noneMatch(p -> p.getStatusType().equals(StatusType.ACTIVE))) {
            groupEntity.delete();
        }
    }

    public void modifyNickname(Long userId, Long groupId, ParticipantNicknameRequest participantNicknameRequest) {
        Group groupEntity = getGroupEntity(groupId);
        Participant participant = participantService.modifyNickname(userService.getUser(userId),
                groupEntity, participantNicknameRequest);

        if (groupEntity.getAdminId().equals(userId)) {
            groupEntity.modifyAdmin(participant);
        }
    }

    public GetGroupListResponse getMyGroups(Long index, Long userId) {
        Slice<Participant> slice = participantService.getParticipantSlice(index, userId);
        List<Participant> participantEntityList = slice.getContent();

        if (participantEntityList.size() == 0) {
            throw new CustomException(CodeType.NO_MORE_GROUP);
        }

        List<GetGroupResponse> groupList = new ArrayList<>();
        for (Participant participant : participantEntityList) {
            Group group = participant.getGroup();
            groupList.add(GetGroupResponse.create(group, group.getAdminId().equals(userId),
                    (int) group.getParticipants().stream()
                            .filter(p -> p.getStatusType().equals(StatusType.ACTIVE)).count(),true));
        }

        return GetGroupListResponse.create(participantEntityList.get(participantEntityList.size() - 1).getId(),
                slice.hasNext(), groupList);
    }

    public GetNicknameResponse getMyNickname(Long userId, Long groupId) {
        return participantService.getMyNickname(userService.getUser(userId), getGroupEntity(groupId));
    }

    public Group getGroupEntity(Long groupId) {
        return groupRepository.findByIdAndStatusType(groupId, StatusType.ACTIVE)
                .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_GROUP));
    }

    public Group saveGroupEntity(Group group) {
        return groupRepository.save(group);
    }
}
