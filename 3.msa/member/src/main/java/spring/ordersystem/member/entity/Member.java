package spring.ordersystem.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
// jpql을 제외하고 모든 조회쿼리에 where del_yn = 'N' 붙이는 효과
// 기본적으로 delYn이 'N'인 경우만 조회되도록 설정
@Where(clause = "del_yn = 'N'")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    private String password;

    @Column(length = 1, nullable = false)
    @Builder.Default
    private String delYn = "N";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    public void deleteMember(String delYn) {
        this.delYn = delYn;
    }
}
