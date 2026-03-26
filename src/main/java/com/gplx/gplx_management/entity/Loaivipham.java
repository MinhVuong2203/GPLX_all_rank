package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "loaivipham")
public class Loaivipham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoaiViPhamID", nullable = false)
    private Integer id;

    @Column(name = "TenViPham")
    private String tenViPham;

    @Column(name = "DiemTru")
    private Integer diemTru;

    @Column(name = "MucPhatTu", precision = 18, scale = 2)
    private BigDecimal mucPhatTu;

    @Column(name = "MucPhatDen", precision = 18, scale = 2)
    private BigDecimal mucPhatDen;

    @Column(name = "MoTa", length = 500)
    private String moTa;


}