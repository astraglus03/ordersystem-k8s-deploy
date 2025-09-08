package spring.ordersystem.ordering.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.ordersystem.common.dto.CommonDto;
import spring.ordersystem.ordering.dto.OrderCreateDto;
import spring.ordersystem.ordering.dto.OrderListResDto;
import spring.ordersystem.ordering.entity.Ordering;
import spring.ordersystem.ordering.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/ordering")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-Email") String email, @Valid @RequestBody List<OrderCreateDto> orderCreateDtos) {
        Long orderId = orderService.createFeignKafka(email, orderCreateDtos);
        return new ResponseEntity<>(CommonDto.builder()
                .data(orderId)
                .status_code(HttpStatus.CREATED.value())
                .status_message("주문 생성 성공")
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getOrderList() {
        List<OrderListResDto> orderList = orderService.getOrderList();
        return new ResponseEntity<>(CommonDto.builder()
                .data(orderList)
                .status_code(HttpStatus.OK.value())
                .status_message("주문 목록 조회 성공")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/myorders")
    public ResponseEntity<?> getMyOrderList(@RequestHeader("X-User-Email") String email){
        List<OrderListResDto> myOrderList = orderService.getMyOrderList(email);
        return new ResponseEntity<>(CommonDto.builder()
                .data(myOrderList)
                .status_code(HttpStatus.OK.value())
                .status_message("내 주문 목록 조회 성공")
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/cancle/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Ordering ordering = orderService.cancelOrder(id);
        return new ResponseEntity<>(CommonDto.builder()
                .data(ordering.getId())
                .status_code(HttpStatus.OK.value())
                .status_message("주문 취소 성공")
                .build(), HttpStatus.OK);
    }

}
