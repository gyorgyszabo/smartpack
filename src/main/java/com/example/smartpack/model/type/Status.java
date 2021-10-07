package com.example.smartpack.model.type;

import lombok.Getter;

@Getter
public enum Status {

    NEW("Package received"),
    IN_TRANSIT("Package is in transit"),
    DELIVERED("Package was delivered successfully"),
    UNDELIVERED("Package was attempted for delivery but failed");

    private final String description;

    Status(String description) {
        this.description = description;
    }

}
