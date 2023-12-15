package A1B1O3.bodyrecord.body.service;

import A1B1O3.bodyrecord.auth.domain.PrincipalDetails;
import A1B1O3.bodyrecord.body.domain.Body;
import A1B1O3.bodyrecord.body.domain.repository.BodyRepository;
import A1B1O3.bodyrecord.body.dto.request.BodyRequest;
import A1B1O3.bodyrecord.body.dto.request.OnlyBodyRequest;
import A1B1O3.bodyrecord.body.dto.response.BodyResponse;
import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.member.domain.repository.MemberRepository;
import A1B1O3.bodyrecord.util.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BodyService {

    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;
    private final UploadFile uploadFile;
    @Transactional(readOnly = true)
   public List<BodyResponse> getAllBodys(final int memberCode){
        final List<Body> bodys = bodyRepository.findAllByMemberCodeMemberCode(memberCode);
        return bodys.stream()
                .map(body -> BodyResponse.from(body))
                .collect(Collectors.toList());
    }
    @Transactional
    public void insert( BodyRequest bodyRequest, PrincipalDetails principalDetails) throws IOException {

        Member member = memberRepository.findById(principalDetails.getMember().getMemberCode()).orElseThrow();
        String img = uploadFile.profileUpload(bodyRequest.getImgFile());
        member.updateImageAndNickname(img,bodyRequest.getMemberNickname());

        Body body = Body.of(bodyRequest.getWeight(),bodyRequest.getMuscle(),bodyRequest.getFat(),member);

        bodyRepository.save(body);
    }
    @Transactional
public Body updateBody(OnlyBodyRequest onlyBodyRequest,PrincipalDetails principalDetails) {
        Member member = memberRepository.findById(principalDetails.getMember().getMemberCode()).orElseThrow();
        Body body = Body.of2(onlyBodyRequest.getWeight(), onlyBodyRequest.getMuscle(), onlyBodyRequest.getFat(),member);

    return bodyRepository.save(body);
}
    public void deleteByMemberCode(final int memberCode){
        bodyRepository.deleteByMemberCodeMemberCode(memberCode);
    }

}
