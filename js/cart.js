let hostname = "https://pikababy-back.onrender.com";

// 會員資料全域變數
let memberData = null;
let memberPoints = 0;

let stockMap = {};
let cartItems = [];

// 檢查會員登入狀態並進行結帳
async function checkLoginAndProceed() {
    try {
        // 檢查會員登入狀態
        const response = await fetch(`${hostname}/customers/check-login`, {
            method: 'GET',
            credentials: 'include' // 包含 Session cookie
        });

        if (response.ok) {
            const loginStatus = await response.json();

            if (loginStatus.isLoggedIn) {
                // 已登入，直接進入結帳流程
                console.log('✅ 會員已登入，進入結帳流程');
                await showCheckoutForm();
            } else {
                // 未登入，提示登入
                showLoginPrompt();
            }
        } else {
            // API錯誤，暫時允許結帳（向下兼容）
            console.warn('⚠️ 無法檢查登入狀態，允許結帳');
            await showCheckoutForm();
        }
    } catch (error) {
        console.error('❌ 檢查登入狀態失敗:', error);
        // 網路錯誤，暫時允許結帳
        await showCheckoutForm();
    }
}

// 顯示登入提示
function showLoginPrompt() {
    const loginConfirm = confirm(
        '請先登入會員才能進行結帳！\n\n' +
        '點擊「確定」前往登入頁面\n' +
        '點擊「取消」繼續瀏覽商品'
    );

    if (loginConfirm) {
        // 儲存當前頁面到登入後返回
        sessionStorage.setItem('returnToCheckout', 'true');
        window.location.href = '../Member/login.html';
    }
}

// 從 localStorage 加載購物車數據並同步商品資訊
async function loadCart() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartContainer = document.getElementById('cart-items');
    cartContainer.innerHTML = '';
    let total = 0;
    // 查詢所有商品庫存
    stockMap = {};
    await Promise.all(cart.map(async (item) => {
        try {
            const response = await fetch(`${hostname}/products/api/stock/${item.id}`);
            if (response.ok) {
                const stockData = await response.json();
                stockMap[item.id] = stockData.stock || 0;
            } else {
                stockMap[item.id] = 99; // 查詢失敗給大值
            }
        } catch {
            stockMap[item.id] = 99;
        }
    }));
    cart.forEach((item, idx) => {
        const imgSrc = item.image && item.image.trim() !== '' ? item.image : 'https://via.placeholder.com/80x80?text=No+Image';
        const disableMinus = item.quantity <= 1 ? 'disabled' : '';
        const disablePlus = item.quantity >= stockMap[item.id] ? 'disabled' : '';
        const row = document.createElement('tr');
        row.innerHTML = `
                    <td><img src="${imgSrc}" alt="${item.name}" style="width:100px;height:100px;object-fit:cover;border-radius:8px;" onerror="this.src='../images/baby.jpg';"></td>
                    <td>${item.name}</td>
                    <td>NT$${item.price}</td>
                    <td>
                        <div style="display:flex;align-items:center;justify-content:center;gap:4px;">
                            <button type="button" onclick="updateQuantity(${idx}, -1)" class="btn btn-sm btn-outline-secondary" ${disableMinus}>-</button>
                            <span id="qty-display-${idx}" style="display:inline-block;width:60px;text-align:center;line-height:32px;">${item.quantity}</span>
                            <button id="plus-btn-${idx}" type="button" onclick="updateQuantity(${idx}, 1)" class="btn btn-sm btn-outline-secondary" ${disablePlus}>+</button>
                        </div>
                        <div style='font-size:0.9em;color:#b8860b;margin-top:2px;'>庫存僅剩${stockMap[item.id]}件</div>
                    </td>
                    <td>NT$${item.price * item.quantity}</td>
                    <td><button onclick="removeItem(${idx})"  class="btn btn-danger">刪除</button></td>
                `;
        cartContainer.appendChild(row);
        total += item.price * item.quantity;
        updatePlusMinusBtnState(row, item.quantity, stockMap[item.id]);
    });
    document.getElementById('cart-total').textContent = `總計：NT$ ${total}`;
}

