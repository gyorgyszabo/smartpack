package com.example.smartpack.controller;

import com.example.smartpack.model.dto.CustomerDto;
import com.example.smartpack.model.dto.ParcelDto;
import com.example.smartpack.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Captor
    ArgumentCaptor<CustomerDto> argumentCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void listAllCustomer_ListProvided_ShouldReturnSameList() throws Exception {
        List<CustomerDto> customerList = List.of(customerOne, customerTwo);

        when(customerService.listAllCustomer()).thenReturn(customerList);

        mockMvc.perform(get("/customer").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nagy Tibor"))
                .andExpect(jsonPath("$[1].name").value("Horváth Katalin"));
    }

    @Test
    void getCustomer_EmptyOptionalProvided_ShouldReturnNotFoundStatus() throws Exception {
        when(customerService.getCustomer(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomer_OptionalOfCustomerProvided_ShouldReturnSameCustomer() throws Exception {
        when(customerService.getCustomer(1L)).thenReturn(Optional.of(customerOne));

        mockMvc.perform(get("/customer/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nagy Tibor"));
    }

    @Test
    void addCustomer_InvalidCustomerAdded_ShouldReturnBadRequestStatus() throws Exception {
        customerOne.setZipCode(10230);

        mockMvc.perform(post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCustomer_CustomerAdded_ShouldPassSameCustomer() throws Exception {
        mockMvc.perform(post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOne))
                .accept(MediaType.APPLICATION_JSON));

        verify(customerService).addCustomer(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getName()).isEqualTo("Nagy Tibor");
    }

    @Test
    void addCustomer_CustomerProvided_ShouldReturnSameCustomer() throws Exception {
        when(customerService.addCustomer(customerOne)).thenReturn(customerTwo);

        mockMvc.perform(post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Horváth Katalin"));
    }

    @Test
    void updateCustomer_UpdatedWithInvalidCustomer_ShouldReturnBadRequestStatus() throws Exception {
        customerOne.setPhoneNumber("+36305584221000000");

        mockMvc.perform(put("/customer/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer_UpdatedWithCustomer_ShouldPassSameCustomer() throws Exception {
        mockMvc.perform(put("/customer/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOne))
                .accept(MediaType.APPLICATION_JSON));

        verify(customerService).updateCustomer(anyLong(), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getName()).isEqualTo("Nagy Tibor");
    }

    @Test
    void updateCustomer_CustomerProvided_ShouldReturnSameCustomer() throws Exception {
        when(customerService.updateCustomer(1L, customerOne)).thenReturn(customerTwo);

        mockMvc.perform(put("/customer/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Horváth Katalin"));
    }

    @Test
    void deleteCustomer_EmptyResultDataAccessExceptionProvided_ShouldReturnNotFoundStatus() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/customer/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void listParcelByCustomerId_EmptyOptionalProvided_ShouldReturnNotFoundStatus() throws Exception {
        when(customerService.listParcelByCustomerId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/{id}/parcel", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void listParcelByCustomerId_OptionalListOfParcelsProvided_ShouldReturnSameList() throws Exception {
        ParcelDto parcelOne = new ParcelDto();
        parcelOne.setCustomerId(1L);
        parcelOne.setRecipientName("Tóth István");
        parcelOne.setRecipientPhoneNumber("+36305478332");
        parcelOne.setRecipientEmail("istvan.toth@fakemail.com");
        parcelOne.setRecipientCity("Budapest");
        parcelOne.setRecipientZipCode(1014);
        parcelOne.setRecipientAddress("Tószegi út 112.");
        parcelOne.setCashOnDelivery(0);
        parcelOne.setParcelSize("S");
        parcelOne.setStatus("DELIVERED");

        ParcelDto parcelTwo = new ParcelDto();
        parcelTwo.setCustomerId(2L);
        parcelTwo.setRecipientName("Gáspár Tamás");
        parcelTwo.setRecipientPhoneNumber("+36309663254");
        parcelTwo.setRecipientEmail("tamas.gaspar@fakemail.com");
        parcelTwo.setRecipientCity("Szolnok");
        parcelTwo.setRecipientZipCode(5000);
        parcelTwo.setRecipientAddress("Kocsis köz 3.");
        parcelTwo.setCashOnDelivery(0);
        parcelTwo.setParcelSize("L");
        parcelTwo.setStatus("UNDELIVERED");

        ParcelDto parcelThree = new ParcelDto();
        parcelThree.setCustomerId(2L);
        parcelThree.setRecipientName("Somogyi Nóra");
        parcelThree.setRecipientPhoneNumber("+36707875521");
        parcelThree.setRecipientEmail("nora.somogyi@fakemail.com");
        parcelThree.setRecipientCity("Siófok");
        parcelThree.setRecipientZipCode(8600);
        parcelThree.setRecipientAddress("Forrás tér 2.");
        parcelThree.setCashOnDelivery(14000);
        parcelThree.setParcelSize("M");
        parcelThree.setStatus("DELIVERED");

        List<ParcelDto> parcelList = List.of(parcelOne, parcelTwo, parcelThree);

        when(customerService.listParcelByCustomerId(1L)).thenReturn(Optional.of(parcelList));

        mockMvc.perform(get("/customer/{id}/parcel", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipientName").value("Tóth István"))
                .andExpect(jsonPath("$[1].recipientName").value("Gáspár Tamás"))
                .andExpect(jsonPath("$[2].recipientName").value("Somogyi Nóra"));
    }

}
