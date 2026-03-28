package com.gplx.gplx_management.service;

import com.gplx.gplx_management.dto.CongdanSearchDto;
import com.gplx.gplx_management.entity.Congdan;
import com.gplx.gplx_management.repository.CongdanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CongdanService {

    private static final String CITIZEN_IMAGE_DIR = "src/main/resources/static/images/congdan";
    private static final String HEALTH_DOC_DIR = "src/main/resources/static/files/health-docs";
    private static final String HEALTH_DOC_RELATIVE_DIR = "files/health-docs/";
    private static final int HEALTH_DOC_PATH_MAX_LENGTH = 255;
    private static final int HEALTH_DOC_FILE_NAME_LENGTH = 24;

    private final CongdanRepository congdanRepository;

    public CongdanService(CongdanRepository congdanRepository) {
        this.congdanRepository = congdanRepository;
    }

    public Page<Congdan> getCitizensPage(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.max(size, 1);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return congdanRepository.findAllByOrderByNgayTaoDesc(pageable);
    }

    public Page<Congdan> getCitizenStatistics(CongdanSearchDto searchDto, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.max(size, 1);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);
        ZoneId zoneId = ZoneId.systemDefault();

        Instant createdFrom = searchDto.getCreatedFrom() == null
                ? null
                : searchDto.getCreatedFrom().atStartOfDay(zoneId).toInstant();
        Instant createdTo = searchDto.getCreatedTo() == null
                ? null
                : searchDto.getCreatedTo().plusDays(1).atStartOfDay(zoneId).toInstant();

        return congdanRepository.searchCitizens(
                trimToNull(searchDto.getCccd()),
                trimToNull(searchDto.getHoTen()),
                createdFrom,
                createdTo,
                pageable);
    }

    public List<Congdan> getTodayCitizens() {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant startOfToday = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant();
        Instant startOfTomorrow = LocalDate.now(zoneId).plusDays(1).atStartOfDay(zoneId).toInstant();
        return congdanRepository.findByNgayTaoBetweenOrderByNgayTaoDesc(startOfToday, startOfTomorrow);
    }

    public Optional<Congdan> findById(Integer id) {
        return congdanRepository.findById(id);
    }

    public Optional<Congdan> findByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            return Optional.empty();
        }
        return congdanRepository.findByCccd(cccd.trim());
    }

    @Transactional
    public Congdan create(Congdan citizen, MultipartFile imageFile, MultipartFile healthPaperFile) {
        normalizeCitizenFields(citizen);
        validateUniqueFieldsForCreate(citizen);
        if (citizen.getPublicId() == null || citizen.getPublicId().isBlank()) {
            citizen.setPublicId(UUID.randomUUID().toString());
        }
        if (citizen.getNgayTao() == null) {
            citizen.setNgayTao(Instant.now());
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            citizen.setAnh3x4(storeImage(imageFile));
        }
        if (healthPaperFile != null && !healthPaperFile.isEmpty()) {
            citizen.setGiayKhamSucKhoe(storeHealthPaper(healthPaperFile));
        }
        return congdanRepository.save(citizen);
    }

    @Transactional
    public Congdan update(Integer id, Congdan payload, MultipartFile imageFile, MultipartFile healthPaperFile) {
        Congdan existing = congdanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found: " + id));

        normalizeCitizenFields(payload);
        validateUniqueFieldsForUpdate(id, payload);

        existing.setPublicId(payload.getPublicId());
        existing.setHoTen(payload.getHoTen());
        existing.setNgaySinh(payload.getNgaySinh());
        existing.setGioiTinh(payload.getGioiTinh());
        existing.setCccd(payload.getCccd());
        existing.setDiaChi(payload.getDiaChi());
        existing.setSoDienThoai(payload.getSoDienThoai());
        existing.setEmail(payload.getEmail());
        existing.setTinhTrangSucKhoe(payload.getTinhTrangSucKhoe());
        existing.setNgayKhamSucKhoe(payload.getNgayKhamSucKhoe());
        existing.setGiayKhamSucKhoe(payload.getGiayKhamSucKhoe());
        existing.setNgayTao(payload.getNgayTao() == null ? existing.getNgayTao() : payload.getNgayTao());

        if (imageFile != null && !imageFile.isEmpty()) {
            String oldImage = existing.getAnh3x4();
            existing.setAnh3x4(storeImage(imageFile));
            deleteImageIfExists(oldImage);
        }

        if (healthPaperFile != null && !healthPaperFile.isEmpty()) {
            String oldHealthPaper = existing.getGiayKhamSucKhoe();
            existing.setGiayKhamSucKhoe(storeHealthPaper(healthPaperFile));
            deleteImageIfExists(oldHealthPaper);
        }

        return congdanRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Congdan existing = congdanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found: " + id));

        deleteImageIfExists(existing.getAnh3x4());
        congdanRepository.delete(existing);
    }

    private String storeImage(MultipartFile imageFile) {
        try {
            Path folderPath = Paths.get(CITIZEN_IMAGE_DIR);
            Files.createDirectories(folderPath);

            String originalName = imageFile.getOriginalFilename();
            String safeOriginalName = originalName == null ? "photo" : originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storedName = UUID.randomUUID() + "_" + safeOriginalName;
            Path destination = folderPath.resolve(storedName);

            Files.copy(imageFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "images/congdan/" + storedName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store citizen image", ex);
        }
    }

    private String storeHealthPaper(MultipartFile file) {
        try {
            Path folderPath = Paths.get(HEALTH_DOC_DIR);
            Files.createDirectories(folderPath);

            String originalName = file.getOriginalFilename();
            String extension = ".dat";
            if (originalName != null) {
                int dotIndex = originalName.lastIndexOf('.');
                if (dotIndex >= 0 && dotIndex < originalName.length() - 1) {
                    String extPart = originalName.substring(dotIndex + 1).replaceAll("[^a-zA-Z0-9]", "");
                    if (!extPart.isBlank()) {
                        String safeExt = extPart.length() > 8 ? extPart.substring(0, 8) : extPart;
                        extension = "." + safeExt;
                    }
                }
            }

            String compactToken = UUID.randomUUID().toString().replace("-", "");
            if (compactToken.length() > HEALTH_DOC_FILE_NAME_LENGTH) {
                compactToken = compactToken.substring(0, HEALTH_DOC_FILE_NAME_LENGTH);
            }
            String storedName = compactToken + extension;
            Path destination = folderPath.resolve(storedName);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            String relativePath = HEALTH_DOC_RELATIVE_DIR + storedName;
            if (relativePath.length() > HEALTH_DOC_PATH_MAX_LENGTH) {
                throw new IllegalArgumentException("Duong dan giay kham suc khoe qua dai");
            }
            return relativePath;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store health document", ex);
        }
    }

    private void deleteImageIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Path imagePath = Paths.get("src/main/resources/static").resolve(relativePath).normalize();
            Files.deleteIfExists(imagePath);
        } catch (IOException ignored) {
            // Intentionally ignored to avoid blocking business operation on cleanup
            // failure.
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void normalizeCitizenFields(Congdan citizen) {
        citizen.setPublicId(trimToNull(citizen.getPublicId()));
        citizen.setHoTen(trimToNull(citizen.getHoTen()));
        citizen.setGioiTinh(trimToNull(citizen.getGioiTinh()));
        citizen.setCccd(trimToNull(citizen.getCccd()));
        citizen.setDiaChi(trimToNull(citizen.getDiaChi()));
        citizen.setSoDienThoai(trimToNull(citizen.getSoDienThoai()));
        citizen.setEmail(trimToNull(citizen.getEmail()));
        citizen.setTinhTrangSucKhoe(trimToNull(citizen.getTinhTrangSucKhoe()));
        String giayKham = trimToNull(citizen.getGiayKhamSucKhoe());
        if (giayKham != null && giayKham.length() > HEALTH_DOC_PATH_MAX_LENGTH) {
            throw new IllegalArgumentException("Giay kham suc khoe vuot qua gioi han 255 ky tu");
        }
        citizen.setGiayKhamSucKhoe(giayKham);
    }

    private void validateUniqueFieldsForCreate(Congdan citizen) {
        if (citizen.getCccd() != null && congdanRepository.existsByCccd(citizen.getCccd())) {
            throw new IllegalArgumentException("CCCD đã tồn tại");
        }
        if (citizen.getEmail() != null && congdanRepository.existsByEmail(citizen.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (citizen.getSoDienThoai() != null && congdanRepository.existsBySoDienThoai(citizen.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
    }

    private void validateUniqueFieldsForUpdate(Integer id, Congdan citizen) {
        if (citizen.getCccd() != null && congdanRepository.existsByCccdAndIdNot(citizen.getCccd(), id)) {
            throw new IllegalArgumentException("CCCD đã tồn tại");
        }
        if (citizen.getEmail() != null && congdanRepository.existsByEmailAndIdNot(citizen.getEmail(), id)) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (citizen.getSoDienThoai() != null
                && congdanRepository.existsBySoDienThoaiAndIdNot(citizen.getSoDienThoai(), id)) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
    }
}
