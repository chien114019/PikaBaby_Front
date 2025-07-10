// 全局變數
let allProducts = []; // 儲存所有商品資料
let isUsingDynamicData = false; // 標記是否使用動態資料

// 載入商品分類
async function loadProductTypes(pageCategory) {
    try {
        console.log("📡 載入商品分類...");
        const response = await fetch(`${hostname}/products/front/product-types`);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const productTypes = await response.json();
        console.log("✅ 成功載入商品分類:", productTypes);

        // 動態填充分類選項
        const categorySelect = document.getElementById('category-select');

        // 清空現有選項（保留"全部商品"）
        // const allOption = categorySelect.querySelector('option[value=""]');
        // categorySelect.innerHTML = '';
        // categorySelect.appendChild(allOption);

        // 添加動態分類選項
        const allOption = document.createElement('option');
        allOption.value = "";
        allOption.textContent = "全部商品";
        categorySelect.appendChild(allOption);
        productTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type.id;
            if (option.value == pageCategory) {
                // 設為selected
                option.setAttribute("selected", "true");
            }
            option.textContent = type.typeName;
            categorySelect.appendChild(option);
        });
    } catch (error) {
        console.error("❌ 載入商品分類失敗:", error);
        // 如果載入失敗，保持原有的固定分類選項
    }
}

// 從資料庫載入商品
async function loadProductsFromDatabase() {
    try {
        showLoadingIndicator();

        console.log("📡 正在呼叫後端API載入已發布商品...");
        const response = await fetch(`${hostname}/products/front/published`);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const products = await response.json();
        console.log("✅ 成功載入已發布商品資料:", products);

        if (products && products.length > 0) {
            // console.log(`📦 API返回 ${products.length} 個商品:`, products);

            // 獲取當前頁面上已存在的商品ID
            const container = document.getElementById('dynamic-products-grid');
            const existingCards = container.querySelectorAll('.product-card[data-product-id]');
            const existingIds = Array.from(existingCards).map(card => parseInt(card.dataset.productId));

            // console.log(`🔍 頁面現有商品ID: [${existingIds.join(', ')}]`);

            // 檢查是否有新商品（不在頁面上的商品）
            const newProducts = products.filter(p => !existingIds.includes(p.id));

            if (existingIds.length === 0) {
                // 頁面上沒有商品，直接顯示所有商品
                // console.log(`🎉 首次載入 ${products.length} 個商品`);
                allProducts = [...products];
                renderDynamicProducts(products);
            } else if (newProducts.length > 0) {
                // 有新商品，增量添加
                // console.log(`🆕 發現 ${newProducts.length} 個新商品，增量添加:`, newProducts);
                allProducts = [...allProducts, ...newProducts];
                renderDynamicProducts(newProducts);
            } else {
                // console.log("📋 沒有新商品，保持現有商品不變");
            }

            isUsingDynamicData = true;
            hideLoadingIndicator();

            // 商品載入完成後，處理待處理的分類篩選
            setTimeout(() => {
                console.log($("#category-select").val())
                const category = $("#category-select").val()
                filterProducts(category);
                // if (window.pendingCategoryFilter) {
                //     console.log(`🎯 商品載入完成，執行待處理的分類篩選: ${window.pendingCategoryFilter}`);
                // filterProducts(window.pendingCategoryFilter);
                //     window.pendingCategoryFilter = null; // 清除待處理的篩選
                // } else {
                //     // 如果沒有待處理的篩選，檢查URL參數
                //     const urlParams = new URLSearchParams(window.location.search);
                //     const categoryParam = urlParams.get('category');
                //     if (categoryParam) {
                //         console.log(`🎯 商品載入完成，處理URL分類參數: ${categoryParam}`);
                //         filterProducts(categoryParam);
                //     }
                // }
            }, 300);
        } else {
            console.log("⚠️ 資料庫中沒有已發布的商品");
            showErrorIndicator("資料庫中沒有已發布的商品");
        }

    } catch (error) {
        console.error("❌ 載入商品失敗:", error);

        // 更詳細的錯誤訊息
        let errorMessage = "載入失敗: ";
        if (error.message.includes("Unexpected token '<'")) {
            errorMessage += "後端服務器沒有啟動，請確認 Spring Boot 應用程序正在運行於" + hostname;
        } else if (error.message.includes("Failed to fetch")) {
            errorMessage += "無法連接到後端服務器，請檢查網路連線和服務器狀態";
        } else {
            errorMessage += error.message;
        }

        showErrorIndicator(errorMessage);
    }
}

