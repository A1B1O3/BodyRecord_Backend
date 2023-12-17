package A1B1O3.bodyrecord.member.presentation;

import A1B1O3.bodyrecord.auth.domain.PrincipalDetails;
import A1B1O3.bodyrecord.body.dto.response.BodyResponse;
import A1B1O3.bodyrecord.member.dto.response.MemberResponse;
import A1B1O3.bodyrecord.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMember(@Value("${image.image-url}") final String imageUrl){
        final List<MemberResponse> memberResponses = memberService.getAllMembers(imageUrl);
        return ResponseEntity.ok(memberResponses);
    }

    @GetMapping("/person")
    public List<MemberResponse> getMemberDetail(@AuthenticationPrincipal PrincipalDetails principalDetails){
        final List<MemberResponse> memberResponses = memberService.getMembers(principalDetails.getMember().getMemberCode());
        return memberResponses;
    }

    @GetMapping("/memberCode")
    public int getMemberCode(@AuthenticationPrincipal PrincipalDetails principalDetails){
        final int memberCode = principalDetails.getMember().getMemberCode();
        return memberCode;
    }

}
