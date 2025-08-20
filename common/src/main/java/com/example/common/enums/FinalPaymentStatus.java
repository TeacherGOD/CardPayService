package com.example.common.enums;

public enum FinalPaymentStatus {
    APPROVED,
    DECLINED;

    public static FinalPaymentStatus fromSource(PaymentStatus source) {
        return source==PaymentStatus.APPROVED?FinalPaymentStatus.APPROVED:FinalPaymentStatus.DECLINED;
    }
}
