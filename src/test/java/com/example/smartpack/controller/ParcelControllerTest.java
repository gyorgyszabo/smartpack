package com.example.smartpack.controller;

import com.example.smartpack.model.dto.ParcelDto;
import com.example.smartpack.service.ParcelService;
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

@WebMvcTest(ParcelController.class)
class ParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParcelService parcelService;

    @Captor
    ArgumentCaptor<ParcelDto> argumentCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ParcelDto parcelOne;
    private ParcelDto parcelTwo;

    @BeforeEach
    void init() {
        parcelOne = new ParcelDto();
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

        parcelTwo = new ParcelDto();
        parcelTwo.setCustomerId(1L);
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
    void listAllParcel_ListProvided_ShouldReturnSameList() throws Exception {
        List<ParcelDto> parcelList = List.of(parcelOne, parcelTwo);

        when(parcelService.listAllParcel()).thenReturn(parcelList);

        mockMvc.perform(get("/parcel").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipientName").value("Tóth István"))
                .andExpect(jsonPath("$[1].recipientName").value("Gáspár Tamás"));
    }

    @Test
    void getParcel_EmptyOptionalProvided_ShouldReturnNotFoundStatus() throws Exception {
        when(parcelService.getParcel(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/parcel/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getParcel_OptionalOfParcelProvided_ShouldReturnSameParcel() throws Exception {
        when(parcelService.getParcel(1L)).thenReturn(Optional.of(parcelOne));

        mockMvc.perform(get("/parcel/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientName").value("Tóth István"));
    }

    @Test
    void addParcel_InvalidParcelAdded_ShouldReturnBadRequestStatus() throws Exception {
        parcelOne.setParcelSize("Z");

        mockMvc.perform(post("/parcel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parcelOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addParcel_ParcelAdded_ShouldPassSameParcel() throws Exception {
        mockMvc.perform(post("/parcel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parcelOne))
                .accept(MediaType.APPLICATION_JSON));

        verify(parcelService).addParcel(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getRecipientName()).isEqualTo("Tóth István");
    }

    @Test
    void addParcel_ParcelProvided_ShouldReturnSameParcel() throws Exception {
        when(parcelService.addParcel(parcelOne)).thenReturn(parcelTwo);

        mockMvc.perform(post("/parcel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parcelOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientName").value("Gáspár Tamás"));
    }

    @Test
    void updateParcel_UpdatedWithInvalidParcel_ShouldReturnBadRequestStatus() throws Exception {
        parcelOne.setStatus("INVALID_STATUS");

        mockMvc.perform(put("/parcel/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parcelOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateParcel_UpdatedWithParcel_ShouldPassSameParcel() throws Exception {
        mockMvc.perform(put("/parcel/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parcelOne))
                .accept(MediaType.APPLICATION_JSON));

        verify(parcelService).updateParcel(anyLong(), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getRecipientName()).isEqualTo("Tóth István");
    }

    @Test
    void updateParcel_ParcelProvided_ShouldReturnSameParcel() throws Exception {
        when(parcelService.updateParcel(1L, parcelOne)).thenReturn(parcelTwo);

        mockMvc.perform(put("/parcel/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parcelOne))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientName").value("Gáspár Tamás"));
    }

    @Test
    void deleteParcel_EmptyResultDataAccessExceptionProvided_ShouldReturnNotFoundStatus() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(parcelService).deleteParcel(1L);

        mockMvc.perform(delete("/parcel/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
