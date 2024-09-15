package com.igeeksky.xcache.samples;

import com.igeeksky.xtool.core.json.SimpleJSON;

import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/15
 */
public class Order {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private String receiver;

    private String address;

    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;

        return Objects.equals(getId(), order.getId()) && Objects.equals(getUserId(), order.getUserId()) && Objects.equals(getProductId(), order.getProductId()) && Objects.equals(getProductName(), order.getProductName()) && Objects.equals(getReceiver(), order.getReceiver()) && Objects.equals(getAddress(), order.getAddress()) && Objects.equals(getPhone(), order.getPhone());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getUserId());
        result = 31 * result + Objects.hashCode(getProductId());
        result = 31 * result + Objects.hashCode(getProductName());
        result = 31 * result + Objects.hashCode(getReceiver());
        result = 31 * result + Objects.hashCode(getAddress());
        result = 31 * result + Objects.hashCode(getPhone());
        return result;
    }

    @Override
    public String toString() {
        return SimpleJSON.toJSONString(this);
    }

}
