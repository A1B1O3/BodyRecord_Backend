package A1B1O3.bodyrecord.challenge.domain.repository;


import A1B1O3.bodyrecord.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    /* 1. 챌린지 카테고리별 조회 */
    List<Challenge> findAllByChallengecategoryCode(ChallengeCategory challengeCategory);

    /* 2. 챌린지 개별 조회 */
    Optional<Challenge> findByChallengeCode(int challengeCode);

    /* 3. 참여중인 챌린지 조회 */
    Optional<Challenge> findAllByChallengeCode(Challenge challengeCode);

    List<Challenge> findByMemberCode(Member member);

    List<Challenge> findByChallengecategoryCode(ChallengeCategory challengeCategory);

    /* 참여중인 챌린지 조회 */
//    List<Challenge> findChallengesByMemberCodeAndChallengeEnddateAfter(int memberCode, LocalDateTime currentDate);

    /* 챌린지 인기순 */
    @Query("SELECT c FROM Challenge c JOIN c.challengeParticipates cp GROUP BY c.challengeCode ORDER BY COUNT(cp.memberCode) DESC")
    List<Challenge> findAllByOrderByChallengeParticipatesCountDesc();

    /* 챌린지를 신규순 (createdAt) */
    List<Challenge> findAllByOrderByCreatedAtDesc();

    /* 챌린지 등록 */
    boolean existsByMemberCodeAndChallengeEnddateAfterAndChallengeEnddateBefore(
            Member member, LocalDate currentDate, LocalDate maxDate);




}