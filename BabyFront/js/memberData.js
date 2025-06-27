function loadMemberData() {
    fetch("http://localhost:8080/customers/front/me", {
        method: "GET",
        credentials: "include"
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById("lastname").value = data.lastName || "";
        document.getElementById("firstname").value = data.firstName || "";
        document.getElementById("gender").value = data.gender || "女性";
        document.getElementById("birthday").value = data.birthday || "";
        document.getElementById("phone").value = data.phone || "";
        document.getElementById("email").value = data.email || "";
    })
    .catch(err => {
        console.error("載入會員資料失敗：", err);
        alert("無法載入會員資料");
    });
}
