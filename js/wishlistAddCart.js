
document.addEventListener("DOMContentLoaded", () => {
    fetch("http://localhost:8080/customers/front/favorites", {
        credentials: "include"
    })
        .then(res => res.json())
        .then(data => renderFavorites(data));
});

function renderFavorites(favorites) {
    const container = document.getElementById("wishlist");
    container.innerHTML = ""; // 清空舊內容

    if (favorites.length === 0) {
        // 顯示提示文字
        container.innerHTML = `
            <div class="text-center text-muted py-5">
                <i class="bi bi-heart" style="font-size: 3rem;"></i><br>
                <p class="mt-3">目前尚無收藏商品</p>
            </div>
        `;
        return;
    }

    // 如果有收藏商品，渲染卡片

    favorites.forEach(product => {
        const card = document.createElement("div");
        card.className = "favorite-card";
        card.dataset.id = product.id;
        card.dataset.name = product.name;
        card.dataset.price = product.price;

        // 處理圖片 URL，優先使用product_image表的圖片
        let imageUrl = product.primaryImageUrl || product.imageUrl;
        console.log("fav: " + imageUrl);
        if (imageUrl && imageUrl.startsWith('/products/front/images/')) {
            imageUrl = 'http://localhost:8080' + imageUrl;
        } else if (!imageUrl || imageUrl.trim() === '' || imageUrl === '/images/default.jpg') {
            imageUrl = '../images/baby.jpg'; // fallback 預設圖
        } else if (imageUrl.startsWith('images/')) {
            imageUrl = '../' + imageUrl;
        } else if (!imageUrl.startsWith('http') && !imageUrl.startsWith('../')) {
            imageUrl = '../images/baby.jpg';
        }

        card.dataset.image = imageUrl

        // 儲存顏色與規格（可選）
        const color = product.color || '無';
        const spec = product.specification || '無';

        card.innerHTML = `
            <img src="${imageUrl}" class="product-img">
            <div class="product-info">
                <div class="product-title">${product.name}</div>
                <div class="product-sub">顏色：${color}｜產品規格：${spec}</div>
                <div class="product-price">$${product.price}</div>
                <div class="btn-group">
                    <button class="btn-cart">加入購物車</button>
                    <button class="btn-remove">移除</button>
                </div>
            </div>
        `;

        // 綁定加入購物車
        card.querySelector(".btn-cart").addEventListener("click", () => {
            addToCart(card);
            alert("已將此商品加入購物車！");
        });

        // 綁定移除收藏
        card.querySelector(".btn-remove").addEventListener("click", () => {
            removeFavorite(card);
            alert("已移除此收藏");
        });

        container.appendChild(card);
    });

    // 加入「全部加入購物車」按鈕
    const btnAll = document.createElement("div");
    btnAll.className = "text-end mt-4";
    btnAll.innerHTML = `<button class="btn-buy-all">全部加入購物車</button>`;
    btnAll.querySelector("button").addEventListener("click", () => {
        document.querySelectorAll(".favorite-card").forEach(card => addToCart(card));
        alert("已將所有商品加入購物車！");
    });

    container.appendChild(btnAll);
}

// 移除卡片並檢查收藏清單是否為空
function removeCardAndCheckEmpty(card) {
    card.remove();
    const container = document.getElementById("wishlist");
    const remainingCards = container.querySelectorAll(".favorite-card");

    if (remainingCards.length === 0) {
        container.innerHTML = `
            <div class="text-center text-muted py-5">
                <i class="bi bi-heart" style="font-size: 3rem;"></i><br>
                <p class="mt-3">目前尚無收藏商品</p>
            </div>
        `;
    }
}

// 加入購物車後，同時移除收藏
function addToCart(card) {
    const id = parseInt(card.dataset.id);
    const name = card.dataset.name;
    const price = parseInt(card.dataset.price);
    const image = card.dataset.image;

    let cart = JSON.parse(localStorage.getItem("cart")) || [];
    const idx = cart.findIndex(item => item.id === id);
    if (idx > -1) {
        cart[idx].quantity += 1;
    } else {
        cart.push({ id, name, price, quantity: 1, image });
    }
    localStorage.setItem("cart", JSON.stringify(cart));

    // 從收藏清單中移除
    removeFavorite(card, false); // 只送出請求，不先刪 DOM
    removeCardAndCheckEmpty(card); // 再手動移除並檢查空清單
}

// 移除收藏（也可直接點擊「移除」按鈕用）
function removeFavorite(card, removeFromDOM = true) {
    const id = card.dataset.id;
    fetch(`http://localhost:8080/customers/front/favorites/${id}`, {
        method: "DELETE",
        credentials: "include"
    }).then(() => {
        if (removeFromDOM) removeCardAndCheckEmpty(card);
    });
}

// // 綁定所有按鈕事件
// document.getElementById("wishlist").addEventListener("click", (e) => {
//     const card = e.target.closest(".favorite-card");
//     if (!card) return;

//     if (e.target.classList.contains("btn-cart")) {
//         addToCart(card);
//     }

//     if (e.target.classList.contains("btn-remove")) {
//         removeFavorite(card);
//     }
// });

