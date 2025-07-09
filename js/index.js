// 輪播圖功能
let currentSlideIndex = 0;
const slides = document.querySelectorAll('.carousel-slide');
const dots = document.querySelectorAll('.dot');

function moveSlide(direction) {
    currentSlideIndex = (currentSlideIndex + direction + slides.length) % slides.length;
    updateSlidePosition();
}

function currentSlide(index) {
    currentSlideIndex = index;
    updateSlidePosition();
}

function updateSlidePosition() {
    const slidesContainer = document.querySelector('.carousel-slides');
    slidesContainer.style.transform = `translateX(-${currentSlideIndex * 100}%)`;
    dots.forEach((dot, index) => {
        dot.style.backgroundColor = index === currentSlideIndex ? '#666' : '#fff';
    });
}

// 渲染排行榜
function renderRanking(selector, data) {
    const container = document.querySelector(selector);
    if (!container) return;
    if (!data || data.length === 0) {
        container.innerHTML = '<div style="text-align:center;color:#888;grid-column:span 3;">暫無商品銷售記錄</div>';
        return;
    }
    let html = '';
    const rankColors = ['#FFD700', '#C0C0C0', '#CD7F32']; // 金、銀、銅
    data.slice(0, 3).forEach((item, idx) => {
        const rankColor = rankColors[idx] || '#e6b800';
        html += `<div class="col-12 col-md-4 d-flex justify-content-center">
                        <a href="Products/product-detail.html?id=${item.id}" style="text-decoration: none; color: inherit;">
                            <div class="ranking-item ranking-card" style="background: #faf6ef; box-shadow: 0 2px 12px rgba(180,150,80,0.07); border-radius: 28px; min-width: 270px; max-width: 340px; margin: 22px 0; padding: 38px 22px 28px 22px; display: flex; flex-direction: column; align-items: center; transition: transform 0.25s, box-shadow 0.25s; cursor: pointer;">
                                <div class="rank-badge2" style="background:${rankColor}; color:#fff; font-size:1.5rem; font-weight:800; width:54px; height:54px; border-radius:50%; display:flex; align-items:center; justify-content:center; box-shadow:0 2px 12px ${rankColor}55; margin-bottom:18px; letter-spacing:1px; border:4px solid #fff;">${idx + 1}</div>
                                <div style="width:120px;height:120px;display:flex;align-items:center;justify-content:center;margin-bottom:20px;">
                                    <img src="${getProductImageUrl(item.image)}" alt="${item.name}" loading="lazy" style="width:110px;height:110px;object-fit:cover;border-radius:50%;box-shadow:0 4px 24px 0 #ffe13533,0 0 0 8px #fff; background:#f8f8f8; border:2px solid #f7e7b6; transition:box-shadow 0.22s, transform 0.22s;" onerror="this.src='images/baby.jpg';">
                                </div>
                                <div style="font-weight:800;font-size:1.18rem;margin-bottom:10px;letter-spacing:1px;color:#222;text-align:center;">${item.name}</div>
                                <div class="rank-labels2" style="display:flex;gap:10px;justify-content:center;margin-bottom:8px;">
                                    <span style="background:#ffe135;color:#222;font-size:1rem;font-weight:700;padding:3px 14px;border-radius:12px;">NT$ ${item.price}</span>
                                    <span style="background:#f7e7b6;color:#7a5c00;font-size:0.98rem;font-weight:600;padding:3px 12px;border-radius:12px;">銷量 ${item.sold} 件</span>
                                </div>
                            </div>
                        </a>
                    </div>`;
    });
    container.innerHTML = html;
}

function getProductImageUrl(url) {
    if (!url || url.trim() === '' || url === '/images/default.jpg') {
        return 'images/baby.jpg';
    }
    if (url.startsWith('/products/front/images/')) {
        return 'https://pikababy-back.onrender.com' + url;
    }
    if (url.startsWith('images/')) {
        return url;
    }
    if (!url.startsWith('http')) {
        return 'images/baby.jpg';
    }
    return url;
}

// 讓Bootstrap下拉功能正常工作，並且在手機版時也能運作
(function () {
    function isMobile() {
        return window.innerWidth < 992;
    }

    // 只在手機版的折疊選單內處理下拉功能
    document.addEventListener('click', function (e) {
        // 檢查是否在折疊的導航欄內
        var navbarCollapse = document.getElementById('navbarSupportedContent');
        var isInCollapsedNav = navbarCollapse && navbarCollapse.classList.contains('show') && isMobile();

        if (!isInCollapsedNav) return; // 讓Bootstrap處理桌面版和正常情況

        var toggle = e.target.closest('.dropdown-toggle');
        if (toggle && toggle.parentElement.classList.contains('dropdown')) {
            e.preventDefault();
            e.stopPropagation();
            var menu = toggle.nextElementSibling;
            if (menu && menu.classList.contains('dropdown-menu')) {
                // 關閉其他下拉選單
                document.querySelectorAll('.dropdown-menu.show').forEach(function (otherMenu) {
                    if (otherMenu !== menu) {
                        otherMenu.classList.remove('show');
                    }
                });
                menu.classList.toggle('show');
            }
        }
    });

    // 關閉時自動收合所有下拉
    var collapse = document.getElementById('navbarSupportedContent');
    if (collapse) {
        collapse.addEventListener('hide.bs.collapse', function () {
            document.querySelectorAll('.dropdown-menu.show').forEach(function (menu) {
                menu.classList.remove('show');
            });
        });
    }
})();