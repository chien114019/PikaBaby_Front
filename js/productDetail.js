let currentProduct = null;
let currentQuantity = 1;

// 載入商品詳情
async function loadProductDetail(productId) {
    try {
        showLoading();

        const response = await fetch(`${hostname}/products/front/detail/${productId}`);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const data = await response.json();

        if (!data.success) {
            throw new Error(data.message || '載入商品失敗');
        }

        console.log(data);


        currentProduct = data;
        displayProductDetail(data);

    } catch (error) {
        console.error('載入商品詳情失敗:', error);
        showError('載入商品失敗: ' + error.message);
    }
}

// 顯示商品詳情
function displayProductDetail(product) {
    // 更新頁面標題
    document.title = `${product.name} - PikaBaby`;

    // 基本資訊
    document.getElementById('product-name').textContent = product.name;

    // 處理價格顯示，確保有有效的價格值
    let displayPrice = product.price;
    if (!displayPrice || displayPrice <= 0) {
        displayPrice = 100; // 預設價格
        console.warn('商品價格無效，使用預設價格:', displayPrice);
    }
    document.getElementById('product-price').textContent = Math.floor(displayPrice);

    document.getElementById('product-id').textContent = product.id;

    console.log('商品詳情顯示 - 商品ID:', product.id, '價格:', displayPrice);

    // 庫存狀態
    updateStockDisplay(product.stock);

    // 規格資訊
    if (product.specification) {
        document.getElementById('product-specification').textContent = product.specification;
        document.getElementById('spec-specification').style.display = 'flex';
    }

    if (product.color) {
        document.getElementById('product-color').textContent = product.color;
        document.getElementById('spec-color').style.display = 'flex';
    }


    // 商品備註
    if (product.note && product.note.trim()) {
        document.getElementById('product-note').textContent = product.note;
        document.getElementById('note-section').style.display = 'block';
    }

    // 商品圖片
    setupProductImages(product);

    hideLoading();
}

// 設置商品圖片
function setupProductImages(product) {
    const mainImage = document.getElementById('main-image');
    const thumbnailContainer = document.getElementById('thumbnail-container');

    // 處理圖片URL
    let imageUrls = product.allImageUrls || [];
    if (imageUrls.length === 0) {
        imageUrls = [product.primaryImageUrl || '../images/baby.jpg'];
    }

    // 轉換相對路徑為完整URL
    imageUrls = imageUrls.map(url => {
        if (url.startsWith('/products/front/images/')) {
            return hostname + url;
        } else if (url.startsWith('../')) {
            return url;
        } else {
            return '../images/baby.jpg';
        }
    });

    // 設置主圖片
    mainImage.src = imageUrls[0];
    mainImage.onerror = function () {
        this.src = '../images/baby.jpg';
    };

    // 生成縮圖
    if (imageUrls.length > 1) {
        thumbnailContainer.innerHTML = '';
        imageUrls.forEach((url, index) => {
            const thumbnail = document.createElement('img');
            thumbnail.src = url;
            thumbnail.className = 'thumbnail' + (index === 0 ? ' active' : '');
            thumbnail.onclick = () => switchMainImage(url, thumbnail);
            thumbnail.onerror = function () {
                this.src = '../images/baby.jpg';
            };
            thumbnailContainer.appendChild(thumbnail);
        });
    } else {
        thumbnailContainer.style.display = 'none';
    }
}

// 切換主圖片
function switchMainImage(newSrc, thumbnailElement) {
    document.getElementById('main-image').src = newSrc;

    // 更新縮圖的active狀態
    document.querySelectorAll('.thumbnail').forEach(thumb => {
        thumb.classList.remove('active');
    });
    thumbnailElement.classList.add('active');
}

// 更新庫存顯示
function updateStockDisplay(stock) {
    const stockSection = document.getElementById('stock-section');
    const stockStatus = document.getElementById('stock-status');
    const addToCartBtn = document.getElementById('add-to-cart-btn');
    const buyNowBtn = document.getElementById('buy-now-btn');

    if (stock === null || stock === undefined) {
        stockStatus.textContent = '庫存資訊不明';
        stockSection.className = 'stock-info';
        // 恢復按鈕狀態
        addToCartBtn.disabled = false;
        buyNowBtn.disabled = false;
        if (addToCartBtn.textContent === '暫時缺貨' || addToCartBtn.textContent === '庫存不足') {
            addToCartBtn.textContent = '加入購物車';
        }
    } else if (stock <= 0) {
        stockStatus.textContent = '庫存不足';
        stockSection.className = 'stock-info out-of-stock';
        addToCartBtn.disabled = true;
        buyNowBtn.disabled = true;
        addToCartBtn.textContent = '庫存不足';
    } else if (stock <= 5) {
        stockStatus.textContent = `剩餘 ${stock} 件`;
        stockSection.className = 'stock-info low-stock';
        document.getElementById('quantity-input').max = stock;
        // 恢復按鈕狀態
        addToCartBtn.disabled = false;
        buyNowBtn.disabled = false;
        if (addToCartBtn.textContent === '暫時缺貨' || addToCartBtn.textContent === '庫存不足') {
            addToCartBtn.textContent = '加入購物車';
        }
    } else {
        stockStatus.textContent = '現貨供應';
        stockSection.className = 'stock-info';
        document.getElementById('quantity-input').max = Math.min(stock, 99);
        // 恢復按鈕狀態
        addToCartBtn.disabled = false;
        buyNowBtn.disabled = false;
        if (addToCartBtn.textContent === '暫時缺貨' || addToCartBtn.textContent === '庫存不足') {
            addToCartBtn.textContent = '加入購物車';
        }
    }
}

