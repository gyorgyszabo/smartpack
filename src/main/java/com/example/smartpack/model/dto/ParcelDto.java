package com.example.smartpack.model.dto;

import com.example.smartpack.model.entity.Customer;
import com.example.smartpack.model.entity.Parcel;
import com.example.smartpack.model.type.ParcelSize;
import com.example.smartpack.model.type.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ParcelDto {

    private Long id;
    private Long customerId;

    @NotBlank
    @Size(min = 5, max = 40)
    private String recipientName;

    @Pattern(regexp = "\\+36[237]0\\d{7}")
    private String recipientPhoneNumber;

    @NotBlank
    @Email
    private String recipientEmail;

    @NotBlank
    @Size(min = 2, max = 25)
    private String recipientCity;

    @NotNull
    @Min(1000)
    @Max(9985)
    private Integer recipientZipCode;

    @NotBlank
    @Size(min = 5, max = 50)
    private String recipientAddress;

    @NotNull
    @Min(0)
    @Max(150000)
    private Integer cashOnDelivery;

    @NotNull
    @Pattern(regexp = "S|M|L|XL")
    private String parcelSize;

    @NotNull
    @Pattern(regexp = "NEW|IN_TRANSIT|DELIVERED|UNDELIVERED")
    private String status;

    public ParcelDto (Parcel parcel) {
        this.id = parcel.getId();
        this.customerId = parcel.getCustomer().getId();
        this.recipientName = parcel.getRecipientName();
        this.recipientPhoneNumber = parcel.getRecipientPhoneNumber();
        this.recipientEmail = parcel.getRecipientEmail();
        this.recipientCity = parcel.getRecipientCity();
        this.recipientZipCode = parcel.getRecipientZipCode();
        this.recipientAddress = parcel.getRecipientAddress();
        this.cashOnDelivery = parcel.getCashOnDelivery();
        this.parcelSize = parcel.getParcelSize().toString();
        this.status = parcel.getStatus().toString();
    }

    public Parcel toEntity() {
        Parcel parcel = new Parcel();
        parcel.setId(id);
        Customer customer = new Customer();
        customer.setId(customerId);
        parcel.setCustomer(customer);
        parcel.setRecipientName(recipientName);
        parcel.setRecipientPhoneNumber(recipientPhoneNumber);
        parcel.setRecipientEmail(recipientEmail);
        parcel.setRecipientCity(recipientCity);
        parcel.setRecipientZipCode(recipientZipCode);
        parcel.setRecipientAddress(recipientAddress);
        parcel.setCashOnDelivery(cashOnDelivery);
        parcel.setParcelSize(ParcelSize.valueOf(parcelSize));
        parcel.setStatus(Status.valueOf(status));
        return parcel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParcelDto parcelDto = (ParcelDto) o;
        return Objects.equals(id, parcelDto.id)
                && Objects.equals(customerId, parcelDto.customerId)
                && Objects.equals(recipientName, parcelDto.recipientName)
                && Objects.equals(recipientPhoneNumber, parcelDto.recipientPhoneNumber)
                && Objects.equals(recipientEmail, parcelDto.recipientEmail)
                && Objects.equals(recipientCity, parcelDto.recipientCity)
                && Objects.equals(recipientZipCode, parcelDto.recipientZipCode)
                && Objects.equals(recipientAddress, parcelDto.recipientAddress)
                && Objects.equals(cashOnDelivery, parcelDto.cashOnDelivery)
                && Objects.equals(parcelSize, parcelDto.parcelSize)
                && Objects.equals(status, parcelDto.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, recipientName, recipientPhoneNumber, recipientEmail,
                recipientCity, recipientZipCode, recipientAddress, cashOnDelivery, parcelSize, status);
    }

}
