package com.example.demo.controller;

import com.example.demo.util.EcpayUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class EcpayController {

    @GetMapping("/ecpay-test")
    public String showEcpayTestPage() {
        return "ecpay-test";
    }

    @PostMapping("/ecpay/checkout")
    public void goEcpayPayment(@RequestParam("amount") String amount,
                               @RequestParam("item") String item,
                               HttpServletResponse response) throws IOException {

        Map<String, String> params = new LinkedHashMap<>();
//        params.put("MerchantID", "2000132");
        params.put("MerchantID", "3002607");
        params.put("MerchantTradeNo", "TEST" + System.currentTimeMillis());
        params.put("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", amount);
        params.put("TradeDesc", "ecpaytest");
        params.put("ItemName", item);
        params.put("ReturnURL", "http://localhost:8080/ecpay/callback");
        params.put("ChoosePayment", "ALL");
        params.put("EncryptType", "1");

        String checkMacValue = EcpayUtil.generateCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);

        System.out.println("✅ 最終送出參數：");
        params.forEach((k, v) -> System.out.println(k + " = " + v));

        // ✅ 產生隨機 nonce（每次請求不同）
        String nonce = UUID.randomUUID().toString().replace("-", "");

        // ✅ 加上 CSP header
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "' https://payment-stage.ecpay.com.tw");

        // ✅ 自動送出表單（使用 nonce）
        StringBuilder html = new StringBuilder();
        html.append("<html><body onload='document.forms[0].submit()'>");
        html.append("<form id='ecpayForm' method='post' action='https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            html.append("<input type='hidden' name='")
                .append(entry.getKey())
                .append("' value='")
                .append(entry.getValue().replace("\"", "&quot;"))
                .append("'/>");
        }
//        html.append("<p>請點擊按鈕前往綠界付款</p>");
//        html.append("<button type='submit'>前往付款</button>");  // ✅ 改為手動送出
        html.append("</form>");
        html.append("</body></html>");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(html.toString());
    }

    @PostMapping("/ecpay/callback")
    @ResponseBody
    public String ecpayCallback(@RequestParam Map<String, String> params) {
        System.out.println("🔔 ECPay 回傳參數：");
        params.forEach((k, v) -> System.out.println(k + " = " + v));
        return "1|OK";
    }
}
