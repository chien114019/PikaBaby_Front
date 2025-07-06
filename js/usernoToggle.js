window.addEventListener("DOMContentLoaded", () => {
  const memberName = sessionStorage.getItem("memberName");
  const loginNavItem = document.getElementById("loginNavItem");
  const memberDropdownItem = document.getElementById("memberDropdownItem");

  if (memberName) {
    // ✅ 已登入，顯示 dropdown
    memberDropdownItem.style.display = "block";
    loginNavItem.style.display = "none";
  } else {
    // ✅ 未登入，顯示登入圖示
    memberDropdownItem.style.display = "none";
    loginNavItem.style.display = "block";
  }
});