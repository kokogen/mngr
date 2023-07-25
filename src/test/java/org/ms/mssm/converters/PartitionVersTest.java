package org.ms.mssm.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ms.mssm.logic.model.PartitionId;

import static org.junit.jupiter.api.Assertions.*;

class PartitionVersTest {

    @Test
    void modelFromKey() {
        String partId = "subject/10101/1677196800000/null";

        PartitionId partitionId = PartitionVers.modelFromKey(partId);
        Assertions.assertEquals(partId, partitionId.partId());
    }
}