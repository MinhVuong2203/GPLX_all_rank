package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "giayphep", indexes = {
        @Index(name = "MaCongDan",
                columnList = "MaCongDan"),
        @Index(name = "MaHang",
                columnList = "MaHang")}, uniqueConstraints = {@UniqueConstraint(name = "SoGiayPhep",
        columnNames = {"SoGiayPhep"})})
public class Giayphep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GiayPhepID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCongDan")
    private Congdan maCongDan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHang")
    private Hanggiayphep maHang;

    @Column(name = "SoGiayPhep", length = 20)
    private String soGiayPhep;

    @Column(name = "NgayCap")
    private LocalDate ngayCap;

    @Column(name = "NgayHetHan")
    private LocalDate ngayHetHan;

    @ColumnDefault("12")
    @Column(name = "SoDiem")
    private Integer soDiem;

    @ColumnDefault("'Còn hiệu lực'")
    @Column(name = "TrangThai", length = 30)
    private String trangThai;

    @Column(name = "GhiChu")
    private String ghiChu;


}