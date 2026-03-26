package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ketquathi", indexes = {@Index(name = "KyThiID",
        columnList = "KyThiID")}, uniqueConstraints = {@UniqueConstraint(name = "HoSoID",
        columnNames = {
                "HoSoID",
                "KyThiID",
                "LanThi"})})
public class Ketquathi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KetQuaID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HoSoID")
    private Hoso hoSoID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KyThiID")
    private Kythi kyThiID;

    @Column(name = "KetQuaTongHop", length = 20)
    private String ketQuaTongHop;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "NgayKetLuan")
    private Instant ngayKetLuan;

    @ColumnDefault("1")
    @Column(name = "LanThi")
    private Integer lanThi;

    @Column(name = "GhiChu")
    private String ghiChu;


}