package spring.ordersystem.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.ordersystem.ordering.entity.Ordering;
import spring.ordersystem.ordering.entity.OrderingDetail;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderCreateDto {
    private Long productId; // 주문할 상품 ID
    private int productCount; // 주문할 상품 수량
}
