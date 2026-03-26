package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "hanggiayphep")
public class Hanggiayphep {
    @Id
    @Column(name = "MaHang", nullable = false, length = 10)
    private String maHang;

    @Column(name = "TenHang", nullable = false, length = 50)
    private String tenHang;

    @Column(name = "LoaiXe", length = 50)
    private String loaiXe;

    @ColumnDefault("18")
    @Column(name = "DoTuoiToiThieu")
    private Integer doTuoiToiThieu;

    @Column(name = "ThoiHanNam")
    private Integer thoiHanNam;

    @ColumnDefault("1")
    @Column(name = "YeuCauThucHanh")
    private Boolean yeuCauThucHanh;

    @Lob
    @Column(name = "MoTaChiTiet")
    private String moTaChiTiet;


}