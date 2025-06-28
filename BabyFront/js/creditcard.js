// creditCard
        const cardInput = document.getElementById('cardNumber');
        const expiryInput = document.getElementById('expiry');
        const cvvInput = document.getElementById('cvv');
        const nextBtn = document.getElementById('nextBtn');
        const maskedCardDiv = document.getElementById('maskedCard');

        function formatCardNumber(value) {
            return value.replace(/\D/g, '').replace(/(.{4})/g, '$1 ').trim();
        }

        function showMaskedCard() {
            const data = localStorage.getItem('creditCard');
            if (data) {
                const { cardNumber } = JSON.parse(data);
                const digits = cardNumber.replace(/\s/g, '');
                const masked = '**** **** **** ' + digits.slice(-4);
                maskedCardDiv.textContent = `已儲存卡號：${masked}`;
            }
        }

        cardInput.addEventListener('input', () => {
            cardInput.value = formatCardNumber(cardInput.value);
            validateForm();
        });

        expiryInput.addEventListener('input', validateForm);
        cvvInput.addEventListener('input', validateForm);

        function validateForm() {
            const card = cardInput.value.replace(/\s/g, '');
            const expiry = expiryInput.value;
            const cvv = cvvInput.value;

            const isCardValid = /^\d{16}$/.test(card);
            const isExpiryValid = /^(0[1-9]|1[0-2])\/\d{2}$/.test(expiry);
            const isCVVValid = /^\d{3}$/.test(cvv);

            if (isCardValid && isExpiryValid && isCVVValid) {
                nextBtn.classList.remove('btn-disabled');
            } else {
                nextBtn.classList.add('btn-disabled');
            }
        }

        document.getElementById('cardForm').addEventListener('submit', function (e) {
            e.preventDefault();

            const cardData = {
                cardNumber: cardInput.value,
                expiry: expiryInput.value,
                cvv: cvvInput.value
            };

            localStorage.setItem('creditCard', JSON.stringify(cardData));
            alert('卡號已儲存至 localStorage！');
            bootstrap.Modal.getInstance(document.getElementById('cardModal')).hide();
            this.reset();
            nextBtn.classList.add('btn-disabled');
            showMaskedCard();
        });

        // 初始化顯示儲存的卡號
        showMaskedCard();