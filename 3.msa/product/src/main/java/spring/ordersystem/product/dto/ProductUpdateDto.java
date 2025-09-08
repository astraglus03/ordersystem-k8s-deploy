package spring.ordersystem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import spring.ordersystem.product.entity.Product;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductUpdateDto {
    private String productName;
    private String category;
    private int price;
    private int stockQuantity;
    private MultipartFile productImage;
}
