function confirmDelete() {
  const password = document.getElementById('confirmPassword').value;

  if (!password) {
    alert("請輸入密碼！");
    return;
  }

  fetch('http://localhost:8080/customers/front/delete', {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ password })
  })
    .then(res => res.json())
    .then(result => {
      alert(result.mesg);
      if (result.success) {
        // 關閉 Modal
        const modalElement = document.getElementById('deleteAccountModal');
        const modalInstance = bootstrap.Modal.getInstance(modalElement);
        modalInstance.hide();

        // 導回登入
        window.location.href = './login.html';
      }
    })
    .catch(error => {
      console.error(error);
      alert("發生錯誤，請稍後再試");
    });
}
//按取消清空欄位
document.getElementById('cancelPwdBtn').addEventListener('click', () => {
    document.getElementById('newPassword').value = '';
    document.getElementById('confirmPassword').value = '';

    const saveBtn = document.getElementById('savePwdBtn');
    saveBtn.classList.add('pwd-btn-disabled');
    saveBtn.disabled = true;
});