// 顯示載入指示器
function showLoadingIndicator() {
    document.getElementById('loading-indicator').style.display = 'block';
    document.getElementById('error-indicator').style.display = 'none';
    document.getElementById('dynamic-products-grid').style.display = 'none';
}

// 隱藏載入指示器
function hideLoadingIndicator() {
    document.getElementById('loading-indicator').style.display = 'none';
}

// 顯示錯誤指示器
function showErrorIndicator(message) {
    document.getElementById('loading-indicator').style.display = 'none';
    document.getElementById('error-indicator').style.display = 'block';
    document.getElementById('error-indicator').querySelector('p').textContent = message;
}

// 渲染動態商品 - 增量添加模式
function renderDynamicProducts(products) {
    console.log(`🎯 開始渲染 ${products.length} 個商品`);

    const container = document.getElementById('dynamic-products-grid');

    // 確保容器可見
    container.style.display = 'grid';

    // 獲取現有商品的 ID
    const existingCards = container.querySelectorAll('.product-card[data-product-id]');
    const existingProductIds = Array.from(existingCards)
        .map(card => card.dataset.productId)
        .filter(id => id);

    // console.log(`🔍 容器中現有商品數量: ${existingProductIds.length}`);
    // console.log(`🔍 容器中現有商品ID: [${existingProductIds.join(', ')}]`);
    // console.log(`🔍 準備處理的商品:`, products);

    // 只添加新商品（避免重複）
    let newProductsCount = 0;
    let skippedCount = 0;

    products.forEach((product, index) => {
        const productIdStr = product.id.toString();
        // console.log(`🔍 檢查商品 ${product.name} (ID: ${product.id})`);

        if (!existingProductIds.includes(productIdStr)) {
            const productCard = createDynamicProductCard(product, index);
            container.appendChild(productCard);
            newProductsCount++;
            // console.log(`➕ 成功新增商品: ${product.name} (ID: ${product.id})`);
        } else {
            skippedCount++;
            // console.log(`⚠️ 跳過重複商品: ${product.name} (ID: ${product.id})`);
        }
    });

    // 重新綁定數量控制事件
    bindQuantityControlEvents();

    // 最終狀態
    const finalCards = container.querySelectorAll('.product-card[data-product-id]');
    console.log(`📦 處理結果: 新增 ${newProductsCount} 個，跳過 ${skippedCount} 個`);
    console.log(`✅ 容器最終狀態: ${finalCards.length} 個商品卡片`);
    console.log(`✅ 最終商品ID列表: [${Array.from(finalCards).map(c => c.dataset.productId).join(', ')}]`);
}

// 根據商品資料獲取分類 (直接使用ProductType名稱)
function getProductCategory(product) {
    // 直接返回 ProductType 的 typeName，如果沒有則返回 'others'
    return product.productTypeId || 'others';
}

// 將資料庫的 ProductType 對應到前端分類 (備用 - 向後兼容)
function mapProductTypeToCategory(productTypeName) {
    // 現在直接返回 ProductType 名稱，不再進行映射
    return productTypeName || 'others';
}

