package service.impl;

import model.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CashierServiceImplTest {
    CashierServiceImpl cashierService = new CashierServiceImpl();
    Cashier cashier;

    @BeforeEach
    void setUp() {
        cashier = new Cashier(5, "me probably 123", "cashier", "cashier.com");
    }

    @Test
    void checkSaveCashierReturnsFalseIfInvalid() {
        Boolean expectedResultOfSave = cashierService.saveCashier(cashier);
        assertThat(expectedResultOfSave).isFalse();
    }
    @Test
    void checkUpdateCashierReturnsFalseIfInvalid() {
        Boolean expectedResultOfSave = cashierService.saveCashier(cashier);
        assertThat(expectedResultOfSave).isFalse();
    }
}
