package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "congdan", uniqueConstraints = {
        @UniqueConstraint(name = "public_id",
                columnNames = {"public_id"}),
        @UniqueConstraint(name = "CCCD",
                columnNames = {"CCCD"}),
        @UniqueConstraint(name = "SoDienThoai",
                columnNames = {"SoDienThoai"}),
        @UniqueConstraint(name = "Email",
                columnNames = {"Email"})})
public class Congdan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCongDan", nullable = false)
    private Integer id;

    @Column(name = "public_id", length = 36)
    private String publicId;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "NgaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "CCCD", nullable = false, length = 20)
    private String cccd;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "Email", length = 100)
    private String email;

    @ColumnDefault("'Khỏe mạnh'")
    @Column(name = "TinhTrangSucKhoe", length = 50)
    private String tinhTrangSucKhoe;

    @Column(name = "NgayKhamSucKhoe")
    private LocalDate ngayKhamSucKhoe;

    @Column(name = "GiayKhamSucKhoe", length = 50)
    private String giayKhamSucKhoe;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "Anh3x4")
    private String anh3x4;


}