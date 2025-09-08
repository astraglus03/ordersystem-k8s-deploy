package spring.ordersystem.ordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ordersystem.ordering.entity.OrderingDetail;

public interface OrderDetailRepository extends JpaRepository<OrderingDetail, Long> {

}
