package com.example.phototeca.services.impl;

import com.example.phototeca.dao.CryptoPrice;
import com.example.phototeca.models.CryptoPriceModel;
import com.example.phototeca.repositories.CryptoPriceRepository;
import com.example.phototeca.services.CryptoPriceService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service("cryptoPriceService")
public class CryptoPriceServiceImpl implements CryptoPriceService {
    private final RestTemplate restTemplate;
    private final CryptoPriceRepository cryptoPriceRepository;

    public CryptoPriceServiceImpl(RestTemplate restTemplate, CryptoPriceRepository cryptoPriceRepository) {
        this.restTemplate = restTemplate;
        this.cryptoPriceRepository = cryptoPriceRepository;
    }

    @Override
    public List<CryptoPriceModel> fetchApiData(String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<CryptoPriceModel>> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    @Override
    public List<CryptoPrice> findAllFromDB() {
        return cryptoPriceRepository.findAll();
    }
}
