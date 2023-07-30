package org.ms.mssm.services;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.ms.mssm.converters.PartitionVers;
import org.ms.mssm.converters.Tasks;
import org.ms.mssm.logic.model.StateProcessorResult;
import org.ms.mssm.repositories.PartitionVerEntityRepository;
import org.ms.mssm.repositories.TaskEntityRepository;

@Singleton
public class StateProcessorResultService {
    private final UnitOfWorkService unitOfWorkService;
    private final TaskEntityRepository taskRepository;
    private final PartitionVerEntityRepository partitionVerEntityRepository;

    public StateProcessorResultService(UnitOfWorkService unitOfWorkService, TaskEntityRepository taskRepository, PartitionVerEntityRepository partitionVerRepository) {
        this.unitOfWorkService = unitOfWorkService;
        this.taskRepository = taskRepository;
        this.partitionVerEntityRepository = partitionVerRepository;
    }

    @Transactional
    public void save(StateProcessorResult stateProcessorResult){

        if(partitionVerEntityRepository.existsById(stateProcessorResult.partitionVer().partVerId()))
            partitionVerEntityRepository.update(PartitionVers.entity(stateProcessorResult.partitionVer()));
        else
            partitionVerEntityRepository.save(PartitionVers.entity(stateProcessorResult.partitionVer()));

        stateProcessorResult.unitOfWorks().forEach(unitOfWorkService::save);

        taskRepository.saveAll(Tasks.entities(stateProcessorResult.tasks()));
    }
}
