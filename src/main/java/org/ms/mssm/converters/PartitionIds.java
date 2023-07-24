package org.ms.mssm.converters;

import org.ms.mssm.entities.PartitionIdEntity;
import org.ms.mssm.logic.model.PartitionId;

import java.util.Set;
import java.util.stream.Collectors;

public class PartitionIds {
    public static PartitionIdEntity entity(PartitionId p){
        return new PartitionIdEntity(p.partId(), p.entityName(), p.dataVersionId(), p.ets(), p.additionalPartKey());
    }

    public static PartitionId model(PartitionIdEntity p){
        return new PartitionId(p.entityName(), p.dataVersionId(), p.ets(), p.additionalPartKey());
    }

    public static Set<PartitionIdEntity> entities(Set<PartitionId> partitionIds){
        return partitionIds.stream().map(PartitionIds::entity).collect(Collectors.toSet());
    }

    public static Set<PartitionId> models(Set<PartitionIdEntity> partitionIds){
        return partitionIds.stream().map(PartitionIds::model).collect(Collectors.toSet());
    }
}
