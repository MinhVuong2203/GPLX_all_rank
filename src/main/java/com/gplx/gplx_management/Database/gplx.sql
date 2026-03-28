CREATE DATABASE IF NOT EXISTS qlgplx;
USE qlgplx;

-- ================== CÔNG DÂN ==================congdan
CREATE TABLE CongDan (
    MaCongDan INT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(36) UNIQUE, 
    HoTen VARCHAR(100) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh VARCHAR(10),
    CCCD VARCHAR(20) UNIQUE NOT NULL,
    DiaChi VARCHAR(255),
    SoDienThoai VARCHAR(15) UNIQUE,
    Email VARCHAR(100) UNIQUE,
    TinhTrangSucKhoe VARCHAR(50) DEFAULT 'Khỏe mạnh',
    NgayKhamSucKhoe DATE,
    GiayKhamSucKhoe VARCHAR(255),
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    Anh3x4 VARCHAR(255)
);



-- ================== HẠNG GPLX ==================
CREATE TABLE HangGiayPhep (
    MaHang VARCHAR(10) PRIMARY KEY,
    TenHang VARCHAR(50) NOT NULL,
    LoaiXe VARCHAR(50),
    DoTuoiToiThieu INT DEFAULT 18,
    ThoiHanNam INT,
    YeuCauThucHanh TINYINT(1) DEFAULT 1,
    MoTaChiTiet TEXT
);

-- ================== MÔN THI ==================
CREATE TABLE MonThi (
    MonThiID INT AUTO_INCREMENT PRIMARY KEY,
    TenMon VARCHAR(100) NOT NULL,
    MoTa VARCHAR(255)
);

-- ================== HẠNG - MÔN THI ==================
DROP TABLE IF EXISTS hang_mon_thi;

CREATE TABLE hang_mon_thi (
    ma_hang VARCHAR(10) NOT NULL,
    mon_thiid INT NOT NULL,
    diem_dat DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (ma_hang, mon_thiid),
    FOREIGN KEY (ma_hang) REFERENCES HangGiayPhep(MaHang),
    FOREIGN KEY (mon_thiid) REFERENCES MonThi(MonThiID)
);

-- ================== HỒ SƠ ==================
CREATE TABLE HoSo (
    HoSoID INT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(36) UNIQUE,
    MaCongDan INT NOT NULL,
    MaHang VARCHAR(10) NOT NULL,
    NgayNop DATETIME DEFAULT CURRENT_TIMESTAMP,
    TrangThai VARCHAR(30) DEFAULT 'Đang xử lý',
    TrangThaiThanhToan TINYINT(1) DEFAULT 0,
    GhiChu VARCHAR(255),
    FOREIGN KEY (MaCongDan) REFERENCES CongDan(MaCongDan),
    FOREIGN KEY (MaHang) REFERENCES HangGiayPhep(MaHang)
);

-- ================== KỲ THI ==================
CREATE TABLE KyThi (
    KyThiID INT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(36) UNIQUE,
    TenKyThi VARCHAR(150),
    NgayBatDau DATE,
    NgayKetThuc DATE,
    DiaDiem VARCHAR(255),
    MaHang VARCHAR(10),
    SoLuongToiDa INT,
    TrangThai VARCHAR(30) DEFAULT 'Sắp diễn ra',
    FOREIGN KEY (MaHang) REFERENCES HangGiayPhep(MaHang)
);

-- ================== LỊCH THI ==================
CREATE TABLE LichThi (
    LichThiID INT AUTO_INCREMENT PRIMARY KEY,
    KyThiID INT,
    MonThiID INT,
    ThoiGian DATETIME,
    FOREIGN KEY (KyThiID) REFERENCES KyThi(KyThiID),
    FOREIGN KEY (MonThiID) REFERENCES MonThi(MonThiID)
);

-- ================== KẾT QUẢ ==================
CREATE TABLE KetQuaThi (
    KetQuaID INT AUTO_INCREMENT PRIMARY KEY,
    HoSoID INT,
    KyThiID INT,
    KetQuaTongHop VARCHAR(20),
    NgayKetLuan DATETIME DEFAULT CURRENT_TIMESTAMP,
    LanThi INT DEFAULT 1,
    GhiChu VARCHAR(255),
    UNIQUE (HoSoID, KyThiID, LanThi),
    FOREIGN KEY (HoSoID) REFERENCES HoSo(HoSoID),
    FOREIGN KEY (KyThiID) REFERENCES KyThi(KyThiID)
);

