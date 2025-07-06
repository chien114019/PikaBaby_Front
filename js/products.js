// 載入商品數據
function loadProducts() {
    fetch('http://localhost:8080/products/front/published')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(products => {
            const productsGrid = document.getElementById('products-grid');
            productsGrid.innerHTML = ''; // 清空現有內容

            products.forEach(product => {
                const productCard = createProductCard(product);
                productsGrid.appendChild(productCard);
            });

            // 初始化篩選
            filterAndSortProducts();
        })
        .catch(error => {
            console.error('Error loading products:', error);
            const productsGrid = document.getElementById('products-grid');
            productsGrid.innerHTML = '<p class="error-message">載入商品失敗，請稍後再試</p>';
        });
}

// 創建商品卡片
function createProductCard(product) {
    const card = document.createElement('div');
    card.className = 'product-card';
    card.dataset.category = product.category || '';
    card.dataset.condition = product.condition || 'new';

    card.innerHTML = `
        <button class="wishlist-btn" onclick="toggleWishlist(this)"></button>
        <a href="/Products/product-detail.html?id=${product.id}" class="product-link">
            <img src="${product.imageUrl}" alt="${product.name}" class="product-image">
            <h3 class="product-title">${product.name}</h3>
            <p class="product-price">售價：${product.price}元</p>
        </a>

        <div class="quantity-control" style="display:flex;justify-content:center;align-items:center;margin-bottom:8px;">
            <button class="quantity-btn minus" type="button">-</button>
            <input type="number" class="quantity-input" value="1" min="1" max="99" style="width:40px;text-align:center;margin:0 5px;">
            <button class="quantity-btn plus" type="button">+</button>
        </div>
        <button class="add-to-cart-btn" 
            data-product-id="${product.id}" 
            data-product-name="${product.name}"
            data-product-price="${product.price}" 
            onclick="addToCart(this)">加入購物車</button>
    `;

    return card;
}

// 篩選和排序商品
function filterAndSortProducts() {
    const products = document.querySelectorAll('.product-card');
    const sortSelect = document.getElementById('sort-select');
    const categorySelect = document.getElementById('category-select');
    const conditionSelect = document.getElementById('condition-select');
    const searchBox = document.querySelector('.search-box');

    const currentFilters = {
        category: categorySelect ? categorySelect.value : '',
        condition: conditionSelect ? conditionSelect.value : '',
        search: searchBox ? searchBox.value.toLowerCase() : '',
        sort: sortSelect ? sortSelect.value : 'default'
    };

    products.forEach(product => {
        const productCategory = product.dataset.category;
        const productCondition = product.dataset.condition;
        const productTitle = product.querySelector('.product-title').textContent.toLowerCase();

        const matchesCategory = !currentFilters.category || productCategory === currentFilters.category;
        const matchesCondition = !currentFilters.condition || productCondition === currentFilters.condition;
        const matchesSearch = !currentFilters.search || productTitle.includes(currentFilters.search);

        if (matchesCategory && matchesCondition && matchesSearch) {
            product.style.display = '';
        } else {
            product.style.display = 'none';
        }
    });

    // 排序可見的商品
    const productsGrid = document.querySelector('.products-grid');
    const visibleProducts = Array.from(products).filter(p => p.style.display !== 'none');
    
    sortProducts(visibleProducts, currentFilters.sort).forEach(product => {
        productsGrid.appendChild(product);
    });
}

// 排序商品
function sortProducts(products, sortBy) {
    return products.sort((a, b) => {
        const priceA = parseInt(a.querySelector('.product-price').textContent.match(/\d+/)[0]);
        const priceB = parseInt(b.querySelector('.product-price').textContent.match(/\d+/)[0]);
        
        switch (sortBy) {
            case 'price-low-high':
                return priceA - priceB;
            case 'price-high-low':
                return priceB - priceA;
            case 'newest':
                return b.dataset.createTime - a.dataset.createTime;
            case 'hot':
                return (b.querySelector('.hot-tag') ? 1 : 0) - (a.querySelector('.hot-tag') ? 1 : 0);
            case 'brand':
                return a.querySelector('.product-title').textContent
                    .localeCompare(b.querySelector('.product-title').textContent, 'zh-TW');
            default:
                return 0;
        }
    });
}

// 頁面載入時初始化
document.addEventListener('DOMContentLoaded', function() {
    loadProducts();
    
    // 添加篩選和排序事件監聽器
    const sortSelect = document.getElementById('sort-select');
    const categorySelect = document.getElementById('category-select');
    const conditionSelect = document.getElementById('condition-select');
    const searchBox = document.querySelector('.search-box');

    [sortSelect, categorySelect, conditionSelect].forEach(select => {
        if (select) {
            select.addEventListener('change', filterAndSortProducts);
        }
    });

    if (searchBox) {
        let searchTimeout;
        searchBox.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(filterAndSortProducts, 300);
        });
    }
}); 