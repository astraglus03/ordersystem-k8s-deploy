package spring.ordersystem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductUpdateStockDto {
    private Long productId; // 상품 ID
    private int stock; // 재고 수량
}
