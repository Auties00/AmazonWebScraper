package it.auties.amazon.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AmazonItemContainer {
    private String name;
    private AmazonItemOffer item;
}