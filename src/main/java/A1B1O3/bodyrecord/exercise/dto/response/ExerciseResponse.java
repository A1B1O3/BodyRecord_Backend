package A1B1O3.bodyrecord.exercise.dto.response;

import A1B1O3.bodyrecord.exercise.domain.Exercise;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Time;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@RequiredArgsConstructor(access = PRIVATE)
public class ExerciseResponse {

    private final String exerciseName;

    private final Float exerciseWeight;

    private final Integer exerciseCount;

    private final Time exerciseTime;

    private final String exerciseImagePath;

    private final Boolean exerciseShare;

    private final Date exerciseDate;


    public static ExerciseResponse from(final Exercise exercise, @Value("${image.image-url}") final String imageUrl) {
        exercise.setExerciseImagePath(imageUrl + exercise.getExerciseImagePath());
        return new ExerciseResponse(
                exercise.getExerciseName(),
                exercise.getExerciseWeight(),
                exercise.getExerciseCount(),
                exercise.getExerciseTime(),
                exercise.getExerciseImagePath(),
                exercise.getExerciseShare(),
                exercise.getExerciseDate()
        );
    }
}