// 數量控制
function changeQuantity(delta) {
    const input = document.getElementById('quantity-input');
    const newValue = parseInt(input.value) + delta;
    const maxStock = parseInt(input.max) || 99;

    if (newValue >= 1 && newValue <= maxStock) {
        input.value = newValue;
        currentQuantity = newValue;
    }
}

// 自訂彈窗控制函數
function showCustomAlert(message = '商品已成功加入購物車！', type = 'success', title = "") {
    const alertOverlay = document.getElementById('customAlert');
    const alertMessage = document.getElementById('customAlertMessage');
    const alertIcon = alertOverlay.querySelector('.custom-alert-icon');
    const alertTitle = alertOverlay.querySelector('.custom-alert-title');

    // 根據類型設置不同的圖示、標題和顏色
    switch (type) {
        case 'success':
            alertIcon.textContent = '🛒';
            if (title != "") {
                alertTitle.textContent = title;
            }
            else {
                alertTitle.textContent = '已加入購物車';
            }
            alertIcon.style.background = 'linear-gradient(145deg, #FFE135 0%, #FFD700 100%)';
            alertIcon.style.color = '#b87c2a';
            break;
        case 'warning':
            alertIcon.textContent = '⚠️';
            alertTitle.textContent = '庫存不足';
            alertIcon.style.background = 'linear-gradient(145deg, #FFC107 0%, #FF9800 100%)';
            alertIcon.style.color = '#fff';
            break;
        case 'error':
            alertIcon.textContent = '❌';
            alertTitle.textContent = '操作失敗';
            alertIcon.style.background = 'linear-gradient(145deg, #DC3545 0%, #C82333 100%)';
            alertIcon.style.color = '#fff';
            break;
        default:
            alertIcon.textContent = '🛒';
            if (title != "") {
                alertTitle.textContent = title;
            }
            else {
                alertTitle.textContent = '已加入購物車';
            }
            alertIcon.style.background = 'linear-gradient(145deg, #FFE135 0%, #FFD700 100%)';
            alertIcon.style.color = '#b87c2a';
    }

    alertMessage.textContent = message;
    alertOverlay.style.display = 'flex';

    // 觸發動畫
    setTimeout(() => {
        alertOverlay.classList.add('show');
    }, 10);
}

function closeCustomAlert() {
    const alertOverlay = document.getElementById('customAlert');
    alertOverlay.classList.remove('show');

    // 等待動畫完成後隱藏
    setTimeout(() => {
        alertOverlay.style.display = 'none';
    }, 300);
}

// 立即購買
function buyNow() {
    if (!currentProduct) {
        showCustomAlert('商品資訊載入中，請稍候', 'warning');
        return;
    }

    // 先加入購物車
    addToCart();

    // 延遲跳轉到購物車頁面
    setTimeout(() => {
        window.location.href = '../Shopping/shoppingcart.html';
    }, 1000);
}

// 切換願望清單
function toggleWishlist(button) {
    button.classList.toggle('active');
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (button.classList.contains('active')) {
        // 加入收藏
        fetch(hostname + '/customers/front/favorites', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ productId: parseInt(productId) })
        })
            .then(res => {
                if (!res.ok) throw new Error('請先登入或稍後再試');
                return res.text();
            })
            .catch(err => {
                // 失敗還原
                btn.classList.remove('active');
                // showMessage(err.message || '加入收藏失敗', 'error');
            });
        button.innerHTML = '♥';
        showCustomAlert('已加入願望清單', 'success', '已加入願望清單');
    } else {
        // 取消收藏
        fetch(`${hostname}/customers/front/favorites/${productId}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(res => {
                if (!res.ok) throw new Error('請先登入或稍後再試');
                return res.text();
            })
            .catch(err => {
                // 失敗還原
                btn.classList.add('active');
                // showMessage(err.message || '移除收藏失敗', 'error');
            });
        button.innerHTML = '♡';
        showCustomAlert('已從願望清單移除', 'info', '已從願望清單移除');
    }
}

// 更新購物車數量
function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    const cartCount = document.querySelector('.cart-count');

    if (cartCount) {
        cartCount.textContent = count;
        cartCount.style.display = count > 0 ? 'inline' : 'none';
    }
}

// 顯示載入狀態
function showLoading() {
    document.getElementById('loading-container').style.display = 'block';
    document.getElementById('error-container').style.display = 'none';
    document.getElementById('product-detail').style.display = 'none';
}

// 隱藏載入狀態
function hideLoading() {
    document.getElementById('loading-container').style.display = 'none';
    document.getElementById('product-detail').style.display = 'grid';
}

// 顯示錯誤
function showError(message) {
    document.getElementById('loading-container').style.display = 'none';
    document.getElementById('product-detail').style.display = 'none';
    document.getElementById('error-container').style.display = 'block';
    document.getElementById('error-message').textContent = message;
}

// 返回商品列表
function goBack() {
    if (window.history.length > 1) {
        window.history.back();
    } else {
        window.location.href = 'product.html';
    }
}