// 同步購物車商品資訊與資料庫
async function syncCartWithDatabase(cart) {
    const updatedCart = [];

    for (const item of cart) {
        try {
            // 從後端獲取最新的商品資訊
            const response = await fetch(`${hostname}/products/front/detail/${item.id}`);

            if (response.ok) {
                const productData = await response.json();
                if (productData.success) {
                    // 更新商品資訊，保留購物車中的數量
                    const updatedItem = {
                        id: item.id,
                        name: productData.name,
                        price: productData.price,
                        quantity: item.quantity,
                        image: productData.primaryImageUrl ?
                            (productData.primaryImageUrl.startsWith('/products/front/images/') ?
                                hostname + productData.primaryImageUrl :
                                productData.primaryImageUrl) :
                            '../images/baby.jpg'
                    };
                    updatedCart.push(updatedItem);
                    console.log(`✅ 已更新商品資訊: ${productData.name}`);
                } else {
                    console.warn(`⚠️ 商品 ${item.name} (ID: ${item.id}) 可能已下架或庫存不足，保留原資訊`);
                    updatedCart.push(item);
                }
            } else {
                console.warn(`⚠️ 無法獲取商品 ${item.name} (ID: ${item.id}) 的最新資訊，保留原資訊`);
                updatedCart.push(item);
            }
        } catch (error) {
            console.error(`❌ 同步商品 ${item.name} (ID: ${item.id}) 時發生錯誤:`, error);
            updatedCart.push(item);
        }
    }

    return updatedCart;
}

function onQuantityInput(idx, value) {
    // 此功能已移除，保留空函式防止錯誤
}

async function removeItem(idx) {
    if (confirm('確定要刪除此商品嗎？')) {
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        cart.splice(idx, 1);
        localStorage.setItem('cart', JSON.stringify(cart));
        await loadCart();
    }
}

function goToCheckout() {
    window.location.href = 'checkout.html';
}

// 顯示結帳表單
async function showCheckoutForm() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    if (cart.length === 0) {
        alert('購物車是空的！');
        return;
    }

    // 渲染訂單項目
    await renderOrderItems(cart);

    // 切換顯示
    document.getElementById('cartContent').style.display = 'none';
    document.getElementById('checkoutForm').style.display = 'block';

    // 初始化會員資料選項
    initializeMemberDataOption();

    // 更新會員點數顯示
    updateMemberPoints();

    // 更新總金額
    updateTotal();
}

