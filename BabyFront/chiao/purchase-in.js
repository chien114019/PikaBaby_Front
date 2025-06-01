
let rowIndex = 0;

// 模擬的商品資料庫（可改為查資料庫）
const productData = {
  A0001: { name: "奶瓶", format: "50個/箱"},
  B0001: { name: "奶嘴", format: "100個/箱" },
  C0001: { name: "濕紙巾", format: "20包/箱" }
};

// 新增一列
function addRow() {
  const tbody = document.getElementById("purchaseBody");
  const tr = document.createElement("tr");
  rowIndex++;

  //readonly只能看不能改
  tr.innerHTML = `
    <td class="row-no">${rowIndex}</td>
    <td><input type="text" class="form-control number" oninput="autoFillProduct(this)"></td>
    <td><input type="text" class="form-control chname" readonly></td>
    <td><input type="text" class="form-control format" readonly></td>
    <td><input type="number" class="form-control qty" value="0" onchange="updateAmount(this)"></td>
    <td><input type="text" class="form-control unit value="目前設定自動帶入，但允許修改"></td>
    <td><input type="number" class="form-control price" value="0" onchange="updateAmount(this)"></td>
    <td><input type="text" class="form-control amount" value="0" readonly></td>
    <td><button type="button" class="btn btn-sm btn-danger" onclick="deleteRow(this)">刪除</button></td>
  `;

  tbody.appendChild(tr);
}

// 自動帶入品名chname跟規格gormat
function autoFillProduct(input) {
  const code = input.value.toUpperCase();
  const data = productData[code];
  const tr = input.closest("tr");

//   if (data) {
//     tr.querySelector(".chname").value = data.name;
//     tr.querySelector(".format").value = data.format;
//   } else {
//     tr.querySelector(".chname").value = "";
//     tr.querySelector(".format").value = "";
//   }
}

//版本二
if (data) {
  tr.querySelector(".chname").value = data.name;
  tr.querySelector(".format").value = data.format;
  
  const unitField = tr.querySelector(".unit");
  if (!unitField.value) {
    unitField.value = data.unit; // 自動帶出單位，但允許修改
  }
}

// 計算金額
function updateAmount(input) {
  const tr = input.closest("tr");
  const qty = parseInt(tr.querySelector(".qty").value) || 0;
  const price = parseInt(tr.querySelector(".price").value) || 0;
  const amount = qty * price;
  tr.querySelector(".amount").value = amount;

  updateTotal();
}

// 刪除一列並重新編號
function deleteRow(button) {
  const tr = button.closest("tr");
  tr.remove();

  // 重新編號
  document.querySelectorAll("#purchaseBody .row-no").forEach((cell, idx) => {
    cell.textContent = idx + 1;
  });

  rowIndex = document.querySelectorAll("#purchaseBody tr").length;
  updateTotal();
}

// 更新小計、稅額、總計
function updateTotal() {
  let subtotal = 0;
  document.querySelectorAll(".amount").forEach(input => {
    subtotal += parseInt(input.value) || 0;
  });

  const tax = Math.round(subtotal * 0.05);
  const total = subtotal + tax;

  document.getElementById("subtotal").textContent = subtotal.toLocaleString();
  document.getElementById("tax").textContent = tax.toLocaleString();
  document.getElementById("total").textContent = total.toLocaleString();
}

// 預設加一筆空白列
window.onload = () => {
  addRow();
};
