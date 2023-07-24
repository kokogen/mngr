package org.ms.mssm.entities;


import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;

@MappedEntity(value = "dag_partition")
public record PartitionTemplate(
        @Id
        Long partitionTemplateId,
        @NonNull
        String entityName,
        @NonNull
        Long dataVersionId,
        String additionalPartKey,
        Long timeShift,
        @Relation(value = Relation.Kind.MANY_TO_ONE)
        Dag dag){
}
