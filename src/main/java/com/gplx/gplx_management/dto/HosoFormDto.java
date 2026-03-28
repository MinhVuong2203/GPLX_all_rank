package com.gplx.gplx_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class HosoFormDto {
    private String publicId;
    private Integer maCongDanId;
    private String maHangId;
    private Instant ngayNop;
    private String trangThai;
    private boolean trangThaiThanhToan;
    private String ghiChu;
}
