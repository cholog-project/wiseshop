package cholog.wiseshop.api.campaign.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;

public record CreateCampaignRequest(
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime startDate,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime endDate,
        int goalQuantity,
        Long productId) {
}
