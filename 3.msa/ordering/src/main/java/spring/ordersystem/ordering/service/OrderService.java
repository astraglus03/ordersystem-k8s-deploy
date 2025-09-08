package spring.ordersystem.ordering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import spring.ordersystem.common.dto.CommonDto;
import spring.ordersystem.common.service.SseAlarmService;
import spring.ordersystem.ordering.dto.OrderCreateDto;
import spring.ordersystem.ordering.dto.OrderListResDto;
import spring.ordersystem.ordering.dto.ProductDto;
import spring.ordersystem.ordering.entity.Ordering;
import spring.ordersystem.ordering.entity.OrderingDetail;
import spring.ordersystem.ordering.feignClient.ProductFeignClient;
import spring.ordersystem.ordering.repository.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SseAlarmService sseAlarmService;
    private final RestTemplate restTemplate;
    private final ProductFeignClient productFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Long createOrder(String email, List<OrderCreateDto> orderCreateDtos) {
        Ordering ordering = Ordering.builder()
                .memberEmail(email)
                .build();

        for(OrderCreateDto orderCreateDto : orderCreateDtos) {
//           상품 조회
            String productDetailUrl = "http://product-service/product/detail/" + orderCreateDto.getProductId();
            HttpHeaders headers = new HttpHeaders();
//            httpentity: httpbody와 httpheader를 세팅하기 위한 객체
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<CommonDto> responseEntity = restTemplate.exchange(productDetailUrl, HttpMethod.GET, entity, CommonDto.class);
            CommonDto commonDto = responseEntity.getBody();
            System.out.println(commonDto);
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto product = objectMapper.convertValue(commonDto.getData(), ProductDto.class);

//
//            if(product.getStockQuantity() - orderCreateDto.getProductCount() < 0) {
//                throw new IllegalArgumentException(product.getProductName()+" 상품의 재고가 부족합니다."+ "현재 재고: " + product.getStockQuantity());
//            }
//
//            주문발생
            OrderingDetail orderingDetail = OrderingDetail.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .quantity(orderCreateDto.getProductCount())
                    .ordering(ordering)
                    .build();
            ordering.getOrderingDetails().add(orderingDetail);
//
//            동기적 재고 감소
            String updateStockUrl = "http://product-service/product/updatestock";
            HttpHeaders stockHeaders = new HttpHeaders();
//            httpentity: httpbody와 httpheader를 세팅하기 위한 객체
            HttpEntity<OrderCreateDto> updateStockEntity = new HttpEntity<>(orderCreateDto, stockHeaders);
            restTemplate.exchange(updateStockUrl, HttpMethod.PUT, updateStockEntity, Void.class);
        }
        orderRepository.save(ordering);

        sseAlarmService.publishMessage("admin@naver.com", email, ordering.getId());
        return ordering.getId();
    }

    public List<OrderListResDto> getOrderList(){
        // 주문 목록 조회
        List<Ordering> orderings = orderRepository.findAll();

        // 주문 목록을 DTO로 변환
        return orderings.stream().map(OrderListResDto::fromEntity).collect(Collectors.toList());
    }

    public List<OrderListResDto> getMyOrderList(String email){
        List<Ordering> orderings = orderRepository.findAllByMemberEmail(email);

        // 주문 목록을 DTO로 변환
        return orderings.stream().map(OrderListResDto::fromEntity).collect(Collectors.toList());
    }

    public Ordering cancelOrder(Long id) {
////        ordering DB에 상태값 변경 CANCEL로 변경
//        Ordering ordering = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 주문이 존재하지 않습니다."));
//        for(OrderingDetail detail : ordering.getOrderingDetails()){
//            detail.getProduct().updateStockForCancel(detail.getQuantity());
////            //        redis의 재고값 증가
//        }
//        ordering.updateCancelStatus();
//        return ordering;
        return null;
    }

//    fallback메서드는 원본 메서드의 매개변수와 정확히 일치해야함.
    public void fallbackProductServiceCircuit(String email, List<OrderCreateDto> orderCreateDtos, Throwable t){
        throw new RuntimeException("상품서버 응답 없음. 나중에 시도해주세요.");
    }

//    테스트 : 4~5번의 정상요청 -> 5번중에 2번의 지연발생 -> circuit open -> 그 다음 요청은 fallback메서드로 처리됨

    @CircuitBreaker(name = "productServiceCircuit", fallbackMethod = "fallbackProductServiceCircuit")
    public Long createFeignKafka(String email, List<OrderCreateDto> orderCreateDtos) {
        Ordering ordering = Ordering.builder()
                .memberEmail(email)
                .build();

        for(OrderCreateDto orderCreateDto : orderCreateDtos) {
//            feign클라이언트를 사용한 동기적 상품 조회
            CommonDto commonDto = productFeignClient.getProductById(orderCreateDto.getProductId());
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto product = objectMapper.convertValue(commonDto.getData(), ProductDto.class);


            if(product.getStockQuantity() - orderCreateDto.getProductCount() < 0) {
                throw new IllegalArgumentException(product.getProductName()+" 상품의 재고가 부족합니다."+ "현재 재고: " + product.getStockQuantity());
            }
//
//            주문발생
            OrderingDetail orderingDetail = OrderingDetail.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .quantity(orderCreateDto.getProductCount())
                    .ordering(ordering)
                    .build();
            ordering.getOrderingDetails().add(orderingDetail);

////            kafka를 사용한 비동기적 재고 감소
//            productFeignClient.updateProductStockQuantity(orderCreateDto);

            kafkaTemplate.send("stock-update-topic", orderCreateDto);

        }
        orderRepository.save(ordering);

        sseAlarmService.publishMessage("admin@naver.com", email, ordering.getId());
        return ordering.getId();
    }
}
