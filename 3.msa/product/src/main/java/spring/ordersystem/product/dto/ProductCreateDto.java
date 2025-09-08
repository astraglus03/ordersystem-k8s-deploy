package spring.ordersystem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import spring.ordersystem.product.entity.Product;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCreateDto {
    private String productName; // 상품 이름
    private String category; // 상품 카테고리
    private int price; // 상품 가격
    private int stockQuantity; // 재고 수량
    private MultipartFile productImage; // 상품 이미지 파일

    public Product toEntity(String email, String imageUrl) {
        return Product.builder()
                .productName(this.productName)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .imageUrl(imageUrl) // 이미지 URL
                .memberEmail(email)
                .build();
    }
}
