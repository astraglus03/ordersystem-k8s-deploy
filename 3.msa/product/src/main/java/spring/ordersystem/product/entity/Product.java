package spring.ordersystem.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID
    private String productName; // 상품 이름
    private String category; // 상품 카테고리
    @Column(length = 1000)
    private String imageUrl; // 상품 이미지 URL
    private int price; // 상품 가격
    private int stockQuantity; // 상품 재고 수량

    private String memberEmail;

    public void update(String productName, String category, int price, int stockQuantity, String imageUrl) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    public void updateStock(int stockQuantity) {
        this.stockQuantity -= stockQuantity;
    }

    public void updateStockForCancel(int stockQuantity) {
        this.stockQuantity += stockQuantity;
    }
}