// 會員資料自動帶入功能
async function initializeMemberDataOption() {
    try {
        const response = await fetch(`${hostname}/customers/profile`, {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            memberData = await response.json();
            console.log('會員資料載入成功:', memberData);

            // 顯示勾選選項
            const memberDataOption = document.querySelector('.mb-4.p-3');
            if (memberDataOption) {
                memberDataOption.style.display = 'block';
            }
        } else {
            console.log('無法載入會員資料，隱藏選項');
            const memberDataOption = document.querySelector('.mb-4.p-3');
            if (memberDataOption) {
                memberDataOption.style.display = 'none';
            }
        }
    } catch (error) {
        console.error('載入會員資料失敗:', error);
        const memberDataOption = document.querySelector('.mb-4.p-3');
        if (memberDataOption) {
            memberDataOption.style.display = 'none';
        }
    }
}

// 切換會員資料使用狀態
function toggleMemberData() {
    const checkbox = document.getElementById('useMemberData');
    const nameField = document.getElementById('recipientName');
    const phoneField = document.getElementById('recipientPhone');
    const emailField = document.getElementById('recipientEmail');
    const addressField = document.getElementById('recipientAddress');

    if (checkbox.checked && memberData) {
        // 填入會員資料並禁用欄位
        nameField.value = memberData.name || '';
        phoneField.value = memberData.phone || '';
        emailField.value = memberData.email || '';
        addressField.value = memberData.address || '';

        // 設為禁用狀態
        nameField.disabled = true;
        phoneField.disabled = true;
        emailField.disabled = true;
        addressField.disabled = true;

        // 視覺效果
        nameField.style.backgroundColor = '#f8f9fa';
        phoneField.style.backgroundColor = '#f8f9fa';
        emailField.style.backgroundColor = '#f8f9fa';
        addressField.style.backgroundColor = '#f8f9fa';

        console.log('已帶入會員資料並鎖定欄位');
    } else {
        // 清空並啟用欄位
        nameField.value = '';
        phoneField.value = '';
        emailField.value = '';
        addressField.value = '';

        // 恢復可編輯狀態
        nameField.disabled = false;
        phoneField.disabled = false;
        emailField.disabled = false;
        addressField.disabled = false;

        // 恢復原始樣式
        nameField.style.backgroundColor = '';
        phoneField.style.backgroundColor = '';
        emailField.style.backgroundColor = '';
        addressField.style.backgroundColor = '';

        console.log('已清空欄位並解除鎖定');
    }
}

// 更新會員點數顯示
async function updateMemberPoints() {
    try {
        const response = await fetch(`${hostname}/customers/points`, {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            const pointsData = await response.json();
            memberPoints = pointsData.points || 0;

            console.log('✅ 會員點數載入成功:', memberPoints);

            // 更新點數顯示
            const availablePointsSpan = document.getElementById('availablePoints');
            const pointsInput = document.getElementById('points');
            const pointsLabel = document.querySelector('label[for="points"]');

            if (availablePointsSpan) {
                availablePointsSpan.textContent = memberPoints;
            }

            if (pointsInput) {
                pointsInput.max = memberPoints;
                pointsInput.value = '0';

                if (memberPoints > 0) {
                    pointsInput.disabled = false;
                    pointsInput.placeholder = `可使用 0-${memberPoints} 點`;
                } else {
                    pointsInput.disabled = true;
                    pointsInput.placeholder = '無可用點數';
                }
            }

            if (pointsLabel && memberPoints <= 0) {
                pointsLabel.innerHTML = '使用點數 <small class="text-muted">(無可用點數)</small>';
            }

        } else {
            console.log('🔍 無法載入會員點數，可能未登入');
            memberPoints = 0;

            // 設為未登入狀態
            const availablePointsSpan = document.getElementById('availablePoints');
            const pointsInput = document.getElementById('points');
            const pointsLabel = document.querySelector('label[for="points"]');

            if (availablePointsSpan) {
                availablePointsSpan.textContent = '0';
            }

            if (pointsInput) {
                pointsInput.disabled = true;
                pointsInput.value = '0';
                pointsInput.placeholder = '請先登入';
            }

            if (pointsLabel) {
                pointsLabel.innerHTML = '使用點數 <small class="text-muted">(請先登入)</small>';
            }
        }
    } catch (error) {
        console.error('❌ 載入會員點數失敗:', error);
        memberPoints = 0;

        // 設為錯誤狀態
        const availablePointsSpan = document.getElementById('availablePoints');
        const pointsInput = document.getElementById('points');

        if (availablePointsSpan) {
            availablePointsSpan.textContent = '0';
        }

        if (pointsInput) {
            pointsInput.disabled = true;
            pointsInput.value = '0';
            pointsInput.placeholder = '載入失敗';
        }
    }
}

// 渲染結帳明細
async function renderOrderItems(cart) {
    const orderItems = document.getElementById('orderItems');
    orderItems.innerHTML = '';
    let subtotal = 0;
    const updatedCart = await syncCartWithDatabase(cart);
    updatedCart.forEach((item, idx) => {
        const imgSrc = item.image && item.image.trim() !== '' ? item.image : 'https://via.placeholder.com/80x80?text=No+Image';
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><img src="${imgSrc}" alt="${item.name}" style="width:60px;height:60px;object-fit:cover;border-radius:8px;" onerror="this.src='../images/baby.jpg';"></td>
            <td>${item.name}</td>
            <td>NT$${item.price}</td>
            <td>${item.quantity}</td>
            <td>NT$${item.price * item.quantity}</td>
            <td><button onclick="removeOrderItem(${idx})" class="btn btn-danger">刪除</button></td>
        `;
        orderItems.appendChild(row);
        subtotal += item.price * item.quantity;
        updatePlusMinusBtnState(row, item.quantity, stockMap[item.id]);
    });
    localStorage.setItem('cart', JSON.stringify(updatedCart));
    document.getElementById('orderSubtotal').textContent = subtotal;
    document.getElementById('orderTotal').textContent = subtotal;
    document.getElementById('shippingFee').textContent = 0;
    document.getElementById('discount').textContent = 0;
    updateTotal();
}

// 結帳明細刪除
async function removeOrderItem(idx) {
    if (confirm('確定要刪除此商品嗎？')) {
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        cart.splice(idx, 1);
        localStorage.setItem('cart', JSON.stringify(cart));
        await renderOrderItems(cart);
        updateTotal();
    }
}

// 隱藏結帳表單
function hideCheckoutForm() {
    document.getElementById('cartContent').style.display = 'block';
    document.getElementById('checkoutForm').style.display = 'none';
}

// 客戶資訊變更時的處理函數（已登入會員不需要此功能）
function onCustomerInfoChange() {
    // 會員登入後，點數會在進入結帳頁面時自動載入
    // 不需要根據表單輸入重新查詢
    console.log('🔄 客戶資訊變更（會員點數已在登入時載入）');
}

// 點數輸入驗證和更新
function validateAndUpdatePoints() {
    const pointsInput = document.getElementById('points');
    const availablePointsSpan = document.getElementById('availablePoints');
    const pointsError = document.getElementById('pointsError');

    if (!pointsInput || !availablePointsSpan) return;

    const inputPoints = parseInt(pointsInput.value) || 0;
    const availablePoints = parseInt(availablePointsSpan.textContent) || 0;

    // 隱藏錯誤訊息
    if (pointsError) {
        pointsError.style.display = 'none';
    }

    // 驗證點數
    if (inputPoints < 0) {
        pointsInput.value = '0';
        if (pointsError) {
            pointsError.textContent = '點數不能為負數';
            pointsError.style.display = 'block';
        }
    } else if (inputPoints > availablePoints) {
        pointsInput.value = availablePoints.toString();
        if (pointsError) {
            pointsError.textContent = `最多只能使用 ${availablePoints} 點`;
            pointsError.style.display = 'block';
        }
    }

    // 更新總額
    updateTotal();
}

// 提交訂單
async function submitOrder(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const cart = JSON.parse(localStorage.getItem('cart')) || [];

    try {
        // 檢查購物車是否為空
        if (cart.length === 0) {
            throw new Error('購物車是空的！');
        }

        // 獲取使用的點數
        const pointsUsed = parseInt(document.getElementById('points').value) || 0;

        // 直接從DOM元素獲取值（避免禁用欄位問題）
        const name = document.getElementById('recipientName').value.trim();
        const phone = document.getElementById('recipientPhone').value.trim();
        const email = document.getElementById('recipientEmail').value.trim();
        const address = document.getElementById('recipientAddress').value.trim();
        const paymentMethod = formData.get('paymentMethod');

        // 前端驗證
        if (!name || !phone || !email || !address || !paymentMethod) {
            throw new Error('請填寫完整的收件人資訊和付款方式');
        }

        // 準備訂單數據
        const orderData = {
            name: name,
            phone: phone,
            email: email,
            address: address,
            paymentMethod: paymentMethod,
            pointsUsed: pointsUsed,
            totalAmount: parseFloat(document.getElementById('orderTotal').textContent.replace(/[^0-9.-]+/g, '')),
            items: cart.map(item => {
                // 確保每個商品都有必要的欄位
                if (!item.id && item.id !== 0) {
                    console.error('Missing product ID for item:', item);
                    throw new Error(`商品 "${item.name}" 缺少商品ID，請重新添加商品到購物車`);
                }
                return {
                    productId: item.id,
                    name: item.name,
                    quantity: parseInt(item.quantity),
                    price: parseFloat(item.price)
                };
            })
        };

        console.log('📝 訂單數據:', orderData);

        // 如果選擇 LINE Pay，則導向 LINE Pay 流程
        if (paymentMethod === 'linepay') {
            await processLinePayPayment(orderData, cart);
            return;
        }

        if (paymentMethod === 'ecpay') {
            await processEcpayPayment(orderData);
            return;
        }


        // 其他付款方式：發送請求到後端 - 使用原本的API路徑
        const response = await fetch(`${hostname}/orders/api/cart`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderData)
        });

        console.log('🔍 伺服器響應狀態:', response.status);
        console.log('🔍 響應 Content-Type:', response.headers.get('content-type'));

        if (!response.ok) {
            // 檢查是否為JSON響應
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                throw new Error(errorData.message || '訂單建立失敗');
            } else {
                // 如果不是JSON，可能是HTML錯誤頁面
                const errorText = await response.text();
                console.error('伺服器回應非JSON格式:', errorText.substring(0, 200));
                throw new Error(`伺服器錯誤 (${response.status}): API端點可能不存在或配置錯誤`);
            }
        }

        const result = await response.json();

        // 清空購物車
        localStorage.setItem('cart', JSON.stringify([]));

        // 構建成功訊息
        let successMessage = `訂單已成功建立！\n訂單編號：${result.orderId}`;
        if (result.pointsUsed && result.pointsUsed > 0) {
            successMessage += `\n使用點數：${result.pointsUsed} 點`;
        }
        if (result.pointsEarned && result.pointsEarned > 0) {
            successMessage += `\n獲得點數：${result.pointsEarned} 點`;
        }
        if (result.remainingPoints !== undefined) {
            successMessage += `\n剩餘點數：${result.remainingPoints} 點`;
        }

        // 顯示成功訊息
        alert(successMessage);

        // 重定向到訂單確認頁面
        window.location.href = 'order-confirmation.html?orderId=' + result.orderId;

    } catch (error) {
        console.error('Error:', error);
        alert(error.message || '訂單建立失敗，請稍後再試');
    }
}

async function processEcpayPayment(orderData) {
    try {
        console.log('🟢 準備導向綠界付款...');

        const response = await fetch(`${hostname}/ecpay/checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({
                orderNumber: 'ORDER_' + Date.now(), // 或 orderData.orderId
                amount: Math.round(orderData.totalAmount),
                item: 'PikaBaby 購物商品'
            })
        });

        const html = await response.text();

        // 開新視窗送出付款表單
        const win = window.open('', '_blank');
        win.document.open();
        win.document.write(html);
        win.document.close();

        // 可選：將訂單儲存起來供 callback 使用
        localStorage.setItem('pendingOrder', JSON.stringify(orderData));

    } catch (error) {
        console.error('❌ 綠界付款錯誤:', error);
        alert('綠界付款初始化失敗：' + error.message);
    }
}

// LINE Pay 付款處理函數
async function processLinePayPayment(orderData, cart) {
    try {
        console.log('🔄 準備 LINE Pay 付款...');

        // 生成唯一訂單ID
        const orderId = 'ORDER_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);

        // 準備 LINE Pay 請求數據
        const linePayRequest = {
            amount: Math.round(orderData.totalAmount), // LINE Pay 需要整數金額
            currency: 'TWD',
            orderId: orderId,
            packages: [{
                id: 'PACKAGE_1',
                amount: Math.round(orderData.totalAmount),
                name: 'PikaBaby 購物',
                products: cart.map((item, index) => ({
                    id: item.id.toString(),
                    name: item.name,
                    quantity: item.quantity,
                    price: index == 0 ? Math.round(item.price) - orderData.pointsUsed : Math.round(item.price)
                }))
            }],
            redirectUrls: {
                confirmUrl: `${window.location.origin}/Shopping/linepay-confirm.html?orderId=${orderId}`,
                cancelUrl: `${window.location.origin}/Shopping/shoppingcart.html?linepay=cancelled`
            }
        };

        console.log('📝 LINE Pay 請求數據:', linePayRequest);

        // 暫存訂單資料到 localStorage（用於付款確認後建立正式訂單）
        localStorage.setItem('pendingOrder', JSON.stringify({
            ...orderData,
            orderId: orderId,
            cart: cart
        }));

        // 發送 LINE Pay 請求
        const response = await fetch(`${hostname}/linepay/request`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(linePayRequest)
        });

        if (!response.ok) {
            throw new Error('LINE Pay 請求失敗');
        }

        const result = await response.json();
        console.log('✅ LINE Pay 響應:', result);

        if (result.returnCode === '0000' && result.info && result.info.paymentUrl) {
            // 成功建立 LINE Pay 付款請求，導向付款頁面
            console.log('🔄 導向 LINE Pay 付款頁面...');
            window.location.href = result.info.paymentUrl.web;
        } else {
            throw new Error('LINE Pay 初始化失敗: ' + (result.returnMessage || '未知錯誤'));
        }

    } catch (error) {
        console.error('❌ LINE Pay 付款處理失敗:', error);
        alert('LINE Pay 付款處理失敗：' + error.message);
    }
}

