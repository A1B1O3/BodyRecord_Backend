package A1B1O3.bodyrecord.exercise.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.util.Date;

@Getter
@RequiredArgsConstructor
public class ExerciseRequest {

    private final String exerciseName;

    private final Float exerciseWeight;

    private final Integer exerciseCount;

    private final Time exerciseTime;

    private final String exerciseImagePath;

    private final Boolean exerciseShare;

    @JsonFormat(pattern = "yyyy-mm-dd")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private final Date exerciseDate;

    private final MultipartFile imgFile;

}



