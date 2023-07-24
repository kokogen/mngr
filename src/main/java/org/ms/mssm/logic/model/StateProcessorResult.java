package org.ms.mssm.logic.model;

import java.util.List;

public record StateProcessorResult(
        PartitionVer partitionVer,
        List<UnitOfWork> unitOfWorks,
        List<Task> tasks
){}
