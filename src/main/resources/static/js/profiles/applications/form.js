(function () {
    const citizenSelect = document.getElementById('maCongDanId');
    const rankSelect = document.getElementById('maHangId');

    const detailHoTen = document.getElementById('detailHoTen');
    const detailCccd = document.getElementById('detailCccd');
    const detailNgaySinh = document.getElementById('detailNgaySinh');
    const detailGioiTinh = document.getElementById('detailGioiTinh');
    const detailSdt = document.getElementById('detailSdt');
    const detailEmail = document.getElementById('detailEmail');
    const detailDiaChi = document.getElementById('detailDiaChi');
    const detailSucKhoe = document.getElementById('detailSucKhoe');
    const detailNgayKham = document.getElementById('detailNgayKham');
    const detailHangThi = document.getElementById('detailHangThi');

    if (!citizenSelect || !rankSelect || !detailHoTen || !detailHangThi) {
        return;
    }

    function selectedOption(selectElement) {
        return selectElement.options[selectElement.selectedIndex] || null;
    }

    function textOrDash(value) {
        return value && value !== 'null' ? value : '-';
    }

    function renderCitizenDetail() {
        const selected = selectedOption(citizenSelect);
        detailHoTen.textContent = textOrDash(selected ? selected.dataset.hoten : null);
        detailCccd.textContent = textOrDash(selected ? selected.dataset.cccd : null);
        detailNgaySinh.textContent = textOrDash(selected ? selected.dataset.ngaysinh : null);
        detailGioiTinh.textContent = textOrDash(selected ? selected.dataset.gioitinh : null);
        detailSdt.textContent = textOrDash(selected ? selected.dataset.phone : null);
        detailEmail.textContent = textOrDash(selected ? selected.dataset.email : null);
        detailDiaChi.textContent = textOrDash(selected ? selected.dataset.diachi : null);
        detailSucKhoe.textContent = textOrDash(selected ? selected.dataset.suckhoe : null);
        detailNgayKham.textContent = textOrDash(selected ? selected.dataset.ngaykham : null);
    }

    function renderRankDetail() {
        const selected = selectedOption(rankSelect);
        if (!selected || !selected.value) {
            detailHangThi.textContent = '-';
            return;
        }
        detailHangThi.textContent = selected.value + ' - ' + textOrDash(selected.dataset.tenhang);
    }

    citizenSelect.addEventListener('change', renderCitizenDetail);
    rankSelect.addEventListener('change', renderRankDetail);

    renderCitizenDetail();
    renderRankDetail();
})();
