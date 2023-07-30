package org.ms.mssm.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.Acknowledgement;
import org.ms.mssm.logic.StateProcessor;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.StateProcessorResult;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.services.StateProcessorResultService;
import org.ms.mssm.services.UnitOfWorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


@KafkaListener(
        groupId = "mssm"
        ,offsetStrategy = OffsetStrategy.SYNC_PER_RECORD
)
public class PartitionVerListener {

    private static Logger logger = LoggerFactory.getLogger(PartitionVerListener.class);

    private final StateProcessor stateProcessor;

    private final StateProcessorResultService stateProcessorResultService;

    private final UnitOfWorkService unitOfWorkService;

    private final TaskSender taskSender;

    private Set<UnitOfWork> unitOfWorks;

    public PartitionVerListener(StateProcessorResultService stateProcessorResultService, UnitOfWorkService unitOfWorkService, TaskSender taskSender) {
        this.stateProcessorResultService = stateProcessorResultService;
        this.unitOfWorkService = unitOfWorkService;
        this.taskSender = taskSender;
        this.unitOfWorks = this.unitOfWorkService.readAll();
        this.stateProcessor = new StateProcessor(this.unitOfWorks);
    }

    @Topic("mssm-partvers")
    public void recieve(PartitionVer partitionVer, Acknowledgement acknowledgement){
        logger.info("PartitionVer received: " + partitionVer);
        StateProcessorResult stateProcessorResult = stateProcessor.process(partitionVer, null);
        stateProcessorResultService.save(stateProcessorResult);
        stateProcessorResult.tasks().forEach(taskSender::send);
        acknowledgement.ack();
    }


}
