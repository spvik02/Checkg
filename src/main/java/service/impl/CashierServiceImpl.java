package service.impl;

import annotation.CacheAlong;
import annotation.CacheMethod;
import dao.CashierDao;
import dao.Dao;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import model.Cashier;
import service.CashierService;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Service for work with CashierDao
 */
public class CashierServiceImpl implements CashierService {

    private Dao<Cashier> cashierDao = new CashierDao();

    /**
     * Returns collection of all cashiers
     * @return all cashiers
     */
    @CacheAlong(value = CacheMethod.GET)
    public Collection<Cashier> getAllCashiers() {
        return cashierDao.getAll();
    }

    /**
     * Returns Optional of cashier with passed id or Optional of empty if cashier with defined id does not exist
     * @return Optional
     */
    @CacheAlong(value = CacheMethod.GET)
    public Optional<Cashier> getCashierById(int id) {
        return cashierDao.getById(id);
    }

    /**
     * Adds new cashier if data is valid and return true. If data is invalid then return false.
     * @param cashier cashier instance
     * @return true if value was added, otherwise, false
     */
    @CacheAlong(value = CacheMethod.POST)
    public boolean saveCashier(Cashier cashier) {
        if(validate(cashier)){
            cashierDao.save(cashier);
            return true;
        }
        return false;
    }

    /**
     * Update cashier and return true if data is valid. Otherwise, returns false.
     * @param cashier
     * @return true if value was added, otherwise, false
     */
    @CacheAlong(value = CacheMethod.PUT)
    public boolean updateCashier(Cashier cashier) {
        if(validate(cashier)){
            cashierDao.update(cashier);
            return true;
        }
        return false;
    }

    /**
     * Deletes cashier by id.
     */
    @CacheAlong(value = CacheMethod.DELETE)
    @Override
    public void deleteCashier(int id) {
        cashierDao.deleteById(id);
    }

    /**
     * Validates cashier data. If data is invalid print constraint violation info to console.
     * @return true if data is valid, false if data is invalid
     */
    private boolean validate(Cashier cashier) {
        Validator validator;
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();

            Set<ConstraintViolation<Cashier>> constraintViolations = validator
                    .validate(cashier);
            if(constraintViolations.size() == 0){
                return true;
            }else{
                for (ConstraintViolation<Cashier> cv : constraintViolations) {
                    System.out.println(String.format(
                            "Error here! property: [%s], value: [%s], message: [%s]",
                            cv.getPropertyPath(), cv.getInvalidValue(), cv.getMessage()));
                }
                return false;
            }
        }
    }
}
