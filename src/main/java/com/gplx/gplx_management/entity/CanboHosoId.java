package com.gplx.gplx_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class CanboHosoId implements Serializable {
    private static final long serialVersionUID = -7576877015493642774L;
    @Column(name = "MaCanBo", nullable = false)
    private Integer maCanBo;

    @Column(name = "HoSoID", nullable = false)
    private Integer hoSoID;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "ThoiGian", nullable = false)
    private Instant thoiGian;


}