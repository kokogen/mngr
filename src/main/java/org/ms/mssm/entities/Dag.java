package org.ms.mssm.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import org.ms.mssm.entities.PartitionTemplate;

import java.util.Set;

@MappedEntity(value = "dag")
public record Dag (
        @Id
        @MappedProperty("dag_id")
        Long dagId,
        @MappedProperty("name")
        String name,
        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "dag")
        Set<PartitionTemplate> partitionTemplates,
        @MappedProperty("slot_dur")
        @NonNull
        Long slotDurationInMillis,
        @MappedProperty("depth")
        @NonNull
        Long depth){
}
