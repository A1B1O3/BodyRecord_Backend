package A1B1O3.bodyrecord.exercise.service;
import A1B1O3.bodyrecord.exercise.exception.AuthException;
import A1B1O3.bodyrecord.exception.BadRequestException;
import A1B1O3.bodyrecord.exercise.domain.Exercise;
import A1B1O3.bodyrecord.exercise.domain.repository.ExerciseRepository;
import A1B1O3.bodyrecord.exercise.dto.request.ExerciseRequest;
import A1B1O3.bodyrecord.exercise.dto.request.ExerciseUpdateRequest;
import A1B1O3.bodyrecord.exercise.dto.response.ExerciseDetailResponse;
import A1B1O3.bodyrecord.exercise.dto.response.ExerciseResponse;

//import A1B1O3.bodyrecord.exercise.dto.response.SearchResponse;
import A1B1O3.bodyrecord.exercise.dto.response.SearchResponse;
//import A1B1O3.bodyrecord.member.Member;
//import A1B1O3.bodyrecord.member.repository.MemberRepository;
import A1B1O3.bodyrecord.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static A1B1O3.bodyrecord.common.exception.type.ExceptionCode.INVALID_EXERCISE_LOG_WITH_MEMBER;
import static A1B1O3.bodyrecord.common.exception.type.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static A1B1O3.bodyrecord.exception.type.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getAllExercise(final int memberCode) {

        final List<Exercise> exercises = exerciseRepository.findAllByMemberMemberCode(memberCode);

        return exercises.stream()
                .map(exercise -> ExerciseResponse.from(exercise))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExerciseDetailResponse getExerciseDetail(final int exerciseCode) {

        final Exercise exercise = exerciseRepository.findByExerciseCode(exerciseCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EXERCISE_CODE));
        return ExerciseDetailResponse.from(exercise);
    }

    @Transactional(readOnly = true)
    public void validateExerciseByMember(final int memberCode, final int exerciseCode) {
        if (!exerciseRepository.existsByMemberMemberCodeAndExerciseCode(memberCode, exerciseCode)) {
            throw new AuthException(INVALID_EXERCISE_LOG_WITH_MEMBER);
        }
    }

    public int save(final int memberCode, ExerciseRequest exerciseRequest) {
        final Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Exercise newExercise = Exercise.of(
                member,
                exerciseRequest.getExerciseName(),
                exerciseRequest.getExerciseDate(),
                exerciseRequest.getExerciseCount(),
                exerciseRequest.getExerciseWeight(),
                exerciseRequest.getExerciseTime(),
                exerciseRequest.getExerciseShare(),
                exerciseRequest.getExerciseImage()

        );

        final Exercise exercise = exerciseRepository.save(newExercise);

        return exercise.getExerciseCode();
    }

    public void update(int exerciseCode, ExerciseUpdateRequest exerciseUpdateRequest) {
        final  Exercise exercise = exerciseRepository.findByExerciseCode(exerciseCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EXERCISE_CODE));

        exercise.update(exerciseUpdateRequest);
        exerciseRepository.save(exercise);
    }

    public void delete(final int exerciseCode) {
        final  Exercise exercise = exerciseRepository.findByExerciseCode(exerciseCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EXERCISE_CODE));
        exerciseRepository.delete(exercise);
    }

    @Transactional(readOnly = true)
    public Slice<SearchResponse> searchCategory(final boolean exerciseShare, final int goalCategoryCode, Pageable pageable) {
        final Slice<Exercise> searchCategoryExercise = exerciseRepository.findByExerciseShareAndMemberGoalCategoryGoalCategoryCode(exerciseShare,goalCategoryCode,pageable);

        return searchCategoryExercise
                .map(exercise -> SearchResponse.from(exercise));
    }

    @Transactional(readOnly = true)
    public Slice<SearchResponse> searchBody(final boolean exerciseShare,
                                            final float weight,
                                            final float fat,
                                            final float muscle,
                                            Pageable pageable) {
        final  Slice<Exercise> searchBodyExercise = exerciseRepository.findByExerciseShareAndMemberBodyWeightAndMemberBodyFatAndMemberBodyMuscle(exerciseShare,weight,fat,muscle,pageable);

        return searchBodyExercise
                .map(exercise -> SearchResponse.from(exercise));

    }



    @Transactional(readOnly = true)
    public Slice<SearchResponse> searchTotal(final boolean exerciseShare,
                                             final int goalCategoryCode,
                                             final float weight,
                                             final float fat,
                                             final float muscle,
                                             Pageable pageable) {
        final Slice<Exercise> searchTotalExercise = exerciseRepository.findByExerciseShareAndMemberGoalCategoryGoalCategoryCodeAndMemberBodyWeightAndMemberBodyFatAndMemberBodyMuscle(exerciseShare, goalCategoryCode, weight, fat, muscle, pageable);

        return searchTotalExercise
                .map(exercise -> SearchResponse.from(exercise));
    }
}
