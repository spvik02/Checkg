package dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    List<T> getAll();
    Optional<T> getById(int id);
    void save(T t);
    void update(T t);
    void deleteById(int id);
}
