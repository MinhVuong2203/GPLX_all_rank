package com.gplx.gplx_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class HangMonThiId implements Serializable {
    private static final long serialVersionUID = 5010558051921057476L;
    @Column(name = "ma_hang", nullable = false, length = 10)
    private String maHang;

    @Column(name = "mon_thiid", nullable = false)
    private Integer monThiid;


}