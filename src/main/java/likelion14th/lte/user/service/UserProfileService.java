package likelion14th.lte.user.service;

import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.user.dto.request.CreateTestUserRequest;
import likelion14th.lte.user.dto.response.UserProfileResponse;
import likelion14th.lte.user.entity.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileService {

    // [Q5. Service 안에서 new UserRepository() 로 객체를 직접 생성하지 않고,
    // 외부에서 의존성 주입(DI)을 받는 이유는 무엇인가요? (결합도와 단위 테스트 관점)]
    // 답변:
    // 결합도 관점: new UserRepository()로 직접 만들면 UserProfileService는 UserRepository에강하게 묶여버립니다.
    // 나중에 DB를 바꾸거나 Repository 구현체가 달라지면
    // Service 코드도 함께 수정해야 하는 번거로움이 생깁니다.
    // 단위 테스트 관점: 직접 생성하면 테스트할 때 실제 DB가 반드시 필요합니다.
    // DI를 사용하면 테스트 시 가짜 Repository를 대신 주입해서
    // DB 없이도 Service 로직만 독립적으로 테스트할 수 있습니다.
    private final UserRepository userRepository;

    // [Q6. (코딩 문제) 만약 클래스 위의 @RequiredArgsConstructor를 지운다면,
    // 우리가 직접 작성해야 할 의존성 주입용 자바 '생성자' 코드는 어떤 모습일까요? 아래에 직접 코딩해 보세요.]
    /*

       protected UserProfileService(UserRepository userRepository) {
           this.userRepository = userRepository;
       }

    */

    @Transactional
    public UserProfileResponse creatTestUser(CreateTestUserRequest request){

        // [Q7. 일반적인 생성자 new User(name, intro, tag) 방식을 쓰지 않고,
        // User.builder()...build() 라는 '빌더 패턴'을 사용하여 객체를 조립했을 때 얻는 장점은 무엇인가요?]
        // 답변:
        // new User("홍길동", "안녕하세요", "tag123") 방식은 매개변수가 많아질수록
        // 몇 번째 자리에 무슨 값을 넣는지 헷갈리고, 순서를 실수로 바꿔도 오류가 안 납니다.
        // 빌더 패턴은 .username("홍길동").introduction("안녕하세요") 처럼
        // 필드 이름을 직접 명시하며 값을 넣기 때문에 훨씬 읽기 쉽고 실수를 줄일 수 있습니다.
        // 또한 필요한 필드만 골라서 설정할 수 있어 선택적 필드가 있을 때 매우 유연합니다.
        User newUser = User.builder()
                .username(request.getUsername())
                .userTag(request.getUserTag())
                .introduction(request.getIntroduction())
                .build();

        User saveUser;
        try{
            // [Q8. 데이터를 저장하는 이 메서드 위에 @Transactional이 반드시 붙어야 하는 이유는 무엇인가요?
            // (저장 도중 DB 서버가 끊겼을 때의 상황을 가정해서 설명하세요)]
            // 답변:
            // 예를 들어 유저 저장 도중 DB 서버가 갑자기 꺼진다면,
            // @Transactional이 없을 경우 데이터가 절반만 저장된 오염된 상태로 남을 수 있습니다.
            // @Transactional이 있으면 저장 작업 전체를 하나의 묶음으로 처리하기 때문에,
            // 중간에 문제가 생기면 작업 전체가 자동으로 취소(롤백)되어
            // DB가 문제 발생 이전의 깨끗한 상태로 되돌아갑니다.
            // 즉, "전부 성공하거나, 전부 없던 일로 하거나"를 보장해주는 안전장치입니다.
            saveUser = userRepository.save(newUser);
        }
        catch (Exception e){
            throw new GeneralException(ErrorCode.BAD_REQUEST);
        }
        return UserProfileResponse.from(saveUser);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

}