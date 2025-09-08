package spring.ordersystem.ordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.ordersystem.ordering.entity.Ordering;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Ordering, Long> {

    List<Ordering> findAllByMemberEmail(String email);
}
