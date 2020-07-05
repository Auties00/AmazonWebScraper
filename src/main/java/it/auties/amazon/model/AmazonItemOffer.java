package it.auties.amazon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmazonItemOffer {
    private int itemPrice;
    private int shippingPrice;
    private String url;
    private String status;
    private String seller;

    @Override
    public String toString(){
        return itemPrice + "," + shippingPrice + "," + status + "," + seller;
    }
}