// 滑動卡片效果 JavaScript
let offset = 0; // 幻燈片偏移量 
let barOffset = 0; // 導航條偏移量 
let intervalID; // 定時器 ID 
let chosenSlideNumber = 1; // 當前選擇的幻燈片編號 

// 切換到指定編號的幻燈片 
function slideTo(slideNumber) {
    // 先移除所有 active
    document.querySelectorAll('.drawerbox').forEach(box => box.classList.remove('active'));
    document.querySelectorAll('.drawer-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.card').forEach(card => card.classList.remove('active'));

    // 只給當前的加上 active
    document.querySelectorAll('.drawerbox')[slideNumber - 1].classList.add('active');
    document.querySelectorAll('.drawer-btn')[slideNumber - 1].classList.add('active');
    document.querySelectorAll('.card')[slideNumber - 1].classList.add('active');

    // 更新目前選擇
    chosenSlideNumber = slideNumber;
}

// 移動導航條 
function barSlide(barOffset) {
    const bar = document.querySelector("#bar");
    bar.style.transform = `translateY(${barOffset}%)`;
}

// 啟動幻燈片輪播 
function startSlide() {
    intervalID = setInterval(() => {
        slideTo(chosenSlideNumber % 4 + 1); // 每次切換到下一個幻燈片 
    }, 10000); // 每隔 10 秒自動切換幻燈片 
}