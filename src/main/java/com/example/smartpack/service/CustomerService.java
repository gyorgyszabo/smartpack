package com.example.smartpack.service;

import com.example.smartpack.model.dto.CustomerDto;
import com.example.smartpack.model.dto.ParcelDto;
import com.example.smartpack.model.entity.Customer;
import com.example.smartpack.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> listAllCustomer() {
        return customerRepository.findAll().stream()
                .map(CustomerDto::new)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDto> getCustomer(Long id) {
        return customerRepository.findById(id).map(CustomerDto::new);
    }

    public CustomerDto addCustomer(CustomerDto customerDto) {
        customerDto.setId(null);
        Customer returnedCustomer = customerRepository.save(customerDto.toEntity());
        return new CustomerDto(returnedCustomer);
    }

    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        customerDto.setId(id);
        Customer returnedCustomer = customerRepository.save(customerDto.toEntity());
        return new CustomerDto(returnedCustomer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<List<ParcelDto>> listParcelByCustomerId(Long id) {
        return customerRepository.findById(id)
                .map(customer -> customer.getParcels().stream().map(ParcelDto::new).collect(Collectors.toList()));
    }

}
