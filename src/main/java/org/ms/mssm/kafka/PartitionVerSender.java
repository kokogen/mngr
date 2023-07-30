package org.ms.mssm.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.ms.mssm.logic.model.PartitionVer;

@KafkaClient(
        id = "mssm-partver-sender"
)
public interface PartitionVerSender {
    @Topic("mssm-partvers")
    void send(PartitionVer partitionVer);
}
