package spring.ordersystem.product.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ordersystem.common.service.S3Uploader;
import spring.ordersystem.product.dto.*;
import spring.ordersystem.product.entity.Product;
import spring.ordersystem.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Uploader s3Uploader;

    public Long createProduct(String email, ProductCreateDto productCreateDto) {

        // 2. 이미지가 있는 경우만 S3 업로드
        String imageUrl = null;
        if (productCreateDto.getProductImage() != null && !productCreateDto.getProductImage().isEmpty()) {
            imageUrl = s3Uploader.upload(productCreateDto.getProductImage(), "product/");
        }
        Product product = productCreateDto.toEntity(email, imageUrl);
        productRepository.save(product);


        return product.getId();
    }

    public void update(ProductUpdateDto productUpdateDto, Long id) {
        // 기존 상품 조회
        Product product = productRepository.findById(id).orElseThrow(() -> new NoSuchElementException("물품을 찾을 수 없습니다."));

        // 기존 이미지 삭제
        if (product.getImageUrl() != null) {
            s3Uploader.delete(product.getImageUrl());
        }

        String newImageUrl = null;

        if(productUpdateDto.getProductImage() !=null){
            newImageUrl = s3Uploader.upload(productUpdateDto.getProductImage(), "product/");
        }

        // 상품 정보 업데이트
        product.update(
                productUpdateDto.getProductName(),
                productUpdateDto.getCategory(),
                productUpdateDto.getPrice(),
                productUpdateDto.getStockQuantity(),
                newImageUrl
        );


    }

    public Page<ProductResDto> getAllProducts(Pageable pageable, ProductSearchDto productSearchDto) {

        Specification<Product> specification = new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
//                predicateList.add(criteriaBuilder.equal(root.get("category"), productSearchDto.getCategory()));
//                predicateList.add(criteriaBuilder.equal(root.get("productName"), productSearchDto.getProductName()));

                if(productSearchDto.getProductName() !=null && !productSearchDto.getProductName().isEmpty()) {
                    predicateList.add(criteriaBuilder.like(root.get("productName"), "%" + productSearchDto.getProductName() + "%"));
                }
                if(productSearchDto.getCategory() != null && !productSearchDto.getCategory().isEmpty()) {
                    predicateList.add(criteriaBuilder.equal(root.get("category"), productSearchDto.getCategory()));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                for(int i=0; i<predicateList.size();i++){
                    predicates[i] = predicateList.get(i);
                }

                return criteriaBuilder.and(predicates);
            }
        };

        Page<Product> productsList = productRepository.findAll(specification, pageable);
        return productsList.map(ProductResDto::fromEntity);
    }

    public ProductResDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다."));
        return ProductResDto.fromEntity(product);
    }

    public Long updateStock(ProductUpdateStockDto productUpdateStockDto) {
        Product product = productRepository.findById(productUpdateStockDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다."));

        if(product.getStockQuantity() < productUpdateStockDto.getStock()) {
            throw new IllegalArgumentException("재고 수량이 현재 재고보다 많을 수 없습니다.");
        }

        // 재고 업데이트
        product.updateStock(productUpdateStockDto.getStock());

        return product.getId();
    }
}
