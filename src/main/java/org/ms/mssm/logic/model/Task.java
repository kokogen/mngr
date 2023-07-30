package org.ms.mssm.logic.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Set;

@Serdeable
public record Task(
        Long uowId,
        Long dagId,
        Long actualEventTimeSlot,
        Long taskId,
        Set<PartitionVer> partitions
){}
