package org.ms.mssm.kafka;

import io.micronaut.configuration.kafka.ConsumerAware;
import io.micronaut.configuration.kafka.KafkaConsumerFactory;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.configuration.kafka.config.KafkaConsumerConfiguration;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.messaging.Acknowledgement;
import io.micronaut.messaging.annotation.SendTo;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.ms.mssm.logic.StateProcessor;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.StateProcessorResult;
import org.ms.mssm.logic.model.Task;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.services.StateProcessorResultService;
import org.ms.mssm.services.UnitOfWorkService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

// It's not used yet
public class PartitionVerHandler implements ConsumerRebalanceListener, ConsumerAware {

    private Consumer consumer;
    private final StateProcessor stateProcessor;

    private final StateProcessorResultService stateProcessorResultService;

    private final UnitOfWorkService unitOfWorkService;

    private Set<UnitOfWork> unitOfWorks;

    public PartitionVerHandler(Consumer consumer, StateProcessorResultService stateProcessorResultService, UnitOfWorkService unitOfWorkService) {


        this.consumer = consumer;

        this.stateProcessorResultService = stateProcessorResultService;
        this.unitOfWorkService = unitOfWorkService;
        this.unitOfWorks = unitOfWorkService.readAll();
        stateProcessor = new StateProcessor(unitOfWorks);
    }


    public List<Task> handle(PartitionVer partitionVer, Acknowledgement acknowledgement){
        StateProcessorResult stateProcessorResult = stateProcessor.process(partitionVer, null);
        stateProcessorResultService.save(stateProcessorResult);
        acknowledgement.ack();
        return stateProcessorResult.tasks();
    }
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {
        // Changes of Set<UnitOfWork> saves handle
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> collection) {
        unitOfWorks = unitOfWorkService.readAll();
        // ToDo: read actual saved offset and set it to kafka
    }

    @Override
    public void setKafkaConsumer(@NonNull Consumer consumer) {
        this.consumer = consumer;
    }
}
