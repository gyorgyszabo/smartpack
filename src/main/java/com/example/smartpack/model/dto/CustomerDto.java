package com.example.smartpack.model.dto;

import com.example.smartpack.model.entity.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDto {

    private Long id;

    @NotBlank
    @Size(min = 5, max = 40)
    private String name;

    @Pattern(regexp = "\\+36[237]0\\d{7}")
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 25)
    private String city;

    @NotNull
    @Min(1000)
    @Max(9985)
    private Integer zipCode;

    @NotBlank
    @Size(min = 5, max = 50)
    private String address;

    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
        this.email = customer.getEmail();
        this.city = customer.getCity();
        this.zipCode = customer.getZipCode();
        this.address = customer.getAddress();
    }

    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setCity(city);
        customer.setZipCode(zipCode);
        customer.setAddress(address);
        return customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDto that = (CustomerDto) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(email, that.email)
                && Objects.equals(city, that.city)
                && Objects.equals(zipCode, that.zipCode)
                && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phoneNumber, email, city, zipCode, address);
    }

}
