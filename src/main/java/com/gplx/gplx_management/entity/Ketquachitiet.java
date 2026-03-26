package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ketquachitiet", indexes = {@Index(name = "MonThiID",
        columnList = "MonThiID")}, uniqueConstraints = {@UniqueConstraint(name = "KetQuaID",
        columnNames = {
                "KetQuaID",
                "MonThiID"})})
public class Ketquachitiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChiTietID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "KetQuaID")
    private Ketquathi ketQuaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MonThiID")
    private Monthi monThiID;

    @Column(name = "Diem", precision = 5, scale = 2)
    private BigDecimal diem;

    @Column(name = "ThoiGianBatDau")
    private Instant thoiGianBatDau;

    @Column(name = "KetQua", length = 20)
    private String ketQua;

    @Column(name = "GhiChu")
    private String ghiChu;


}