package spring.ordersystem.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberLoginResDto {
    private String accessToken; // 액세스 토큰
    private String refreshToken; // 리프레시 토큰
}