function updateTotal() {
    const subtotal = parseFloat(document.getElementById('orderSubtotal').textContent);
    const shippingFee = parseFloat(document.getElementById('shippingFee').textContent);
    const discountElem = document.getElementById('discount');
    const discount = discountElem ? parseFloat(discountElem.textContent) : 0;
    const points = parseInt(document.getElementById('points').value) || 0;
    const total = subtotal + shippingFee - discount - points;
    document.getElementById('orderTotal').textContent = total;
}

// 加購商品加入購物車（結帳頁專用）
async function addAddonToCart(productName, price, imageUrl, btn, productId) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    const idx = cart.findIndex(item => item.name === productName);
    if (idx > -1) {
        cart[idx].quantity += 1;
    } else {
        if (!productId) {
            alert('無法加入商品：缺少商品ID');
            return;
        }
        cart.push({
            id: productId,
            name: productName,
            price: price,
            quantity: 1,
            image: imageUrl
        });
    }
    localStorage.setItem('cart', JSON.stringify(cart));
    alert(`已將 ${productName} 加入購物車！`);
    await loadCart();
    await renderOrderItems(cart);
    updateTotal();
    // 按鈕變成已加入且不能再點
    if (btn) {
        btn.disabled = true;
        btn.textContent = '已加入購物車';
    }
}

