package spring.ordersystem.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import spring.ordersystem.product.dto.ProductUpdateStockDto;

@Component
@RequiredArgsConstructor
public class StockKafkaListener {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "stock-update-topic", containerFactory = "kafkaListener")
    public void stockConsumer(String message){
        System.out.println("컨슈머 메시지 수신" + message);
        try {
            ProductUpdateStockDto dto = objectMapper.readValue(message, ProductUpdateStockDto.class);
            productService.updateStock(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("메시지 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
