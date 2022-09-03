package model;

import lombok.Data;

@Data
public class Item {
    private String itemId;
    private String itemName;
    private Integer count;
}
