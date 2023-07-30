package org.ms.mssm.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.ms.mssm.logic.model.Task;

@KafkaClient(
        id = "mssm-task-sender"
)
public interface TaskSender {
    @Topic("mssm-tasks")
    void send(Task task);
}
