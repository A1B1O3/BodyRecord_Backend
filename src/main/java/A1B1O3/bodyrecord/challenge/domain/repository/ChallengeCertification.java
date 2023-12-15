package A1B1O3.bodyrecord.challenge.domain.repository;

import A1B1O3.bodyrecord.common.BaseEntity;
import A1B1O3.bodyrecord.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import static A1B1O3.bodyrecord.common.type.StatusType.USEABLE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PUBLIC;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PUBLIC)
@SQLDelete(sql = "UPDATE challenge_certification SET state = 'DELETED' WHERE challengecer_code = ?")
@Where(clause = "state = 'USEABLE'")
public class ChallengeCertification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int challengecerCode;

    @Column
    private String challengeImage;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "challenge_code")
    private Challenge challengeCode;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_code")
    private Member memberCode;

    public ChallengeCertification(final int challengecerCode,
                                  final String challengeImage,
                                  final Challenge challengeCode
    ) {
        super(USEABLE);
        this.challengecerCode = challengecerCode;
        this.challengeImage = challengeImage;
        this.challengeCode = challengeCode;
    }

    public String getFullImageUrl(String imageUrlPrefix) {
        return imageUrlPrefix + challengeImage;
    }
}
