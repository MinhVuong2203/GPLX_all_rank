package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "hoso", indexes = {
        @Index(name = "MaCongDan",
                columnList = "MaCongDan"),
        @Index(name = "MaHang",
                columnList = "MaHang")}, uniqueConstraints = {@UniqueConstraint(name = "public_id",
        columnNames = {"public_id"})})
public class Hoso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HoSoID", nullable = false)
    private Integer id;

    @Column(name = "public_id", length = 36)
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaCongDan", nullable = false)
    private Congdan maCongDan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaHang", nullable = false)
    private Hanggiayphep maHang;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "NgayNop")
    private Instant ngayNop;

    @ColumnDefault("'Đang xử lý'")
    @Column(name = "TrangThai", length = 30)
    private String trangThai;

    @ColumnDefault("0")
    @Column(name = "TrangThaiThanhToan")
    private Boolean trangThaiThanhToan;

    @Column(name = "GhiChu")
    private String ghiChu;


}