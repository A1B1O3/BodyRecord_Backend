package A1B1O3.bodyrecord.member.service;

import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.member.domain.repository.MemberRepository;
import A1B1O3.bodyrecord.member.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
//    @Transactional
//    public Member insert(GoogleUser googleUser, MemberRequest memberRequest) {
//        Member member = Member.of(googleUser, memberRequest);
//        return memberRepository.save(member);
//    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers( @Value("${image.image-url}") final String imageUrl) {
        final List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> MemberResponse.from(member,imageUrl))
                .collect(Collectors.toList());
    }

}
