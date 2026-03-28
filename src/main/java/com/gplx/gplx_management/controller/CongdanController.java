package com.gplx.gplx_management.controller;

import com.gplx.gplx_management.dto.CongdanFormDto;
import com.gplx.gplx_management.dto.CongdanSearchDto;
import com.gplx.gplx_management.entity.Congdan;
import com.gplx.gplx_management.service.AddressDataService;
import com.gplx.gplx_management.service.CongdanService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/profiles/citizens")
public class CongdanController {

    private static final Pattern CCCD_PATTERN = Pattern.compile("^(\\d{9}|\\d{12})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final CongdanService congdanService;
    private final AddressDataService addressDataService;

    public CongdanController(CongdanService congdanService, AddressDataService addressDataService) {
        this.congdanService = congdanService;
        this.addressDataService = addressDataService;
    }

    @GetMapping
    public String citizenWorkspace(@RequestParam(name = "cccd", required = false) String cccd, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Congdan> citizen = congdanService.findByCccd(cccd);
        CongdanFormDto citizenForm = citizen.map(this::toDto).orElseGet(CongdanFormDto::new);
        Integer citizenId = citizen.map(Congdan::getId).orElse(null);
        AddressParts addressParts = splitAddress(citizenForm.getDiaChi());
        citizenForm.setDiaChi(addressParts.detail());

        if (cccd != null && !cccd.isBlank() && citizen.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy công dân với CCCD: " + cccd);
            return "redirect:/profiles/citizens";
        }

        model.addAttribute("citizen", citizenForm);
        model.addAttribute("citizenId", citizenId);
        model.addAttribute("searchValue", cccd);
        model.addAttribute("isEditing", citizenId != null);
        model.addAttribute("todayCount", congdanService.getTodayCitizens().size());
        model.addAttribute("provinces", addressDataService.getProvinces());
        model.addAttribute("savedWardName", addressParts.ward());
        model.addAttribute("savedProvinceName", addressParts.province());
        return "profiles/citizens/form";
    }

    @GetMapping("/statistics")
    public String citizenStatistics(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "cccd", required = false) String cccd,
            @RequestParam(name = "hoTen", required = false) String hoTen,
            @RequestParam(name = "createdFrom", required = false) String createdFrom,
            @RequestParam(name = "createdTo", required = false) String createdTo,
            Model model) {
        CongdanSearchDto criteria = new CongdanSearchDto();
        criteria.setCccd(cccd);
        criteria.setHoTen(hoTen);
        criteria.setCreatedFrom(parseLocalDate(createdFrom));
        criteria.setCreatedTo(parseLocalDate(createdTo));

        Page<Congdan> citizenPage = congdanService.getCitizenStatistics(criteria, page, size);
        model.addAttribute("citizenPage", citizenPage);
        model.addAttribute("criteria", criteria);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("todayCitizens", congdanService.getTodayCitizens());
        return "profiles/citizens/list";
    }

