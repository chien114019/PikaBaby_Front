
  function deleteAddress() {
    if (confirm("確定要刪除此地址嗎？")) {
      // 這裡可以加上後端 API 請求，例如：
      fetch('/api/address/123', {
        method: 'DELETE'
      })
      .then(response => {
        if (response.ok) {
          alert("地址已刪除");
          location.reload(); // 重新整理頁面或改為移除 DOM 元素
        } else {
          alert("刪除失敗");
        }
      })
      .catch(err => {
        console.error(err);
        alert("發生錯誤");
      });
    }
  }

