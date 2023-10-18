package com.example.phototeca.services.impl;

import com.example.phototeca.dao.CryptoPrice;
import com.example.phototeca.models.CryptoPriceModel;
import com.example.phototeca.services.CryptoPriceService;
import com.example.phototeca.services.DataUpdaterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("dataUpdaterService")
public class DataUpdaterServiceImpl implements DataUpdaterService {

    @Value("${crypto.price.url}")
    private String cryptoPriceUrl;

    @Value("${crypto.price.percentage.threshold}")
    private BigDecimal cryptoPricePercentageThreshold;

    private boolean makeDataComparison = false;

    private List<String> cryptosOverThreshold = new ArrayList<>();

    private final CryptoPriceService cryptoPriceService;

    public DataUpdaterServiceImpl(CryptoPriceService cryptoPriceService) {
        this.cryptoPriceService = cryptoPriceService;
    }

    @Scheduled(fixedDelayString = "${crypto.price.check.in.millis}")
    public void checkPriceForCrypto() {
        if(makeDataComparison){
            cryptosOverThreshold = new ArrayList<>();
            List<CryptoPriceModel> cryptoPriceFromApi = cryptoPriceService.fetchApiData(cryptoPriceUrl);
            List<CryptoPrice> cryptoPriceFromDB = cryptoPriceService.findAllFromDB();

            cryptoPriceFromApi.forEach(cryptoPriceApi -> {
                Optional<CryptoPrice> cryptoDB = cryptoPriceFromDB.stream()
                        .filter(cryptoPriceDb -> cryptoPriceDb.getSymbol().equals(cryptoPriceApi.getSymbol()))
                        .findFirst();
                if(cryptoDB.isPresent()){
                    BigDecimal priceFromApi = cryptoPriceApi.getPrice();
                    BigDecimal priceFromDB = cryptoDB.get().getPrice();

                    // Calculate the percentage change
                    BigDecimal percentageChange = priceFromApi.subtract(priceFromDB)
                            .divide(priceFromDB, BigDecimal.TEN.intValue(), RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100));

                    if (percentageChange.abs().compareTo(cryptoPricePercentageThreshold) > 0) {
                        if (percentageChange.compareTo(BigDecimal.ZERO) > 0) {
                            cryptosOverThreshold.add(cryptoDB.get() + " increased it's value by more than " + cryptoPricePercentageThreshold + "%.");
                        } else {
                            cryptosOverThreshold.add(cryptoDB.get() + " decreased it's value for more than " + cryptoPricePercentageThreshold + "%.");
                        }
                    }
                }
            });
        }
    }

    @Override
    public void startDataComparison() {
        makeDataComparison = true;
    }

    @Override
    public void stopDataComparison() {
        makeDataComparison = false;
    }

    @Override
    public List<String> getCryptosChangesOverThreshold() {
        return cryptosOverThreshold;
    }
}
