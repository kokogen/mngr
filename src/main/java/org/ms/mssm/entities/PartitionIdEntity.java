package org.ms.mssm.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

@MappedEntity(value = "partition")
public record PartitionIdEntity (
        @Id
        @MappedProperty("part_id")
        String partId,
        @MappedProperty("entity")
        @NonNull
        String entityName,
        @MappedProperty("dataversion_id")
        @NonNull
        Long dataVersionId,
        @MappedProperty("ets")
        @NonNull
        Long ets,
        @MappedProperty("add_part_key")
        String additionalPartKey
){}
