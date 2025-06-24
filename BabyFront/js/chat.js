function toggleChat() {
    const chatWindow = document.getElementById("chat-window");
    const isOpen = chatWindow.style.display === "block";
    chatWindow.style.display = isOpen ? "none" : "block";
}

document.addEventListener("DOMContentLoaded", () => {
    const chatToggle = document.getElementById("chat-toggle");
    const chatWindow = document.getElementById("chat-window");
    const chatLog = document.getElementById("chat-log");
    const chatInput = document.getElementById("chat-input");

    chatToggle.onclick = toggleChat;

    function sendMsg() {
        const msg = chatInput.value.trim();
        if (msg === "") return;

        chatLog.innerHTML += `<div><strong>你：</strong>${msg}</div>`;

        // 將輸入統一轉為小寫，避免大小寫問題
        const msgLower = msg.toLowerCase();
        let reply = "pika pika!";

        // 關鍵字比對邏輯（多關鍵字陣列 + some）
        // else if (["", "", "", ""].some(word => msgLower.includes(word))) {reply = "";}
        if (["運費", "免運", "運送"].some(word => msgLower.includes(word))) {
            reply = "滿千元我們會免費把寶貝用品送到你家門口！未滿千元，酌收運費60元唷～";
        } else if (["付款", "付款方式", "付錢"].some(word => msgLower.includes(word))) {
            reply = "目前只接受信用卡支付，其他支付方式Pika努力中，等Pika好消息！";
        } else if (["退貨", "退費", "換貨"].some(word => msgLower.includes(word))) {
            reply = "如果收到的寶貝不適合，可以在7天內跟我們說，我們會安排退貨或換貨，記得保持商品完整唷～";
        } else if (["客服", "聯絡", "電話"].some(word => msgLower.includes(word))) {
            reply = "真人客服電話是 (04)2515-0025，服務時間是週一到週五 9:00-18:00。";
        } else if (["出貨", "到貨", "多久", "什麼時候"].some(word => msgLower.includes(word))) {
            reply = "訂單確認後，1~3天內就會出貨唷！有時候小幫手們會忙不過來，還請耐心等候一下下～";
        } else if (["訂單", "查詢", "進度", "物流"].some(word => msgLower.includes(word))) {
            reply = "訂單狀態請至『會員專區->我的訂單』查看，小幫手也可以幫你查查進度唷～";
        } else if (["不會", "幫我一下", "不懂", "問題太多"].some(word => msgLower.includes(word))) {
            reply = "這個問題稍微複雜一點，我們已經筆記下來囉～請稍後聯繫真人客服幫你處理唷❤️";
        }
        else if (["笨蛋", "白痴", "豬頭", "混帳"].some(word => msgLower.includes(word))) {
            reply = "嗚嗚人家只是隻Pika，不要罵人家QQ等等聰明的真人小編就會回覆您了";
        }
        else {
            // 都沒有符合的問題，給預設回覆
            reply = "您好~已收到您的訊息，Pika小編照順序回覆中！請等等Pika!";
        }

        setTimeout(() => {
            chatLog.innerHTML += `<div><strong>皮卡：</strong>${reply}</div>`;
            chatLog.scrollTop = chatLog.scrollHeight;
        }, 500);

        chatInput.value = "";
    }

    chatInput.addEventListener("keydown", function (event) {
        if (event.key === "Enter") {
            sendMsg();
        }
    });
});