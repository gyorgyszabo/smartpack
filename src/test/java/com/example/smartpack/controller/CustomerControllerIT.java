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
class CustomerControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private CustomerDto customerOne;
    private CustomerDto customerTwo;

    @BeforeEach
    void init() {
        customerOne = new CustomerDto();
        customerOne.setName("Nagy Tibor");
        customerOne.setPhoneNumber("+36305584221");
        customerOne.setEmail("tibor.nagy@fakemail.com");
        customerOne.setCity("Budapest");
        customerOne.setZipCode(1023);
        customerOne.setAddress("Akácfa utca 17.");

        customerTwo = new CustomerDto();
        customerTwo.setName("Horváth Katalin");
        customerTwo.setPhoneNumber("+36703483345");
        customerTwo.setEmail("katalin.horvath@fakemail.com");
        customerTwo.setCity("Szeged");
        customerTwo.setZipCode(6727);
        customerTwo.setAddress("Fecske utca 4.");
    }

    @Test
    void listAllCustomer_EmptyDatabase_ShouldReturnEmptyList() {
        ResponseEntity<List<CustomerDto>> responseEntity = testRestTemplate.exchange(
                "/customer",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void listAllCustomer_CustomersAdded_ShouldReturnSameCustomers() {
        testRestTemplate.postForObject("/customer", customerOne, CustomerDto.class);
        testRestTemplate.postForObject("/customer", customerTwo, CustomerDto.class);

        ResponseEntity<List<CustomerDto>> responseEntity = testRestTemplate.exchange(
                "/customer",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .extracting(CustomerDto::getName)
                .containsExactly("Nagy Tibor", "Horváth Katalin");
    }

    @Test
    void getCustomer_NotExistingCustomer_ShouldReturnNotFoundStatus() {
        ResponseEntity<CustomerDto> responseEntity = testRestTemplate.getForEntity(
                "/customer/{id}",
                CustomerDto.class,
                1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCustomer_CustomerAdded_ShouldReturnSameCustomer() {
        CustomerDto storedCustomer = testRestTemplate.postForObject("/customer", customerOne, CustomerDto.class);

        ResponseEntity<CustomerDto> responseEntity = testRestTemplate.getForEntity(
                "/customer/{id}",
                CustomerDto.class,
                storedCustomer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting(CustomerDto::getName).isEqualTo("Nagy Tibor");
    }

    @Test
    void addCustomer_InvalidCustomerAdded_ShouldReturnBadRequestStatus() {
        customerOne.setZipCode(10230);
        ResponseEntity<CustomerDto> responseEntity = testRestTemplate.postForEntity(
                "/customer",
                customerOne,
                CustomerDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void addCustomer_CustomerAdded_ShouldReturnSameCustomer() {
        ResponseEntity<CustomerDto> responseEntity = testRestTemplate.postForEntity(
                "/customer",
                customerOne,
                CustomerDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting(CustomerDto::getName).isEqualTo("Nagy Tibor");
    }

    @Test
    void updateCustomer_UpdatedWithInvalidCustomer_ShouldReturnBadRequestStatus() {
        CustomerDto storedCustomer = testRestTemplate.postForObject("/customer", customerOne, CustomerDto.class);

        customerOne.setPhoneNumber("+36305584221000000");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDto> httpEntity = new HttpEntity<>(customerOne, httpHeaders);

        ResponseEntity<CustomerDto> responseEntity = testRestTemplate.exchange(
                "/customer/{id}",
                HttpMethod.PUT,
                httpEntity,
                CustomerDto.class,
                storedCustomer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateCustomer_UpdatedWithCustomer_ShouldUpdateCustomer() {
        CustomerDto storedCustomer = testRestTemplate.postForObject("/customer", customerOne, CustomerDto.class);

        customerOne.setPhoneNumber("+36307148196");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDto> httpEntity = new HttpEntity<>(customerOne, httpHeaders);

        ResponseEntity<CustomerDto> responseEntity = testRestTemplate.exchange(
                "/customer/{id}",
                HttpMethod.PUT,
                httpEntity,
                CustomerDto.class,
                storedCustomer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).extracting(CustomerDto::getName).isEqualTo("Nagy Tibor");
        assertThat(responseEntity.getBody()).extracting(CustomerDto::getPhoneNumber).isEqualTo("+36307148196");
    }

    @Test
    void deleteCustomer_NotExistingCustomer_ShouldReturnNotFoundStatus() {
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/customer/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteCustomer_CustomerDeleted_ShouldDeleteCustomer() {
        CustomerDto storedCustomer = testRestTemplate.postForObject("/customer", customerOne, CustomerDto.class);

        testRestTemplate.delete("/customer/{id}", storedCustomer.getId());

        ResponseEntity<List<CustomerDto>> responseEntity = testRestTemplate.exchange(
                "/customer",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void listParcelByCustomerId_NotExistingCustomer_ShouldReturnNotFoundStatus() {
        ResponseEntity<Void> responseEntity = testRestTemplate.getForEntity(
                "/customer/{id}/parcel",
                Void.class,
                1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listParcelByCustomerId_CustomersAndParcelsAdded_ShouldReturnParcelsOfSpecifiedCustomer() {
        CustomerDto storedCustomerOne = testRestTemplate.postForObject("/customer", customerOne, CustomerDto.class);
        CustomerDto storedCustomerTwo = testRestTemplate.postForObject("/customer", customerTwo, CustomerDto.class);

        ParcelDto parcelOne = new ParcelDto();
        parcelOne.setCustomerId(storedCustomerOne.getId());
        parcelOne.setRecipientName("Tóth István");
        parcelOne.setRecipientPhoneNumber("+36305478332");
        parcelOne.setRecipientEmail("istvan.toth@fakemail.com");
        parcelOne.setRecipientCity("Budapest");
        parcelOne.setRecipientZipCode(1014);
        parcelOne.setRecipientAddress("Tószegi út 112.");
        parcelOne.setCashOnDelivery(0);
        parcelOne.setParcelSize("S");
        parcelOne.setStatus("DELIVERED");
        testRestTemplate.postForObject("/parcel", parcelOne, ParcelDto.class);

        ParcelDto parcelTwo = new ParcelDto();
        parcelTwo.setCustomerId(storedCustomerTwo.getId());
        parcelTwo.setRecipientName("Gáspár Tamás");
        parcelTwo.setRecipientPhoneNumber("+36309663254");
        parcelTwo.setRecipientEmail("tamas.gaspar@fakemail.com");
        parcelTwo.setRecipientCity("Szolnok");
        parcelTwo.setRecipientZipCode(5000);
        parcelTwo.setRecipientAddress("Kocsis köz 3.");
        parcelTwo.setCashOnDelivery(0);
        parcelTwo.setParcelSize("L");
        parcelTwo.setStatus("UNDELIVERED");
        testRestTemplate.postForObject("/parcel", parcelTwo, ParcelDto.class);

        ParcelDto parcelThree = new ParcelDto();
        parcelThree.setCustomerId(storedCustomerTwo.getId());
        parcelThree.setRecipientName("Somogyi Nóra");
        parcelThree.setRecipientPhoneNumber("+36707875521");
        parcelThree.setRecipientEmail("nora.somogyi@fakemail.com");
        parcelThree.setRecipientCity("Siófok");
        parcelThree.setRecipientZipCode(8600);
        parcelThree.setRecipientAddress("Forrás tér 2.");
        parcelThree.setCashOnDelivery(14000);
        parcelThree.setParcelSize("M");
        parcelThree.setStatus("DELIVERED");
        testRestTemplate.postForObject("/parcel", parcelThree, ParcelDto.class);

        ResponseEntity<List<ParcelDto>> responseEntity = testRestTemplate.exchange(
                "/customer/{id}/parcel",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                storedCustomerTwo.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .extracting(ParcelDto::getRecipientName)
                .containsExactly("Gáspár Tamás", "Somogyi Nóra");
    }

}
