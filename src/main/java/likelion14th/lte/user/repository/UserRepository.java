package likelion14th.lte.user.repository;

import likelion14th.lte.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// [추가문제] (필수 X) 이 코드는 인터페이스일 뿐이고 구현체(implements) 클래스가 없습니다.
// 그런데 어떻게 프로그램 실행 시 DB와 통신하는 객체로 동작할 수 있나요?
// 답변: Spring Data JPA가 애플리케이션 실행 시점에 JpaRepository를 상속한 인터페이스를 감지하고,
// 내부적으로 SimpleJpaRepository라는 구현체를 자동으로 생성하여 스프링 빈으로 등록합니다.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
}