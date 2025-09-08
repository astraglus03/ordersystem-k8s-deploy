package spring.ordersystem.ordering.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 ID
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.ORDERED; // 주문 상태

    private String memberEmail;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ordering", orphanRemoval = true)
    @Builder.Default
    private List<OrderingDetail> orderingDetails = new ArrayList<>(); // 주문 상세 정보

    public void updateCancelStatus(){
        this.status = OrderStatus.CANCELLED;
    }
}
