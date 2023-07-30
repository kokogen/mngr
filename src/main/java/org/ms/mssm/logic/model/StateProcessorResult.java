package org.ms.mssm.logic.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record StateProcessorResult(
        PartitionVer partitionVer,
        List<UnitOfWork> unitOfWorks,
        List<Task> tasks
){}
