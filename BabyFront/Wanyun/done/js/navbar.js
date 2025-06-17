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
    // if (window.scrollY > $("header").height() - 10) {
    //  if (window.scrollY >100) {
    if (window.scrollY > $(".nav-logo-row").height() - 10) {
        console.log(`${$("header").height()}`)
        $(".navbar").css("height", "fit-content");
        $(".navbar img").css("display", "inline-block");
        $(".navbar img").css("width", "90px");
    }
    else {
        $(".navbar").css("height", "60px");
        $(".navbar img").css("display", "none");
        $(".navbar img").css("width", "0px");
    }
});