// 根據商品名稱推斷商品分類 (備選方案)
function inferCategoryFromName(productName) {
    if (!productName) return 'others';

    const name = productName.toLowerCase();

    if (name.includes('奶瓶') || name.includes('奶嘴') || name.includes('餐具') || name.includes('奶昔')) {
        return 'feeding';
    } else if (name.includes('服裝') || name.includes('衣服') || name.includes('上衣') || name.includes('褲子') || name.includes('外套')) {
        return 'clothes';
    } else if (name.includes('推車') || name.includes('嬰兒車')) {
        return 'stroller';
    } else if (name.includes('汽座') || name.includes('安全座椅')) {
        return 'car-seat';
    } else if (name.includes('床') || name.includes('餐椅') || name.includes('家具')) {
        return 'furniture';
    } else if (name.includes('玩具') || name.includes('遊戲') || name.includes('積木')) {
        return 'toys';
    } else if (name.includes('清潔') || name.includes('護理') || name.includes('洗澡') || name.includes('洗髮')) {
        return 'care';
    } else if (name.includes('學步') || name.includes('爬行')) {
        return 'walking';
    } else if (name.includes('教育') || name.includes('學習') || name.includes('書籍')) {
        return 'education';
    } else if (name.includes('電器') || name.includes('機器')) {
        return 'electronics';
    } else {
        return 'others';
    }
}

// 創建動態商品卡片
function createDynamicProductCard(product, index) {
    const card = document.createElement('div');
    card.className = 'product-card';
    card.dataset.category = getProductCategory(product); // 優先使用ProductType分類
    card.dataset.condition = product.condition || 'new';
    card.dataset.productId = product.id;

    const urlCategory = window.location.href.split("=")[1];
    if (card.dataset.category == urlCategory) {
        card.style.display = "flex"
    }
    else {
        card.style.display = "none"
    }

    // 處理圖片URL - 優先使用product_image表的圖片
    let imageUrl = product.primaryImageUrl || product.imageUrl;
    console.log(imageUrl);

    // 如果是API路徑，轉換為完整URL
    if (imageUrl && imageUrl.startsWith('/products/front/images/')) {
        imageUrl = hostname + imageUrl;
    } else if (!imageUrl || imageUrl.trim() === '' || imageUrl === '/images/default.jpg') {
        // 使用本地預設圖片
        imageUrl = '../images/baby.jpg';
    } else if (imageUrl.startsWith('images/')) {
        // 如果是images/開頭，加上../前綴
        imageUrl = '../' + imageUrl;
    } else if (!imageUrl.startsWith('http') && !imageUrl.startsWith('../')) {
        // 其他情況使用預設圖片
        imageUrl = '../images/baby.jpg';
    }

    // 處理價格顯示 - 現在從後端API動態獲取
    const price = product.price || 100; // 如果沒有價格則使用預設值
    const formattedPrice = typeof price === 'number' ? price.toFixed(0) : price;

    // 處理商品名稱，確保不會太長
    const productName = product.name && product.name.length > 30
        ? product.name.substring(0, 30) + '...'
        : product.name || '未命名商品';

    card.innerHTML = `
        <button class="wishlist-btn" onclick="toggleWishlist(this)"></button>
        <a href="#" class="product-link" onclick="viewProductDetail(${product.id})">
            <img src="${imageUrl}" alt="${productName}" class="product-image" 
                 onerror="this.src='../images/baby.jpg'; this.onerror=null;"
                 loading="lazy">
            <h3 class="product-title" title="${product.name || '未命名商品'}">${productName}</h3>
            <p class="product-price">售價：${formattedPrice}元</p>
        </a>

        <div class="quantity-control">
            <button class="quantity-btn minus" type="button" onclick="changeQuantity(this, -1)">-</button>
            <div class="quantity-number">
                <input type="number" class="quantity-input" value="1" min="1" max="99" readonly onchange="validateQuantity(this)">
            </div>
            <button class="quantity-btn plus" type="button" onclick="changeQuantity(this, 1)">+</button>
        </div>
        <button class="add-to-cart-btn" 
            data-product-id="${product.id}" 
            data-product-name="${product.name || '未命名商品'}"
            data-product-price="${formattedPrice}"
            data-product-image="${imageUrl}"
            onclick="addToCart(this)">加入購物車</button>
    `;

    // 添加過渡效果
    card.style.transition = 'opacity 0.3s ease, transform 0.3s ease';

    return card;
}

