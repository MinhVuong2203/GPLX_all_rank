package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "kythi", indexes = {@Index(name = "MaHang",
        columnList = "MaHang")}, uniqueConstraints = {@UniqueConstraint(name = "public_id",
        columnNames = {"public_id"})})
public class Kythi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KyThiID", nullable = false)
    private Integer id;

    @Column(name = "public_id", length = 36)
    private String publicId;

    @Column(name = "TenKyThi", length = 150)
    private String tenKyThi;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "DiaDiem")
    private String diaDiem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHang")
    private Hanggiayphep maHang;

    @Column(name = "SoLuongToiDa")
    private Integer soLuongToiDa;

    @ColumnDefault("'Sắp diễn ra'")
    @Column(name = "TrangThai", length = 30)
    private String trangThai;


}