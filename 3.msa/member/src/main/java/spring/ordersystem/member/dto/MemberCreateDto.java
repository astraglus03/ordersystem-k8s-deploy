package spring.ordersystem.member.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.ordersystem.member.entity.Member;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberCreateDto {
    private String name;
    private String email;
//    8글자 이상 검증 어노테이션
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .password(encodedPassword)
                .build();
    }
}
