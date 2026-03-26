package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "lichthi", indexes = {
        @Index(name = "KyThiID",
                columnList = "KyThiID"),
        @Index(name = "MonThiID",
                columnList = "MonThiID")})
public class Lichthi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LichThiID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KyThiID")
    private Kythi kyThiID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MonThiID")
    private Monthi monThiID;

    @Column(name = "ThoiGian")
    private Instant thoiGian;


}