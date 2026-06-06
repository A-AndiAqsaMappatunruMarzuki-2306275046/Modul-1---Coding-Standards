package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void testCreateOrderPage() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createOrder"));
    }

    @Test
    void testOrderHistoryPage() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"));
    }

    @Test
    void testOrderHistoryPost() throws Exception {
        when(orderService.findAllByAuthor("Andi")).thenReturn(new ArrayList<>());
        mockMvc.perform(post("/order/history").param("author", "Andi"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistoryResult"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("author"));
    }

    @Test
    void testPayOrderPage() throws Exception {
        mockMvc.perform(get("/order/pay/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("payOrder"))
                .andExpect(model().attributeExists("orderId"));
    }

    @Test
    void testPayOrderPost() throws Exception {
        mockMvc.perform(post("/order/pay/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("payOrderSuccess"))
                .andExpect(model().attributeExists("paymentId"));
    }
}