package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {
    PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
    }

    @Test
    void testSaveAndFindById() {
        Payment payment = new Payment("1", "VOUCHER", new HashMap<>());
        paymentRepository.save(payment);
        Payment found = paymentRepository.findById("1");
        assertNotNull(found);
        assertEquals("1", found.getId());
    }

    @Test
    void testGetAllPayments() {
        Payment p1 = new Payment("1", "VOUCHER", new HashMap<>());
        Payment p2 = new Payment("2", "BANK", new HashMap<>());
        paymentRepository.save(p1);
        paymentRepository.save(p2);
        assertEquals(2, paymentRepository.getAllPayments().size());
    }
}