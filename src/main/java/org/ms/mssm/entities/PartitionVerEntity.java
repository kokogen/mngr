package org.ms.mssm.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

@MappedEntity(value = "partition_ver")
public record PartitionVerEntity(
        @Id
        @MappedProperty("part_ver_id")
        String partVerId,
        @NonNull
        @MappedProperty("part_id")
        String partId,
        @NonNull
        @MappedProperty("ver")
        Long ver,
        @MappedProperty("session_id")
        @NonNull
        String sessionId,
        @MappedProperty("operation_id")
        @NonNull
        String operationId
){}
