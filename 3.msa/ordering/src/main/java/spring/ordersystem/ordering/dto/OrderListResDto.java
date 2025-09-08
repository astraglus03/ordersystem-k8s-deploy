package spring.ordersystem.ordering.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.ordersystem.ordering.entity.OrderStatus;
import spring.ordersystem.ordering.entity.Ordering;
import spring.ordersystem.ordering.entity.OrderingDetail;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    private OrderStatus orderStatus;
    private List<OrderDetailResDto> orderDetails; // 주문 상세 내역

    public static OrderListResDto fromEntity(Ordering ordering) {

        return OrderListResDto.builder()
                .id(ordering.getId())
                .memberEmail(ordering.getMemberEmail())
                .orderStatus(ordering.getStatus())
                .orderDetails(ordering.getOrderingDetails().stream().map(OrderDetailResDto::fromEntity).toList())
                .build();
    }
}
