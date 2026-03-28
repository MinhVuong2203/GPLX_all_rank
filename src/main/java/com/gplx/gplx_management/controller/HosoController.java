package com.gplx.gplx_management.controller;

import com.gplx.gplx_management.dto.HosoApprovalDto;
import com.gplx.gplx_management.dto.HosoFormDto;
import com.gplx.gplx_management.entity.Congdan;
import com.gplx.gplx_management.entity.Hanggiayphep;
import com.gplx.gplx_management.entity.Hoso;
import com.gplx.gplx_management.repository.CongdanRepository;
import com.gplx.gplx_management.repository.HanggiayphepRepository;
import com.gplx.gplx_management.service.HosoService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profiles/applications")
public class HosoController {

    private final HosoService hosoService;
    private final CongdanRepository congdanRepository;
    private final HanggiayphepRepository hanggiayphepRepository;

    public HosoController(HosoService hosoService,
            CongdanRepository congdanRepository,
            HanggiayphepRepository hanggiayphepRepository) {
        this.hosoService = hosoService;
        this.congdanRepository = congdanRepository;
        this.hanggiayphepRepository = hanggiayphepRepository;
    }

    @GetMapping
    public String listApplications(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Hoso> applicationPage = hosoService.getApplicationsPage(page, size);
        model.addAttribute("applicationPage", applicationPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("licenseRanks", hosoService.getAllLicenseRanks());
        return "profiles/applications/list";
    }

    @GetMapping("/approval")
    public String approvalBoard(
            @RequestParam(name = "trangThai", required = false, defaultValue = "Đang xử lý") String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "selectedId", required = false) Integer selectedId,
            Model model) {
        Page<Hoso> approvalPage = hosoService.getApprovalPage(trangThai, page, size);
        List<HosoApprovalDto> rows = approvalPage.getContent().stream().map(this::toApprovalDto).toList();

        HosoApprovalDto selectedRow = null;
        if (selectedId != null) {
            selectedRow = rows.stream().filter(r -> selectedId.equals(r.getHoSoId())).findFirst().orElse(null);
        }
        if (selectedRow == null && !rows.isEmpty()) {
            selectedRow = rows.get(0);
        }

        model.addAttribute("approvalPage", approvalPage);
        model.addAttribute("approvalRows", rows);
        model.addAttribute("selectedRow", selectedRow);
        model.addAttribute("statusOptions", hosoService.getApprovalStatuses());
        model.addAttribute("selectedStatus", trangThai);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "profiles/applications/approval";
    }

    @GetMapping("/new")
    public String newApplicationForm(@RequestParam(name = "cccd", required = false) String cccd, Model model) {
        prepareApplicationForm(model, new HosoFormDto(), "/profiles/applications/create", "Thêm hồ sơ", cccd);
        return "profiles/applications/form";
    }

    @PostMapping("/create")
    public String createApplication(@ModelAttribute("application") HosoFormDto formDto,
            @RequestParam(name = "ngayNopInput", required = false) String ngayNopInput,
            RedirectAttributes redirectAttributes) {
        formDto.setNgayNop(parseDateTimeInput(ngayNopInput));
        Hoso application = buildApplicationFromDto(formDto);
        hosoService.create(application);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm hồ sơ thành công.");
        return "redirect:/profiles/applications/approval";
    }

