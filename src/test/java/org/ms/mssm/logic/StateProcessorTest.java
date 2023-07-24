package org.ms.mssm.logic;

import org.junit.jupiter.api.Test;
import org.ms.mssm.logic.model.PartitionVer;
import org.ms.mssm.logic.model.PartitionId;
import org.ms.mssm.logic.model.StateProcessorResult;
import org.ms.mssm.logic.model.UnitOfWork;

import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class StateProcessorTest {

    public static Consumer<StateProcessorResult> f = (r) -> {
        System.out.println(r.partitionVer().partVerId());
        r.unitOfWorks().forEach(u -> System.out.println("\tunit: " + u.dagId() + ", " + u.actualEventTimeSlot() + "\r\n\tparts: " + u.partitionMap().values()));
        r.tasks().forEach(t -> System.out.println("\ttask: " + t.dagId() + ", " + t.actualEventTimeSlot() + "\r\n\tparts: " + t.partitions()));
    };

    public static List<PartitionId> createPartitionIds(){
        List<PartitionId> partitionIds = new ArrayList<>();

        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0));

        for(long i=0; i<5; i++) {
            partitionIds.add( new PartitionId("Subject", 10_000_101L, ldt.plusDays(i).toInstant(ZoneOffset.UTC).toEpochMilli(), null));
            partitionIds.add( new PartitionId("Device",  10_000_301L, ldt.plusDays(i).toInstant(ZoneOffset.UTC).toEpochMilli(), null));
            partitionIds.add( new PartitionId("View",    10_000_601L, ldt.plusDays(i).toInstant(ZoneOffset.UTC).toEpochMilli(), null));
        }

        return partitionIds;
    }

    public static HashSet<UnitOfWork> createUnitOfWorkSet(){
        HashSet<UnitOfWork> unitOfWorkSet = new HashSet<>();

        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0));

        long depth = 86400 * 2 * 1000;

        unitOfWorkSet.add(
                new UnitOfWork(
                        2L, 2L,
                        ldt.toInstant(ZoneOffset.UTC).toEpochMilli(),
                        Set.of(
                                new PartitionId("Subject", 10_000_101L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null),
                                new PartitionId("Device",  10_000_301L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null)
                        ),
                        depth)
        );
