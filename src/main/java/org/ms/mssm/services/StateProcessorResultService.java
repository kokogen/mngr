package org.ms.mssm.services;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.ms.mssm.converters.PartitionVers;
import org.ms.mssm.converters.Tasks;
import org.ms.mssm.converters.UnitOfWorks;
import org.ms.mssm.logic.model.StateProcessorResult;
import org.ms.mssm.repositories.PartitionVerEntityRepository;
import org.ms.mssm.repositories.TaskEntityRepository;
import org.ms.mssm.repositories.UnitOfWorkEntityRepository;

@Singleton
public class StateProcessorResultService {
    private final UnitOfWorkEntityRepository unitOfWorkEntityRepository;
    private final TaskEntityRepository taskRepository;
    private final PartitionVerEntityRepository partitionVerEntityRepository;

    public StateProcessorResultService(UnitOfWorkEntityRepository unitOfWorkEntityRepository, TaskEntityRepository taskRepository, PartitionVerEntityRepository partitionVerRepository) {
        this.unitOfWorkEntityRepository = unitOfWorkEntityRepository;
        this.taskRepository = taskRepository;
        this.partitionVerEntityRepository = partitionVerRepository;
    }

    @Transactional
    public void save(StateProcessorResult stateProcessorResult){
        partitionVerEntityRepository.save(PartitionVers.entity(stateProcessorResult.partitionVer()));
        unitOfWorkEntityRepository.updateAll(UnitOfWorks.entities(stateProcessorResult.unitOfWorks()));
        taskRepository.saveAll(Tasks.entities(stateProcessorResult.tasks()));
    }
}
