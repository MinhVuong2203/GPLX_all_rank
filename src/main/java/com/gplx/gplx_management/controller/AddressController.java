package com.gplx.gplx_management.controller;

import com.gplx.gplx_management.dto.AddressOptionDto;
import com.gplx.gplx_management.service.AddressDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressDataService addressDataService;

    public AddressController(AddressDataService addressDataService) {
        this.addressDataService = addressDataService;
    }

    @GetMapping("/provinces")
    public List<AddressOptionDto> provinces() {
        return addressDataService.getProvinces();
    }

    @GetMapping("/wards")
    public List<AddressOptionDto> wards(@RequestParam("provinceCode") String provinceCode) {
        return addressDataService.getWardsByProvinceCode(provinceCode);
    }
}
