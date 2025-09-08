package spring.ordersystem.ordering.entity;

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
public class OrderingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 상세 ID
    private int quantity; // 주문 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering; // 주문 정보

    private Long productId; // 상품 ID

//    조회의 빈도에 따라 msa 도메인 설계에서 적절한 반정규화를 통한 성능 향상 가능
    private String productName; // 상품 이름
}
