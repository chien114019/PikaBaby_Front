package com.example.demo.service;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Linepay.CheckoutPaymentRequestForm;
import com.example.demo.model.Linepay.ConfirmData;
import com.example.demo.model.Linepay.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LinePayService {

	@Value("${linepay.channelid}")
	private String ChannelID;
	
	@Value("${linepay.channelsecret}")
	private String ChannelSecret;
	
	@Value("${linepay.host}")
	private String linepayHost;
	
	private ObjectMapper mapper;
	private RestTemplate template;
	private CheckoutPaymentRequestForm form;
	
	public LinePayService() {
		mapper = new ObjectMapper();
		template = new RestTemplate();
	}

	public ResponseEntity<Response> RequestService(CheckoutPaymentRequestForm form) throws Exception {
		System.out.println("RequestService()");
		
		this.form = form;
		
		String requestUri = "/v3/payments/request";
		String requestNonce = UUID.randomUUID().toString();	
		String requestBody = mapper.writeValueAsString(form);
		String requestSignature = encrypt(ChannelSecret, ChannelSecret + requestUri + requestBody + requestNonce);

		HttpEntity<String> entity = createHttpEntity(requestBody, requestNonce, requestSignature);
		String requestUrl = linepayHost + requestUri;
		
		ResponseEntity<Response> responseEntity =  template.exchange(requestUrl, HttpMethod.POST, 
				entity, Response.class);
		
//		System.out.println(responseEntity.getBody().getReturnCode());
		
		return responseEntity;
		
	}
	
	public Response ConfirmService(String transactionId, String orderId) throws Exception {
		System.out.println("ConfirmService");
		
        ConfirmData confirmData = new ConfirmData();
        confirmData.setAmount(form.getAmount());
        confirmData.setCurrency("TWD");
        
    	String confirmUri = String.format("/v3/payments/%s/confirm", transactionId);
    	String confirmNonce = UUID.randomUUID().toString();
		String confirmBody = mapper.writeValueAsString(confirmData);
		String confirmSignature = encrypt(ChannelSecret, ChannelSecret + confirmUri + confirmBody + confirmNonce);
		
		HttpEntity<String> entity = createHttpEntity(confirmBody, confirmNonce, confirmSignature);
		String confirmUrl = linepayHost + confirmUri;
		
		ResponseEntity<Response> responseEntity =  template.exchange(confirmUrl, HttpMethod.POST, 
				entity, Response.class);
		
//		System.out.println(responseEntity.getBody().getInfo());
		
		return responseEntity.getBody();
	}
	
	private HttpEntity<String> createHttpEntity(String body, String nonce, String signature) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-LINE-Authorization-Nonce", nonce);
		headers.set("X-LINE-Authorization", signature);
		headers.set("X-LINE-ChannelId", ChannelID);
		headers.set("Content-Type", "application/json");	
		
		return new HttpEntity(body, headers);
	}
	
	private static String encrypt(final String keys, final String data) {
        return toBase64String(HmacUtils.getHmacSha256(keys.getBytes()).doFinal(data.getBytes()));
    }

	private static String toBase64String(byte[] bytes) {
        byte[] byteArray = Base64.encodeBase64(bytes);
        return new String(byteArray);
    }
}
