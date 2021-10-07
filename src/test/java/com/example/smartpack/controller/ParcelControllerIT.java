package com.example.smartpack.controller;

import com.example.smartpack.model.dto.CustomerDto;
import com.example.smartpack.model.dto.ParcelDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"DELETE FROM parcel", "DELETE FROM customer"})
class ParcelControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private ParcelDto parcelOne;
    private ParcelDto parcelTwo;

    @BeforeEach
    void init() {
        CustomerDto customer = new CustomerDto();
        customer.setName("Nagy Tibor");
        customer.setPhoneNumber("+36305584221");
        customer.setEmail("tibor.nagy@fakemail.com");
        customer.setCity("Budapest");
        customer.setZipCode(1023);
        customer.setAddress("Akácfa utca 17.");
        CustomerDto storedCustomer = testRestTemplate.postForObject("/customer", customer, CustomerDto.class);
        long customerId = storedCustomer.getId();

        parcelOne = new ParcelDto();
        parcelOne.setCustomerId(customerId);
        parcelOne.setRecipientName("Tóth István");
        parcelOne.setRecipientPhoneNumber("+36305478332");
        parcelOne.setRecipientEmail("istvan.toth@fakemail.com");
        parcelOne.setRecipientCity("Budapest");
        parcelOne.setRecipientZipCode(1014);
        parcelOne.setRecipientAddress("Tószegi út 112.");
        parcelOne.setCashOnDelivery(0);
        parcelOne.setParcelSize("S");
        parcelOne.setStatus("DELIVERED");

        parcelTwo = new ParcelDto();
        parcelTwo.setCustomerId(customerId);
        parcelTwo.setRecipientName("Gáspár Tamás");
        parcelTwo.setRecipientPhoneNumber("+36309663254");
        parcelTwo.setRecipientEmail("tamas.gaspar@fakemail.com");
        parcelTwo.setRecipientCity("Szolnok");
        parcelTwo.setRecipientZipCode(5000);
        parcelTwo.setRecipientAddress("Kocsis köz 3.");
        parcelTwo.setCashOnDelivery(0);
        parcelTwo.setParcelSize("L");
        parcelTwo.setStatus("UNDELIVERED");
    }

    @Test
    void listAllParcel_EmptyDatabase_ShouldReturnEmptyList() {
        ResponseEntity<List<ParcelDto>> responseEntity = testRestTemplate.exchange(
                "/parcel",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void listAllParcel_ParcelsAdded_ShouldReturnSameParcels() {
        testRestTemplate.postForObject("/parcel", parcelOne, ParcelDto.class);
        testRestTemplate.postForObject("/parcel", parcelTwo, ParcelDto.class);

        ResponseEntity<List<ParcelDto>> responseEntity = testRestTemplate.exchange(
                "/parcel",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .extracting(ParcelDto::getRecipientName)
                .containsExactly("Tóth István", "Gáspár Tamás");
    }

    @Test
    void getParcel_NotExistingParcel_ShouldReturnNotFoundStatus() {
        ResponseEntity<ParcelDto> responseEntity = testRestTemplate.getForEntity(
                "/parcel/{id}",
                ParcelDto.class,
                1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getParcel_ParcelAdded_ShouldReturnSameParcel() {
        ParcelDto storedParcel = testRestTemplate.postForObject("/parcel", parcelOne, ParcelDto.class);

        ResponseEntity<ParcelDto> responseEntity = testRestTemplate.getForEntity(
                "/parcel/{id}",
                ParcelDto.class,
                storedParcel.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting(ParcelDto::getRecipientName).isEqualTo("Tóth István");
    }

    @Test
    void addParcel_InvalidParcelAdded_ShouldReturnBadRequestStatus() {
        parcelOne.setParcelSize("Z");
        ResponseEntity<ParcelDto> responseEntity = testRestTemplate.postForEntity(
                "/parcel",
                parcelOne,
                ParcelDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void addParcel_ParcelAdded_ShouldReturnSameParcelWithNewStatus() {
        ResponseEntity<ParcelDto> responseEntity = testRestTemplate.postForEntity(
                "/parcel",
                parcelOne,
                ParcelDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting(ParcelDto::getRecipientName).isEqualTo("Tóth István");
        assertThat(responseEntity.getBody()).extracting(ParcelDto::getStatus).isEqualTo("NEW");
    }

    @Test
    void updateParcel_UpdatedWithInvalidParcel_ShouldReturnBadRequestStatus() {
        ParcelDto storedParcel = testRestTemplate.postForObject("/parcel", parcelOne, ParcelDto.class);

        parcelOne.setStatus("INVALID_STATUS");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ParcelDto> httpEntity = new HttpEntity<>(parcelOne, httpHeaders);

        ResponseEntity<ParcelDto> responseEntity = testRestTemplate.exchange(
                "/parcel/{id}",
                HttpMethod.PUT,
                httpEntity,
                ParcelDto.class,
                storedParcel.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateParcel_UpdatedWithParcel_ShouldUpdateParcel() {
        ParcelDto storedParcel = testRestTemplate.postForObject("/parcel", parcelOne, ParcelDto.class);

        parcelOne.setStatus("IN_TRANSIT");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ParcelDto> httpEntity = new HttpEntity<>(parcelOne, httpHeaders);

        ResponseEntity<ParcelDto> responseEntity = testRestTemplate.exchange(
                "/parcel/{id}",
                HttpMethod.PUT,
                httpEntity,
                ParcelDto.class,
                storedParcel.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting(ParcelDto::getRecipientName).isEqualTo("Tóth István");
        assertThat(responseEntity.getBody()).extracting(ParcelDto::getStatus).isEqualTo("IN_TRANSIT");
    }

    @Test
    void deleteParcel_NotExistingParcel_ShouldReturnNotFoundStatus() {
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/parcel/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteParcel_ParcelDeleted_ShouldDeleteParcel() {
        ParcelDto storedParcel = testRestTemplate.postForObject("/parcel", parcelOne, ParcelDto.class);

        testRestTemplate.delete("/parcel/{id}", storedParcel.getId());

        ResponseEntity<List<ParcelDto>> responseEntity = testRestTemplate.exchange(
                "/parcel",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

}
