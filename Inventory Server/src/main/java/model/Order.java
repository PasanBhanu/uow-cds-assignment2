package model;

import lombok.Data;

@Data
public class Order {
    private String orderNumber;
    private String itemId;
    private Integer count;
}
