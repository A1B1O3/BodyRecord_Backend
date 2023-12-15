package A1B1O3.bodyrecord.admin.dto.response;

import A1B1O3.bodyrecord.challenge.domain.repository.ChallengeCertification;
import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.report.domain.repository.Report;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

import static lombok.AccessLevel.PUBLIC;

@Getter
@Setter
@RequiredArgsConstructor(access = PUBLIC)
public class ChallengeCertificationReportDetailResponse {

    private final int reportCode;
    private final String memberName;
    private final LocalDate reportDate;
    private final int challengecerCode;
    private final String reportContent;

    public static ChallengeCertificationReportDetailResponse from(final Report report, Member member, ChallengeCertification challengeCertification) {
        return new ChallengeCertificationReportDetailResponse(
                report.getReportCode(),
                member.getMemberName(),
                report.getReportDate(),
                challengeCertification.getChallengecerCode(),
                report.getReportContent()

        );
    }

}
