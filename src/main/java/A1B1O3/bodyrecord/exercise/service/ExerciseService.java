package A1B1O3.bodyrecord.exercise.service;

import A1B1O3.bodyrecord.body.domain.Body;
import A1B1O3.bodyrecord.body.domain.repository.BodyRepository;
import A1B1O3.bodyrecord.common.exception.AuthException;
import A1B1O3.bodyrecord.common.exception.BadRequestException;
import A1B1O3.bodyrecord.exercise.domain.Exercise;
import A1B1O3.bodyrecord.exercise.domain.repository.ExerciseRepository;
import A1B1O3.bodyrecord.exercise.dto.request.ExerciseRequest;
import A1B1O3.bodyrecord.exercise.dto.request.ExerciseUpdateRequest;
import A1B1O3.bodyrecord.exercise.dto.response.ExerciseDetailResponse;
import A1B1O3.bodyrecord.exercise.dto.response.ExerciseResponse;
import A1B1O3.bodyrecord.exercise.dto.response.SearchResponse;
import A1B1O3.bodyrecord.member.domain.Member;
import A1B1O3.bodyrecord.member.domain.repository.MemberRepository;
import A1B1O3.bodyrecord.util.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static A1B1O3.bodyrecord.common.exception.type.ExceptionCode.*;



@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final MemberRepository memberRepository;
    private final BodyRepository bodyRepository;
    private final UploadFile uploadFile;



    @Transactional(readOnly = true)
    public List<ExerciseResponse> getAllExercise(final int memberCode, @Value("${image.image-url}") final String imageUrl) {

        final List<Exercise> exercises = exerciseRepository.findAllByMemberMemberCode(memberCode);

        return exercises.stream()
                .map(exercise -> ExerciseResponse.from(exercise, imageUrl))
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public ExerciseDetailResponse getExerciseDetail(final int exerciseCode, final String imageUrl) {
        final Exercise exercise = exerciseRepository.findByExerciseCode(exerciseCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EXERCISE_LOG_ID));
        exercise.setExerciseImagePath(imageUrl + exercise.getExerciseImagePath());
        return ExerciseDetailResponse.from(exercise);
    }



    @Transactional(readOnly = true)
    public void validateExerciseByMember(final int memberCode, final int exerciseCode) {
        if (!exerciseRepository.existsByMemberMemberCodeAndExerciseCode(memberCode, exerciseCode)) {
            throw new AuthException(INVALID_EXERCISE_LOG_WITH_MEMBER);
        }
    }


    public int save(final int memberCode, ExerciseRequest exerciseRequest) throws IOException {
        final Member member = (Member) memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Exercise newExercise = Exercise.of(
                member,
                exerciseRequest.getExerciseName(),
                exerciseRequest.getExerciseCount(),
                exerciseRequest.getExerciseWeight(),
                exerciseRequest.getExerciseTime(),
                exerciseRequest.getExerciseShare(),
                exerciseRequest.getExerciseImagePath(),
                exerciseRequest.getExerciseDate()
        );

        if(!exerciseRequest.getImgFile().isEmpty()){
            String img = uploadFile.fileUpload(exerciseRequest.getImgFile());
            newExercise.setExerciseImagePath(img);
        }


        final Exercise exercise = exerciseRepository.save(newExercise);

        return exercise.getExerciseCode();
    }

    @Transactional
    public void update(int exerciseCode, ExerciseUpdateRequest exerciseUpdateRequest) throws IOException {
        final Exercise exercise = exerciseRepository.findByExerciseCode(exerciseCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EXERCISE_LOG_ID));

        List<ExerciseUpdateRequest> updatelist = new ArrayList<>();
        updatelist.add(exerciseUpdateRequest);


        for(int i = 0; i < updatelist.size(); i++){
            if(updatelist.get(i).getExerciseName() != null) exercise.setExerciseName(updatelist.get(i).getExerciseName());
            if(updatelist.get(i).getExerciseWeight() != null) exercise.setExerciseWeight(updatelist.get(i).getExerciseWeight());
            if(updatelist.get(i).getExerciseCount() != null) exercise.setExerciseCount(updatelist.get(i).getExerciseCount());
            if(updatelist.get(i).getExerciseTime() != null) exercise.setExerciseTime(updatelist.get(i).getExerciseTime());
            if(updatelist.get(i).getImgFile().getOriginalFilename()!= null){
                File f = new File(exerciseUpdateRequest.getExerciseImagePath());
                if(f.exists()){
                    f.delete();
                }
                String img = uploadFile.fileUpload(exerciseUpdateRequest.getImgFile());
                exercise.setExerciseImagePath(img);
            }
            if(updatelist.get(i).getExerciseShare() != null) exercise.setExerciseShare(updatelist.get(i).getExerciseShare());
            if(updatelist.get(i).getExerciseDate() != null) exercise.setExerciseDate(updatelist.get(i).getExerciseDate());

            exerciseRepository.save(exercise);
        }

    }

    public void delete(final int exerciseCode) {
        final  Exercise exercise = exerciseRepository.findByExerciseCode(exerciseCode)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_EXERCISE_LOG_ID));
        exerciseRepository.delete(exercise);
    }

    @Transactional(readOnly = true)
    public Slice<SearchResponse> searchBody(Pageable pageable, float minWeight, float maxWeight, float minFat, float maxFat, float minMuscle, float maxMuscle){
        List<Exercise> searchBodyExercise = exerciseRepository.findByExerciseShareIsTrue(pageable);
        if (minWeight > 0.0 && maxWeight > 0.0 && minFat > 0.0 && maxFat > 0.0 && minMuscle > 0.0 && maxMuscle > 0.0){
            List<Body> body = bodyRepository.findByWeightBetweenAndFatBetweenAndMuscleBetween(minWeight, maxWeight, minFat, maxFat, minMuscle, maxMuscle);
            List<Exercise> exercise = exerciseRepository.findByExerciseShareIsTrue(pageable);

            searchBodyExercise = exercise.stream().filter(e -> body.stream().anyMatch
                    (b -> e.getMember().equals(b.getMemberCode()))).collect(Collectors.toList());


        }

        return new SliceImpl<>(searchBodyExercise.stream().map(exercise -> SearchResponse.from(exercise)).collect(Collectors.toList()));

    }


}
