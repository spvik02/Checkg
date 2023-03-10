package dao;

import model.Cashier;

import java.util.*;

public class CashierDao implements Dao<Cashier> {
    /**
     * Map with cashiers where key represents id and value represents instance of the Cashier class.
     */
    private Map<Integer, Cashier> cashiers = new HashMap<>();

    public CashierDao() {
        cashiers.put(1, new Cashier(1, "Donald Duck", "Junior Cashier", "DonnyD@domain.com"));
        cashiers.put(2, new Cashier(2, "Clint Barton", "Senior Cashier", "ClintBarton@gmail.com"));
        cashiers.put(3, new Cashier(3, "Finch Penguin", "Senior Cashier", "Pengui@gmail.com"));
        cashiers.put(4, new Cashier(4, "Cashier one", "Junior Cashier", "cashier1@gmail.com"));
    }

    @Override
    public Optional<Cashier> getById(int id) {
        return Optional.ofNullable(cashiers.getOrDefault(id, null));
    }

    @Override
    public List<Cashier> getAll() {
        return cashiers.values().stream().toList();
    }

    @Override
    public void save(Cashier cashier) {
        cashiers.put(cashier.getId(), cashier);
    }

    @Override
    public void update(Cashier cashier) {
        cashiers.put(cashier.getId(), cashier);
    }

    @Override
    public void deleteById(int id) {
        cashiers.remove(id);
    }
}
