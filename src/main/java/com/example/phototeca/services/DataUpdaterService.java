package com.example.phototeca.services;

import java.util.List;

public interface DataUpdaterService {
    void startDataComparison();
    void stopDataComparison();
    List<String> getCryptosChangesOverThreshold();
}
