package org.ms.mssm.converters;

import org.ms.mssm.entities.TaskEntity;
import org.ms.mssm.logic.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public class Tasks {
    public static TaskEntity entity(Task task){
        return new TaskEntity(
                task.uowId(),
                task.dagId(),
                task.taskId(),
                task.actualEventTimeSlot(),
                PartitionVers.entities(task.partitions())
        );
    }

    public static Task model(TaskEntity taskEntity){
        return new Task(
                taskEntity.uowId(),
                taskEntity.dagId(),
                taskEntity.actualEventTimeSlot(),
                taskEntity.taskId(),
                PartitionVers.models(taskEntity.partitions())
        );
    }

    public static List<TaskEntity> entities(List<Task> tasks){
        return tasks.stream().map(Tasks::entity).collect(Collectors.toList());
    }

    public static List<Task> models(List<TaskEntity> tasks){
        return tasks.stream().map(Tasks::model).collect(Collectors.toList());
    }
}
