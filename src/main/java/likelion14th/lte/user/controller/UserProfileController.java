package likelion14th.lte.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.user.dto.request.CreateTestUserRequest;
import likelion14th.lte.user.dto.request.UserIntroRequest;
import likelion14th.lte.user.dto.response.UserProfileResponse;
import likelion14th.lte.user.entity.User;
import likelion14th.lte.user.service.UserProfileService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
@RestController
@Slf4j
@RequestMapping("/api/profile")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileController {

    // [Q9. Controller 내부에서 userRepository.findById()를 직접 호출해서 유저를 찾지 않고,
    // 반드시 userProfileService를 호출하여 작업을 위임해야 하는 이유는 무엇인가요? (단일 책임 원칙 관점)]
    // 답변:
    // Controller의 역할은 오직 HTTP 요청을 받고 응답을 돌려주는 것입니다.
    // 만약 Controller가 Repository를 직접 호출하면 데이터 조회, 예외 처리, 비즈니스 로직까지
    // Controller가 모두 떠안게 되어 역할이 뒤섞입니다.
    // Service에 위임하면 각 계층이 자신의 역할에만 집중할 수 있고,
    // 나중에 조회 로직이 바뀌어도 Controller는 수정할 필요가 없습니다.
    // 이것이 단일 책임 원칙입니다.
    public final UserProfileService userProfileService;

    @GetMapping
    @Operation(summary = "유저 프로필 조회", description = "유저아이디를 받아 유저 프로필을 반환하는 api")
    public ApiResponse<UserProfileResponse> getUserProfile(
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = Long.valueOf(jwt.getSubject());
        UserProfileResponse userProfileResponse = userProfileService.getUserProfile(userId);
        return ApiResponse.onSuccess(SuccessCode.OK, userProfileResponse);
    }

    @PostMapping
    @Operation(summary = "테스트 유저를 생성", description = "이름, 한줄소개, 유저 태그를 받아 유저를 생성")
    public ApiResponse<UserProfileResponse> createTestUserProfile(
            // [Q10. 클라이언트가 보낸 JSON 텍스트 데이터가 어떻게 자바 객체인 CreateTestUserRequest로
            // 변환되는지 앞의 어노테이션과 연관 지어 설명해 보세요.]
            // 답변:
            // @RequestBody 어노테이션이 이 변환을 담당합니다.
            // 클라이언트가 {"username":"홍길동", "userTag":"tag123"} 형태의 JSON을 HTTP Body에 담아 전송하면
            // Spring이 @RequestBody를 보고 Jackson 라이브러리를 동작시킵니다.
            // Jackson이 JSON의 키와 CreateTestUserRequest의 필드명을 매핑하여
            // 자동으로 자바 객체를 생성하고 값을 채워줍니다.
            // 즉, @RequestBody + Jackson이 JSON → 자바 객체 변환을 처리합니다.
            @RequestBody CreateTestUserRequest createTestUserRequest
    ){
        UserProfileResponse response = userProfileService.creatTestUser(createTestUserRequest);
        return ApiResponse.onSuccess(SuccessCode.CREATED, response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 프로필 추가 및 수정", description = "유저 프로필 이미지를 추가하거나 수정합니다.")
    public ApiResponse<UserProfileResponse> putUserProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("image") MultipartFile file
    ){
        Long userId = Long.valueOf(jwt.getSubject());

        UserProfileResponse response = userProfileService.putProfileimage(userId,file);
        return ApiResponse.onSuccess(SuccessCode.PROFILE_PUT_SUCCESS, response);
    }

    @DeleteMapping
    @Operation(summary = "프로필 이미지 삭제", description = "로그인한 본인의 프로필 이미지를 삭제합니다.")
    public ApiResponse<UserProfileResponse> deleteProfileImage(
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = Long.valueOf(jwt.getSubject());
        UserProfileResponse response = userProfileService.deleteProfileImage(userId);
        return ApiResponse.onSuccess(SuccessCode.PROFILE_DELETE_SUCCESS, response);
    }

    @GetMapping("/touser")
    @Operation(summary = "다른 유저 프로필 조회", description = "toUserId에 해당하는 다른 유저의 프로필을 조회합니다.")
    public ApiResponse<UserProfileResponse> getOtherUserProfile(
            @RequestParam Long toUserId
    ){
        UserProfileResponse response = userProfileService.getUserProfile(toUserId);
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, response);
    }

    @PutMapping("/intro")
    @Operation(summary = "한줄 소개 수정", description = "로그인한 본인의 한줄 소개를 수정합니다.")
    public ApiResponse<UserProfileResponse> updateIntroduction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserIntroRequest request
    ){
        Long userId = Long.valueOf(jwt.getSubject());
        UserProfileResponse response = userProfileService.updateIntroduction(userId, request.getIntroduce());
        return ApiResponse.onSuccess(SuccessCode.USER_PROFILE_UPDATE_SUCCESS, response);
    }
}