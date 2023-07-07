package com.sosim.server.user;

import com.sosim.server.common.auditing.BaseTimeEntity;
import com.sosim.server.event.Event;
import com.sosim.server.oauth.dto.request.OAuth2UserInfoRequest;
import com.sosim.server.participant.Participant;
import com.sosim.server.type.SocialType;
import com.sosim.server.type.StatusType;
import com.sosim.server.type.UserType;
import com.sosim.server.type.WithdrawalGroundsType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(name = "`USER`")
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Setter
    @Column(name = "EMAIL")
    private String email;

    @Setter
    @Column(name = "WITHDRAWAL_DATE")
    private LocalDateTime withdrawalDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SOCIAL_TYPE")
    private SocialType socialType;


    @NotNull
    @Column(name = "SOCIAL_ID")
    private String socialId;


    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE")
    private UserType userType;


    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "WITHDRAWAL_GROUNDS_TYPE")
    private WithdrawalGroundsType withdrawalGroundsType;

    @OneToMany(mappedBy = "user")
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Participant> participants = new ArrayList<>();

    public static User create(OAuth2UserInfoRequest oAuth2UserInfoRequest) {
        User user = User.builder()
                .email(oAuth2UserInfoRequest.getEmail())
                .socialType(oAuth2UserInfoRequest.getOAuth2SocialType())
                .socialId(oAuth2UserInfoRequest.getOAuth2Id())
                .userType(UserType.ACTIVE)
                .build();
        user.setStatusType(StatusType.ACTIVE);
        return user;
    }
}
