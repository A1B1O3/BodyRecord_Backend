package A1B1O3.bodyrecord.exercise.domain.repository;

import A1B1O3.bodyrecord.exercise.domain.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    Optional<Exercise> findByExerciseCode(final int exerciseCode);

    boolean existsByMemberMemberCodeAndExerciseCode(final int memberCode, final int exerciseCode);

}
