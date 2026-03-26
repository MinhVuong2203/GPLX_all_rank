package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "canbo", indexes = {@Index(name = "MaChucVu",
        columnList = "MaChucVu")}, uniqueConstraints = {
        @UniqueConstraint(name = "public_id",
                columnNames = {"public_id"}),
        @UniqueConstraint(name = "Username",
                columnNames = {"Username"})})
public class Canbo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCanBo", nullable = false)
    private Integer id;

    @Column(name = "public_id", length = 36)
    private String publicId;

    @Column(name = "HoTen", length = 100)
    private String hoTen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChucVu")
    private Chucvu maChucVu;

    @Column(name = "Email", length = 120)
    private String email;

    @Column(name = "DienThoai", length = 15)
    private String dienThoai;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "Username", length = 100)
    private String username;

    @Column(name = "Password", length = 100)
    private String password;

    @Column(name = "Anh3x4", length = 256)
    private String anh3x4;

    @ColumnDefault("1")
    @Column(name = "TrangThai")
    private Boolean trangThai;


}