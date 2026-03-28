package com.gplx.gplx_management.repository;

import com.gplx.gplx_management.entity.HangMonThi;
import com.gplx.gplx_management.entity.HangMonThiId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HangMonThiRepository extends JpaRepository<HangMonThi, HangMonThiId> {
}
