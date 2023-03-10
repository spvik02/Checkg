package service.handler;

import cache.Cache;
import model.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CashierService;
import service.impl.CashierServiceImpl;
import service.proxy.CashierServiceProxy;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashierServiceHandlerTest {
    private CashierService cashierService = new CashierServiceImpl();

    @InjectMocks
    private CashierServiceHandler cashierServiceHandler = new CashierServiceHandler(cashierService);

    @Mock
    private CashierServiceProxy cashierServiceProxy;
    @Mock
    private Cache<Integer, Cashier> cashierCache;

    @Test
    void checkInvokeOnGetCashierByIdShouldGetAndPutToCash1Time() {
        Class[] arg = new Class[1];
        arg[0] = int.class;
        Method methodGetCashierById;
        try {
            methodGetCashierById = cashierService.getClass().getMethod("getCashierById", arg);
            cashierServiceHandler.invoke(cashierServiceProxy, methodGetCashierById, new Object[]{1});
            verify(cashierCache, times(1)).get(anyInt());
            verify(cashierCache, times(1)).put(anyInt(), any());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void checkInvokeOnGetCashierByIdShouldPutToCash0Time() {
        doReturn(new Cashier()).when(cashierCache).get(1);
        Class[] arg = new Class[1];
        arg[0] = int.class;
        Method methodGetCashierById;
        try {
            methodGetCashierById = cashierService.getClass().getMethod("getCashierById", arg);
            cashierServiceHandler.invoke(cashierServiceProxy, methodGetCashierById, new Object[]{1});
            verify(cashierCache, times(0)).put(anyInt(), any());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void checkInvokeOnPutInvalidDataShouldPutToCash0Time() {
        Class[] arg = new Class[]{Cashier.class};
        Method methodUpdateCashier;
        try {
            methodUpdateCashier = cashierService.getClass().getMethod("updateCashier", arg);
            cashierServiceHandler.invoke(cashierServiceProxy, methodUpdateCashier, new Object[]{new Cashier()});
            verify(cashierCache, times(0)).put(anyInt(), any());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void checkInvokeOnPutValidDataShouldPutToCash1Time() {
        doNothing().when(cashierCache).put(any(), any());
        Cashier cashier = new Cashier(1, "cas name", "cashier", "casname@gmail.com");
        Class[] arg = new Class[]{Cashier.class};
        Method methodUpdateCashier;
        try {
            methodUpdateCashier = cashierService.getClass().getMethod("updateCashier", arg);
            boolean actualResult = (boolean) cashierServiceHandler.invoke(cashierServiceProxy, methodUpdateCashier, new Object[]{cashier});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        verify(cashierCache, times(1)).put(any(), any());
    }

    @Test
    void checkInvokeOnDeleteShouldDeleteFromCash1Time() {
        Class[] arg = new Class[]{int.class};
        Method methodUpdateCashier;
        try {
            methodUpdateCashier = cashierService.getClass().getMethod("deleteCashier", arg);
            cashierServiceHandler.invoke(cashierServiceProxy, methodUpdateCashier, new Object[]{1});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        verify(cashierCache, times(1)).delete(anyInt());
    }
}