-- ================== KẾT QUẢ CHI TIẾT ==================
CREATE TABLE KetQuaChiTiet (
    ChiTietID INT AUTO_INCREMENT PRIMARY KEY,
    KetQuaID INT,
    MonThiID INT,
    Diem DECIMAL(5,2),
    ThoiGianBatDau DATETIME,
    KetQua VARCHAR(20),
    GhiChu VARCHAR(255),
    UNIQUE (KetQuaID, MonThiID),
    FOREIGN KEY (KetQuaID) REFERENCES KetQuaThi(KetQuaID) ON DELETE CASCADE,
    FOREIGN KEY (MonThiID) REFERENCES MonThi(MonThiID)
);

-- ================== GIẤY PHÉP ==================
CREATE TABLE GiayPhep (
    GiayPhepID INT AUTO_INCREMENT PRIMARY KEY,
    MaCongDan INT,
    MaHang VARCHAR(10),
    SoGiayPhep VARCHAR(20) UNIQUE,
    NgayCap DATE,
    NgayHetHan DATE,
    SoDiem INT DEFAULT 12,
    TrangThai VARCHAR(30) DEFAULT 'Còn hiệu lực',
    GhiChu VARCHAR(255),
    FOREIGN KEY (MaCongDan) REFERENCES CongDan(MaCongDan),
    FOREIGN KEY (MaHang) REFERENCES HangGiayPhep(MaHang)
);

-- ================== VI PHẠM ==================
CREATE TABLE LoaiViPham (
    LoaiViPhamID INT AUTO_INCREMENT PRIMARY KEY,
    TenViPham VARCHAR(255),
    DiemTru INT,
    MucPhatTu DECIMAL(18,2),
    MucPhatDen DECIMAL(18,2),
    MoTa VARCHAR(500)
);

CREATE TABLE ViPham (
    ViPhamID INT AUTO_INCREMENT PRIMARY KEY,
    GiayPhepID INT,
    LoaiViPhamID INT,
    ThoiGianViPham DATETIME DEFAULT CURRENT_TIMESTAMP,
    DiaDiem VARCHAR(255),
    BienKiemSoat VARCHAR(20),
    MucPhat DECIMAL(18,2),
    TrangThai VARCHAR(30) DEFAULT 'Chưa xử lý',
    GhiChu VARCHAR(500),
    FOREIGN KEY (GiayPhepID) REFERENCES GiayPhep(GiayPhepID),
    FOREIGN KEY (LoaiViPhamID) REFERENCES LoaiViPham(LoaiViPhamID)
);

-- ================== CHỨC VỤ ==================
CREATE TABLE ChucVu (
    MaChucVu INT AUTO_INCREMENT PRIMARY KEY,
    TenChucVu VARCHAR(50) UNIQUE
);

INSERT INTO ChucVu (TenChucVu)
VALUES ('Quản lý'), ('Cán bộ hồ sơ'), ('Cán bộ sát hạch'), ('Cán bộ cấp GPLX'), ('Cán bộ xử lý vi phạm');

-- ================== CÁN BỘ ==================
CREATE TABLE CanBo (
    MaCanBo INT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(36) UNIQUE,
    HoTen VARCHAR(100),
    MaChucVu INT,
    Email VARCHAR(120),
    DienThoai VARCHAR(15),
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    Username VARCHAR(100) UNIQUE,
    Password VARCHAR(100),
    Anh3x4 VARCHAR(256),
    TrangThai TINYINT(1) DEFAULT 1,
    FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu)
);

-- ================== CÁN BỘ - HỒ SƠ ==================
CREATE TABLE CanBo_HoSo (
    MaCanBo INT,
    HoSoID INT,
    ThoiGian DATETIME DEFAULT CURRENT_TIMESTAMP,
    TrangThaiDuyet VARCHAR(50),
    PRIMARY KEY (MaCanBo, HoSoID, ThoiGian),
    FOREIGN KEY (MaCanBo) REFERENCES CanBo(MaCanBo),
    FOREIGN KEY (HoSoID) REFERENCES HoSo(HoSoID)
);


