package io.maxilog.service.impl;

import io.maxilog.domain.Task;
import io.maxilog.dto.TaskDTO;
import io.maxilog.mapper.TaskMapper;
import io.maxilog.repository.TaskRepository;
import io.quarkus.security.identity.SecurityIdentity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TaskService {

    private final SecurityIdentity identity;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Inject
    public TaskService(SecurityIdentity identity, TaskRepository taskRepository, TaskMapper taskMapper) {
        this.identity = identity;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<TaskDTO> getMyTasks() {
        return taskRepository.findByCreatedBy(identity.getPrincipal().getName())
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskDTO save(TaskDTO taskDTO){
        Task task = taskMapper.toEntity(taskDTO);
        task.setCreatedBy(identity.getPrincipal().getName());
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }
}
