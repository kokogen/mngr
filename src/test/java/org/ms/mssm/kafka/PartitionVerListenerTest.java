package org.ms.mssm.kafka;

import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.ms.mssm.logic.model.PartitionId;
import org.ms.mssm.logic.model.PartitionVer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class PartitionVerListenerTest {

    @Test
    public void test_partitionVer_send(){
        try(ApplicationContext applicationContext = ApplicationContext.run()) {
            PartitionVerSender partitionVerSender = applicationContext.getBean(PartitionVerSender.class);

            LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0));
            PartitionId partitionId = new PartitionId("subject", 10_101L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null);
            PartitionVer partitionVer = new PartitionVer(partitionId, ldt.plusHours(25).toInstant(ZoneOffset.UTC).toEpochMilli(), "sess#1", "oper#1");

            System.out.println("_-_-__________-_-_");
            partitionVerSender.send(partitionVer);
        }
    }

}