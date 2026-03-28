package com.gplx.gplx_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class HosoApprovalDto {
    private Integer hoSoId;
    private String publicId;
    private Instant ngayNop;
    private String trangThai;
    private Boolean trangThaiThanhToan;
    private String ghiChu;

    private String hang;
    private String tenHang;

    private Integer maCongDan;
    private String hoTen;
    private String cccd;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String email;
    private String diaChi;
    private String tinhTrangSucKhoe;
    private LocalDate ngayKhamSucKhoe;
    private String giayKhamSucKhoe;
    private String anh3x4;
}
