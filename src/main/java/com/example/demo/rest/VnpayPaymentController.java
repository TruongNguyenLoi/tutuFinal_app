package com.example.demo.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.entity.order.Order;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.VnpayConfig;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/vnpay")
public class VnpayPaymentController {
	
	@PostMapping("/make")
	public Map<String, String> createPayment(HttpServletRequest request,
			@RequestParam(name = "vnp_Amount") Integer amount) {
//		String vnp_Version = "2.1.0";
//		String vnp_Command = "pay";
		String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
//		String vnp_IpAddr = VnpayConfig.getIpAddress(request);
//		String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", "2.1.0");
		vnp_Params.put("vnp_Command", "pay");
		vnp_Params.put("vnp_TmnCode", "PHMV72EV");
		vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
		vnp_Params.put("vnp_CurrCode", "VND");

			vnp_Params.put("vnp_BankCode", "NCB");

		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "vnp_OrderInfo");
		vnp_Params.put("vnp_OrderType","100005");
		vnp_Params.put("vnp_Locale", "vn");

		vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_Returnurl);
		vnp_Params.put("vnp_IpAddr", "192.168.10.175");

		Date dt = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(dt);
		String vnp_CreateDate = dateString;
		String vnp_TransDate = vnp_CreateDate;
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
		vnp_Params.put("vnp_TransDate", vnp_TransDate);

		// Build data to hash and querystring
		List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator<String> itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(fieldValue);
				// Build query
				try {
					query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
					query.append('=');
					query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}

		String queryUrl = query.toString();
		String vnp_SecureHash = VnpayConfig.Sha256(VnpayConfig.vnp_HashSecret + hashData.toString());
		// System.out.println("HashData=" + hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;
		vnp_Params.put("redirect_url", paymentUrl);
//		return "redirect:" + paymentUrl;
		return vnp_Params;
	}


//	public Map<String,Object> createPayment(Order booking, HttpServletRequest req) throws UnsupportedEncodingException {
//		String vnp_Version = "2.1.0";
//		String vnp_Command = "pay";
//		long amount = booking.getTotal_price()* 100L;
//		String orderType = "topup";
//
//		String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
//		String vnp_TmnCode = VnpayConfig.vnp_TmnCode;
//
//		return getStringObjectMap(vnp_Version, vnp_Command, amount, orderType, vnp_TxnRef, vnp_TmnCode);
//	}
//
//	public Map<String,Object> createPayment(Order booking, HttpServletRequest req, String vnp_TxnRef) throws UnsupportedEncodingException {
//		String vnp_Version = "2.1.0";
//		String vnp_Command = "pay";
//		long amount = booking.getTotal_price()* 100L;
//
//		String orderType = "topup";
//
//		String vnp_TmnCode = VnpayConfig.vnp_TmnCode;
//
//		return getStringObjectMap(vnp_Version, vnp_Command, amount, orderType, vnp_TxnRef, vnp_TmnCode);
//	}
//
//	private Map<String, Object> getStringObjectMap(String vnp_Version, String vnp_Command, long amount, String orderType, String vnp_TxnRef, String vnp_TmnCode) throws UnsupportedEncodingException {
//		Map<String, String> vnp_Params = new HashMap<>();
//		vnp_Params.put("vnp_Version", vnp_Version);
//		vnp_Params.put("vnp_Command", vnp_Command);
//		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//		vnp_Params.put("vnp_Amount", String.valueOf(amount));
//		vnp_Params.put("vnp_CurrCode", "VND");
//		vnp_Params.put("vnp_BankCode", "NCB");
//		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
//		vnp_Params.put("vnp_Locale", "vn");
//		vnp_Params.put("vnp_OrderType", orderType);
//		vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_Returnurl);
//		vnp_Params.put("vnp_IpAddr", "116.110.43.200");
//		vnp_Params.put("vnp_Locale", "vn");
//
//		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//		String vnp_CreateDate = formatter.format(cld.getTime());
//		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//
//		cld.add(Calendar.MINUTE, 15);
//		String vnp_ExpireDate = formatter.format(cld.getTime());
//		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//
//		List fieldNames = new ArrayList(vnp_Params.keySet());
//		Collections.sort(fieldNames);
//		StringBuilder hashData = new StringBuilder();
//		StringBuilder query = new StringBuilder();
//		Iterator itr = fieldNames.iterator();
//		while (itr.hasNext()) {
//			String fieldName = (String) itr.next();
//			String fieldValue = (String) vnp_Params.get(fieldName);
//			if ((fieldValue != null) && (fieldValue.length() > 0)) {
//				//Build hash data
//				hashData.append(fieldName);
//				hashData.append('=');
//				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
//				//Build query
//				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
//				query.append('=');
//				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
//				if (itr.hasNext()) {
//					query.append('&');
//					hashData.append('&');
//				}
//			}
//		}
//		String queryUrl = query.toString();
//		String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
//		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//		String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;
//		Map<String,Object> map = new HashMap<>();
//		map.put("link",paymentUrl);
//		map.put("paymentId",vnp_TxnRef);
//		return map;
//	}
//

	@GetMapping(value = "/result")
	public Map<String, String> completePayment(HttpServletRequest request, 
			@RequestParam(name = "vnp_OrderInfo") String vnp_OrderInfo,
			@RequestParam(name = "vnp_Amount") Integer vnp_Amount,
			@RequestParam(name = "vnp_BankCode", defaultValue = "") String vnp_BankCode,
			@RequestParam(name = "vnp_BankTranNo") String vnp_BankTranNo,
			@RequestParam(name = "vnp_CardType") String vnp_CardType,
			@RequestParam(name = "vnp_PayDate") String vnp_PayDate,
			@RequestParam(name = "vnp_ResponseCode") String vnp_ResponseCode,
			@RequestParam(name = "vnp_TransactionNo") String vnp_TransactionNo,
			@RequestParam(name = "vnp_TxnRef") String vnp_TxnRef
			) {
		Map<String, String> response = new HashMap<>();
		
		String year = vnp_PayDate.substring(0, 4);
		String month = vnp_PayDate.substring(4, 6);
		String date = vnp_PayDate.substring(6, 8);
		String hour = vnp_PayDate.substring(8, 10);
		String minutes = vnp_PayDate.substring(10, 12);
		String second = vnp_PayDate.substring(12, 14);
		
		String timePay = date + "-" + month + "-" + year + " " + hour + ":" + minutes + ":" + second;
		
		response.put("vnp_OrderInfo", vnp_OrderInfo);
		response.put("vnp_Amount", vnp_Amount.toString());
		response.put("vnp_BankCode", vnp_BankCode);
		response.put("vnp_BankTranNo", vnp_BankTranNo);
		response.put("vnp_CardType", vnp_CardType);
		response.put("vnp_PayDate", timePay);
		response.put("vnp_ResponseCode", vnp_ResponseCode);
		response.put("vnp_TransactionNo", vnp_TransactionNo);
		response.put("vnp_TxnRef", vnp_TxnRef);
		
		return response;
	}
}
