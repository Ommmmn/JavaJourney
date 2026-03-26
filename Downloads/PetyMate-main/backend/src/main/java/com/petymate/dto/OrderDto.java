package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CartItem {
        @NotNull
        private Long productId;
        @NotNull @Min(1)
        private Integer qty;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ShippingAddress {
        @NotBlank
        private String name;
        @NotBlank
        private String phone;
        @NotBlank
        private String address;
        @NotBlank
        private String city;
        @NotBlank
        private String state;
        @NotBlank
        private String pincode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateCartOrderRequest {
        @NotEmpty(message = "Cart cannot be empty")
        private List<CartItem> items;
        @NotNull
        private ShippingAddress shippingAddress;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VerifyPaymentRequest {
        @NotBlank
        private String razorpayOrderId;
        @NotBlank
        private String razorpayPaymentId;
        @NotBlank
        private String razorpaySignature;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartOrderResponse {
        private String razorpayOrderId;
        private int amount;
        private String currency;
        private String keyId;
        private List<OrderItemDto> orderItems;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderResponse {
        private Long id;
        private BigDecimal totalAmount;
        private String status;
        private String shippingName;
        private String shippingPhone;
        private String shippingAddress;
        private String shippingCity;
        private String shippingState;
        private String shippingPincode;
        private String trackingNumber;
        private List<OrderItemDto> items;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private String productPhoto;
        private Integer qty;
        private BigDecimal unitPrice;
    }
}
