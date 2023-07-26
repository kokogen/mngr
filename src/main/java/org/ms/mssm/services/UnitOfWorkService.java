package org.ms.mssm.services;

import jakarta.inject.Singleton;
import org.ms.mssm.converters.UnitOfWorks;
import org.ms.mssm.entities.PartitionIdEntity;
import org.ms.mssm.entities.PartitionVerEntity;
import org.ms.mssm.entities.UnitOfWorkEntity;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.repositories.PartitionIdEntityRepository;
import org.ms.mssm.repositories.PartitionVerEntityRepository;
import org.ms.mssm.repositories.UnitOfWorkEntityRepository;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class UnitOfWorkService {
    private final UnitOfWorkEntityRepository unitOfWorkEntityRepository;
    private final PartitionVerEntityRepository partitionVerEntityRepository;
    private final PartitionIdEntityRepository partitionIdEntityRepository;

    public UnitOfWorkService(UnitOfWorkEntityRepository unitOfWorkEntityRepository, PartitionVerEntityRepository partitionVerEntityRepository, PartitionIdEntityRepository partitionIdEntityRepository) {
        this.unitOfWorkEntityRepository = unitOfWorkEntityRepository;
        this.partitionVerEntityRepository = partitionVerEntityRepository;
        this.partitionIdEntityRepository = partitionIdEntityRepository;
    }

    public Set<UnitOfWork> readAll(){
        return UnitOfWorks.models(new HashSet<>(unitOfWorkEntityRepository.findAll()));
    }

    public UnitOfWork readById(Long uowId){
        UnitOfWorkEntity uowe = unitOfWorkEntityRepository.findById(uowId).get();
        Set<PartitionVerEntity> partitionVerEntities = unitOfWorkEntityRepository.getPartitionVerListByUowId(uowId);
        Set<PartitionIdEntity> partitionIdEntities = unitOfWorkEntityRepository.getPartitionIdListByUowId(uowId);

        UnitOfWorkEntity uowe2 = new UnitOfWorkEntity(uowId, uowe.dagId(), uowe.actualEventTimeSlot(), partitionVerEntities, partitionIdEntities, uowe.depth());
        return UnitOfWorks.model(uowe2);
    }

    public void save(UnitOfWork unitOfWork){

        UnitOfWorkEntity uowe = UnitOfWorks.entity(unitOfWork);

        uowe.partitions().forEach(p -> {
            if(!partitionVerEntityRepository.existsById(p.partVerId())) partitionVerEntityRepository.save(p);
        });

        uowe.requiredPartitionIds().forEach(p -> {
            if(!partitionIdEntityRepository.existsById(p.partId())) partitionIdEntityRepository.save(p);
        });

        if(unitOfWorkEntityRepository.existsById(uowe.uowId())) {
            unitOfWorkEntityRepository.update(uowe);
            unitOfWorkEntityRepository.deletePartitionVerSetByUowId(uowe.uowId());
            unitOfWorkEntityRepository.deletePartitionIdSetByUowId(uowe.uowId());
        } else {
            unitOfWorkEntityRepository.save(uowe);
        }

        uowe.requiredPartitionIds().forEach(p -> unitOfWorkEntityRepository.addPartitionIdEntity(uowe.uowId(), p.partId()));
        uowe.partitions().forEach(pv -> unitOfWorkEntityRepository.addPartitionVerEntity(uowe.uowId(), pv.partVerId()));
    }

}
