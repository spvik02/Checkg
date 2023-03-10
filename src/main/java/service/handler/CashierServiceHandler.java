package service.handler;

import annotation.CacheAlong;
import annotation.CacheMethod;
import cache.Cache;
import cache.CacheFactory;
import model.Cashier;
import service.CashierService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Handler with invoke method that add logic to specified interface method calls
 */
public class CashierServiceHandler implements InvocationHandler {
    /**
     * Interface for which method calls add logic
     */
    private final CashierService cashierService;
    /**
     * Cache based on .yaml file
     */
    private Cache<Integer, Cashier> cashierCache = new CacheFactory<Integer, Cashier>().createCache();
    public CashierServiceHandler(CashierService cashierService) {
        this.cashierService = cashierService;
    }
    /**
     * Additional Cache logic to invocation methods annotated with CacheAlong annotation.
     * GET - ищем в кеше и если там данных нет, то достаем объект из dao, сохраняем в кеш и возвращаем.
     * POST - сохраняем в dao и потом сохраняем в кеше.
     * DELETE - удаляем из dao и потом удаляем в кеша.
     * PUT - обновление/вставка в dao и потом обновление/вставка в кеше.
     *
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke;
        CacheAlong annotation = cashierService.getClass()
                .getMethod(method.getName(), method.getParameterTypes())
                .getAnnotation(CacheAlong.class);

        if(annotation != null){
            CacheMethod value = annotation.value();
            switch (value) {
                case GET:
                    if (args == null) {
                        invoke = method.invoke(cashierService, args);
                    } else {
                        invoke = cashierCache.get((Integer) args[0]);
                        if (invoke != null) {
                            invoke = Optional.of(invoke);
                        }
                        if (invoke == null) {
                            invoke = method.invoke(cashierService, args);
                            if (((Optional) invoke).isPresent()) {
                                cashierCache.put((Integer) args[0], (Cashier) ((Optional) invoke).get());
                            }
                        }
                    }
                    break;
                case PUT, POST:
                    invoke = method.invoke(cashierService, args);
                    if ((boolean) invoke) {
                        cashierCache.put(((Cashier) args[0]).getId(), (Cashier) args[0]);
                    }
                    break;
                case DELETE:
                    invoke = method.invoke(cashierService, args);
                    cashierCache.delete((int) args[0]);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }else {
            invoke = method.invoke(cashierService, args);
        }
        return invoke;
    }
}
