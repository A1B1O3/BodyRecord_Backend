package A1B1O3.bodyrecord.body.dto.response;

import A1B1O3.bodyrecord.body.domain.Body;
import A1B1O3.bodyrecord.common.type.StatusType;
import A1B1O3.bodyrecord.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class BodyResponse {
    private final int bodyCode;
    private final float weight;
    private final float muscle;
    private final float fat;
    private final StatusType state;
    public static BodyResponse from(final Body body) {
        return new BodyResponse(
                body.getBodyCode(),
                body.getWeight(),
                body.getMuscle(),
                body.getFat(),
                body.getState()
        );
    }
}
