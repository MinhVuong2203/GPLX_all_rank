package com.gplx.gplx_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class CongdanFormDto {
    private String publicId;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String cccd;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String tinhTrangSucKhoe;
    private LocalDate ngayKhamSucKhoe;
    private String giayKhamSucKhoe;
    private Instant ngayTao;
    private String anh3x4;
}
