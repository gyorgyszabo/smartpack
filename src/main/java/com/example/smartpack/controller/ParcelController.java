package com.example.smartpack.controller;

import com.example.smartpack.model.dto.ParcelDto;
import com.example.smartpack.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parcel")
public class ParcelController {

    private final ParcelService parcelService;
    private final String notFoundMessage = "Parcel not found";
    private final String validationFailedMessage = "Validation failed for Parcel. Error count: ";

    @Autowired
    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping
    public List<ParcelDto> listAllParcel() {
        return parcelService.listAllParcel();
    }

    @GetMapping("/{id}")
    public ParcelDto getParcel(@PathVariable Long id) {
        return parcelService.getParcel(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage));
    }

    @PostMapping
    public ParcelDto addParcel(@Valid @RequestBody ParcelDto parcelDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    validationFailedMessage + bindingResult.getErrorCount());
        }
        return parcelService.addParcel(parcelDto);
    }

    @PutMapping("/{id}")
    public ParcelDto updateParcel(@PathVariable Long id,
                                  @Valid @RequestBody ParcelDto parcelDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    validationFailedMessage + bindingResult.getErrorCount());
        }
        return parcelService.updateParcel(id, parcelDto);
    }

    @DeleteMapping("/{id}")
    public void deleteParcel(@PathVariable Long id) {
        try {
            parcelService.deleteParcel(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage);
        }
    }

}
