package likelion14th.lte.user.entity;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
// [Q1. @NoArgsConstructor는 매개변수가 없는 기본 생성자를 만듭니다.
// 그런데 왜 누구나 쓸 수 있게 PUBLIC으로 열어두지 않고, 굳이 PROTECTED로 막아두었을까요? (객체 생성의 안전성과 JPA 관점)]
// 답변: JPA는 내부적으로 리플렉션을 통해 객체를 생성할 때 기본 생성자가 필요합니다.
// 그러나 PUBLIC으로 열어두면 외부에서 new User()처럼 빈 껍데기 객체를 자유롭게 만들 수 있어
// 데이터 무결성이 깨질 위험이 있습니다. PROTECTED로 설정하면 JPA는 사용할 수 있으면서도,
// 외부에서 무분별하게 빈 객체를 생성하는 것을 막아 객체 생성의 안전성을 보장합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [Q2. @Column(nullable = false) 어노테이션이 DB와 자바 코드 사이에서 하는 역할은 무엇인가요?]
    // 답변: 이 어노테이션은 Java 코드와 DB 테이블 사이의 계약서 역할을 합니다.
    // DB 관점에서는 DDL 생성 시 해당 컬럼에 NOT NULL 제약조건을 자동으로 추가하고,
    // Java 관점에서는 이 필드는 반드시 값이 있어야 한다는 의도를 코드로 명시합니다.
    @Column(nullable = false)
    private String username;

    @Column(length = 16, nullable = false, unique = true)
    private String userTag;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String s3ImageKey;

    @Builder(access = AccessLevel.PUBLIC)
    private User(String username, String userTag, String introduction) {
        this.username = username;
        this.userTag = userTag;
        this.introduction = introduction;
    }

    // [Q3. @Setter를 위 @Getter처럼 사용하면 모든 멤버들에 setter 메서드가 생성됩니다.
    // 하지만 왜 @Setter를 쓰지 않고 updateIntroduction()이라는 명확한 메서드를 만든 객체지향적인 이유는 무엇인가요?]
    // 답변: @Setter를 사용하면 외부에서 모든 필드를 무분별하게 변경할 수 있어 캡슐화가 깨집니다.
    // updateIntroduction()처럼 의도가 명확한 메서드를 만들면 소개글만 변경 가능하다는
    // 비즈니스 규칙이 표현되고, 변경 범위를 제한하여 Side Effect를 방지합니다.
    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }
}