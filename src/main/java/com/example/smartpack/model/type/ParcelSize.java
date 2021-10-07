package com.example.smartpack.model.type;

import lombok.Getter;

@Getter
public enum ParcelSize {

    S("10cm x 40cm x 60cm", 15, 1099),
    M("20cm x 40cm x 60cm", 25, 1199),
    L("40cm x 40cm x 60cm", 25, 1699),
    XL("60cm x 40cm x 60cm", 25, 2299);

    private final String sizeLimit;
    private final int weightLimit;
    private final int price;

    ParcelSize(String sizeLimit, int weightLimit, int price) {
        this.sizeLimit = sizeLimit;
        this.weightLimit = weightLimit;
        this.price = price;
    }

}
