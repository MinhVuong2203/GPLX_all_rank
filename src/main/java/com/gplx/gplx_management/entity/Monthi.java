package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "monthi")
public class Monthi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MonThiID", nullable = false)
    private Integer id;

    @Column(name = "TenMon", nullable = false, length = 100)
    private String tenMon;

    @Column(name = "MoTa")
    private String moTa;


}