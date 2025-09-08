package spring.ordersystem.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.ordersystem.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    // 추가적인 메소드 정의가 필요할 경우 여기에 작성
    // 예: List<Product> findByCategory(String category);
    // 예: List<Product> findByProductNameContaining(String productName);
}
