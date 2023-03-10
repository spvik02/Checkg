package service;
import model.Cashier;

import java.util.Collection;
import java.util.Optional;

public interface CashierService {
    /**
     * Returns collection of all cashiers
     * @return all cashiers
     */
    Collection<Cashier> getAllCashiers();
    /**
     * Returns Optional of cashier with passed id or Optional of empty if cashier with defined id does not exist
     * @return Optional
     */
    Optional<Cashier> getCashierById(int id);
    /**
     * Save cashier and return true. Otherwise, returns false.
     * @return true if value was added, otherwise, false
     */
    boolean saveCashier(Cashier cashier);
    /**
     * Update cashier and return true. Otherwise, returns false.
     * @return true if value was added, otherwise, false
     */
    boolean updateCashier(Cashier cashier);
    /**
     * Deletes cashier by id.
     */
    void deleteCashier(int id);
}
