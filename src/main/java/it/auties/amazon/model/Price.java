package it.auties.amazon.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Price<N>{
    private final N item;
    private final N shipping;
}