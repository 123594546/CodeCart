package com.codecart.common.constants;

public final class BusinessConstants {

    private BusinessConstants() {
    }

    public static final class RoleCode {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";

        private RoleCode() {
        }
    }

    public static final class UserStatus {
        public static final String ENABLED = "ENABLED";
        public static final String DISABLED = "DISABLED";
        public static final String LOCKED = "LOCKED";

        private UserStatus() {
        }
    }

    public static final class CategoryStatus {
        public static final String ENABLED = "ENABLED";
        public static final String DISABLED = "DISABLED";

        private CategoryStatus() {
        }
    }

    public static final class ProductStatus {
        public static final String ON_SALE = "ON_SALE";
        public static final String OFF_SALE = "OFF_SALE";

        private ProductStatus() {
        }
    }

    public static final class OrderStatus {
        public static final String PENDING_PAYMENT = "PENDING_PAYMENT";
        public static final String COMPLETED = "COMPLETED";
        public static final String PAY_FAILED = "PAY_FAILED";
        public static final String CANCELLED = "CANCELLED";

        private OrderStatus() {
        }
    }

    public static final class OrderSourceType {
        public static final String DIRECT = "DIRECT";
        public static final String CART = "CART";

        private OrderSourceType() {
        }
    }

    public static final class OrderPayStatus {
        public static final String UNPAID = "UNPAID";
        public static final String PAID = "PAID";
        public static final String FAILED = "FAILED";

        private OrderPayStatus() {
        }
    }

    public static final class PayRecordStatus {
        public static final String WAIT_PAY = "WAIT_PAY";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";

        private PayRecordStatus() {
        }
    }

    public static final class PayMethod {
        public static final String MOCK = "MOCK";

        private PayMethod() {
        }
    }

    public static final class CodeStatus {
        public static final String UNUSED = "UNUSED";
        public static final String LOCKED = "LOCKED";
        public static final String ISSUED = "ISSUED";

        private CodeStatus() {
        }
    }

    public static final class BatchStatus {
        public static final String PROCESSING = "PROCESSING";
        public static final String COMPLETED = "COMPLETED";
        public static final String PARTIAL_FAILED = "PARTIAL_FAILED";
        public static final String FAILED = "FAILED";

        private BatchStatus() {
        }
    }

    public static final class IssueStatus {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";

        private IssueStatus() {
        }
    }
}
