package com.gplx.gplx_management.service;

import com.gplx.gplx_management.dto.AddressOptionDto;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddressDataService {

    private static final Path ADDRESS_JSON_PATH = Path
            .of("src/main/java/com/gplx/gplx_management/Database/SauXacNhap.json");

    private final ObjectMapper objectMapper;

    private volatile JsonNode rootCache;

    public AddressDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<AddressOptionDto> getProvinces() {
        JsonNode root = loadRoot();
        List<AddressOptionDto> provinces = new ArrayList<>();

        for (JsonNode province : root) {
            String code = textOrEmpty(province.get("matinhTMS"));
            String name = textOrEmpty(province.get("tentinhmoi"));
            if (!code.isBlank() && !name.isBlank()) {
                provinces.add(new AddressOptionDto(code, name));
            }
        }
        return provinces;
    }

    public List<AddressOptionDto> getWardsByProvinceCode(String provinceCode) {
        JsonNode province = findProvinceByCode(provinceCode);
        List<AddressOptionDto> wards = new ArrayList<>();
        if (province == null) {
            return wards;
        }

        JsonNode wardArray = province.get("phuongxa");
        if (wardArray == null || !wardArray.isArray()) {
            return wards;
        }

        for (JsonNode ward : wardArray) {
            String code = textOrEmpty(ward.get("maphuongxa"));
            String name = textOrEmpty(ward.get("tenphuongxa"));
            if (!code.isBlank() && !name.isBlank()) {
                wards.add(new AddressOptionDto(code, name));
            }
        }
        return wards;
    }

    private JsonNode findProvinceByCode(String provinceCode) {
        if (provinceCode == null || provinceCode.isBlank()) {
            return null;
        }
        JsonNode root = loadRoot();
        for (JsonNode province : root) {
            String code = textOrEmpty(province.get("matinhTMS"));
            if (provinceCode.trim().equals(code)) {
                return province;
            }
        }
        return null;
    }

    private JsonNode loadRoot() {
        JsonNode localRef = rootCache;
        if (localRef != null) {
            return localRef;
        }

        synchronized (this) {
            if (rootCache != null) {
                return rootCache;
            }
            try {
                if (!Files.exists(ADDRESS_JSON_PATH)) {
                    throw new IllegalStateException("Address data file not found: " + ADDRESS_JSON_PATH);
                }
                rootCache = objectMapper.readTree(Files.newBufferedReader(ADDRESS_JSON_PATH));
                return rootCache;
            } catch (IOException ex) {
                throw new IllegalStateException("Could not load address data", ex);
            }
        }
    }

    private String textOrEmpty(JsonNode node) {
        return node == null ? "" : node.asText("").trim();
    }
}
