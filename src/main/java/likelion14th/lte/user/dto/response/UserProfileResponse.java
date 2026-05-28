package likelion14th.lte.user.dto.response;

import likelion14th.lte.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileResponse {

    private String userName;
    private String profileImageUrl;
    private String introduction;

    // [Q4. Controller가 DB에서 꺼낸 원본 Entity(User)를 클라이언트 화면에 그대로 반환하지 않고,
    // 굳이 from() 메서드를 통해 DTO로 한번 변환해서 내보내는 핵심적인 이유 2가지는 무엇인가요?]
    // 답변:
    // 이유 1. 보안: User Entity에는 비밀번호, s3ImageKey 등 외부에 절대 노출되면 안 되는 필드가 있습니다.
    // Entity를 그대로 반환하면 이런 민감한 정보가 클라이언트에 통째로 전달되는 사고가 발생합니다.
    // DTO는 화면에 필요한 데이터(이름, 프로필 이미지, 소개글)만 골라 담아서 보내므로 안전합니다.
    //
    // 이유 2. 유연성: DB 테이블 구조(Entity)가 바뀌어도 DTO만 수정하면 클라이언트에게 보내는
    // API 응답 형식은 그대로 유지할 수 있습니다.
    // 예를 들어 이 코드처럼 username + "#" + userTag를 합쳐서 새로운 형태로 가공하는 것도 가능합니다.
    // 즉, DB 구조 변경이 화면까지 영향을 미치는 것을 막아줍니다.
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getUsername() + "#" + user.getUserTag(),
                user.getProfileImage(),
                user.getIntroduction()
        );
    }
}