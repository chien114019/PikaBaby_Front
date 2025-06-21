
function logout() {
  fetch("http://localhost:8080/customers/logout", {
    method: "POST"
  })
  .then(res => {
    if (!res.ok) throw new Error("伺服器錯誤：" + res.status);
    return res.json();
  })
  .then(data => {
    console.log("登出結果：", data);
    alert(data.mesg);
    if (data.success) {
      window.location.href = "../index.html"; // ← 視資料夾結構改成 ./ 或 /Member/login.html
    }
  })
  .catch(err => {
    console.error("登出失敗：", err.message);
    alert("登出失敗，請稍後再試！");
  });
}
