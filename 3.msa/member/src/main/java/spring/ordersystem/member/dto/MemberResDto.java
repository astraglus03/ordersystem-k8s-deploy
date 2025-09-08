package spring.ordersystem.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.ordersystem.member.entity.Member;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberResDto {
    private Long id; // 회원 ID
    private String name; // 회원 이름
    private String email; // 회원 이메일

    public static MemberResDto fromEntity(Member member) {
        return MemberResDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }
}
