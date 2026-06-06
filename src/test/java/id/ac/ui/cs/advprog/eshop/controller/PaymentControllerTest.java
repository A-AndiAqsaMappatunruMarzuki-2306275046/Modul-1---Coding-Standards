package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void testPaymentDetailPage() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetailForm"));
    }

    @Test
    void testPaymentDetailById() throws Exception {
        Payment mockPayment = new Payment("1", "BANK", new HashMap<>());
        when(paymentService.getPayment("1")).thenReturn(mockPayment);
        mockMvc.perform(get("/payment/detail/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void testPaymentAdminList() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminList"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    void testPaymentAdminDetail() throws Exception {
        Payment mockPayment = new Payment("1", "BANK", new HashMap<>());
        when(paymentService.getPayment("1")).thenReturn(mockPayment);
        mockMvc.perform(get("/payment/admin/detail/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void testPaymentAdminSetStatusSuccess() throws Exception {
        Payment mockPayment = new Payment("1", "BANK", new HashMap<>());
        when(paymentService.getPayment("1")).thenReturn(mockPayment);

        mockMvc.perform(post("/payment/admin/set-status/1").param("status", "SUCCESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));
        verify(paymentService, times(1)).setStatus(mockPayment, "SUCCESS");
    }

    @Test
    void testPaymentAdminSetStatusNotFound() throws Exception {
        when(paymentService.getPayment("invalid-id")).thenReturn(null);

        mockMvc.perform(post("/payment/admin/set-status/invalid-id").param("status", "SUCCESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));
        verify(paymentService, never()).setStatus(any(Payment.class), anyString());
    }
}