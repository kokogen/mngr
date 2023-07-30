package org.ms.mssm.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.data.annotation.sql.JoinTable;

import java.util.Set;

@MappedEntity(value = "uow")
public record UnitOfWorkEntity (
        @Id
        @MappedProperty("uow_id")
        Long uowId,
        @NonNull
        @MappedProperty("dag_id")
        Long dagId,
        @NonNull
        @MappedProperty("ets")
        Long actualEventTimeSlot,

        @Relation(Relation.Kind.MANY_TO_MANY)
        @JoinTable(
                name = "uow_partition_ver",
                joinColumns = @JoinColumn(name = "uow_id", referencedColumnName = "uow_id"),
                inverseJoinColumns = @JoinColumn(name = "part_ver_id", referencedColumnName = "part_ver_id")
        )
        Set<PartitionVerEntity> partitions,
        @Relation(Relation.Kind.MANY_TO_MANY)
        @JoinTable(
                name = "uow_required_partition",
                joinColumns = @JoinColumn(name = "uow_id", referencedColumnName = "uow_id"),
                inverseJoinColumns = @JoinColumn(name = "part_id", referencedColumnName = "part_id")
        )
        Set<PartitionIdEntity> requiredPartitionIds,
        @NonNull
        @MappedProperty("depth")
        Long depth
){}
