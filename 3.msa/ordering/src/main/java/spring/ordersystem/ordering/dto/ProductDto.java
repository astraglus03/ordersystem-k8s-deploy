package spring.ordersystem.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {
    private Long id; // 상품 ID
    private String productName; // 상품 이름
    private String category; // 상품 카테고리
    private int price; // 상품 가격
    private int stockQuantity; // 재고 수량
    private String imageUrl; // 상품 이미지 URL
}
