package org.ms.mssm.converters;

import org.ms.mssm.entities.UnitOfWorkEntity;
import org.ms.mssm.logic.model.UnitOfWork;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UnitOfWorks {
    public static UnitOfWorkEntity entity(UnitOfWork unitOfWork){
        return new UnitOfWorkEntity(
                unitOfWork.uowId(),
                unitOfWork.dagId(),
                unitOfWork.actualEventTimeSlot(),
                PartitionVers.entities(unitOfWork.partitionMap().values()),
                PartitionIds.entities(unitOfWork.partitionIdSet()),
                unitOfWork.depth()
        );
    }

    public static UnitOfWork model(UnitOfWorkEntity unitOfWorkEntity){
        return new UnitOfWork(
                unitOfWorkEntity.uowId(),
                unitOfWorkEntity.dagId(),
                unitOfWorkEntity.actualEventTimeSlot(),
                PartitionVers.modelsMap(unitOfWorkEntity.partitions()),
                PartitionIds.models(unitOfWorkEntity.requiredPartitionIds()),
                unitOfWorkEntity.depth()
        );
    }

    public static Set<UnitOfWorkEntity> entities(List<UnitOfWork> unitOfWorks){
        return unitOfWorks.stream().map(UnitOfWorks::entity).collect(Collectors.toSet());
    }

    public static Set<UnitOfWork> models(Set<UnitOfWorkEntity> unitOfWorks){
        return unitOfWorks.stream().map(UnitOfWorks::model).collect(Collectors.toSet());
    }
}
