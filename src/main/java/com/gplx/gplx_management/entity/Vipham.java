package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "vipham", indexes = {
        @Index(name = "GiayPhepID",
                columnList = "GiayPhepID"),
        @Index(name = "LoaiViPhamID",
                columnList = "LoaiViPhamID")})
public class Vipham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ViPhamID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GiayPhepID")
    private Giayphep giayPhepID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LoaiViPhamID")
    private Loaivipham loaiViPhamID;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "ThoiGianViPham")
    private Instant thoiGianViPham;

    @Column(name = "DiaDiem")
    private String diaDiem;

    @Column(name = "BienKiemSoat", length = 20)
    private String bienKiemSoat;

    @Column(name = "MucPhat", precision = 18, scale = 2)
    private BigDecimal mucPhat;

    @ColumnDefault("'Chưa xử lý'")
    @Column(name = "TrangThai", length = 30)
    private String trangThai;

    @Column(name = "GhiChu", length = 500)
    private String ghiChu;


}