    @GetMapping("/{id}/edit")
    public String editApplicationForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return hosoService.findById(id)
                .map(application -> {
                    HosoFormDto dto = toFormDto(application);
                    prepareApplicationForm(model, dto, "/profiles/applications/" + id + "/update",
                            "Cập nhật hồ sơ", null);
                    return "profiles/applications/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hồ sơ.");
                    return "redirect:/profiles/applications";
                });
    }

    @PostMapping("/{id}/update")
    public String updateApplication(@PathVariable Integer id,
            @ModelAttribute("application") HosoFormDto formDto,
            @RequestParam(name = "ngayNopInput", required = false) String ngayNopInput,
            RedirectAttributes redirectAttributes) {
        formDto.setNgayNop(parseDateTimeInput(ngayNopInput));
        Hoso application = buildApplicationFromDto(formDto);
        hosoService.update(id, application);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công.");
        return "redirect:/profiles/applications/approval?selectedId=" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        hosoService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa hồ sơ thành công.");
        return "redirect:/profiles/applications/approval";
    }

    @GetMapping("/{id}/approval")
    public String approvalForm(@PathVariable Integer id) {
        return "redirect:/profiles/applications/approval?selectedId=" + id;
    }

    @PostMapping("/{id}/approval")
    public String approveByRank(@PathVariable Integer id,
            @RequestParam("maHang") String maHang,
            RedirectAttributes redirectAttributes) {
        hosoService.approveByRank(id, maHang);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xét duyệt hồ sơ cho hạng " + maHang + ".");
        redirectAttributes.addAttribute("selectedId", id);
        redirectAttributes.addAttribute("trangThai", "Đủ điều kiện");
        return "redirect:/profiles/applications/approval";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Integer id,
            @RequestParam("trangThai") String trangThai,
            RedirectAttributes redirectAttributes) {
        hosoService.updateStatus(id, trangThai);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công.");
        redirectAttributes.addAttribute("selectedId", id);
        redirectAttributes.addAttribute("trangThai", trangThai);
        return "redirect:/profiles/applications/approval";
    }

    private void prepareApplicationForm(Model model, HosoFormDto application, String formAction, String pageTitle,
            String cccd) {
        List<Congdan> citizens = congdanRepository.findAll();

        Optional<Congdan> selectedByCccd = Optional.empty();
        if (cccd != null && !cccd.isBlank()) {
            selectedByCccd = citizens.stream().filter(c -> cccd.equals(c.getCccd())).findFirst();
        }
        if (selectedByCccd.isPresent()) {
            application.setMaCongDanId(selectedByCccd.get().getId());
        }

        model.addAttribute("application", application);
        model.addAttribute("citizens", citizens);
        model.addAttribute("licenseRanks", hosoService.getAllLicenseRanks());
        model.addAttribute("approvalStatuses", hosoService.getApprovalStatuses());
        model.addAttribute("searchCccd", cccd);
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
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

    private Hoso buildApplicationFromDto(HosoFormDto dto) {
        Congdan congdan = congdanRepository.findById(dto.getMaCongDanId())
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found: " + dto.getMaCongDanId()));
        Hanggiayphep hanggiayphep = hanggiayphepRepository.findById(dto.getMaHangId())
                .orElseThrow(() -> new IllegalArgumentException("License rank not found: " + dto.getMaHangId()));

        Hoso application = new Hoso();
        application.setPublicId(dto.getPublicId());
        application.setMaCongDan(congdan);
        application.setMaHang(hanggiayphep);
        application.setNgayNop(dto.getNgayNop());
        application.setTrangThai(dto.getTrangThai());
        application.setTrangThaiThanhToan(dto.isTrangThaiThanhToan());
        application.setGhiChu(dto.getGhiChu());
        return application;
    }

    private HosoFormDto toFormDto(Hoso application) {
        HosoFormDto dto = new HosoFormDto();
        dto.setPublicId(application.getPublicId());
        dto.setMaCongDanId(application.getMaCongDan() != null ? application.getMaCongDan().getId() : null);
        dto.setMaHangId(application.getMaHang() != null ? application.getMaHang().getMaHang() : null);
        dto.setNgayNop(application.getNgayNop());
        dto.setTrangThai(application.getTrangThai());
        dto.setTrangThaiThanhToan(Boolean.TRUE.equals(application.getTrangThaiThanhToan()));
        dto.setGhiChu(application.getGhiChu());
        return dto;
    }

    private HosoApprovalDto toApprovalDto(Hoso application) {
        HosoApprovalDto dto = new HosoApprovalDto();
        dto.setHoSoId(application.getId());
        dto.setPublicId(application.getPublicId());
        dto.setNgayNop(application.getNgayNop());
        dto.setTrangThai(application.getTrangThai());
        dto.setTrangThaiThanhToan(application.getTrangThaiThanhToan());
        dto.setGhiChu(application.getGhiChu());

        if (application.getMaHang() != null) {
            dto.setHang(application.getMaHang().getMaHang());
            dto.setTenHang(application.getMaHang().getTenHang());
        }
        if (application.getMaCongDan() != null) {
            dto.setMaCongDan(application.getMaCongDan().getId());
            dto.setHoTen(application.getMaCongDan().getHoTen());
            dto.setCccd(application.getMaCongDan().getCccd());
            dto.setSoDienThoai(application.getMaCongDan().getSoDienThoai());
            dto.setNgaySinh(application.getMaCongDan().getNgaySinh());
            dto.setGioiTinh(application.getMaCongDan().getGioiTinh());
            dto.setEmail(application.getMaCongDan().getEmail());
            dto.setDiaChi(application.getMaCongDan().getDiaChi());
            dto.setTinhTrangSucKhoe(application.getMaCongDan().getTinhTrangSucKhoe());
            dto.setNgayKhamSucKhoe(application.getMaCongDan().getNgayKhamSucKhoe());
            dto.setGiayKhamSucKhoe(application.getMaCongDan().getGiayKhamSucKhoe());
            dto.setAnh3x4(application.getMaCongDan().getAnh3x4());
        }
        return dto;
    }
}