INSERT INTO HangGiayPhep VALUES
('A1', 'Xe máy <175cc', 'Xe máy', 18, NULL, 1, 'Không thời hạn'),
('B2', 'Ô tô <9 chỗ', 'Ô tô', 18, 10, 1, '10 năm'),
('C', 'Xe tải', 'Xe tải', 21, 5, 1, '5 năm');

INSERT INTO MonThi (TenMon) VALUES
('Lý thuyết'),
('Mô phỏng'),
('Sa hình'),
('Đường trường');

-- A1
INSERT INTO Hang_MonThi VALUES ('A1', 1, 21);

-- B2
INSERT INTO Hang_MonThi VALUES 
('B2', 1, 26),
('B2', 2, 8),
('B2', 3, 80),
('B2', 4, 80);

-- C
INSERT INTO hang_mon_thi VALUES 
('C', 1, 28),
('C', 3, 80),
('C', 4, 80);

INSERT INTO CongDan (public_id, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai)
VALUES
(UUID(), 'Nguyễn Văn A', '2000-01-01', 'Nam', '123456789001', '0900000001'),
(UUID(), 'Trần Thị B', '1998-05-10', 'Nữ', '123456789002', '0900000002');


INSERT INTO HoSo (public_id, MaCongDan, MaHang, TrangThai)
VALUES
(UUID(), 1, 'A1', 'Đủ điều kiện'),
(UUID(), 2, 'B2', 'Đủ điều kiện');

INSERT INTO KyThi (public_id, TenKyThi, NgayBatDau, DiaDiem, MaHang)
VALUES
(UUID(), 'Kỳ thi A1 tháng 3', '2026-03-01', 'Hà Nội', 'A1'),
(UUID(), 'Kỳ thi B2 tháng 3', '2026-03-01', 'HCM', 'B2');

INSERT INTO LichThi (KyThiID, MonThiID, ThoiGian)
VALUES
(1, 1, NOW()),
(2, 1, NOW()),
(2, 2, NOW()),
(2, 3, NOW()),
(2, 4, NOW());

INSERT INTO KetQuaThi (HoSoID, KyThiID, KetQuaTongHop, LanThi)
VALUES
(1, 1, 'Đạt', 1),
(2, 2, 'Không đạt', 1);

-- A1 (đậu)
INSERT INTO KetQuaChiTiet (KetQuaID, MonThiID, Diem, KetQua)
VALUES
(1, 1, 25, 'Đạt');

-- B2 (rớt sa hình)
INSERT INTO KetQuaChiTiet VALUES
(NULL, 2, 1, 30, NOW(), 'Đạt', NULL),
(NULL, 2, 2, 9, NOW(), 'Đạt', NULL),
(NULL, 2, 3, 70, NOW(), 'Không đạt', NULL),
(NULL, 2, 4, 0, NOW(), 'Vắng', NULL);

INSERT INTO GiayPhep (MaCongDan, MaHang, SoGiayPhep, NgayCap)
VALUES
(1, 'A1', 'GPLX001', CURDATE());

INSERT INTO LoaiViPham (TenViPham, DiemTru, MucPhatTu, MucPhatDen)
VALUES
('Vượt đèn đỏ', 4, 500000, 1000000),
('Không đội mũ bảo hiểm', 2, 200000, 300000);

INSERT INTO ViPham (GiayPhepID, LoaiViPhamID, DiaDiem)
VALUES
(1, 1, 'Hà Nội'),
(1, 2, 'Hà Nội');

INSERT INTO CanBo (public_id, HoTen, MaChucVu, Username, Password)
VALUES
(UUID(), 'Admin', 1, 'admin', '123456'),
(UUID(), 'Cán bộ hồ sơ', 2, 'hoso', '123456');

INSERT INTO CanBo_HoSo (MaCanBo, HoSoID, TrangThaiDuyet)
VALUES
(2, 1, 'Đủ điều kiện'),
(2, 2, 'Đủ điều kiện');