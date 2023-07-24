package org.ms.mssm.services;

import jakarta.inject.Singleton;
import org.ms.mssm.converters.UnitOfWorks;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.repositories.UnitOfWorkEntityRepository;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class UnitOfWorkService {
    private final UnitOfWorkEntityRepository unitOfWorkEntityRepository;

    public UnitOfWorkService(UnitOfWorkEntityRepository unitOfWorkEntityRepository) {
        this.unitOfWorkEntityRepository = unitOfWorkEntityRepository;
    }

    public Set<UnitOfWork> readAll(){
        return UnitOfWorks.models(new HashSet<>(unitOfWorkEntityRepository.findAll()));
    }

}