    @PostMapping("/create")
    public String createCitizen(@ModelAttribute("citizen") CongdanFormDto citizenDto,
            @RequestParam(name = "ngayTaoInput", required = false) String ngayTaoInput,
            @RequestParam(name = "diaChiTinh", required = false) String diaChiTinh,
            @RequestParam(name = "diaChiPhuongXa", required = false) String diaChiPhuongXa,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(name = "healthPaperFile", required = false) MultipartFile healthPaperFile,
            RedirectAttributes redirectAttributes) {
        citizenDto.setNgayTao(parseDateTimeInput(ngayTaoInput));
        String validationError = validateCitizenForm(citizenDto);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thêm công dân thất bại: " + validationError);
            return "redirect:/profiles/citizens";
        }
        citizenDto.setDiaChi(composeAddress(citizenDto.getDiaChi(), diaChiPhuongXa, diaChiTinh));
        try {
            congdanService.create(toEntity(citizenDto), imageFile, healthPaperFile);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm công dân thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thêm công dân thất bại: " + ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Thêm công dân thất bại: " + resolveFriendlyErrorMessage(ex));
        }
        return "redirect:/profiles/citizens";
    }

    @PostMapping("/{id}/update")
    public String updateCitizen(@PathVariable Integer id,
            @ModelAttribute("citizen") CongdanFormDto citizenDto,
            @RequestParam(name = "ngayTaoInput", required = false) String ngayTaoInput,
            @RequestParam(name = "diaChiTinh", required = false) String diaChiTinh,
            @RequestParam(name = "diaChiPhuongXa", required = false) String diaChiPhuongXa,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(name = "healthPaperFile", required = false) MultipartFile healthPaperFile,
            RedirectAttributes redirectAttributes) {
        citizenDto.setNgayTao(parseDateTimeInput(ngayTaoInput));
        String validationError = validateCitizenForm(citizenDto);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật công dân thất bại: " + validationError);
            return "redirect:/profiles/citizens?cccd=" + citizenDto.getCccd();
        }
        citizenDto.setDiaChi(composeAddress(citizenDto.getDiaChi(), diaChiPhuongXa, diaChiTinh));
        try {
            congdanService.update(id, toEntity(citizenDto), imageFile, healthPaperFile);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật công dân thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật công dân thất bại: " + ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cập nhật công dân thất bại: " + resolveFriendlyErrorMessage(ex));
        }
        return "redirect:/profiles/citizens?cccd=" + citizenDto.getCccd();
    }

    @PostMapping("/{id}/delete")
    public String deleteCitizen(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        congdanService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa công dân thành công.");
        return "redirect:/profiles/citizens";
    }

    @GetMapping("/search")
    public String searchByCccd(@RequestParam("cccd") String cccd) {
        return "redirect:/profiles/citizens?cccd=" + cccd;
    }

    @GetMapping("/today")
    public String todayCitizens(Model model) {
        model.addAttribute("todayCitizens", congdanService.getTodayCitizens());
        return "profiles/citizens/today";
    }

    private CongdanFormDto toDto(Congdan citizen) {
        CongdanFormDto dto = new CongdanFormDto();
        dto.setPublicId(citizen.getPublicId());
        dto.setHoTen(citizen.getHoTen());
        dto.setNgaySinh(citizen.getNgaySinh());
        dto.setGioiTinh(citizen.getGioiTinh());
        dto.setCccd(citizen.getCccd());
        dto.setDiaChi(citizen.getDiaChi());
        dto.setSoDienThoai(citizen.getSoDienThoai());
        dto.setEmail(citizen.getEmail());
        dto.setTinhTrangSucKhoe(citizen.getTinhTrangSucKhoe());
        dto.setNgayKhamSucKhoe(citizen.getNgayKhamSucKhoe());
        dto.setGiayKhamSucKhoe(citizen.getGiayKhamSucKhoe());
        dto.setNgayTao(citizen.getNgayTao());
        dto.setAnh3x4(citizen.getAnh3x4());
        return dto;
    }

    private Congdan toEntity(CongdanFormDto dto) {
        Congdan citizen = new Congdan();
        citizen.setPublicId(dto.getPublicId());
        citizen.setHoTen(dto.getHoTen());
        citizen.setNgaySinh(dto.getNgaySinh());
        citizen.setGioiTinh(dto.getGioiTinh());
        citizen.setCccd(dto.getCccd());
        citizen.setDiaChi(dto.getDiaChi());
        citizen.setSoDienThoai(dto.getSoDienThoai());
        citizen.setEmail(dto.getEmail());
        citizen.setTinhTrangSucKhoe(dto.getTinhTrangSucKhoe());
        citizen.setNgayKhamSucKhoe(dto.getNgayKhamSucKhoe());
        citizen.setGiayKhamSucKhoe(dto.getGiayKhamSucKhoe());
        citizen.setNgayTao(dto.getNgayTao());
        citizen.setAnh3x4(dto.getAnh3x4());
        return citizen;
    }

    private Instant parseDateTimeInput(String dateTimeInput) {
        if (dateTimeInput == null || dateTimeInput.isBlank()) {
            return null;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeInput);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private java.time.LocalDate parseLocalDate(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return java.time.LocalDate.parse(input);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String composeAddress(String detailAddress, String wardName, String provinceName) {
        String detail = trimToNull(detailAddress);
        String ward = trimToNull(wardName);
        String province = trimToNull(provinceName);

        StringBuilder builder = new StringBuilder();
        if (detail != null) {
            builder.append(detail);
        }
        if (ward != null) {
            if (builder.length() > 0) {
                builder.append(" - ");
            }
            builder.append(ward);
        }
        if (province != null) {
            if (builder.length() > 0) {
                builder.append(" - ");
            }
            builder.append(province);
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private String validateCitizenForm(CongdanFormDto dto) {
        String hoTen = trimToNull(dto.getHoTen());
        if (hoTen == null) {
            return "Họ tên không được để trống";
        }

        String cccd = trimToNull(dto.getCccd());
        if (cccd == null || !CCCD_PATTERN.matcher(cccd).matches()) {
            return "CCCD phải gồm đúng 9 hoặc 12 chữ số";
        }

        String phone = trimToNull(dto.getSoDienThoai());
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            return "Số điện thoại phải gồm 10 hoặc 11 chữ số";
        }

        String email = trimToNull(dto.getEmail());
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            return "Email không đúng định dạng";
        }
        return null;
    }

    private AddressParts splitAddress(String fullAddress) {
        String normalized = trimToNull(fullAddress);
        if (normalized == null) {
            return new AddressParts(null, null, null);
        }

        String[] parts = normalized.split("\\s-\\s");
        if (parts.length >= 3) {
            String province = trimToNull(parts[parts.length - 1]);
            String ward = trimToNull(parts[parts.length - 2]);
            StringBuilder detail = new StringBuilder();
            for (int i = 0; i < parts.length - 2; i++) {
                if (detail.length() > 0) {
                    detail.append(" - ");
                }
                detail.append(parts[i].trim());
            }
            return new AddressParts(trimToNull(detail.toString()), ward, province);
        }

        return new AddressParts(normalized, null, null);
    }

    private record AddressParts(String detail, String ward, String province) {
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String resolveFriendlyErrorMessage(Exception ex) {
        String rootMessage = ex.getMessage();
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
                rootMessage = cause.getMessage();
            }
            cause = cause.getCause();
        }

        if (rootMessage == null) {
            return "Dữ liệu không hợp lệ";
        }

        String normalized = rootMessage.toLowerCase();
        if (normalized.contains("cccd")) {
            return "CCCD đã tồn tại";
        }
        if (normalized.contains("email")) {
            return "Email đã tồn tại";
        }
        if (normalized.contains("sodienthoai") || normalized.contains("so_dien_thoai")
                || normalized.contains("so dien thoai") || normalized.contains("phone")) {
            return "Số điện thoại đã tồn tại";
        }
        return "Dữ liệu không hợp lệ";
    }
}
