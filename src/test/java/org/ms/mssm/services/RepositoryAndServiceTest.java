package org.ms.mssm.services;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.ms.mssm.converters.PartitionIds;
import org.ms.mssm.converters.PartitionVers;
import org.ms.mssm.entities.PartitionVerEntity;
import org.ms.mssm.logic.model.PartitionId;
import org.ms.mssm.entities.PartitionIdEntity;
import org.ms.mssm.entities.UnitOfWorkEntity;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.repositories.PartitionIdEntityRepository;
import org.ms.mssm.repositories.PartitionVerEntityRepository;
import org.ms.mssm.repositories.TaskEntityRepository;
import org.ms.mssm.repositories.UnitOfWorkEntityRepository;

import java.time.*;
import java.util.Optional;
import java.util.Set;

@MicronautTest
class RepositoryAndServiceTest {

    @Inject
    private StateProcessorResultService stateProcessorResultService;
    @Inject
    private UnitOfWorkEntityRepository unitOfWorkEntityRepository;
    @Inject
    private TaskEntityRepository taskRepository;
    @Inject
    private PartitionVerEntityRepository partitionVerEntityRepository;
    @Inject
    private PartitionIdEntityRepository partitionIdEntityRepository;



    private UnitOfWorkEntity createUoW(){
        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0));
        PartitionId partitionId  = new PartitionId("subject",  10_101L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null);
        PartitionId partitionId1 = new PartitionId("property", 10_201L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null);
        PartitionVer partitionVer = new PartitionVer(partitionId, ldt.plusHours(25).toInstant(ZoneOffset.UTC).toEpochMilli(), "sess#1", "oper#1");

        Set<PartitionVerEntity> parts1 = Set.of(
                PartitionVers.entity(partitionVer)
        );

        Set<PartitionIdEntity> parts2 = Set.of(
                PartitionIds.entity(partitionId),
                PartitionIds.entity(partitionId1)
        );

        long depth = 86400 * 2 * 1000;
        return new UnitOfWorkEntity(1l, 1l, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), parts1, parts2, depth);
    }

    @Test
    public void test_save_and_read(){
        UnitOfWorkEntity uow_in = createUoW();
        UnitOfWorkEntity uow = unitOfWorkEntityRepository.save(uow_in);
        System.out.println(uow);
        Optional<UnitOfWorkEntity> uowOptional = unitOfWorkEntityRepository.findById(uow_in.uowId());
        System.out.println(uowOptional.get());
    }
}