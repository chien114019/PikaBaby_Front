// 導覽列
const $dropdown = $(".dropdown");
const $dropdownMenu = $(".dropdown-menu");
const showClass = "show";
$(window).on("load resize", function () {
    if (this.matchMedia("(min-width: 768px)").matches) {
        $dropdown.hover(
            function (e) {
                const $this = $(this);
                $this.addClass(showClass);
                $this.find($dropdownMenu).addClass(showClass);
                $this.find($dropdownMenu).addClass("d-flex");
                $this.find($dropdownMenu).css("left", `-${$this.position().left}px`);
            },
            function () {
                const $this = $(this);
                $this.removeClass(showClass);
                $this.find($dropdownMenu).removeClass(showClass);
                $this.find($dropdownMenu).removeClass("d-flex");

            }
        );
    } else {
        $dropdown.off("mouseenter mouseleave");
    }
});

document.addEventListener("scroll", function () {
    if (window.scrollY > $("header").height() - 10) {
        console.log(`${$("header").height()}`)
        $(".navbar").css("height", "fit-content");
        $(".navbar img#logo").css("display", "inline-block");
        $(".navbar img#logo").css("width", "90px");
    }
    else {
        $(".navbar").css("height", "60px");
        $(".navbar img#logo").css("display", "none");
        $(".navbar img#logo").css("width", "0px");
    }
});

let spans = $(".top-banner span")
setInterval(function () {  // 設置倒數計時: 結束時間 - 當前時間

    // 當前時間
    var time = new Date();
    var nowTime = time.getTime(); // 獲取當前毫秒數
    time.setMonth(6); //   獲取當前 月份 (從 '0' 開始算)
    time.setDate(15); //   獲取當前 日
    time.setHours(15); //   獲取當前 時
    time.setMinutes(0); //   獲取當前 分
    time.setSeconds(0);
    var endTime = time.getTime();

    // 倒數計時: 差值
    var offsetTime = (endTime - nowTime) / 1000; // ** 以秒為單位
    var sec = parseInt(offsetTime % 60); // 秒
    var min = parseInt((offsetTime / 60) % 60); // 分 ex: 90秒
    var hr = parseInt((offsetTime / 60 / 60) % 24); // 時
    var day = parseInt(offsetTime / 60 / 60 / 24);

    spans[0].textContent = day;
    spans[1].textContent = hr;
    spans[2].textContent = min;
    spans[3].textContent = sec;
}, 1000);

function setUserToggle(data) {
    const loginNavItem = document.getElementById("loginNavItem");
    const memberDropdownItem = document.getElementById("memberDropdownItem");

    if (data) {
        // ✅ 已登入，顯示 dropdown
        memberDropdownItem.style.display = "block";
        loginNavItem.style.display = "none";
    }
}