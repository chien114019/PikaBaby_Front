document.querySelector('[data-bs-target="#editModal"]').addEventListener('click', () => {
    fetch("http://localhost:8080/customers/front/me", {
        method: "GET",
        credentials: "include"
    })
        .then(res => res.json())
        .then(data => {
            // 分拆 name 為姓氏與名字（假設用空格分隔）
            const nameParts = data.name.split('');
            document.getElementById('lastname').value = nameParts[0] || '';
            document.getElementById('firstname').value = nameParts.slice(1).join('') || '';

            document.getElementById('gender').value = data.gender || '';
            document.getElementById('phone').value = data.phone || '';
            document.getElementById('email').value = data.email || '';

            // 生日格式：yyyy-MM-dd
            if (data.birthday) {
                const birthdayInput = document.querySelector('input[type="date"]');
                birthdayInput.value = data.birthday;
            }

            // 寶寶生日：清空並加入 baby container
            const babyContainer = document.getElementById("baby-container");
            babyContainer.innerHTML = "";

            const babyNames = ["大寶", "二寶", "三寶"];
            (data.babyBirthdays || []).forEach((b, i) => {
                const babyField = document.createElement("div");
                babyField.classList.add("mb-4", "baby-field");
                babyField.innerHTML = `
        <label class="form-label info-form-label">${babyNames[i] || "寶寶"}</label>
        <div class="d-flex gap-2">
            <input type="month" class="form-control info-form-control" name="baby${i + 1}" value="${b?.slice(0, 7)}" />
            <button type="button" class="btn membercards-btn btn-danger btn-delete" onclick="removeBabyField(this)">X</button>
        </div>`;
                babyContainer.appendChild(babyField);
            });
        })
        .catch(err => console.error("會員資料載入失敗：", err));
});

// 送出表單 PUT 更新
document.getElementById("memberEditForm").addEventListener("submit", function (e) {
    e.preventDefault();


const payload = {
    lastName: document.getElementById('lastname').value.trim(),
    firstName: document.getElementById('firstname').value.trim(),
    gender: document.getElementById('gender').value,
    phone: document.getElementById('phone').value.trim(),
    birthday: document.getElementById('birthday').value,
    babyBirthdays: Array.from(document.querySelectorAll('#baby-container input[type="month"]'))
        .map(input => input.value.trim())
        .filter(val => /^\d{4}-\d{2}$/.test(val))
};

    console.log("送出資料：", JSON.stringify(payload, null, 2));
    fetch("http://localhost:8080/customers/front/me/update", {
        method: "PUT",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (!res.ok) throw new Error("更新失敗");
            return res.text();
        })
        .then(msg => {
            alert("✅ " + msg);
            location.reload(); // 或重新查詢會員資料
        })
        .catch(err => {
            console.error("更新錯誤：", err);
            alert("❌ 更新失敗，請檢查輸入或稍後再試");
        });
});
