package org.ms.mssm.kafka;

// It's not used yet
public record KafkaPosition (
        String topic,
        int partition,
        long offset
){}
