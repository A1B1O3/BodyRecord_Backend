package A1B1O3.bodyrecord.body.domain.repository;

import A1B1O3.bodyrecord.body.domain.Body;
import A1B1O3.bodyrecord.body.dto.request.BodyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyRepository extends JpaRepository<Body,Long> {



    List<Body> findAllByMemberCodeMemberCode(final Integer memberCode);
    Body save(BodyRequest bodyRequest);
    boolean existsByMemberCodeAndBodyCode(final Integer memberCode, final Integer bodyCode);


    public void deleteByMemberCodeMemberCode(final Integer memberCode);

}
