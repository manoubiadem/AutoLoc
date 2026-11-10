package tn.esprit.autoloc.service;

public interface IGenericService<T, ID>
        extends ICheckGenericService<T>,
        IReadGenericService<T, ID>,
        IWriteGenericService<T, ID> {
}