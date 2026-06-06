package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
public class Payment {
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_REJECTED = "REJECTED";

    private final String id;
    private final String method;
    private final Map<String, String> paymentData;

    @Setter
    private String status;

    public Payment(String id, String method, Map<String, String> paymentData) {
        this.id = id;
        this.method = method;
        this.paymentData = paymentData;

        if ("VOUCHER".equals(method)) {
            this.status = validateVoucher(paymentData.get("voucherCode")) ? STATUS_SUCCESS : STATUS_REJECTED;
        } else if ("BANK".equals(method)) {
            this.status = validateBankTransfer(paymentData) ? STATUS_SUCCESS : STATUS_REJECTED;
        } else {
            this.status = STATUS_REJECTED;
        }
    }

    private boolean validateVoucher(String code) {
        if (code == null || code.length() != 16 || !code.startsWith("ESHOP")) return false;
        long numCount = code.chars().filter(Character::isDigit).count();
        return numCount == 8;
    }

    private boolean validateBankTransfer(Map<String, String> data) {
        String bankName = data.get("bankName");
        String refCode = data.get("referenceCode");
        return bankName != null && !bankName.isEmpty() && refCode != null && !refCode.isEmpty();
    }
}