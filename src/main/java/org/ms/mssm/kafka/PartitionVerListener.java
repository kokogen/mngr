package org.ms.mssm.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.Acknowledgement;
import io.micronaut.messaging.annotation.SendTo;
import org.ms.mssm.logic.StateProcessor;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.StateProcessorResult;
import org.ms.mssm.logic.model.Task;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.services.StateProcessorResultService;
import org.ms.mssm.services.UnitOfWorkService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@KafkaListener(
        groupId = "mssm"
        ,offsetStrategy = OffsetStrategy.SYNC_PER_RECORD
)
public class PartitionVerListener {

    private final StateProcessor stateProcessor;

    private final StateProcessorResultService stateProcessorResultService;

    private final UnitOfWorkService unitOfWorkService;

    private final TaskSender taskSender;

    private Set<UnitOfWork> unitOfWorks;

    public PartitionVerListener(StateProcessorResultService stateProcessorResultService, UnitOfWorkService unitOfWorkService, TaskSender taskSender) {
        this.stateProcessorResultService = stateProcessorResultService;
        this.unitOfWorkService = unitOfWorkService;
        this.taskSender = taskSender;
        this.unitOfWorks = new HashSet<UnitOfWork>();
        this.stateProcessor = new StateProcessor(this.unitOfWorks);
    }

    @Topic("mssm-parts")
    public void recieve(PartitionVer partitionVer, Acknowledgement acknowledgement){
        StateProcessorResult stateProcessorResult = stateProcessor.process(partitionVer, null);
        stateProcessorResultService.save(stateProcessorResult);
        stateProcessorResult.tasks().forEach(taskSender::sendTask);
        acknowledgement.ack();
    }


}
