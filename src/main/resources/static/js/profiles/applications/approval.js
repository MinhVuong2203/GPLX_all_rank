(function () {
    const rows = document.querySelectorAll('.approval-row');
    const approveForm = document.getElementById('approveForm');
    const rejectForm = document.getElementById('rejectForm');

    if (!rows.length) {
        return;
    }

    function byId(id) {
        return document.getElementById(id);
    }

    const fields = {
        hoSoId: byId('dHoSoId'),
        maCongDan: byId('dMaCongDan'),
        hoTen: byId('dHoTen'),
        cccd: byId('dCccd'),
        ngaySinh: byId('dNgaySinh'),
        gioiTinh: byId('dGioiTinh'),
        sdt: byId('dSdt'),
        email: byId('dEmail'),
        diaChi: byId('dDiaChi'),
        hang: byId('dHang'),
        tenHang: byId('dTenHang'),
        trangThai: byId('dTrangThai'),
        thanhToan: byId('dThanhToan'),
        sucKhoe: byId('dSucKhoe'),
        ngayKham: byId('dNgayKham'),
        ghiChu: byId('dGhiChu'),
        giayKhamLink: byId('dGiayKhamLink'),
        giayKhamEmpty: byId('dGiayKhamEmpty'),
        anh: byId('dAnh'),
        anhEmpty: byId('dAnhEmpty')
    };

    function textOrDash(value) {
        return value && value !== 'null' ? value : '-';
    }

    function selectRow(row) {
        rows.forEach(function (r) {
            r.classList.remove('row-selected');
        });
        row.classList.add('row-selected');

        const d = row.dataset;
        fields.hoSoId.textContent = textOrDash(d.id);
        fields.maCongDan.textContent = textOrDash(d.macongdan);
        fields.hoTen.textContent = textOrDash(d.hoten);
        fields.cccd.textContent = textOrDash(d.cccd);
        fields.ngaySinh.textContent = textOrDash(d.ngaysinh);
        fields.gioiTinh.textContent = textOrDash(d.gioitinh);
        fields.sdt.textContent = textOrDash(d.sdt);
        fields.email.textContent = textOrDash(d.email);
        fields.diaChi.textContent = textOrDash(d.diachi);
        fields.hang.textContent = textOrDash(d.hang);
        fields.tenHang.textContent = textOrDash(d.tenhang);
        fields.trangThai.textContent = textOrDash(d.trangthai);
        fields.thanhToan.textContent = textOrDash(d.thanhtoan);
        fields.sucKhoe.textContent = textOrDash(d.suckhoe);
        fields.ngayKham.textContent = textOrDash(d.ngaykham);
        fields.ghiChu.textContent = textOrDash(d.ghichu);

        if (d.giaykham && d.giaykham !== 'null') {
            if (fields.giayKhamLink) {
                fields.giayKhamLink.textContent = d.giaykham;
                fields.giayKhamLink.href = '/' + d.giaykham;
                fields.giayKhamLink.style.display = 'inline';
            }
            if (fields.giayKhamEmpty) {
                fields.giayKhamEmpty.style.display = 'none';
            }
        } else {
            if (fields.giayKhamLink) {
                fields.giayKhamLink.style.display = 'none';
            }
            if (fields.giayKhamEmpty) {
                fields.giayKhamEmpty.style.display = 'inline';
            }
        }

        if (d.anh && d.anh !== 'null') {
            if (fields.anh) {
                fields.anh.src = '/' + d.anh;
                fields.anh.style.display = 'inline-block';
            }
            if (fields.anhEmpty) {
                fields.anhEmpty.style.display = 'none';
            }
        } else {
            if (fields.anh) {
                fields.anh.style.display = 'none';
            }
            if (fields.anhEmpty) {
                fields.anhEmpty.style.display = 'inline';
            }
        }

        if (approveForm) {
            approveForm.action = '/profiles/applications/' + d.id + '/status';
        }
        if (rejectForm) {
            rejectForm.action = '/profiles/applications/' + d.id + '/status';
        }
    }

    rows.forEach(function (row) {
        row.addEventListener('click', function () {
            selectRow(row);
        });
    });
})();
