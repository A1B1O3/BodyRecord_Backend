package A1B1O3.bodyrecord.admin.presentation;

import A1B1O3.bodyrecord.admin.dto.response.*;
import A1B1O3.bodyrecord.admin.service.AdminService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "관리자")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @ApiOperation(value = "관리자 챌린지 신고 목록 조회", notes = "관리자가 챌린지 신고 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 신고 목록 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지 신고 목록 조회 실패"),
    })
    /* 1. 관리자 챌린지신고 목록조회 */
    @GetMapping("/challenge-reports")
    public ResponseEntity<List<ChallengeReportResponse>> getChallengeReports() {
        List<ChallengeReportResponse> challengeReports = adminService.getChallengeReports();
        return ResponseEntity.ok(challengeReports);
    }

    @ApiOperation(value = "관리자 챌린지 신고 상세 조회", notes = "관리자가 특정 챌린지 신고의 상세 정보를 조회합니다.")
    @ApiImplicitParam(name = "reportCode", value = "챌린지 신고 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 신고 상세 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지 신고 상세 조회 실패"),
    })
    /* 2. 관리자 챌린지신고 상세조회 */
    @GetMapping("/challenge-reports/{reportCode}")
    public ResponseEntity<ChallengeReportDetailResponse> getChallengeReportDetails(@PathVariable int reportCode) {
        ChallengeReportDetailResponse challengeReportDetails = adminService.getChallengeReportDetails(reportCode);
        return ResponseEntity.ok(challengeReportDetails);
    }

    @ApiOperation(value = "관리자 챌린지 인증 신고 목록 조회", notes = "관리자가 챌린지 인증 신고 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 인증 신고 목록 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지 인증 신고 목록 조회 실패"),
    })
    /* 3. 관리자 챌린지인증신고 목록조회 */
    @GetMapping("/challenge-certifications-reports")
    public ResponseEntity<List<ChallengeCertificationReportResponse>> getChallengeCertificationReports() {
        List<ChallengeCertificationReportResponse> challengeCertificationReports = adminService.getChallengeCertificationReports();
        return ResponseEntity.ok(challengeCertificationReports);
    }

    @ApiOperation(value = "관리자 챌린지 인증 신고 상세 조회", notes = "관리자가 특정 챌린지 인증 신고의 상세 정보를 조회합니다.")
    @ApiImplicitParam(name = "reportCode", value = "챌린지 인증 신고 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 인증 신고 상세 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지 인증 신고 상세 조회 실패"),
    })
    /* 4. 관리자 챌린지인증신고 상세조회 */
    @GetMapping("/certification-reports/{reportCode}")
    public ResponseEntity<ChallengeCertificationReportDetailResponse> getChallengeCertificationReportDetails(@PathVariable int reportCode) {
        ChallengeCertificationReportDetailResponse certificationReportDetails = adminService.getChallengeCertificationReportDetails(reportCode);
        return ResponseEntity.ok(certificationReportDetails);
    }

    @ApiOperation(value = "관리자 챌린지 목록 조회", notes = "관리자가 등록된 모든 챌린지 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 목록 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지 목록 조회 실패"),
    })
    /* 5. 관리자 챌린지 목록 조회 */
    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeAdminResponse>> getAllChallenges() {
        List<ChallengeAdminResponse> challenges = adminService.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    @ApiOperation(value = "관리자 챌린지 상세 조회", notes = "관리자가 특정 챌린지의 상세 정보를 조회합니다.")
    @ApiImplicitParam(name = "challengeCode", value = "챌린지 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 상세 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지 상세 조회 실패"),
    })
    /* 6. 관리자 챌린지 상세 조회 */
    @GetMapping("/challenges/{challengeCode}")
    public ResponseEntity<ChallengeDetailAdminResponse> getChallengeDetailsAdmin(@PathVariable int challengeCode) {
        ChallengeDetailAdminResponse challengeDetails = adminService.getChallengeDetailsAdmin(challengeCode);
        return ResponseEntity.ok(challengeDetails);
    }

    @ApiOperation(value = "관리자 챌린지 삭제", notes = "관리자가 특정 챌린지를 삭제합니다.")
    @ApiImplicitParam(name = "challengeCode", value = "챌린지 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지 삭제 성공"),
            @ApiResponse(code = 404, message = "챌린지 찾을 수 없음"),
    })
    /* 7. 관리자 챌린지 삭제 */
    @DeleteMapping("/challenges/{challengeCode}")
    public ResponseEntity<Void> deleteChallengeAdmin(@PathVariable int challengeCode) {
        try {
            adminService.deleteChallengeAdmin(challengeCode);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "관리자 챌린지인증 목록 조회", notes = "관리자가 등록된 모든 챌린지인증 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지인증 목록 조회 성공"),
            @ApiResponse(code = 500, message = "챌린지인증 목록 조회 실패"),
    })
    /* 8. 관리자 챌린지인증 조회 */
    @GetMapping("/certifications")
    public ResponseEntity<List<ChallengeCertificationAdminResponse>> getChallengeCertificationsAdmin(@Value("${image.image-url}") String imageUrlPrefix) {
        List<ChallengeCertificationAdminResponse> certifications = adminService.getChallengeCertificationsAdmin(imageUrlPrefix);
        return ResponseEntity.ok(certifications);
    }

    @ApiOperation(value = "관리자 챌린지인증 삭제", notes = "관리자가 특정 챌린지인증을 삭제합니다.")
    @ApiImplicitParam(name = "challengecerCode", value = "챌린지인증 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "챌린지인증 삭제 성공"),
            @ApiResponse(code = 404, message = "챌린지인증 찾을 수 없음"),
    })
    /* 9. 관리자 챌린지인증 삭제 */
    @DeleteMapping("/certifications/{challengecerCode}")
    public ResponseEntity<Void> deleteChallengeCertification(@PathVariable int challengecerCode) {
        try {
            adminService.deleteChallengeCertification(challengecerCode);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "관리자 신고승인", notes = "신고를 승인하는 API입니다.")
    @ApiImplicitParam(name = "reportCode", value = "신고 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "신고 승인 성공"),
            @ApiResponse(code = 404, message = "신고 찾을 수 없음"),
    })
    /* 10. 관리자 신고승인 */
    @PostMapping("/approve-report/{reportCode}")
    public ResponseEntity<String> approveReport(@PathVariable int reportCode) {
        try {
            adminService.approveReport(reportCode);
            return ResponseEntity.ok("Report with code " + reportCode + " has been approved.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ApiOperation(value = "관리자 신고반려", notes = "신고를 반려하는 API입니다.")
    @ApiImplicitParam(name = "reportCode", value = "신고 코드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "신고 반려 성공"),
            @ApiResponse(code = 404, message = "신고 찾을 수 없음"),
    })
    /* 11. 관리자 신고반려 */
    @PostMapping("/reject-report/{reportCode}")
    public ResponseEntity<String> rejectReport(@PathVariable int reportCode) {
        try {
            adminService.rejectReport(reportCode);
            return ResponseEntity.ok("Report with code " + reportCode + " has been rejected.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
