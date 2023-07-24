package org.ms.mssm.logic;

import org.ms.mssm.logic.model.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StateProcessor {
    private final ConcurrentSkipListSet<UnitOfWork> unitOfWorkSet;

    public StateProcessor(Set<UnitOfWork> unitOfWorkSet) {
        this.unitOfWorkSet = new ConcurrentSkipListSet<>();
        unitOfWorkSet.forEach(this::addNewUnitOfWork);
    }

    protected boolean isUnitOfWorkCompleted(UnitOfWork uow, PartitionVer partitionVer){
        long delta = partitionVer.ver() - partitionVer.partitionId().ets();
        return (uow.partitionMap().size() == uow.partitionIdSet().size()) && (uow.depth() > delta);
    }

    private Task createNewTask(UnitOfWork uow){
        return new Task(uow.uowId(), uow.dagId(), uow.actualEventTimeSlot(), Instant.now().toEpochMilli(), new HashSet<>(uow.partitionMap().values()));
    }

    public StateProcessorResult process(PartitionVer partitionVer, Consumer<StateProcessorResult> resultConsumer){

        List<UnitOfWork> units = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        PartitionId partitionId = partitionVer.partitionId();

        for(UnitOfWork uow: unitOfWorkSet){
            if(!uow.partitionIdSet().contains(partitionId)) continue;
            uow.partitionMap().put(partitionId, partitionVer);
            units.add(uow);

            if(isUnitOfWorkCompleted(uow, partitionVer)) tasks.add(createNewTask(uow));
        }

        StateProcessorResult result = new StateProcessorResult(partitionVer, units, tasks);
        if (resultConsumer != null) resultConsumer.accept(result);
        return result;
    }

    public StateProcessorResult processParallel(PartitionVer partitionVer, Consumer<StateProcessorResult> resultConsumer){

        Instant now = Instant.now();
        List<Task> tasks = new ArrayList<>();
        PartitionId partitionId = partitionVer.partitionId();

        List<CompletableFuture<Pair>> features = unitOfWorkSet.stream()
                .map(uof ->
                        CompletableFuture.supplyAsync(() -> uof)
                        .thenApply(u -> {
                            if(u.partitionIdSet().contains(partitionId)) {
                                u.partitionMap().put(partitionId, partitionVer);
                                if(isUnitOfWorkCompleted(u, partitionVer))
                                    return new Pair(u, createNewTask(u));
                                else
                                    return new Pair(u, null);
                            }
                            return null;
                        })
                )
                .toList();

        CompletableFuture<Void> voidFeature = CompletableFuture.allOf(features.toArray(new CompletableFuture[0]));

        List<Pair> pairs = voidFeature.thenApply(v ->
                    features.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                ).join();

        StateProcessorResult result = new StateProcessorResult(
                partitionVer,
                pairs.stream().map(Pair::unitOfWork).collect(Collectors.toList()),
                pairs.stream().map(Pair::task).filter(Objects::nonNull).collect(Collectors.toList())
        );

        if (resultConsumer != null) resultConsumer.accept(result);
        return result;
    }

    public void addNewUnitOfWork(UnitOfWork unitOfWork){
        if(!unitOfWorkSet.contains(unitOfWork)) unitOfWorkSet.add(unitOfWork);
    }

    public void delUnitOfWork(UnitOfWork unitOfWork){
        unitOfWorkSet.remove(unitOfWork);
    }

}
