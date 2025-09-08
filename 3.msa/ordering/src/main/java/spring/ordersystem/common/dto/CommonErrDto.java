package spring.ordersystem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommonErrDto {
    private int status_code; // HTTP 상태 코드
    private String status_message; // 상태 메시지
}
