package com.example.smartpack.service;

import com.example.smartpack.model.dto.ParcelDto;
import com.example.smartpack.model.entity.Parcel;
import com.example.smartpack.repository.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    @Autowired
    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    public List<ParcelDto> listAllParcel() {
        return parcelRepository.findAll().stream()
                .map(ParcelDto::new)
                .collect(Collectors.toList());
    }

    public Optional<ParcelDto> getParcel(Long id) {
        return parcelRepository.findById(id).map(ParcelDto::new);
    }

    public ParcelDto addParcel(ParcelDto parcelDto) {
        parcelDto.setStatus("NEW");
        parcelDto.setId(null);
        Parcel returnedParcel = parcelRepository.save(parcelDto.toEntity());
        return new ParcelDto(returnedParcel);
    }

    public ParcelDto updateParcel(Long id, ParcelDto parcelDto) {
        parcelDto.setId(id);
        Parcel returnedParcel = parcelRepository.save(parcelDto.toEntity());
        return new ParcelDto(returnedParcel);
    }

    public void deleteParcel(Long id) {
        parcelRepository.deleteById(id);
    }

}
