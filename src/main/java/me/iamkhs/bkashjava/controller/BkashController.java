package me.iamkhs.bkashjava.controller;

import me.iamkhs.bkashjava.service.BkashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/bkash")
public class BkashController {

    private final BkashService bkashService;

    public BkashController(BkashService bkashService) {
        this.bkashService = bkashService;
    }

    @GetMapping("/pay")
    public ResponseEntity<String> payWithBkash() {
        String token = bkashService.getToken();
        String result = bkashService.createPayment(token);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestParam Map<String, String> params) {
        String status = params.get("status");
        String paymentId = params.get("paymentID");

        if ("success".equalsIgnoreCase(status)) {
            String token = bkashService.getToken();
            String executeResponse = bkashService.executePayment(paymentId, token);

            // You can store the transaction info (trxID, etc.) in DB here
            return ResponseEntity.ok(executeResponse);
        } else {
            // Handle failure/cancel
            return ResponseEntity.ok("Payment failed or cancelled");
        }
    }

}