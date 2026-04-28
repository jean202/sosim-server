package com.sosim.server.group;

import com.sosim.server.common.response.Response;
import com.sosim.server.config.exception.CustomException;
import com.sosim.server.group.dto.request.CreateGroupRequest;
import com.sosim.server.group.dto.response.CreateGroupResponse;
import com.sosim.server.group.dto.response.GetGroupListResponse;
import com.sosim.server.group.dto.response.GetGroupResponse;
import com.sosim.server.group.dto.request.UpdateGroupRequest;
import com.sosim.server.participant.dto.response.GetNicknameResponse;
import com.sosim.server.participant.dto.response.GetParticipantListResponse;
import com.sosim.server.participant.dto.request.ParticipantNicknameRequest;
import com.sosim.server.security.AuthUser;
import com.sosim.server.type.CodeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/group")
    public ResponseEntity<?> createGroup(@AuthenticationPrincipal AuthUser authUser,
                                         @Validated @RequestBody CreateGroupRequest createGroupRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingError(bindingResult);
        }

        CreateGroupResponse createGroupResponse = groupService.createGroup(Long.valueOf(authUser.getId()), createGroupRequest);
        CodeType createGroup = CodeType.CREATE_GROUP;

        return new ResponseEntity<>(Response.create(createGroup, createGroupResponse), createGroup.getHttpStatus());
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroup(@AuthenticationPrincipal AuthUser authUser,
                                      @PathVariable("groupId") Long groupId) {
        GetGroupResponse getGroupResponse = groupService.getGroup(
                authUser != null ? Long.parseLong(authUser.getId()) : 0, groupId);
        CodeType getGroup = CodeType.GET_GROUP;

        return new ResponseEntity<>(Response.create(getGroup, getGroupResponse), getGroup.getHttpStatus());
    }

    @GetMapping("/group/{groupId}/participants")
    public ResponseEntity<?> getGroupParticipants(@AuthenticationPrincipal AuthUser authUser,
                                                  @PathVariable("groupId") Long groupId) {
        GetParticipantListResponse groupParticipant = groupService.getGroupParticipant(Long.parseLong(authUser.getId()), groupId);
        CodeType getParticipants = CodeType.GET_PARTICIPANTS;

        return new ResponseEntity<>(Response.create(getParticipants, groupParticipant), getParticipants.getHttpStatus());
    }

    @PatchMapping("/group/{groupId}")
    public ResponseEntity<?> modifyGroup(@AuthenticationPrincipal AuthUser authUser,
                                         @PathVariable("groupId") Long groupId,
                                         @Validated @RequestBody UpdateGroupRequest updateGroupRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingError(bindingResult);
        }

        CreateGroupResponse updatedGroupDto = groupService.updateGroup(Long.valueOf(authUser.getId()), groupId, updateGroupRequest);
        CodeType modifyGroup = CodeType.MODIFY_GROUP;

        return new ResponseEntity<>(Response.create(modifyGroup, updatedGroupDto), modifyGroup.getHttpStatus());
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<?> deleteGroup(@AuthenticationPrincipal AuthUser authUser,
                                         @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(Long.parseLong(authUser.getId()), groupId);
        CodeType deleteGroup = CodeType.DELETE_GROUP;

        return new ResponseEntity<>(Response.create(deleteGroup, null), deleteGroup.getHttpStatus());
    }

    @PostMapping("/group/{groupId}/participant")
    public ResponseEntity<?> intoGroup(@AuthenticationPrincipal AuthUser authUser,
                                       @PathVariable("groupId") Long groupId,
                                       @Validated @RequestBody ParticipantNicknameRequest participantNicknameRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingError(bindingResult);
        }

        groupService.intoGroup(Long.parseLong(authUser.getId()), groupId, participantNicknameRequest);
        CodeType intoGroup = CodeType.INTO_GROUP;

        return new ResponseEntity<>(Response.create(intoGroup, null), intoGroup.getHttpStatus());
    }

    @PatchMapping("/group/{groupId}/admin")
    public ResponseEntity<?> modifyAdmin(@AuthenticationPrincipal AuthUser authUser,
                                         @PathVariable("groupId") Long groupId,
                                         @RequestBody ParticipantNicknameRequest participantNicknameRequest) {

        groupService.modifyAdmin(Long.parseLong(authUser.getId()), groupId, participantNicknameRequest);
        CodeType modifyGroupAdmin = CodeType.MODIFY_GROUP_ADMIN;

        return new ResponseEntity<>(Response.create(modifyGroupAdmin, null), modifyGroupAdmin.getHttpStatus());
    }

    @DeleteMapping("/group/{groupId}/participant")
    public ResponseEntity<?> withdrawGroup(@AuthenticationPrincipal AuthUser authUser,
                                           @PathVariable("groupId") Long groupId) {
        groupService.withdrawGroup(Long.parseLong(authUser.getId()), groupId);
        CodeType withdrawGroup = CodeType.WITHDRAW_GROUP;

        return new ResponseEntity<>(Response.create(withdrawGroup, null), withdrawGroup.getHttpStatus());
    }

    @PatchMapping("/group/{groupId}/participant")
    public ResponseEntity<?> modifyNickname(@AuthenticationPrincipal AuthUser authUser,
                                            @PathVariable ("groupId") Long groupId,
                                            @Validated @RequestBody ParticipantNicknameRequest participantNicknameRequest) {
        groupService.modifyNickname(Long.parseLong(authUser.getId()), groupId, participantNicknameRequest);
        CodeType modifyNickname = CodeType.MODIFY_NICKNAME;

        return new ResponseEntity<>(Response.create(modifyNickname, null), modifyNickname.getHttpStatus());
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getMyGroups(@AuthenticationPrincipal AuthUser authUser,
                                         @RequestParam("page") Long page) {
        if (page == null) {
            throw new CustomException(CodeType.BINDING_ERROR);
        }

        GetGroupListResponse groupList = groupService.getMyGroups(page, Long.parseLong(authUser.getId()));
        CodeType getGroups = CodeType.GET_GROUPS;

        return new ResponseEntity<>(Response.create(getGroups, groupList), getGroups.getHttpStatus());
    }

    @GetMapping("/group/{groupId}/participant")
    public ResponseEntity<?> getMyNickname(@AuthenticationPrincipal AuthUser authUser,
                                           @PathVariable("groupId") Long groupId) {
        GetNicknameResponse getNicknameResponse =
                groupService.getMyNickname(Long.valueOf(authUser.getId()), groupId);
        CodeType getNickname = CodeType.GET_NICKNAME;

        return new ResponseEntity<>(Response.create(getNickname, getNicknameResponse), getNickname.getHttpStatus());
    }

    private void bindingError(BindingResult bindingResult) {
        throw new CustomException(CodeType.BINDING_ERROR, bindingResult.getFieldError().getField(),
                bindingResult.getFieldError().getDefaultMessage());
    }
}
