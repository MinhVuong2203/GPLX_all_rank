package com.gplx.gplx_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "canbo_hoso", indexes = {@Index(name = "HoSoID",
        columnList = "HoSoID")})
public class CanboHoso {
    @EmbeddedId
    private CanboHosoId id;

    @MapsId("maCanBo")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaCanBo", nullable = false)
    private Canbo maCanBo;

    @MapsId("hoSoID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HoSoID", nullable = false)
    private Hoso hoSoID;

    @Column(name = "TrangThaiDuyet", length = 50)
    private String trangThaiDuyet;


}