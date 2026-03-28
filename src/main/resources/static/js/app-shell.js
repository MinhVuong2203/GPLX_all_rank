(function () {
    let loadingTimer = null;
    let loadingPercent = 0;

    function getStoredTheme() {
        return localStorage.getItem("gplx-theme");
    }

    function setStoredTheme(theme) {
        localStorage.setItem("gplx-theme", theme);
    }

    function preferredTheme() {
        const saved = getStoredTheme();
        if (saved === "dark" || saved === "light") {
            return saved;
        }
        return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    }

    function updateThemeUI(theme) {
        const btn = document.getElementById("themeToggle");
        if (!btn) {
            return;
        }
        const icon = theme === "dark" ? "bi-sun-fill" : "bi-moon-stars-fill";
        const text = theme === "dark" ? "Light" : "Dark";
        btn.innerHTML = '<i class="bi ' + icon + '"></i><span class="ms-1 d-none d-md-inline">' + text + '</span>';
        btn.setAttribute("aria-label", "Switch theme");
    }

    function applyTheme(theme) {
        document.documentElement.setAttribute("data-bs-theme", theme);
        updateThemeUI(theme);
        setStoredTheme(theme);
    }

    function loadSidebarState() {
        const collapsed = localStorage.getItem("gplx-sidebar-collapsed") === "1";
        if (collapsed) {
            document.body.classList.add("sidebar-collapsed");
        }
    }

    function setSidebarCollapsed(collapsed) {
        document.body.classList.toggle("sidebar-collapsed", collapsed);
        localStorage.setItem("gplx-sidebar-collapsed", collapsed ? "1" : "0");
    }

    function initButtons() {
        const themeToggle = document.getElementById("themeToggle");
        if (themeToggle) {
            themeToggle.addEventListener("click", function () {
                const current = document.documentElement.getAttribute("data-bs-theme") || "light";
                applyTheme(current === "dark" ? "light" : "dark");
            });
        }

        const sidebarToggle = document.getElementById("sidebarToggle");
        if (sidebarToggle) {
            sidebarToggle.addEventListener("click", function () {
                if (window.innerWidth < 992) {
                    document.body.classList.toggle("sidebar-open");
                } else {
                    setSidebarCollapsed(!document.body.classList.contains("sidebar-collapsed"));
                }
            });
        }

        const sidebarCollapse = document.getElementById("sidebarCollapse");
        if (sidebarCollapse) {
            sidebarCollapse.addEventListener("click", function () {
                setSidebarCollapsed(!document.body.classList.contains("sidebar-collapsed"));
            });
        }
    }

    function closeSidebarOnMobileNav() {
        if (window.innerWidth < 992) {
            document.querySelectorAll(".app-sidebar .nav-link").forEach(function (link) {
                link.addEventListener("click", function () {
                    document.body.classList.remove("sidebar-open");
                });
            });
        }
    }

    function ensureLoadingBar() {
        let bar = document.getElementById("pageLoadingBar");
        if (bar) {
            return bar;
        }
        bar = document.createElement("div");
        bar.id = "pageLoadingBar";
        bar.className = "page-loading-bar";

        const progress = document.createElement("div");
        progress.id = "pageLoadingBarProgress";
        progress.className = "page-loading-bar-progress";
        bar.appendChild(progress);
        document.body.appendChild(bar);
        return bar;
    }

    function ensureCarLoader() {
        let loader = document.getElementById("pageLoadingCar");
        if (loader) {
            return loader;
        }

        loader = document.createElement("div");
        loader.id = "pageLoadingCar";
        loader.className = "page-loading-car";
        loader.setAttribute("aria-hidden", "true");
        loader.innerHTML =
            '<div class="page-loading-car-box">' +
            '<svg class="page-loading-car-icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">' +
            '<circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.8"></circle>' +
            '<circle cx="12" cy="12" r="2.1" fill="none" stroke="currentColor" stroke-width="1.8"></circle>' +
            '<path d="M12 9.9V5.6M10.3 13.2L7.2 17.8M13.7 13.2L16.8 17.8" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path>' +
            '</svg>' +
            '<div class="page-loading-car-text">Đang tải dữ liệu...</div>' +
            "</div>";

        document.body.appendChild(loader);
        return loader;
    }

    function setLoadingProgress(percent) {
        const progress = document.getElementById("pageLoadingBarProgress");
        if (!progress) {
            return;
        }
        loadingPercent = Math.max(0, Math.min(100, percent));
        progress.style.width = loadingPercent + "%";
    }

    function startLoading() {
        const bar = ensureLoadingBar();
        const carLoader = ensureCarLoader();
        bar.classList.add("show");
        carLoader.classList.add("show");
        if (loadingTimer) {
            clearInterval(loadingTimer);
        }
        setLoadingProgress(12);
        loadingTimer = setInterval(function () {
            if (loadingPercent < 85) {
                setLoadingProgress(loadingPercent + Math.max(1.5, (90 - loadingPercent) * 0.08));
            }
        }, 110);
    }

    function finishLoading() {
        const bar = ensureLoadingBar();
        const carLoader = ensureCarLoader();
        if (loadingTimer) {
            clearInterval(loadingTimer);
            loadingTimer = null;
        }
        setLoadingProgress(100);
        window.setTimeout(function () {
            bar.classList.remove("show");
            carLoader.classList.remove("show");
            setLoadingProgress(0);
        }, 220);
    }

    function beginLeaveTransition() {
        if (document.body.classList.contains("page-leaving")) {
            return;
        }
        document.body.classList.add("page-leaving");
        startLoading();
    }

    function isNavigableLink(link) {
        if (!link || !link.href) {
            return false;
        }
        if (link.target === "_blank" || link.hasAttribute("download")) {
            return false;
        }
        if (link.href.startsWith("javascript:")) {
            return false;
        }
        const targetUrl = new URL(link.href, window.location.origin);
        return targetUrl.origin === window.location.origin && targetUrl.href !== window.location.href;
    }

    function initPageTransitions() {
        document.body.classList.add("page-entering");
        window.setTimeout(function () {
            document.body.classList.remove("page-entering");
        }, 360);

        document.addEventListener("click", function (event) {
            const link = event.target.closest("a");
            if (!isNavigableLink(link)) {
                return;
            }
            if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
                return;
            }
            event.preventDefault();
            const destination = link.href;
            beginLeaveTransition();
            window.setTimeout(function () {
                window.location.href = destination;
            }, 120);
        });

        document.querySelectorAll("form").forEach(function (form) {
            form.addEventListener("submit", function () {
                beginLeaveTransition();
            });
        });

        window.addEventListener("beforeunload", function () {
            startLoading();
        });

        window.addEventListener("load", function () {
            finishLoading();
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        ensureLoadingBar();
        ensureCarLoader();
        startLoading();
        applyTheme(preferredTheme());
        loadSidebarState();
        initButtons();
        closeSidebarOnMobileNav();
        initPageTransitions();
    });
})();
