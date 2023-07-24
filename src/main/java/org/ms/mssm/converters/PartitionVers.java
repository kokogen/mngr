package org.ms.mssm.converters;

import org.ms.mssm.entities.PartitionVerEntity;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.PartitionId;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PartitionVers {
    public static PartitionVerEntity entity(PartitionVer partitionVer){
        return new PartitionVerEntity(
                partitionVer.partVerId(),
                partitionVer.partitionId().partId(),
                partitionVer.ver(),
                partitionVer.sessionId(),
                partitionVer.operationId());
    }

    public static PartitionId modelFromKey(String partId){
        String[] tokens = partId.split("/");
        String entityName = tokens[0];
        Long dataVersionId = Long.getLong(tokens[1]);
        Long ets = Long.getLong(tokens[2]);
        String addPart = tokens[3];
        if(addPart.equals("null")) addPart = null;
        return new PartitionId(entityName, dataVersionId, ets, addPart);
    }

    public static PartitionVer model(PartitionVerEntity partitionVerEntity){
        PartitionId partitionId = modelFromKey(partitionVerEntity.partId());
        return new PartitionVer(
                        partitionId,
                        partitionVerEntity.ver(),
                        partitionVerEntity.sessionId(),
                        partitionVerEntity.operationId());
}

    public static Set<PartitionVerEntity> entities(Collection<PartitionVer> partitions){
        return partitions.stream().map(PartitionVers::entity).collect(Collectors.toSet());
    }

    public static Set<PartitionVer> models(Set<PartitionVerEntity> partitions){
        return partitions.stream().map(PartitionVers::model).collect(Collectors.toSet());
    }

    public static Map<PartitionId, PartitionVer> modelsMap(Set<PartitionVerEntity> partitions){
        return partitions.stream().map(PartitionVers::model).collect(Collectors.toMap(PartitionVer::partitionId, p -> p));
    }
}
