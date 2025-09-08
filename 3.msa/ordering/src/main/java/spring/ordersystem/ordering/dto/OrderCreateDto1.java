package spring.ordersystem.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderCreateDto1 {
    private Long storeId; // 주문할 가게 ID
    private String payment;
    private List<ProductDetailDto> details;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class ProductDetailDto{
        private Long productId; // 주문할 상품 ID
        private int productCount; // 주문할 상품 수량
    }
}
