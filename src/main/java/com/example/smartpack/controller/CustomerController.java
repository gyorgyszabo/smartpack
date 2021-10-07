package com.example.smartpack.controller;

import com.example.smartpack.model.dto.CustomerDto;
import com.example.smartpack.model.dto.ParcelDto;
import com.example.smartpack.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final String notFoundMessage = "Customer not found";
    private final String validationFailedMessage = "Validation failed for Customer. Error count: ";

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerDto> listAllCustomer() {
        return customerService.listAllCustomer();
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage));
    }

    @PostMapping
    public CustomerDto addCustomer(@Valid @RequestBody CustomerDto customerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    validationFailedMessage + bindingResult.getErrorCount());
        }
        return customerService.addCustomer(customerDto);
    }

    @PutMapping("/{id}")
    public CustomerDto updateCustomer(@PathVariable Long id,
                                      @Valid @RequestBody CustomerDto customerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    validationFailedMessage + bindingResult.getErrorCount());
        }
        return customerService.updateCustomer(id, customerDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage);
        }
    }

    @GetMapping("/{id}/parcel")
    public List<ParcelDto> listParcelByCustomerId(@PathVariable Long id) {
        return customerService.listParcelByCustomerId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage));
    }

}
