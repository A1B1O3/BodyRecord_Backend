package A1B1O3.bodyrecord.challenge.service;

import A1B1O3.bodyrecord.challenge.domain.repository.*;
import A1B1O3.bodyrecord.challenge.dto.request.ChallengeRequest;
import A1B1O3.bodyrecord.challenge.dto.response.*;
import A1B1O3.bodyrecord.common.exception.UnauthorizedException;
import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.member.domain.repository.MemberRepository;
import A1B1O3.bodyrecord.util.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final UploadFile uploadFile;

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipateRepository challengeParticipateRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final MemberRepository memberRepository;
    private final ChallengeCertificationRepository challengeCertificationRepository;

    /* 1. 챌린지 카테고리별 조회 */
    @Transactional(readOnly = true)
    public List<ChallengeCategoryResponse> getChallengesByCategory(int challengeCategoryCode) {
        ChallengeCategory challengeCategory = challengeCategoryRepository
                .findByChallengecategoryCode(challengeCategoryCode)
                .orElseThrow(() -> new EntityNotFoundException("ChallengeCategory not found with challengeCategoryCode: " + challengeCategoryCode));
        List<Challenge> challenges = challengeRepository.findAllByChallengecategoryCode(challengeCategory);
        return challenges.stream()
                .map(challenge -> ChallengeCategoryResponse.from(challenge, challengeCategory))
                .collect(Collectors.toList());
    }


    /* 2. 챌린지 개별 상세 조회 */
    @Transactional(readOnly = true)
    public ChallengeDetailResponse getChallengeDetails(int challengeCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with challengeCode: " + challengeCode));
        return ChallengeDetailResponse.from(challenge);
    }


    /* 3. 참여중인 챌린지 조회 */
    @Transactional(readOnly = true)
    public List<MyChallengeResponse> getParticipatingChallengeDetails(int memberCode) {
        Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with code: " + memberCode));

        List<ChallengeParticipate> participatingChallenges = challengeParticipateRepository
                .findByMemberCodeAndState(member, ChallengeParticipateState.JOIN);

        LocalDate currentDate = LocalDate.now();

        return participatingChallenges.stream()
                .filter(challengeParticipate -> challengeParticipate.getChallenge().getChallengeEnddate().isAfter(currentDate))
                .map(challengeParticipate -> MyChallengeResponse.from(challengeParticipate.getChallenge(), member))
                .collect(Collectors.toList());
    }


    /* 4. 챌린지 등록 */
    @Transactional
    public ChallengeResponse createChallenge(int memberCode, ChallengeRequest challengeRequest) {
        Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with code: " + memberCode));

        // 멤버가 이미 종료일이 지나지 않은 챌린지를 가지고 있는지 확인
        boolean hasActiveChallenge = challengeRepository.existsByMemberCodeAndChallengeEnddateAfterAndChallengeEnddateBefore(
                member, LocalDate.now(), LocalDate.of(9999, 12, 31));

        if (hasActiveChallenge) {
            throw new IllegalStateException("멤버가 이미 종료일이 지나지 않은 챌린지를 가지고 있습니다.");
        }

        ChallengeCategory challengeCategory = challengeCategoryRepository
                .findByChallengecategoryCode(challengeRequest.getChallengecategoryCode())
                .orElseThrow(() -> new EntityNotFoundException("ChallengeCategory not found with code: " + challengeRequest.getChallengecategoryCode()));

        // 챌린지 생성, 저장
        Challenge challenge = new Challenge();
        challenge.setChallengeTitle(challengeRequest.getChallengeTitle());
        challenge.setChallengeContent(challengeRequest.getChallengeContent());
        challenge.setMemberCode(member);
        challenge.setChallengecategoryCode(challengeCategory);
        challenge.setChallengeStartdate(challengeRequest.getChallengeStartdate());
        challenge.setChallengeEnddate(challengeRequest.getChallengeEnddate());
        Challenge createdChallenge = challengeRepository.save(challenge);

        // 챌린지 참여 생성, 저장
        ChallengeParticipate challengeParticipate = new ChallengeParticipate();
        challengeParticipate.setChallengeCode(createdChallenge);
        challengeParticipate.setMemberCode(member);
        challengeParticipate.setState(ChallengeParticipateState.JOIN);
        challengeParticipateRepository.save(challengeParticipate);

        return ChallengeResponse.from(createdChallenge, challengeCategory);
    }


    /* 5. 챌린지 삭제 */
    @Transactional
    public void deleteChallenge(int challengeCode, int memberCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        //본인의 챌린지인지 확인
        if (challenge.getMemberCode().getMemberCode() != memberCode) {
            throw new UnauthorizedException("You don't have permission to delete this challenge.");
        }

        //챌린지 참여 삭제
        challengeParticipateRepository.leaveByChallengeCode(challenge);

        //챌린지 삭제
        challengeRepository.delete(challenge);
    }


    /* 6. 챌린지 인증 */
    public void certifyChallenge(int challengeCode, int memberCode, MultipartFile challengeImageFile) {
        Challenge challenge = challengeRepository
                .findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with code: " + memberCode));

        try {
            ChallengeCertification challengeCertification = new ChallengeCertification();
            challengeCertification.setChallengeCode(challenge);
            challengeCertification.setMemberCode(member);
            challengeCertification.setChallengeImage(uploadFile.saveChallengeImage(challengeImageFile));

            challengeCertificationRepository.save(challengeCertification);
        } catch (IOException e) {
            //파일 저장 실패
            e.printStackTrace();
        }
    }


    /* 7. 챌린지 인증 조회 */
    public List<ChallengeCertificationResponse> getChallengeCertifications(int challengeCode, String imageUrlPrefix) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        List<ChallengeCertification> certifications = challengeCertificationRepository.findByChallengeCode(challenge);

        return certifications.stream()
                .map(certification -> ChallengeCertificationResponse.from(certification, challenge, certification.getMemberCode(), imageUrlPrefix))
                .collect(Collectors.toList());
    }


    /* 8. 챌린지 인증 삭제 */
    @Transactional
    public void deleteChallengeCertification(int challengeCertificationCode, int memberCode) {
        ChallengeCertification certification = challengeCertificationRepository.findById(challengeCertificationCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge certification not found with code: " + challengeCertificationCode));

        //본인의 인증인지 확인
        if (certification.getMemberCode().getMemberCode() != memberCode) {
            throw new UnauthorizedException("You don't have permission to delete this challenge certification.");
        }

        //인증 삭제
        challengeCertificationRepository.deleteById(challengeCertificationCode);
    }


    /* 9. 챌린지 참여 */
    public void joinChallenge(int challengeCode, int memberCode) {
        Challenge challenge = challengeRepository
                .findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with code: " + memberCode));

        ChallengeParticipate challengeParticipate = new ChallengeParticipate();
        challengeParticipate.setChallengeCode(challenge);
        challengeParticipate.setMemberCode(member);
        challengeParticipate.setState(ChallengeParticipateState.JOIN);

        challengeParticipateRepository.save(challengeParticipate);
    }


    /* 10. 챌린지 탈퇴 */
    public void leaveChallenge(int challengeCode, int memberCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with code: " + memberCode));

        List<ChallengeParticipate> challengeParticipations = challengeParticipateRepository
                .findByChallengeCodeAndMemberCodeAndState(challenge, member, ChallengeParticipateState.JOIN);

        if (challengeParticipations.isEmpty()) {
            throw new EntityNotFoundException("You are not a participant of this challenge.");
        }

        for (ChallengeParticipate participation : challengeParticipations) {

            participation.leaveChallenge();
        }
    }


    /* 11. 인기순 챌린지 조회 */
    public List<ChallengePopularResponse> getPopularChallenges() {
        List<Challenge> popularChallenges = challengeRepository.findAllByOrderByChallengeParticipatesCountDesc();
        return popularChallenges.stream()
                .map(ChallengePopularResponse::fromEntity)
                .collect(Collectors.toList());
    }


    /* 12. 신규순 챌린지 조회 */
    public List<ChallengeNewResponse> getChallengesByCreatedAtDesc() {
        List<Challenge> challenges = challengeRepository.findAllByOrderByCreatedAtDesc();
        return challenges.stream()
                .map(challenge -> ChallengeNewResponse.from(challenge))
                .collect(Collectors.toList());
    }


    /* 13. 챌린지 달성률 */
    public int getChallengeAchievementRate(int memberCode, int challengeCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with code: " + memberCode));

        ChallengeParticipate participation = challengeParticipateRepository
                .findFirstByChallengeCodeAndMemberCodeAndStateOrderByCreatedAtAsc(challenge, member, ChallengeParticipateState.JOIN)
                .orElseThrow(() -> new EntityNotFoundException("No participation found for member " + memberCode + " in challenge " + challengeCode));

        //챌린지 시작일, 종료일
        LocalDate startDate = challenge.getChallengeStartdate();
        LocalDate endDate = challenge.getChallengeEnddate();
        //날짜
        LocalDate currentDate = LocalDate.now();
        //챌린지 참여일
        LocalDate participateDate = participation.getCreatedAt();
        //참여한날로부터 현재까지 기간
        long totalDays = ChronoUnit.DAYS.between(participateDate, currentDate);
        //총기간
        long challengeDays = ChronoUnit.DAYS.between(startDate, endDate);
        //계산
        double achievementRate = (double) totalDays / challengeDays * 100;
        //
        int roundedRate = (int) achievementRate;
        //최대 100%
        return Math.min(100, roundedRate);
//        return Math.min(100, achievementRate);
    }

    /* 14. 챌린지의 현재 회원수 */
    public int getChallengeParticipantsCount(int challengeCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));

        List<ChallengeParticipate> participants = challengeParticipateRepository
                .findByChallengeCodeAndState(challenge, ChallengeParticipateState.JOIN);

        return participants.size();
    }

}

