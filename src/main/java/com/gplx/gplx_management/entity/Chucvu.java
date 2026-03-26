package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chucvu", uniqueConstraints = {@UniqueConstraint(name = "TenChucVu",
        columnNames = {"TenChucVu"})})
public class Chucvu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChucVu", nullable = false)
    private Integer id;

    @Column(name = "TenChucVu", length = 50)
    private String tenChucVu;


}