window.addEventListener("DOMContentLoaded", () => {
  const memberName = sessionStorage.getItem("memberName");
  const loginNavItem = document.getElementById("loginNavItem");
  const memberDropdownItem = document.getElementById("memberDropdownItem");

  if (memberName) {
    // ✅ 已登入，顯示 dropdown
    memberDropdownItem.style.display = "block";
  } else {
    // ✅ 未登入，顯示登入圖示
    loginNavItem.style.display = "block";
  }
});
