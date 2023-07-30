package org.ms.mssm.logic.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Objects;

@Serdeable
public record PartitionVer(
        PartitionId partitionId,
        Long ver,
        String sessionId,
        String operationId
){
    public String partVerId(){
        return partitionId + "/" + ver;
    }

    public boolean equals(Object o) {
        if(!(o instanceof PartitionVer)) return false;
        try {
            PartitionVer obj = (PartitionVer)o;
            return (this.partitionId.equals(obj.partitionId())) &&
                    (this.ver.equals(obj.ver()));
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(partitionId, ver);
    }
}
