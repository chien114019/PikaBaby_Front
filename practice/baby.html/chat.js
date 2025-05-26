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

                let reply = "pika pika!";
                if (msg.includes("運費")) reply = "本店全館滿千免運，未滿酌收 60 元運費。";
                if (msg.includes("付款方式")) reply = "我們接受信用卡、ATM 轉帳和貨到付款。";
                if (msg.includes("退貨")) reply = "商品可於收到後 7 天內申請退貨，需保持商品完整。";
                if (msg.includes("客服")) reply = "客服電話：1234-5678，服務時間：週一至週五 9:00-18:00。";

                setTimeout(() => {
                    chatLog.innerHTML += `<div><strong>機器人：</strong>${reply}</div>`;
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