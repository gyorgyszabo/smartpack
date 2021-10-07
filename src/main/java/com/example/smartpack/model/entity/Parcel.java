package com.example.smartpack.model.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import com.example.smartpack.model.type.ParcelSize;
import com.example.smartpack.model.type.Status;

@Entity
@Getter
@Setter
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    private String recipientName;
    private String recipientPhoneNumber;
    private String recipientEmail;
    private String recipientCity;
    private Integer recipientZipCode;
    private String recipientAddress;
    private Integer cashOnDelivery;

    @Enumerated(EnumType.STRING)
    private ParcelSize parcelSize;

    @Enumerated(EnumType.STRING)
    private Status status;

}
