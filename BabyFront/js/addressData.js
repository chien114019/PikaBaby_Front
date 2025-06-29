//地址渲染
let orderAddress = null;
let shippingAddress = null;

fetch("http://localhost:8080/customers/front/address", {
    method: "GET",
    credentials: "include"
})
    .then(res => res.json())
    .then(addresses => {
        const formatAddress = (addr) =>
            `${addr.zipcode} ${addr.city}${addr.district}${addr.street}`;

        orderAddress = addresses.find(addr => addr.isDefaultOrder);
        shippingAddress = addresses.find(addr => addr.isDefaultShipping);

        document.getElementById("memberOrderAddress").textContent =
            orderAddress ? formatAddress(orderAddress) : "尚未設定";

        if (orderAddress) {
            const outerDiv = $("#memberOrderAddress").closest("p").closest("div")
            $(outerDiv).find("div").find("button").each(function () {
                $(this).css("display", "inline");
            })
        }

        document.getElementById("memberShippingAddress").textContent =
            shippingAddress ? formatAddress(shippingAddress) : "尚未設定";

        if (shippingAddress) {
            const outerDiv = $("#memberShippingAddress").closest("p").closest("div")
            $(outerDiv).find("div").find("button").each(function () {
                $(this).css("display", "inline");
            })
        }
    })
    .catch(err => {
        console.error("載入地址失敗", err);
        document.getElementById("memberOrderAddress").textContent = "載入失敗";
        document.getElementById("memberShippingAddress").textContent = "載入失敗";
    });

// 共用 zipData
const zipData = {
    "台中市": {
        "中區": "400",
        "東區": "401",
        "南區": "402",
        "西區": "403",
        "北區": "404", "北屯區": "406", "西屯區": "407", "南屯區": "408",
        "太平區": "411",
        "大里區": "412",
        "霧峰區": "413",
        "烏日區": "414"
    },
    "台北市": { "大安區": "106" },
    "高雄市": { "鼓山區": "804" }
};

// 將城市與行政區連動邏輯套用在表單上
function setCityDistrictLinkage(form) {
    const cityEl = form.querySelector("#city");
    const districtEl = form.querySelector("#district");
    const zipcodeEl = form.querySelector("#zipcode");

    cityEl.addEventListener("change", () => {
        const city = cityEl.value;
        districtEl.innerHTML = '<option selected disabled>請選擇</option>';
        for (const dist in zipData[city]) {
            const opt = document.createElement("option");
            opt.value = dist;
            opt.textContent = dist;
            districtEl.appendChild(opt);
        }
        zipcodeEl.value = "";
    });

    districtEl.addEventListener("change", () => {
        const city = cityEl.value;
        const district = districtEl.value;
        zipcodeEl.value = zipData[city][district] || "";
    });
}

// 初始化：綁定所有表單
document.addEventListener("DOMContentLoaded", () => {
    setCityDistrictLinkage(document.getElementById("addressEditForm"));
    setCityDistrictLinkage(document.getElementById("addressAddForm"));
});

// 編輯地址
function editAddress(type) {
    const data = type === 'order' ? orderAddress : shippingAddress;
    if (!data) return;

    const form = document.getElementById("addressEditForm");
    form.querySelector("#addressname").value = data.name || "";
    form.querySelector("#phone").value = data.phone || "";
    form.querySelector("#street").value = data.street || "";
    form.querySelector("#city").value = data.city;
    form.querySelector("#city").dispatchEvent(new Event("change"));
    setTimeout(() => {
        form.querySelector("#district").value = data.district;
    }, 100);
    form.querySelector("#zipcode").value = data.zipcode || "";
    form.querySelector("#isDefaultOrder").checked = data.isDefaultOrder || false;
    form.querySelector("#isDefaultShipping").checked = data.isDefaultShipping || false;
    form.setAttribute("data-id", data.id);
}

// 新增地址
function addAddress() {
    const form = document.getElementById("addressAddForm");
    form.querySelector("#addressname").value = "";
    form.querySelector("#phone").value = "";
    form.querySelector("#street").value = "";
    form.querySelector("#city").value = "";
    form.querySelector("#district").innerHTML = '<option selected disabled>請選擇</option>';
    form.querySelector("#zipcode").value = "";
    form.querySelector("#isDefaultOrder").checked = false;
    form.querySelector("#isDefaultShipping").checked = false;
}


//編輯更新送出表單
document.getElementById("addressEditForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const addressId = this.getAttribute("data-id");

    const updated = {
        id: addressId,
        name: document.getElementById("addressname").value,
        phone: document.getElementById("phone").value,
        city: document.getElementById("city").value,
        district: document.getElementById("district").value,
        zipcode: document.getElementById("zipcode").value,
        street: document.getElementById("street").value,
        isDefaultOrder: document.getElementById("isDefaultOrder").checked,
        isDefaultShipping: document.getElementById("isDefaultShipping").checked
    };

    fetch(`http://localhost:8080/customers/front/address/${addressId}`, {
        method: "PUT",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updated)
    })
        .then(res => {
            if (!res.ok) throw new Error("更新失敗");
            alert("地址已更新！");
            location.reload(); // 或更新畫面
        })
        .catch(err => alert("錯誤：" + err.message));
});

//新增送出表單
document.getElementById("addressAddForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const form = this; // ✅ 這行不可少
    if (orderAddress && shippingAddress) {
        alert("最多只能新增兩筆預設地址（訂單與配送各一）");
        return;
    }

    const newAddress = {
        name: this.querySelector("#addressname").value,
        phone: this.querySelector("#phone").value,
        city: this.querySelector("#city").value,
        district: this.querySelector("#district").value,
        zipcode: this.querySelector("#zipcode").value,
        street: this.querySelector("#street").value,
        isDefaultOrder: this.querySelector("#isDefaultOrder").checked,
        isDefaultShipping: this.querySelector("#isDefaultShipping").checked
    };

    fetch("http://localhost:8080/customers/front/address", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(newAddress)
    })
        .then(res => {
            if (!res.ok) throw new Error("新增失敗");
            alert("地址新增成功！");
            location.reload();
        })
        .catch(err => alert("錯誤：" + err.message));
});
//刪除地址
function deleteAddress(type) {
    const target = type === 'order' ? orderAddress : shippingAddress;
    if (!target || !target.id) return;

    if (!confirm("確定要刪除此地址嗎？")) return;

    fetch(`http://localhost:8080/customers/front/address/${target.id}`, {
        method: "DELETE",
        credentials: "include"
    })
        .then(res => {
            if (!res.ok) throw new Error("刪除失敗");
            alert("地址已刪除！");
            location.reload();
        })
        .catch(err => alert("錯誤：" + err.message));
}


