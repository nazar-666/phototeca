package com.example.phototeca.services;

import com.example.phototeca.dao.CryptoPrice;
import com.example.phototeca.models.CryptoPriceModel;

import java.util.List;

public interface CryptoPriceService {
    List<CryptoPriceModel> fetchApiData(String apiUrl);
    List<CryptoPrice> findAllFromDB();
}
