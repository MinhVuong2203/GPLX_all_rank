package com.gplx.gplx_management.repository;

import com.gplx.gplx_management.entity.Congdan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CongdanRepository extends JpaRepository<Congdan, Integer> {
    Optional<Congdan> findByCccd(String cccd);

        boolean existsByCccd(String cccd);

        boolean existsByEmail(String email);

        boolean existsBySoDienThoai(String soDienThoai);

        boolean existsByCccdAndIdNot(String cccd, Integer id);

        boolean existsByEmailAndIdNot(String email, Integer id);

        boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Integer id);

    List<Congdan> findByNgayTaoBetweenOrderByNgayTaoDesc(Instant startInclusive, Instant endExclusive);

    Page<Congdan> findAllByOrderByNgayTaoDesc(Pageable pageable);

    @Query("""
            SELECT c FROM Congdan c
            WHERE (:cccd IS NULL OR c.cccd LIKE CONCAT('%', :cccd, '%'))
              AND (:hoTen IS NULL OR LOWER(c.hoTen) LIKE CONCAT('%', LOWER(:hoTen), '%'))
              AND (:startTime IS NULL OR c.ngayTao >= :startTime)
              AND (:endTime IS NULL OR c.ngayTao < :endTime)
            ORDER BY c.ngayTao DESC
            """)
    Page<Congdan> searchCitizens(@Param("cccd") String cccd,
            @Param("hoTen") String hoTen,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable);
}
