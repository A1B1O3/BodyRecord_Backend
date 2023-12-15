package A1B1O3.bodyrecord.exercise.presentation;

import A1B1O3.bodyrecord.auth.domain.PrincipalDetails;
import A1B1O3.bodyrecord.exercise.dto.request.ExerciseRequest;
import A1B1O3.bodyrecord.exercise.dto.request.ExerciseUpdateRequest;
import A1B1O3.bodyrecord.exercise.dto.response.ExerciseDetailResponse;
import A1B1O3.bodyrecord.exercise.dto.response.ExerciseResponse;
import A1B1O3.bodyrecord.exercise.dto.response.SearchResponse;
import A1B1O3.bodyrecord.exercise.service.ExerciseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;




@RestController
@RequiredArgsConstructor
@Api(tags = {"운동기록"})
@RequestMapping("/exercise/log")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @ApiOperation(value = "나의 운동기록 전체 조회", notes = "회원코드를 기반으로 특정 회원의 운동기록 전체 목록을 보여준다.")
    @ApiImplicitParam(name = "principalDetails", value = "인가된 회원")
    @ApiResponse(code = 200, message = "success", response = ExerciseResponse.class)

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises(@AuthenticationPrincipal PrincipalDetails principalDetails, @Value("${image.image-url}") String imageUrl){
        final List<ExerciseResponse> exerciseResponse = exerciseService.getAllExercise(principalDetails.getMember().getMemberCode(),imageUrl);
        return ResponseEntity.ok(exerciseResponse);
    }
    @ApiOperation(value = "나의 운동기록 상세 조회", notes = "회원코드와 운동코드를 기반으로 특정 회원의 특정 운동 기록을 보여준다.")
    @ApiImplicitParams ({
        @ApiImplicitParam(name = "principalDetails", value = "인가된 회원"),
        @ApiImplicitParam(name = "exerciseCode", value = "운동코드")
    })
    @ApiResponses({
            @ApiResponse(code= 200, message = "success", response = ExerciseDetailResponse.class),
            @ApiResponse(code= 2000, message = "invalid exercise log with member")
    })

    @GetMapping("/{exerciseCode}")
    public ResponseEntity<ExerciseDetailResponse> getExercise(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable final int exerciseCode, @Value("${image.image-url}") final String imageUrl) {
        exerciseService.validateExerciseByMember(principalDetails.getMember().getMemberCode(), exerciseCode);
       final ExerciseDetailResponse exerciseDetailResponse = exerciseService.getExerciseDetail(exerciseCode, imageUrl);
       return ResponseEntity.ok(exerciseDetailResponse);
    }


    @ApiOperation(value = "운동기록 등록", notes = "회원 자신의 운동기록을 등록한다.")
    @ApiImplicitParams ({
            @ApiImplicitParam(name = "principalDetails", value = "인가된 회원"),
            @ApiImplicitParam(name = "exerciseRequest", value = "운동기록요청")
    })
    @ApiResponses({
            @ApiResponse(code= 201, message = "created"),
            @ApiResponse(code= 1001, message = "not found member id")
    })

    @PostMapping
    public ResponseEntity<Void>saveExercise(@AuthenticationPrincipal PrincipalDetails principalDetails, @ModelAttribute @Valid final ExerciseRequest exerciseRequest) throws IOException {
        final int exerciseCode = exerciseService.save(principalDetails.getMember().getMemberCode(),exerciseRequest);

        return ResponseEntity.created(URI.create("/exercise/log/"+exerciseCode)).build();
    }

    @ApiOperation(value = "운동기록 수정", notes = "회원 자신의 운동기록을 수정한다.")
    @ApiImplicitParams ({
            @ApiImplicitParam(name = "principalDetails", value = "인가된 회원"),
            @ApiImplicitParam(name = "exerciseCode", value = "운동코드"),
            @ApiImplicitParam(name = "exerciseUpdateRequest", value = "운동기록수정요청")
    })
    @ApiResponses({
            @ApiResponse(code= 201, message = "created"),
            @ApiResponse(code= 2000, message = "invalid exercise log with member"),
            @ApiResponse(code= 2001, message = "not found exercise log id")
    })

    @PatchMapping("/{exerciseCode}")
    public ResponseEntity<Void> updateExercise(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @PathVariable final int exerciseCode,
                                               @ModelAttribute @Valid final ExerciseUpdateRequest exerciseUpdateRequest) throws IOException {
        exerciseService.validateExerciseByMember(principalDetails.getMember().getMemberCode(), exerciseCode);
        exerciseService.update(exerciseCode, exerciseUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "운동기록 삭제", notes = "회원 자신의 운동기록을 삭제한다.")
    @ApiImplicitParams ({
            @ApiImplicitParam(name = "principalDetails", value = "인가된 회원"),
            @ApiImplicitParam(name = "exerciseCode", value = "운동코드"),
    })
    @ApiResponses({
            @ApiResponse(code= 204, message = "no content"),
            @ApiResponse(code= 2001, message = "not found exercise log id")
    })

    @DeleteMapping("/{exerciseCode}")
    public ResponseEntity<Void> deleteExercise(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable final int exerciseCode){
        exerciseService.validateExerciseByMember(principalDetails.getMember().getMemberCode(), exerciseCode);
        exerciseService.delete(exerciseCode);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "운동기록 검색", notes = "다른 회원들의 기록을 검색한다.")
    @ApiImplicitParams ({
            @ApiImplicitParam(name = "minWeight", value = "최소몸무게"),
            @ApiImplicitParam(name = "maxWeight", value = "최대몸무게"),
            @ApiImplicitParam(name = "minFat", value = "최소체지방률"),
            @ApiImplicitParam(name = "maxFat", value = "최대체지방률"),
            @ApiImplicitParam(name = "minMuscle", value = "최소근육량"),
            @ApiImplicitParam(name = "maxMuscle", value = "최대근육량"),
    })
    @ApiResponses({
            @ApiResponse(code= 200, message = "success")
    })

    /*운동기록 체성분별 검색*/
    @GetMapping("/search/body")
    public ResponseEntity<Slice<SearchResponse>> searchBody(@PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                            @RequestParam(required = false, defaultValue = "0.0") float minWeight,
                                                            @RequestParam(required = false, defaultValue = "0.0") float maxWeight,
                                                            @RequestParam(required = false, defaultValue = "0.0") float minFat,
                                                            @RequestParam(required = false, defaultValue = "0.0") float maxFat,
                                                            @RequestParam(required = false, defaultValue = "0.0") float minMuscle,
                                                            @RequestParam(required = false, defaultValue = "0.0") float maxMuscle){
        final Slice<SearchResponse> searchResponse = exerciseService.searchBody(pageable, minWeight, maxWeight, minFat, maxFat, minMuscle, maxMuscle);
        return ResponseEntity.ok(searchResponse);
    }


}
