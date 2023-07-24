package org.ms.mssm.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.data.annotation.sql.JoinTable;

import java.util.Set;

@MappedEntity(value = "task")
public record TaskEntity (
        @NonNull
        @MappedProperty("uow_id")
        Long uowId,
        @MappedProperty("dag_id")
        @NonNull
        Long dagId,
        @MappedProperty("ets")
        @NonNull
        Long actualEventTimeSlot,
        @Id
        @MappedProperty("task_id")
        Long taskId,
        @Relation(Relation.Kind.MANY_TO_MANY)
        @JoinTable(
                name = "task_partition",
                joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "task_id"),
                inverseJoinColumns = @JoinColumn(name = "part_ver_id", referencedColumnName = "part_ver_id")
                        /*{
                        @JoinColumn(name = "entity", referencedColumnName = "entity"),
                        @JoinColumn(name = "dataversion_id", referencedColumnName = "dataversion_id"),
                        @JoinColumn(name = "ets", referencedColumnName = "ets"),
                        @JoinColumn(name = "add_part_key", referencedColumnName = "add_part_key")
                }*/
        )
        Set<PartitionVerEntity> partitions
){}