// 篩選功能
function filterProducts(category) {
    // 更新 URL 參數
    const url = new URL(window.location.href);
    url.searchParams.set('category', category);
    window.history.pushState({}, '', url);

    // 如果當前頁面不是商品頁面，則跳轉到商品頁面
    if (!window.location.pathname.includes('product.html')) {
        window.location.href = `product.html?category=${category}`;
    }
}

// updateQuantity與onQuantityInput都要即時更新加減號disabled狀態
async function updateQuantity(idx, delta) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    const item = cart[idx];
    const stock = stockMap[item.id] || 99; // 直接用全域 stockMap
    let currentQuantity = item.quantity;
    let newQuantity = currentQuantity + delta;
    if (currentQuantity >= stock && delta > 0) {
        newQuantity = stock;
    }
    if (currentQuantity <= 1 && delta < 0) {
        newQuantity = 1;
    }
    newQuantity = Math.min(Math.max(newQuantity, 1), stock);
    cart[idx].quantity = newQuantity;
    localStorage.setItem('cart', JSON.stringify(cart));
    // 只更新該行
    const cartTable = document.getElementById('cart-table');
    const row = cartTable.rows[idx + 1];
    const qtyDisplay = document.getElementById(`qty-display-${idx}`);
    if (row && qtyDisplay) {
        qtyDisplay.textContent = newQuantity;
        row.cells[4].textContent = `NT$${cart[idx].price * cart[idx].quantity}`;
        updatePlusMinusBtnState(row, newQuantity, stock);
    }
    // 更新總計
    let total = 0;
    cart.forEach(item => { total += item.price * item.quantity; });
    document.getElementById('cart-total').textContent = `總計：NT$ ${total}`;
}

// 統一控制加減按鈕狀態
function updatePlusMinusBtnState(row, newQuantity, stock) {
    const minusBtn = row.cells[3].querySelector('button:nth-child(1)');
    const plusBtn = row.cells[3].querySelector('button:nth-child(3)');
    if (minusBtn) minusBtn.disabled = newQuantity <= 1;
    if (plusBtn) plusBtn.disabled = newQuantity >= stock;
}

// 新增繼續購物功能
function continueShopping() {
    window.location.href = '../Products/product.html';
}