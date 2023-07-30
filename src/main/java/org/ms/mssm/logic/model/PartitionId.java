package org.ms.mssm.logic.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PartitionId(
    String entityName,
    Long dataVersionId,
    Long ets,
    String additionalPartKey){

    public String partId(){
        StringBuilder sb = new StringBuilder();
        sb
                .append(entityName)
                .append("/").append(dataVersionId)
                .append("/").append(ets).append("/");

        if (additionalPartKey != null)
            sb.append(additionalPartKey);
        else
            sb.append("null");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {

        if(o == null) return false;
        if(!(o instanceof PartitionId)) return false;

        try {
            PartitionId obj = (PartitionId)o;
            /*
            if ((this.entityName != null) && (obj.entityName == null)) return false;
            if ((this.entityName == null) && (obj.entityName != null)) return false;
            if ((this.dataVersionId != null) && (obj.dataVersionId == null)) return false;
            if ((this.dataVersionId == null) && (obj.dataVersionId != null)) return false;
            if ((this.ets != null) && (obj.ets == null)) return false;
            if ((this.ets == null) && (obj.ets != null)) return false;
            if ((this.additionalPartKey != null) && (obj.additionalPartKey == null)) return false;
            if ((this.additionalPartKey == null) && (obj.additionalPartKey != null)) return false;
            boolean a = (this.entityName == null) && (obj.entityName == null);
            boolean b = (this.dataVersionId == null) && (obj.dataVersionId == null);
            boolean c = (this.ets == null) && (obj.ets == null);
            boolean d = (this.additionalPartKey == null) && (obj.additionalPartKey == null);
            return
                    (a || this.entityName.equals(obj.entityName)) &&
                    (b || this.dataVersionId.equals(obj.dataVersionId)) &&
                    (c || this.ets.equals(obj.ets)) &&
                    (d || this.additionalPartKey.equals(obj.additionalPartKey));
             */
            return this.partId().equals(obj.partId());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return  this.partId().hashCode();
        //return Objects.hash(entityName, dataVersionId, ets, additionalPartKey);
    }

    @Override
    public String toString() {
        return partId();
        /*
        return "{"+
                "ent=" + entityName +
                ", ver=" + dataVersionId +
                ", ets=" + ets +
                ", add=" + additionalPartKey
                +"}";*/
    }
}
