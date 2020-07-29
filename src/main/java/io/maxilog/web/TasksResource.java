package io.maxilog.web;

import io.maxilog.dto.TaskDTO;
import io.maxilog.service.impl.TaskService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TasksResource {

    private final TaskService taskService;

    @Inject
    public TasksResource(TaskService taskService) {
        this.taskService = taskService;
    }

    @GET
    @Path("/me")
    public List<TaskDTO> getMyTasks() {
        return taskService.getMyTasks();
    }

    @POST
    @Path("/")
    public Response addTask(TaskDTO taskDTO) throws URISyntaxException {
        return Response.created(new URI("/tasks/me")).entity(taskService.save(taskDTO)).build();
    }

}
