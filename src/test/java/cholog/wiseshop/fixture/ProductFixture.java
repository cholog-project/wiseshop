package cholog.wiseshop.fixture;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;

public class ProductFixture {

    public static CreateProductRequest getCreateProductRequest() {
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;
        int totalQuantity = 5;

        return new CreateProductRequest(name, description, price, totalQuantity);
    }
}
