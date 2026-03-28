package com.gplx.gplx_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CongdanSearchDto {
    private String cccd;
    private String hoTen;
    private LocalDate createdFrom;
    private LocalDate createdTo;
}
