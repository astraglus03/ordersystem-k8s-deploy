package spring.ordersystem.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.ordersystem.common.dto.CommonDto;
import spring.ordersystem.common.dto.CommonErrDto;
import spring.ordersystem.product.dto.*;
import spring.ordersystem.product.service.ProductService;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestHeader("X-User-Email") String email, @ModelAttribute ProductCreateDto productCreateDto) {
        Long product = productService.createProduct(email, productCreateDto);
        return new ResponseEntity<>(CommonDto.builder()
                .data(product)
                .status_code(HttpStatus.CREATED.value())
                .status_message("상품 등록 성공")
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@ModelAttribute @Valid ProductUpdateDto productUpdateDto, @PathVariable Long id) {
        try {
            productService.update(productUpdateDto, id);
            return new ResponseEntity<>(CommonDto.builder()
                    .data(id)
                    .status_code(HttpStatus.OK.value())
                    .status_message("상품 수정 성공")
                    .build(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(CommonErrDto.builder()
                    .status_code(HttpStatus.BAD_REQUEST.value())
                    .status_message(e.getMessage())
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updatestock")
    public ResponseEntity<?> updateStock(@RequestBody ProductUpdateStockDto productUpdateStockDto) {
        Long id = productService.updateStock(productUpdateStockDto);

        return new ResponseEntity<>(CommonDto.builder()
                .data(id)
                .status_code(HttpStatus.OK.value())
                .status_message("상품재고 수정 성공")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getProductList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, ProductSearchDto productSearchDto) {
        Page<ProductResDto> allProducts = productService.getAllProducts(pageable, productSearchDto);
        return new ResponseEntity<>(CommonDto.builder()
                .data(allProducts)
                .status_code(HttpStatus.OK.value())
                .status_message("상품 목록 조회 성공")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long productId) throws InterruptedException {
        Thread.sleep(3000);
        ProductResDto productDetail = productService.getProductDetail(productId);
        return new ResponseEntity<>(CommonDto.builder()
                .data(productDetail)
                .status_code(HttpStatus.OK.value())
                .status_message("상품 상세 조회 성공")
                .build(), HttpStatus.OK);
    }
}