/*
        ldt = LocalDateTime.of(LocalDate.of(2023, 2, 25), LocalTime.of(0, 0));
        unitOfWorkSet.add(
                new UnitOfWork(
                        1L, 1L,
                        ldt.toInstant(ZoneOffset.UTC).toEpochMilli(),
                        Set.of(
                                new PartitionId("View",   10_000_601L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null),
                                new PartitionId("View",   10_000_601L, ldt.minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(), null),
                                new PartitionId("Device", 10_000_301L, ldt.toInstant(ZoneOffset.UTC).toEpochMilli(), null),
                                new PartitionId("Device", 10_000_301L, ldt.minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(), null)
                        ),
                        depth)
        );
 */
        return unitOfWorkSet;
    }

    @Test
    public void test_just_print_it() {
        HashSet<UnitOfWork> unitOfWorkSet = createUnitOfWorkSet();
        List<PartitionId> partitionIds = createPartitionIds();

        StateProcessor sp = new StateProcessor(unitOfWorkSet);
        for(int i = 0; i< partitionIds.size(); i++) {
            PartitionVer partitionVer = new PartitionVer(partitionIds.get(i), partitionIds.get(i).ets()+90_000_000, "session#" + i, "operation#" + i);
            sp.process(partitionVer, f);
            System.out.println(".");
        }
    }

    @Test
    public void test_parallel_just_print_it() {
        HashSet<UnitOfWork> unitOfWorkSet = createUnitOfWorkSet();
        List<PartitionId> partitionIds = createPartitionIds();

        StateProcessor sp = new StateProcessor(unitOfWorkSet);
        for(int i = 0; i< partitionIds.size(); i++) {
            PartitionVer partitionVer = new PartitionVer(partitionIds.get(i), partitionIds.get(i).ets()+90_000_000, "session#" + i, "operation#" + i);
            sp.processParallel(partitionVer, f);
            System.out.println(".");
        }
    }

    @Test
    public void test_one_partver_hit_but_not_complete(){
        HashSet<UnitOfWork> unitOfWorkSet = createUnitOfWorkSet();

        PartitionId partitionId = new PartitionId(
                "Subject",
                10_000_101L,
                            LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
                null
                );

        PartitionVer partitionVer = new PartitionVer(
                    partitionId,
                    LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                "sess1", "op1"
                );

        StateProcessor sp = new StateProcessor(unitOfWorkSet);

        StateProcessorResult rslt = sp.processParallel(partitionVer, f);

        Optional<UnitOfWork> uow_opt = rslt.unitOfWorks().stream().filter(u -> u.uowId().equals(2L)).findAny();
        assertTrue(uow_opt.isPresent());
        assertEquals(2L, (long) uow_opt.get().uowId());
        assertEquals(1, uow_opt.get().partitionMap().size());
        assertTrue(uow_opt.get().partitionMap().containsKey(partitionId));
        assertEquals(partitionVer, uow_opt.get().partitionMap().get(partitionId));
    }

    @Test
    public void test_one_partver_didnt_hit(){
        HashSet<UnitOfWork> unitOfWorkSet = createUnitOfWorkSet();

        PartitionId partitionId = new PartitionId(
                "View",
                10_000_601L,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
                null
        );

        PartitionVer partitionVer = new PartitionVer(
                partitionId,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                "sess1", "op1"
        );

        StateProcessor sp = new StateProcessor(unitOfWorkSet);

        StateProcessorResult rslt = sp.processParallel(partitionVer, f);

        assertTrue(rslt.unitOfWorks().isEmpty());
    }

    @Test
    public void test_partvers_completed(){
        HashSet<UnitOfWork> unitOfWorkSet = createUnitOfWorkSet();

        PartitionId partitionId = new PartitionId(
                "Subject",
                10_000_101L,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
                null
        );

        PartitionVer partitionVer = new PartitionVer(
                partitionId,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                "sess1", "op1"
        );

        PartitionId partitionId2 = new PartitionId(
                "Device",
                10_000_301L,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
                null
        );

        PartitionVer partitionVer2 = new PartitionVer(
                partitionId2,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                "sess1", "op1"
        );

        StateProcessor sp = new StateProcessor(unitOfWorkSet);
        sp.processParallel(partitionVer, f);
        StateProcessorResult rslt = sp.processParallel(partitionVer2, f);

        Optional<UnitOfWork> uow_opt = rslt.unitOfWorks().stream().filter(u -> u.uowId().equals(2L)).findAny();
        assertTrue(uow_opt.isPresent());
        assertEquals(2L, uow_opt.get().uowId());
        assertEquals(2, uow_opt.get().partitionMap().size());
        assertTrue(uow_opt.get().partitionMap().containsKey(partitionId));
        assertTrue(uow_opt.get().partitionMap().containsKey(partitionId2));
        assertEquals(partitionVer, uow_opt.get().partitionMap().get(partitionId));
        assertEquals(partitionVer2, uow_opt.get().partitionMap().get(partitionId2));

        assertEquals(1,rslt.tasks().size());
        assertEquals(uow_opt.get().uowId(), rslt.tasks().get(0).uowId());
        assertEquals(uow_opt.get().partitionMap().values().size(), rslt.tasks().get(0).partitions().size());
        rslt.tasks().get(0).partitions().forEach(p ->
            assertTrue(uow_opt.get().partitionMap().containsValue(p))
        );
    }

    @Test
    public void test_partvers_but_expired(){
        HashSet<UnitOfWork> unitOfWorkSet = createUnitOfWorkSet();

        PartitionId partitionId = new PartitionId(
                "Subject",
                10_000_101L,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
                null
        );

        PartitionVer partitionVer = new PartitionVer(
                partitionId,
                Instant.now().toEpochMilli(),
                "sess1", "op1"
        );

        PartitionId partitionId2 = new PartitionId(
                "Device",
                10_000_301L,
                LocalDateTime.of(LocalDate.of(2023, 2, 24), LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
                null
        );

        PartitionVer partitionVer2 = new PartitionVer(
                partitionId2,
                Instant.now().toEpochMilli(),
                "sess1", "op1"
        );

        StateProcessor sp = new StateProcessor(unitOfWorkSet);
        sp.processParallel(partitionVer, f);
        StateProcessorResult rslt = sp.processParallel(partitionVer2, f);

        Optional<UnitOfWork> uow_opt = rslt.unitOfWorks().stream().filter(u -> u.uowId().equals(2L)).findAny();
        assertTrue(uow_opt.isPresent());
        assertEquals(2L, uow_opt.get().uowId());
        assertEquals(2, uow_opt.get().partitionMap().size());
        assertTrue(uow_opt.get().partitionMap().containsKey(partitionId));
        assertTrue(uow_opt.get().partitionMap().containsKey(partitionId2));
        assertEquals(partitionVer, uow_opt.get().partitionMap().get(partitionId));
        assertEquals(partitionVer2, uow_opt.get().partitionMap().get(partitionId2));

        assertEquals(0,rslt.tasks().size());

    }
}