package spring.ordersystem.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.ordersystem.ordering.entity.OrderingDetail;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDetailResDto {
    private Long detailId; // 주문 상세 ID
    private String productName; // 상품 이름
    private int quantity; // 주문 수량


    public static OrderDetailResDto fromEntity(OrderingDetail orderingDetail) {
        return OrderDetailResDto.builder()
                .detailId(orderingDetail.getId())
                .productName(orderingDetail.getProductName()) // 상품 이름
                .quantity(orderingDetail.getQuantity()) // 주문 수량
                .build();
    }
}
