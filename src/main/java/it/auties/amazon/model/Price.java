package it.auties.amazon.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Price<E>{
    private E item;
    private E shipping;
}