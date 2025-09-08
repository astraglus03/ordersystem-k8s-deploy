package spring.ordersystem.ordering.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import spring.ordersystem.common.dto.CommonDto;
import spring.ordersystem.ordering.dto.OrderCreateDto;

// 이름 유레카에 등록된 application.name과 동일하게 작성 -> 나중에 서비스명으로 변경필요함.
// URL부분은 k8s의 service명
@FeignClient(name = "product-service", url = "http://product-service")
public interface ProductFeignClient {
    @GetMapping("/product/detail/{productId}")
    CommonDto getProductById(@PathVariable Long productId);

    @PutMapping("product/updatestock")
    void updateProductStockQuantity(@RequestBody OrderCreateDto dto);
}