// 重試載入商品
function retryLoadProducts() {
    console.log("🔄 重新載入商品...");
    loadProductsFromDatabase();
}

// 簡單的重新載入商品功能
function refreshProducts() {
    console.log("🔄 重新載入商品...");
    loadProductsFromDatabase();
}



// 數量控制
function changeQuantity(button, delta) {
    const input = button.parentNode.querySelector('.quantity-input');
    let currentValue = parseInt(input.value) || 1;
    const newValue = Math.max(1, Math.min(99, currentValue + delta));
    input.value = newValue;
}

// 驗證數量輸入
function validateQuantity(input) {
    let value = parseInt(input.value) || 1;
    value = Math.max(1, Math.min(99, value));
    input.value = value;
}

// 綁定數量控制事件
function bindQuantityControlEvents() {
    // 為所有數量按鈕綁定事件（如果還沒有onclick）
    document.querySelectorAll('.quantity-btn.minus').forEach(btn => {
        if (!btn.onclick) {
            btn.onclick = () => changeQuantity(btn, -1);
        }
    });

    document.querySelectorAll('.quantity-btn.plus').forEach(btn => {
        if (!btn.onclick) {
            btn.onclick = () => changeQuantity(btn, 1);
        }
    });

    document.querySelectorAll('.quantity-input').forEach(input => {
        if (!input.onchange) {
            input.onchange = () => validateQuantity(input);
        }
    });
}

// 查看商品詳情
function viewProductDetail(productId) {
    console.log(`🔍 查看商品詳情: ${productId}`);
    // 跳轉到商品詳情頁面
    window.location.href = `product-detail.html?id=${productId}`;
}

