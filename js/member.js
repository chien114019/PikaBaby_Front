// 切換頁面
function switchTab(event, tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');

    document.querySelectorAll('.nav-button').forEach(el => el.classList.remove('active'));
    event.currentTarget.classList.add('active');
}

// addBaby
function addBabyField() {
    const container = document.getElementById('baby-container');
    const babyFields = container.querySelectorAll('.baby-field');

    // 最多三筆
    if (babyFields.length >= 3) {
        alert("最多只能新增三位寶寶");
        return;
    }

    const index = babyFields.length + 1;
    const babyNames = ['寶寶', '二寶', '三寶'];
    const babyLabel = babyNames[index - 1] || `寶寶 ${index}`;

    const newDiv = document.createElement('div');
    newDiv.className = "mb-4 baby-field";
    newDiv.innerHTML = `
      <label class="form-label info-form-label">${babyLabel}出生年月</label>
      <div class="d-flex gap-2">
        <input type="month" class="form-control info-form-control" name="baby${index}" />
        <button type="button" class="btn membercards-btn btn-danger btn-delete"
                                onclick="removeBabyField(this)">X</button>
      </div>
    `;

    container.appendChild(newDiv);
}

function removeBabyField(button) {
    const container = document.getElementById('baby-container');
    const fieldDiv = button.closest('.baby-field');
    if (fieldDiv) {
        const input = fieldDiv.querySelector('input[type="month"]');
        if (input) input.value = ""; // 清除值（確保不被送出）

        fieldDiv.remove(); //移除整個欄位
    }
    // 重新編號與命名
    const babyFields = container.querySelectorAll('.baby-field');
    const babyNames = ['大寶', '二寶', '三寶'];

    babyFields.forEach((field, i) => {
        const label = field.querySelector('label');
        const input = field.querySelector('input');
        const name = babyNames[i] || `寶寶 ${i + 1}`;

        label.textContent = `${name}出生年月`;
        input.name = `baby${i + 1}`;
    });
}

function cancelOrder(id) {
    $("#confirmBtn").on("click", function () {
        $.ajax({
            method: "PUT",
            url: `${hostname}/orders/front/cancel/${id}`,
            success: function (resp) {
                alert(resp.mesg);
                window.location.reload();
            },
            error: function () {
                alert("系統錯誤，取消失敗");
            }
        })
    })
}

//控制顯示密碼
function showPassword(id, icon) {
    const input = document.getElementById(id);
    input.type = 'text';
    icon.classList.remove("bi-eye-slash");
    icon.classList.add("bi-eye");
}

function hidePassword(id, icon) {
    const input = document.getElementById(id);
    input.type = 'password';
    icon.classList.remove("bi-eye");
    icon.classList.add("bi-eye-slash");
}
