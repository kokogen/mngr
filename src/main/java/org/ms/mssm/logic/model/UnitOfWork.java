package org.ms.mssm.logic.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Serdeable
public record UnitOfWork(
        Long uowId,
        Long dagId,
        Long actualEventTimeSlot,
        Map<PartitionId, PartitionVer> partitionMap,
        Set<PartitionId> partitionIdSet,
        Long depth
) implements Comparable<UnitOfWork> {

    public UnitOfWork(Long uowId, Long dagId, Long actualEventTimeSlot, Set<PartitionId> partitionIdSet, Long lag) {
        this(uowId, dagId, actualEventTimeSlot, new HashMap<>(), partitionIdSet, lag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitOfWork that = (UnitOfWork) o;
        return Objects.equals(uowId, that.uowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uowId);
    }

    @Override
    public int compareTo(UnitOfWork o) {
        return this.hashCode() - o.hashCode();
    }
}
