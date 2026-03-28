(function () {
    const provinceSelect = document.getElementById('diaChiTinhSelect');
    const wardSelect = document.getElementById('diaChiPhuongXaSelect');
    const provinceNameInput = document.getElementById('diaChiTinhName');
    const wardNameInput = document.getElementById('diaChiPhuongXaName');
    const savedProvinceNameInput = document.getElementById('savedProvinceName');
    const savedWardNameInput = document.getElementById('savedWardName');
    const imageFileInput = document.getElementById('imageFileInput');
    const citizenImagePreview = document.getElementById('citizenImagePreview');

    if (!provinceSelect || !wardSelect || !provinceNameInput || !wardNameInput) {
        return;
    }

    function resetWard() {
        wardSelect.innerHTML = '<option value="">-- Chon phuong / xa --</option>';
        wardSelect.disabled = true;
        wardNameInput.value = '';
    }

    function updateHiddenNames() {
        const selectedProvince = provinceSelect.options[provinceSelect.selectedIndex];
        const selectedWard = wardSelect.options[wardSelect.selectedIndex];
        provinceNameInput.value = selectedProvince && selectedProvince.value ? selectedProvince.text : '';
        wardNameInput.value = selectedWard && selectedWard.value ? selectedWard.text : '';
    }

    function getSavedProvinceName() {
        return savedProvinceNameInput && savedProvinceNameInput.value ? savedProvinceNameInput.value.trim() : '';
    }

    function getSavedWardName() {
        return savedWardNameInput && savedWardNameInput.value ? savedWardNameInput.value.trim() : '';
    }

    async function loadWardsByProvince(provinceCode) {
        resetWard();
        if (!provinceCode) {
            updateHiddenNames();
            return;
        }

        try {
            const response = await fetch('/api/addresses/wards?provinceCode=' + encodeURIComponent(provinceCode));
            if (!response.ok) {
                return;
            }
            const wards = await response.json();
            wards.forEach(function (ward) {
                const option = document.createElement('option');
                option.value = ward.code;
                option.textContent = ward.name;
                wardSelect.appendChild(option);
            });

            const savedWardName = getSavedWardName();
            if (savedWardName) {
                const targetWardOption = Array.from(wardSelect.options).find(function (option) {
                    return option.text.trim() === savedWardName;
                });
                if (targetWardOption) {
                    wardSelect.value = targetWardOption.value;
                }
            }

            wardSelect.disabled = false;
            updateHiddenNames();
        } catch (error) {
            // Keep form usable even when address API is unavailable.
        }
    }

    function restoreSavedAddress() {
        const savedProvinceName = getSavedProvinceName();
        if (!savedProvinceName) {
            return;
        }

        const targetProvinceOption = Array.from(provinceSelect.options).find(function (option) {
            return option.text.trim() === savedProvinceName;
        });

        if (!targetProvinceOption) {
            return;
        }

        provinceSelect.value = targetProvinceOption.value;
        loadWardsByProvince(provinceSelect.value);
    }

    function setupImagePreview() {
        if (!imageFileInput || !citizenImagePreview) {
            return;
        }

        imageFileInput.addEventListener('change', function () {
            const file = imageFileInput.files && imageFileInput.files[0];
            if (!file) {
                return;
            }

            const objectUrl = URL.createObjectURL(file);
            citizenImagePreview.src = objectUrl;
            citizenImagePreview.style.display = 'inline-block';
        });
    }

    provinceSelect.addEventListener('change', function () {
        loadWardsByProvince(provinceSelect.value);
    });

    wardSelect.addEventListener('change', updateHiddenNames);
    setupImagePreview();
    restoreSavedAddress();
    updateHiddenNames();
})();
