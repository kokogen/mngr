package org.ms.mssm.logic.model;

import java.util.Set;

public record Task(
        Long uowId,
        Long dagId,
        Long actualEventTimeSlot,
        Long taskId,
        Set<PartitionVer> partitions
){}
