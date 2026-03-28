package com.gplx.gplx_management.service;

import com.gplx.gplx_management.entity.Hanggiayphep;
import com.gplx.gplx_management.entity.Hoso;
import com.gplx.gplx_management.repository.HanggiayphepRepository;
import com.gplx.gplx_management.repository.HosoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HosoService {

    private final HosoRepository hosoRepository;
    private final HanggiayphepRepository hanggiayphepRepository;

    public HosoService(HosoRepository hosoRepository, HanggiayphepRepository hanggiayphepRepository) {
        this.hosoRepository = hosoRepository;
        this.hanggiayphepRepository = hanggiayphepRepository;
    }

    public Page<Hoso> getApplicationsPage(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.max(size, 1);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "ngayNop"));
        return hosoRepository.findAllByOrderByNgayNopDesc(pageable);
    }

    public Page<Hoso> getApprovalPage(String trangThai, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.max(size, 1);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);

        if (trangThai == null || trangThai.isBlank()) {
            return hosoRepository.findAllByOrderByNgayNopDesc(pageable);
        }
        return hosoRepository.findByTrangThaiOrderByNgayNopDesc(trangThai, pageable);
    }

    public List<String> getApprovalStatuses() {
        return Arrays.asList("Đang xử lý", "Đủ điều kiện", "Không đủ điều kiện");
    }

    public Optional<Hoso> findById(Integer id) {
        return hosoRepository.findById(id);
    }

    public List<Hanggiayphep> getAllLicenseRanks() {
        return hanggiayphepRepository.findAll(Sort.by(Sort.Direction.ASC, "maHang"));
    }

    @Transactional
    public Hoso create(Hoso hoso) {
        if (hoso.getPublicId() == null || hoso.getPublicId().isBlank()) {
            hoso.setPublicId(UUID.randomUUID().toString());
        }
        if (hoso.getNgayNop() == null) {
            hoso.setNgayNop(Instant.now());
        }
        return hosoRepository.save(hoso);
    }

    @Transactional
    public Hoso update(Integer id, Hoso payload) {
        Hoso existing = hosoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

        existing.setPublicId(payload.getPublicId());
        existing.setMaCongDan(payload.getMaCongDan());
        existing.setMaHang(payload.getMaHang());
        existing.setNgayNop(payload.getNgayNop() == null ? existing.getNgayNop() : payload.getNgayNop());
        existing.setTrangThai(payload.getTrangThai());
        existing.setTrangThaiThanhToan(payload.getTrangThaiThanhToan());
        existing.setGhiChu(payload.getGhiChu());

        return hosoRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Hoso existing = hosoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));
        hosoRepository.delete(existing);
    }

    @Transactional
    public Hoso approveByRank(Integer id, String maHang) {
        Hoso existing = hosoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

        Hanggiayphep hang = hanggiayphepRepository.findById(maHang)
                .orElseThrow(() -> new IllegalArgumentException("License rank not found: " + maHang));

        existing.setMaHang(hang);
        existing.setTrangThai("Đủ điều kiện");
        return hosoRepository.save(existing);
    }

    @Transactional
    public Hoso updateStatus(Integer id, String trangThai) {
        Hoso existing = hosoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));
        existing.setTrangThai(trangThai);
        return hosoRepository.save(existing);
    }
}
