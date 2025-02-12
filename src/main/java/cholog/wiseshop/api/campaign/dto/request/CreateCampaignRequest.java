package cholog.wiseshop.api.campaign.dto.request;

import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.stock.Stock;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;

public record CreateCampaignRequest(
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime startDate,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime endDate,
    int goalQuantity,
    CreateProductRequest productRequest
) {

    public record CreateProductRequest(
        String name,
        String description,
        int price,
        int totalQuantity
    ) {

        public Product from() {
            return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(new Stock(totalQuantity))
                .build();
        }
    }
}