// 通用訊息顯示函數
function showMessage(message, type = 'info') {
    // 創建臨時提示元素
    const toast = document.createElement('div');

    let backgroundColor = '#17a2b8'; // info
    if (type === 'success') backgroundColor = '#28a745';
    if (type === 'error') backgroundColor = '#dc3545';
    if (type === 'warning') backgroundColor = '#ffc107';

    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${backgroundColor};
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 9999;
        font-weight: 500;
        max-width: 300px;
        word-wrap: break-word;
    `;
    toast.textContent = message;

    document.body.appendChild(toast);

    // 3秒後移除提示
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 3000);
}

// 自訂彈窗控制函數
function showCustomAlert(message = '商品已成功加入購物車！', type = 'success') {
    const alertOverlay = document.getElementById('customAlert');
    const alertMessage = document.getElementById('customAlertMessage');
    const alertIcon = alertOverlay.querySelector('.custom-alert-icon');
    const alertTitle = alertOverlay.querySelector('.custom-alert-title');

    // 根據類型設置不同的圖示、標題和顏色
    switch (type) {
        case 'success':
            alertIcon.textContent = '🛒';
            alertTitle.textContent = '已加入購物車';
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
            alertTitle.textContent = '已加入購物車';
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

// 收藏/取消收藏功能
function toggleWishlist(btn) {
    const card = btn.closest('.product-card');
    const productId = card?.dataset.productId || card?.querySelector('[data-product-id]')?.getAttribute('data-product-id');
    if (!productId) {
        // alert('找不到商品ID，無法收藏');
        return;
    }
    if (!btn.classList.contains('active')) {
        btn.classList.add('active');
        // 加入收藏
        fetch(`${hostname}/customers/front/favorites`, {
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
    } else {
        // 立即切換為灰色
        btn.classList.remove('active');
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
    }
}

// 加入購物車功能
function addToCart(button) {
    console.log("🛒 開始加入購物車...");

    const productId = button.getAttribute('data-product-id');
    const productName = button.getAttribute('data-product-name');
    const productPrice = parseFloat(button.getAttribute('data-product-price'));
    const productImage = button.getAttribute('data-product-image');

    console.log("🔍 商品資訊:", { productId, productName, productPrice, productImage });

    // 檢查必要資料
    if (!productId || !productName || isNaN(productPrice)) {
        console.error("❌ 商品資料不完整:", { productId, productName, productPrice });
        // showMessage('商品資料錯誤，無法加入購物車', 'error');
        return;
    }

    // 獲取數量 - 修正查找邏輯
    const productCard = button.closest('.product-card');
    const quantityInput = productCard?.querySelector('.quantity-input');
    const quantity = parseInt(quantityInput?.value) || 1;

    console.log("🔍 數量輸入框查找:", { productCard: !!productCard, quantityInput: !!quantityInput, quantity });

    console.log("📦 準備加入購物車的商品:", { productId, productName, productPrice, quantity });

    // 禁用按鈕，防止重複點擊
    button.disabled = true;
    const originalText = button.textContent;
    button.textContent = '請稍候...';

    // 檢查庫存
    checkStockAndAddToCart(productId, productName, productPrice, productImage, quantity, quantityInput, button, originalText);
}

// 檢查庫存並加入購物車
async function checkStockAndAddToCart(productId, productName, productPrice, productImage, quantity, quantityInput, button, originalText) {
    try {
        // 檢查購物車中是否已有此商品
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        const existingIndex = cart.findIndex(item => item.id === parseInt(productId));
        let totalQuantityInCart = quantity;

        if (existingIndex > -1) {
            totalQuantityInCart = cart[existingIndex].quantity + quantity;
        }

        // 從後端檢查庫存
        const response = await fetch(`${hostname}/products/api/stock/${productId}`);

        if (response.ok) {
            const stockData = await response.json();
            const availableStock = stockData.stock || 0;

            console.log(`📦 商品 ${productName} 庫存檢查: 可用庫存=${availableStock}, 要加入數量=${quantity}, 購物車已有=${existingIndex > -1 ? cart[existingIndex].quantity : 0}`);

            // 檢查庫存是否足夠
            if (availableStock <= 0) {
                // 完全沒庫存
                showCustomAlert(`很抱歉，「${productName}」目前已賣完！`, 'warning');
                return;
            } else if (totalQuantityInCart > availableStock) {
                // 庫存不足
                const remainingStock = Math.max(0, availableStock - (existingIndex > -1 ? cart[existingIndex].quantity : 0));
                if (remainingStock <= 0) {
                    showCustomAlert(`非常抱歉！！「${productName}」商品已經賣光了！`, 'warning');
                } else {
                    showCustomAlert(`庫存不足！「${productName}」店裡庫存僅剩 ${availableStock} 件，還可以加入 ${remainingStock} 件`, 'warning');
                }
                return;
            }
        } else {
            console.warn('無法檢查庫存，允許加入購物車');
        }

        // 庫存充足，執行加入購物車
        button.textContent = '加入中...';
        await performAddToCart(productId, productName, productPrice, productImage, quantity, quantityInput, cart, existingIndex);

    } catch (error) {
        console.error('❌ 庫存檢查失敗:', error);
        console.warn('庫存檢查失敗，允許加入購物車');
        // 如果庫存檢查失敗，仍然允許加入購物車
        button.textContent = '加入中...';
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        const existingIndex = cart.findIndex(item => item.id === parseInt(productId));
        await performAddToCart(productId, productName, productPrice, productImage, quantity, quantityInput, cart, existingIndex);
    } finally {
        // 恢復按鈕狀態
        button.disabled = false;
        button.textContent = originalText;
    }
}

// 執行加入購物車操作
async function performAddToCart(productId, productName, productPrice, productImage, quantity, quantityInput, cart, existingIndex) {
    try {
        // 創建商品物件
        const product = {
            id: parseInt(productId),
            name: productName,
            price: productPrice,
            quantity: quantity,
            image: productImage || '../images/baby.jpg'
        };

        if (existingIndex > -1) {
            // 商品已存在，增加數量
            cart[existingIndex].quantity += quantity;
            console.log(`➕ 增加商品數量: ${productName} x${quantity} (總計: ${cart[existingIndex].quantity})`);
        } else {
            // 新商品，直接加入
            cart.push(product);
            console.log(`🛒 新增商品到購物車: ${productName} x${quantity}`);
        }

        // 儲存到 localStorage
        localStorage.setItem('cart', JSON.stringify(cart));
        console.log("💾 購物車已儲存到 localStorage");

        // 顯示自訂彈窗
        showCustomAlert(`已將 ${productName} 加入購物車！`);

        // 重置數量為1
        if (quantityInput) {
            quantityInput.value = 1;
        }

        // 更新購物車數量顯示
        updateCartCount();

    } catch (error) {
        console.error('❌ 加入購物車失敗:', error);
        showCustomAlert('加入購物車失敗，請稍後再試', 'error');
    }
}

// 購物車功能
function updateCartCount() {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    let count = cart.reduce((sum, item) => sum + item.quantity, 0);
    const cartCount = document.querySelector('.cart-count');
    if (cartCount) {
        cartCount.textContent = count;
        cartCount.style.display = count > 0 ? 'flex' : 'none';
    }
}

// 排序函數
function sortProducts(products, sortBy) {
    const productsArray = Array.from(products);

    switch (sortBy) {
        case 'price-low-high':
            return productsArray.sort((a, b) => {
                const priceA = parseInt(a.querySelector('.product-price').textContent.match(/\d+/)[0]);
                const priceB = parseInt(b.querySelector('.product-price').textContent.match(/\d+/)[0]);
                return priceA - priceB;
            });
        case 'price-high-low':
            return productsArray.sort((a, b) => {
                const priceA = parseInt(a.querySelector('.product-price').textContent.match(/\d+/)[0]);
                const priceB = parseInt(b.querySelector('.product-price').textContent.match(/\d+/)[0]);
                return priceB - priceA;
            });
        case 'newest':
            return productsArray; // 保持原有順序，假設是按最新排序
        case 'hot':
            return productsArray.sort((a, b) => {
                const isHotA = a.querySelector('.hot-tag') !== null;
                const isHotB = b.querySelector('.hot-tag') !== null;
                return isHotB - isHotA;
            });
        case 'brand':
            return productsArray.sort((a, b) => {
                const titleA = a.querySelector('.product-title').textContent;
                const titleB = b.querySelector('.product-title').textContent;
                return titleA.localeCompare(titleB, 'zh-TW');
            });
        case 'category':
            return productsArray.sort((a, b) => {
                const categoryA = a.dataset.category || '未分類';
                const categoryB = b.dataset.category || '未分類';

                // 先按分類名稱排序
                const categoryCompare = categoryA.localeCompare(categoryB, 'zh-TW');
                if (categoryCompare !== 0) {
                    return categoryCompare;
                }

                // 如果分類相同，再按商品名稱排序
                const titleA = a.querySelector('.product-title').textContent;
                const titleB = b.querySelector('.product-title').textContent;
                return titleA.localeCompare(titleB, 'zh-TW');
            });
        default:
            return productsArray;
    }
}

// 整合的篩選和排序函數
function filterAndSortProducts() {
    // 動態獲取當前所有商品
    const products = document.querySelectorAll('.product-card');

    products.forEach(product => {
        console.log("product dataset:" + product.dataset.category);
        const productCategory = product.dataset.category;
        const productCondition = product.dataset.condition;
        const productTitle = product.querySelector('.product-title').textContent.toLowerCase();

        // 檢查分類匹配 - 支持直接的ProductType名稱匹配和舊式分類映射
        let matchesCategory = !currentFilters.category;
        if (currentFilters.category) {
            // 直接匹配ProductType名稱
            matchesCategory = productCategory === currentFilters.category;

            // 如果沒有直接匹配，嘗試舊式分類映射（向後兼容）
            if (!matchesCategory) {
                matchesCategory = checkLegacyCategoryMatch(productCategory, currentFilters.category);
            }
        }

        const matchesCondition = !currentFilters.condition || productCondition === currentFilters.condition;
        const matchesSearch = !currentFilters.search || productTitle.includes(currentFilters.search.toLowerCase());

        if (matchesCategory && matchesCondition && matchesSearch) {
            product.style.display = '';
            product.style.opacity = '1';
            product.style.transform = 'translateY(0)';
        } else {
            product.style.display = 'none';
            product.style.opacity = '0';
            product.style.transform = 'translateY(20px)';
        }
    });

    // 排序可見的商品
    const visibleProducts = Array.from(products).filter(p => p.style.display !== 'none');
    console.log("visibleProducts: " + visibleProducts)
    if (visibleProducts.length == 0) {
        document.getElementById("null-indicator").style.display = "block";
    }
    else {
        document.getElementById("null-indicator").style.display = "none";
        const sortedProducts = sortProducts(visibleProducts, currentFilters.sort);

        // 重新排列DOM
        const productsGrid = document.getElementById('dynamic-products-grid');
        sortedProducts.forEach(product => {
            productsGrid.appendChild(product);
        });
    }

    // 更新選擇器的狀態
    if (categorySelect && currentFilters.category) {
        categorySelect.value = currentFilters.category;
    }
    if (conditionSelect && currentFilters.condition) {
        conditionSelect.value = currentFilters.condition;
    }
    if (sortSelect && currentFilters.sort) {
        sortSelect.value = currentFilters.sort;
    }
}

// 檢查舊式分類映射（向後兼容）
function checkLegacyCategoryMatch(productCategory, filterCategory) {
    const legacyMapping = {
        'feeding': ['嬰兒用品'], // 奶瓶/奶嘴、哺乳用品
        'clothes': ['嬰兒服裝', '幼兒服裝', '兒童服裝'],
        'stroller': ['嬰兒推車'],
        'furniture': ['嬰兒床', '餐椅'],
        'care': ['嬰兒用品'],
        'toys': ['玩具'],
        'car-seat': ['汽座'],
        'walking': ['學步用品'],
        'dining': ['嬰兒用品'], // 餐具用品
        'education': ['幼教用品'],
        'appliance': ['電器用品']
    };

    return legacyMapping[filterCategory] && legacyMapping[filterCategory].includes(productCategory);
}

// 將舊式分類代碼轉換為對應的ProductType名稱
function mapLegacyCategoryToProductType(legacyCategory) {
    const categoryMapping = {
        'feeding': '嬰兒用品',
        'clothes': ['嬰兒服裝', '幼兒服裝', '兒童服裝'], // 多對應，需要特殊處理
        'stroller': '嬰兒推車',
        'furniture': ['嬰兒床', '餐椅'], // 多對應
        'care': '嬰兒用品',
        'toys': '玩具',
        'car-seat': '汽座',
        'walking': '學步用品',
        'dining': '嬰兒用品',
        'education': '幼教用品',
        'appliance': '電器用品'
    };

    const mapped = categoryMapping[legacyCategory];

    // 如果是數組，返回第一個作為主要分類
    if (Array.isArray(mapped)) {
        return mapped[0];
    }

    return mapped || legacyCategory;
}

// 顯示通知
function showNotification(message) {
    const notification = document.querySelector('.cart-notification');
    notification.textContent = message;
    notification.style.display = 'block';

    setTimeout(() => {
        notification.style.display = 'none';
    }, 2000);
}