package A1B1O3.bodyrecord.admin.service;

import A1B1O3.bodyrecord.admin.dto.response.*;
import A1B1O3.bodyrecord.challenge.domain.repository.*;
import A1B1O3.bodyrecord.common.BaseEntity;
import A1B1O3.bodyrecord.common.type.StatusType;
import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.member.domain.repository.MemberRepository;
import A1B1O3.bodyrecord.report.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipateRepository challengeParticipateRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final MemberRepository memberRepository;
    private final ChallengeCertificationRepository challengeCertificationRepository;
    private final ReportRepository reportRepository;
    private final ReportCategoryRepository reportCategoryRepository;

    /* 1. 챌린지신고 목록조회 */
    @Transactional(readOnly = true)
    public List<ChallengeReportResponse> getChallengeReports() {
        ReportCategory challengeReportCategory = reportCategoryRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("챌린지 신고 카테고리를 찾을 수 없습니다."));

        List<Report> challengeReports = reportRepository.findByReportcategoryCode(challengeReportCategory);

        return challengeReports.stream()
                .map(report -> {
                    Member member = (Member) memberRepository.findByMemberCode(report.getMemberCode().getMemberCode())
                            .orElseThrow(() -> new EntityNotFoundException("Reported member not found with code: " + report.getMemberCode().getMemberCode()));
                    return ChallengeReportResponse.from(report, member);
                })
                .collect(Collectors.toList());
    }


    /* 2. 챌린지신고 상세조회 */
    @Transactional(readOnly = true)
    public ChallengeReportDetailResponse getChallengeReportDetails(int reportCode) {
        Report report = reportRepository.findById(reportCode)
                .orElseThrow(() -> new EntityNotFoundException("챌린지 신고를 찾을 수 없습니다. Report Code: " + reportCode));

        Member member = (Member) memberRepository.findByMemberCode(report.getMemberCode().getMemberCode())
                .orElseThrow(() -> new EntityNotFoundException("Reported member not found with code: " + report.getMemberCode().getMemberCode()));

        return ChallengeReportDetailResponse.from(report, member);
    }


    /* 3. 챌린지인증신고 목록조회 */
    @Transactional(readOnly = true)
    public List<ChallengeCertificationReportResponse> getChallengeCertificationReports() {
        ReportCategory challengeReportCategory = reportCategoryRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("챌린지 신고 카테고리를 찾을 수 없습니다."));

        List<Report> challengeCertificationReports = reportRepository.findByReportcategoryCode(challengeReportCategory);

        return challengeCertificationReports.stream()
                .map(report -> {
                    Member member = (Member) memberRepository.findByMemberCode(report.getMemberCode().getMemberCode())
                            .orElseThrow(() -> new EntityNotFoundException("Reported member not found with code: " + report.getMemberCode().getMemberCode()));
                    return ChallengeCertificationReportResponse.from(report, member);
                })
                .collect(Collectors.toList());
    }



    /* 4. 챌린지인증신고 상세조회 */
    @Transactional(readOnly = true)
    public ChallengeCertificationReportDetailResponse getChallengeCertificationReportDetails(int reportCode) {
        Report report = reportRepository.findById(reportCode)
                .orElseThrow(() -> new EntityNotFoundException("챌린지 인증 신고를 찾을 수 없습니다. Report Code: " + reportCode));

        Member member = (Member) memberRepository.findByMemberCode(report.getMemberCode().getMemberCode())
                .orElseThrow(() -> new EntityNotFoundException("Reported member not found with code: " + report.getMemberCode().getMemberCode()));

        ChallengeCertification challengeCertification = report.getChallengecerCode();

        return ChallengeCertificationReportDetailResponse.from(report, member, challengeCertification);
    }


    /* 5. 챌린지 목록조회 */
    @Transactional(readOnly = true)
    public List<ChallengeAdminResponse> getAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.stream()
                .map(ChallengeAdminResponse::from)
                .collect(Collectors.toList());
    }


    /* 6. 챌린지 상세조회 */
    @Transactional(readOnly = true)
    public ChallengeDetailAdminResponse getChallengeDetailsAdmin(int challengeCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));
        return ChallengeDetailAdminResponse.from(challenge);
    }


    /* 7. 관리자 챌린지 삭제 */
    public void deleteChallengeAdmin(int challengeCode) {
        Challenge challenge = challengeRepository.findByChallengeCode(challengeCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found with code: " + challengeCode));
        challengeRepository.delete(challenge);
    }


    /* 8. 관리자 챌린지 인증 조회 */
    @Transactional(readOnly = true)
    public List<ChallengeCertificationAdminResponse> getChallengeCertificationsAdmin(String imageUrlPrefix) {
        List<ChallengeCertification> certifications = challengeCertificationRepository.findAll();
        return certifications.stream()
                .map(certification -> ChallengeCertificationAdminResponse.from(certification, certification.getChallengeCode(), certification.getMemberCode(), imageUrlPrefix))
                .collect(Collectors.toList());
    }


    /* 9. 관리자 챌린지 인증 삭제 */
    public void deleteChallengeCertification(int challengeCertificationCode) {
        ChallengeCertification certification = challengeCertificationRepository.findById(challengeCertificationCode)
                .orElseThrow(() -> new EntityNotFoundException("Challenge certification not found with code: " + challengeCertificationCode));

        challengeCertificationRepository.deleteById(challengeCertificationCode);
    }


    /* 10. 신고 승인 */
    public void approveReport(int reportCode) {
        Report report = reportRepository.findById(reportCode)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with code: " + reportCode));

        // 신고 상태를 PROCESSED로 변경
        report.setState(ReportState.PROCESSED);
        reportRepository.save(report);
    }


    /* 11. 신고 반려 */
    public void rejectReport(int reportCode) {
        Report report = reportRepository.findById(reportCode)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with code: " + reportCode));

        // 신고 상태를 RETURNED로 변경
        report.setState(ReportState.RETURNED);
        reportRepository.save(report);
    }
}
