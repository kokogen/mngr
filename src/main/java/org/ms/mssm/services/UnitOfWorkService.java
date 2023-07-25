package org.ms.mssm.services;

import jakarta.inject.Singleton;
import org.ms.mssm.converters.UnitOfWorks;
import org.ms.mssm.entities.UnitOfWorkEntity;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.repositories.UnitOfWorkEntityRepository;

import java.util.HashSet;
import java.util.Optional;
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

    public UnitOfWork readById(Long uowId){
        return UnitOfWorks.model(unitOfWorkEntityRepository.findById(uowId).get());
    }

    public void save(UnitOfWork unitOfWork){

        UnitOfWorkEntity uowe = UnitOfWorks.entity(unitOfWork);

        if(unitOfWorkEntityRepository.existsById(unitOfWork.uowId())) {
            unitOfWorkEntityRepository.update(uowe);
            unitOfWorkEntityRepository.deletePartitionVerSetByUowId(unitOfWork.uowId());
        } else
            unitOfWorkEntityRepository.save(uowe);

        unitOfWork.partitionMap().values().forEach(pv -> unitOfWorkEntityRepository.addPartitionVerEntity(unitOfWork.uowId(), pv.partVerId()));
    }

}
