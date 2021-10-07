package com.example.smartpack.repository;

import com.example.smartpack.model.entity.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
}
