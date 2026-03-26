package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "hang_mon_thi", indexes = {@Index(name = "mon_thiid",
        columnList = "mon_thiid")})
public class HangMonThi {
    @EmbeddedId
    private HangMonThiId id;

    @MapsId("maHang")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_hang", nullable = false)
    private Hanggiayphep maHang;

    @MapsId("monThiid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mon_thiid", nullable = false)
    private Monthi monThiid;

    @Column(name = "diem_dat", nullable = false, precision = 5, scale = 2)
    private BigDecimal diemDat;


}