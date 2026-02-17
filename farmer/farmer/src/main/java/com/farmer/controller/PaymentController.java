package com.farmer.controller;

import com.farmer.config.RazorpayConfig;
import com.farmer.entity.Order;
import com.farmer.repository.OrderRepository;
import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    private final OrderRepository orderRepo;
    private final RazorpayConfig razorpayConfig;
    private final com.farmer.service.EmailService emailService;
    private final com.farmer.service.PdfInvoiceService pdfService;

    public PaymentController(OrderRepository orderRepo, RazorpayConfig razorpayConfig,
            com.farmer.service.EmailService emailService, com.farmer.service.PdfInvoiceService pdfService) {
        this.orderRepo = orderRepo;
        this.razorpayConfig = razorpayConfig;
        this.emailService = emailService;
        this.pdfService = pdfService;
    }

    // ✅ Create Razorpay Order
    @PostMapping("/create/{orderId}")
    public Object createPayment(@PathVariable Long orderId) throws Exception {

        Order order = orderRepo.findById(orderId).orElseThrow();

        RazorpayClient client = new RazorpayClient(razorpayConfig.getKey(), razorpayConfig.getSecret());

        JSONObject options = new JSONObject();
        options.put("amount", order.getTotalAmount() * 100); // double -> paise
        options.put("currency", "INR");
        options.put("receipt", "order_" + orderId);

        com.razorpay.Order razorOrder = client.orders.create(options);

        order.setRazorpayOrderId(razorOrder.get("id"));
        orderRepo.save(order);

        return new JSONObject(razorOrder.toString()).toMap();
    }

    // ✅ Verify Payment Signature
    @PostMapping("/verify")
    public String verifyPayment(@RequestBody Map<String, String> data) throws Exception {

        String razorpayOrderId = data.get("razorpay_order_id");
        String razorpayPaymentId = data.get("razorpay_payment_id");
        String razorpaySignature = data.get("razorpay_signature");

        Order order = orderRepo.findByRazorpayOrderId(razorpayOrderId);

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);

        boolean isValid = Utils.verifyPaymentSignature(options, razorpayConfig.getSecret());

        if (!isValid) {
            order.setPaymentStatus("FAILED");
            orderRepo.save(order);
            return "Payment verification failed ❌";
        }

        order.setPaymentStatus("PAID");
        order.setStatus("PROCESSING");
        order.setPaymentId(razorpayPaymentId);
        orderRepo.save(order);

        // Send Email with Invoice
        java.io.File invoice = pdfService.generateInvoice(order);
        emailService.sendPaymentSuccessEmail(order, invoice);

        return "Payment verified successfully ✅";
    }
}
