# 🚗 HỆ THỐNG QUẢN LÝ GPLX – LUỒNG XỬ LÝ CHÍNH

## 🎯 Mục tiêu hệ thống

Hệ thống dùng để quản lý toàn bộ quy trình cấp Giấy phép lái xe (GPLX), bao gồm:

* Quản lý công dân
* Quản lý hồ sơ đăng ký
* Tổ chức kỳ thi
* Ghi nhận kết quả thi
* Cấp giấy phép
* Quản lý vi phạm sau khi cấp


## 🧠 Tổng quan luồng xử lý

Hệ thống hoạt động theo quy trình sau:

```
Công dân → Hồ sơ → Duyệt → Kỳ thi → Kết quả → GPLX → Vi phạm
```

## 📌 1. Quản lý công dân

* Tạo mới công dân
* Cập nhật thông tin cá nhân
* Lưu trữ:

  * Họ tên
  * CCCD
  * Ngày sinh
  * Sức khỏe

👉 Mỗi công dân có thể đăng ký nhiều hồ sơ thi khác nhau.


## 📌 2. Tạo hồ sơ đăng ký

* Công dân đăng ký thi GPLX
* Chọn hạng GPLX (A1, B2, C,...)

Thông tin lưu:

* Công dân
* Hạng đăng ký
* Ngày nộp
* Trạng thái hồ sơ

👉 Trạng thái ban đầu:

```
Đang xử lý
```


## 📌 3. Duyệt hồ sơ

Cán bộ kiểm tra:

* Độ tuổi
* Sức khỏe
* Điều kiện thi

👉 Cập nhật trạng thái:

* Đủ điều kiện
* Không đủ điều kiện

👉 Chỉ hồ sơ "Đủ điều kiện" mới được thi.



## 📌 4. Tổ chức kỳ thi

* Tạo kỳ thi theo từng hạng GPLX
* Thiết lập:

  * Tên kỳ thi
  * Ngày thi
  * Địa điểm

👉 Gán hồ sơ vào kỳ thi tương ứng.



## 📌 5. Nhập kết quả thi

Sau khi thi, cán bộ nhập kết quả:

### Gồm:

* Điểm các môn (lý thuyết, thực hành...)
* Kết quả từng môn

👉 Hệ thống tự xác định:

```
Đạt / Không đạt
```

👉 Lưu:

* Kết quả chi tiết
* Kết quả tổng hợp


## 📌 6. Cấp giấy phép lái xe

Nếu công dân thi đạt:

* Tạo bản ghi GPLX
* Gán:

  * Hạng GPLX
  * Ngày cấp
  * Trạng thái

👉 Trạng thái mặc định:

```
Còn hiệu lực
```

## 📌 7. Quản lý vi phạm

Sau khi có GPLX:

* Ghi nhận vi phạm
* Trừ điểm GPLX

Thông tin gồm:

* Loại vi phạm
* Mức phạt
* Điểm trừ

👉 Nếu hết điểm:

* Có thể thu hồi GPLX


## 🔄 Mối quan hệ tổng thể

* 1 Công dân → nhiều Hồ sơ
* 1 Hồ sơ → 1 Kỳ thi
* 1 Hồ sơ → nhiều lần thi
* 1 Kỳ thi → nhiều hồ sơ
* 1 GPLX → nhiều vi phạm


## 🎯 Kết luận

Hệ thống tập trung vào:

* Quản lý quy trình cấp GPLX
* Không mô phỏng thi thực tế
* Đảm bảo đơn giản, dễ triển khai nhưng vẫn đầy đủ nghiệp vụ
