package org.ms.mssm.services;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ms.mssm.converters.PartitionIds;
import org.ms.mssm.converters.PartitionVers;
import org.ms.mssm.converters.UnitOfWorks;
import org.ms.mssm.entities.PartitionVerEntity;
import org.ms.mssm.logic.model.PartitionId;
import org.ms.mssm.entities.PartitionIdEntity;
import org.ms.mssm.entities.UnitOfWorkEntity;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.UnitOfWork;
import org.ms.mssm.repositories.PartitionIdEntityRepository;
import org.ms.mssm.repositories.PartitionVerEntityRepository;
import org.ms.mssm.repositories.TaskEntityRepository;
import org.ms.mssm.repositories.UnitOfWorkEntityRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;


class RepositoryAndServiceTest {

    @Inject
    EmbeddedServer embeddedServer;

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
        return new UnitOfWorkEntity(1L, 1L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), parts1, parts2, depth);
    }

    @Test
    public void test_save_and_read(){
        try(ApplicationContext applicationContext = ApplicationContext.run()) {
            UnitOfWorkService unitOfWorkService  = applicationContext.getBean(UnitOfWorkService.class);
            UnitOfWorkEntity uow_in = createUoW();

            unitOfWorkService.save(UnitOfWorks.model(uow_in));
            UnitOfWork uow = unitOfWorkService.readById(uow_in.uowId());

            Assertions.assertEquals(uow_in.requiredPartitionIds().size(), uow.partitionIdSet().size());
            Assertions.assertEquals(uow_in.partitions().size(), uow.partitionMap().values().size());
        }
    }

    @Test
    public void test_getPartitionVerListByUowId(){
        try(ApplicationContext applicationContext = ApplicationContext.run()) {
            UnitOfWorkService unitOfWorkService  = applicationContext.getBean(UnitOfWorkService.class);
            UnitOfWorkEntityRepository unitOfWorkEntityRepository = applicationContext.getBean(UnitOfWorkEntityRepository.class);
            UnitOfWorkEntity uow_in = createUoW();

            unitOfWorkService.save(UnitOfWorks.model(uow_in));
            UnitOfWork uow = unitOfWorkService.readById(uow_in.uowId());

            System.out.println("1:_-_-_-_-_-_-_");
            System.out.println(uow);

            Set<PartitionVerEntity> partvers = unitOfWorkEntityRepository.getPartitionVerListByUowId(uow_in.uowId());

            System.out.println("2:_-_-_-_-_-_-_");
            System.out.println(partvers);
        }
    }
}