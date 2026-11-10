package tn.esprit.autoloc.service;

public interface IWriteGenericService<T, ID> {

    T create(T entity);

    T update(ID id, T entity);

    void deleteById(ID id);
}