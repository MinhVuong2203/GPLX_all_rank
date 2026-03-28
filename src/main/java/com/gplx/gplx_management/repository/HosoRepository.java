package com.gplx.gplx_management.repository;

import com.gplx.gplx_management.entity.Hoso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HosoRepository extends JpaRepository<Hoso, Integer> {
    Page<Hoso> findAllByOrderByNgayNopDesc(Pageable pageable);

    Page<Hoso> findByTrangThaiOrderByNgayNopDesc(String trangThai, Pageable pageable);
}
