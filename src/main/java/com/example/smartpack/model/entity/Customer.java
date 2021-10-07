package com.example.smartpack.model.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber;
    private String email;
    private String city;
    private Integer zipCode;
    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<Parcel> parcels;

}
