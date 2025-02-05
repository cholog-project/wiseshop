package cholog.wiseshop.api.campaign.dto.request;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;

public record CreateCampaignRequest(
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime startDate,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime endDate,
    int goalQuantity,
    CreateProductRequest product) {

}
