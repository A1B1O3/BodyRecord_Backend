package A1B1O3.bodyrecord.member.dto.response;

import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.member.domain.MemberState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class MemberResponse {
    private final int memberCode;
    private final String memberSocialid;
    private final String memberName;
    private final String memberImage;
    private final String memberNickname;
    private final MemberState status;
    public static MemberResponse from(final Member member, @Value("${image.image-url}") final String imageUrl) {
        member.setMemberImage(imageUrl + member.getMemberImage());
        return new MemberResponse(
                member.getMemberCode(),
                member.getMemberSocialid(),
                member.getMemberName(),
                member.getMemberImage(),
                member.getMemberNickname(),
                member.getState()
        );
    }


}
