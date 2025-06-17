
  function deleteAccount() {
    if (confirm("確定要刪除帳號嗎？此操作無法恢復！")) {
      const memberId = 1; // ← 這裡改成當前使用者 ID，或從後端取得

      fetch(`/api/member/${memberId}`, {
        method: 'DELETE'
      })
      .then(response => {
        if (response.ok) {
          alert("帳號已成功刪除");
          window.location.href = "/logout"; // 或導向首頁
        } else {
          alert("刪除失敗，請稍後再試");
        }
      })
      .catch(error => {
        console.error("錯誤：", error);
        alert("發生錯誤，請稍後再試");
      });
    }
  }

