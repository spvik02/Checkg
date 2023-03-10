package service.proxy;

import annotation.CacheAlong;
import annotation.CacheMethod;
import model.Cashier;
import service.CashierService;
import service.handler.CashierServiceHandler;
import service.impl.CashierServiceImpl;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Optional;

/**
 * CashierServiceProxy act like instances of CashierService interfaces but allow for customized method invocation
 * with additional logic in CashierServiceHandler.
 */
public class CashierServiceProxy implements CashierService {
    /**
     * Replace the implementations of CashierServiceImpl with Proxy instance for the specified interfaces
     * that dispatches method invocations to the specified invocation handler.
     */
    private static CashierService cashierService;
    static {
        cashierService = new CashierServiceImpl();
        ClassLoader cashierServiceClassLoader = cashierService.getClass().getClassLoader();
        Class<?>[] cashierServiceInterfaces = cashierService.getClass().getInterfaces();
        cashierService = (CashierService) Proxy.newProxyInstance(cashierServiceClassLoader,
                cashierServiceInterfaces, new CashierServiceHandler(cashierService));
    }

    @Override
    public Collection<Cashier> getAllCashiers() {
        return cashierService.getAllCashiers();
    }

    @Override
    public Optional<Cashier> getCashierById(int id) {
        return cashierService.getCashierById(id);
    }

    @Override
    public boolean saveCashier(Cashier cashier) {
        return cashierService.saveCashier(cashier);
    }

    @Override
    public boolean updateCashier(Cashier cashier) {
        return cashierService.updateCashier(cashier);
    }

    @Override
    public void deleteCashier(int id) {
        cashierService.deleteCashier(id);
    }
}
