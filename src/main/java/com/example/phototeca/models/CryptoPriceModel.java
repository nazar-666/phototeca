package com.example.phototeca.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoPriceModel {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("price")
    private BigDecimal price;
}
