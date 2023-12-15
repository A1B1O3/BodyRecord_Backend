package A1B1O3.bodyrecord.challenge.domain.repository;

import A1B1O3.bodyrecord.common.BaseEntity;
import A1B1O3.bodyrecord.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static A1B1O3.bodyrecord.common.type.StatusType.USEABLE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PUBLIC;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PUBLIC)
@SQLDelete(sql = "UPDATE challenge SET state = 'DELETED' WHERE challenge_code = ?")
@Where(clause = "state = 'USEABLE'")
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int challengeCode;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_code")
    private Member memberCode;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "challengecategory_code")
    private ChallengeCategory challengecategoryCode;

    @Column(nullable = false, length = 100)
    private String challengeTitle;

    @Column(nullable = false, length = 1000)
    private String challengeContent;

    @Column(nullable = false)
    private LocalDate challengeStartdate;

    @Column(nullable = false)
    private LocalDate challengeEnddate;

    @OneToMany(mappedBy = "challengeCode")
    private List<ChallengeParticipate> challengeParticipates;


    public Challenge(final int challengeCode,
                     final Member memberCode,
                     final ChallengeCategory challengecategoryCode,
                     final String challengeTitle,
                     final String challengeContent,
                     final LocalDate challengeStartdate,
                     final LocalDate challengeEnddate
    ) {
        super(USEABLE);
        this.challengeCode = challengeCode;
        this.memberCode = memberCode;
        this.challengecategoryCode = challengecategoryCode;
        this.challengeTitle = challengeTitle;
        this.challengeContent = challengeContent;
        this.challengeStartdate = challengeStartdate;
        this.challengeEnddate = challengeEnddate;
    }

}
