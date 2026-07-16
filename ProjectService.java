package org.donel.taskmanagerdesktop.services;

import java.io.IOException;
import java.util.List;

import org.donel.taskmanagerdesktop.api.ApiClient;
import org.donel.taskmanagerdesktop.Controllers.ProjectResponse;
import org.donel.taskmanagerdesktop.Controllers.UpdateProjectRequest;
import org.donel.taskmanagerdesktop.Controllers.CreateProjectRequest;
import org.donel.taskmanagerdesktop.Controllers.ProjectMemberResponse;
import org.donel.taskmanagerdesktop.Controllers.DeliverableResponse;
import org.donel.taskmanagerdesktop.Controllers.AddMemberRequest;
import org.donel.taskmanagerdesktop.Controllers.TaskItemResponse;
import org.donel.taskmanagerdesktop.Controllers.*;




public class ProjectService {

    private final ApiClient apiClient;

    public ProjectService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<ProjectResponse> getProjects() throws IOException, InterruptedException, ApiException,ApiException {
        ProjectResponse[] response = apiClient.get("/projects", ProjectResponse[].class);
        return response == null ? List.of() : List.of(response);
    }

    public ProjectResponse getProject(long id) throws IOException, InterruptedException, ApiException {
        return apiClient.get("/projects/" + id, ProjectResponse.class);
    }

    public ProjectResponse createProject(CreateProjectRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.post("/projects", request, ProjectResponse.class);
    }

    public ProjectResponse updateProject(long id, UpdateProjectRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.put("/projects/" + id, request, ProjectResponse.class);
    }

    public ProjectResponse finishProject(long id) throws IOException, InterruptedException, ApiException {
        return apiClient.patch("/projects/" + id + "/finish", null, ProjectResponse.class);
    }

    public List<ProjectMemberResponse> getMembers(long projectId) throws IOException, InterruptedException, ApiException {
        ProjectMemberResponse[] response = apiClient.get("/projects/" + projectId + "/members", ProjectMemberResponse[].class);
        return response == null ? List.of() : List.of(response);
    }

    public ProjectMemberResponse addMember(long projectId, AddMemberRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.post("/projects/" + projectId + "/members", request, ProjectMemberResponse.class);
    }

    public List<DeliverableResponse> getDeliverables(long projectId) throws IOException, InterruptedException, ApiException {
        DeliverableResponse[] response = apiClient.get("/projects/" + projectId + "/deliverables", DeliverableResponse[].class);
        return response == null ? List.of() : List.of(response);
    }

    public DeliverableResponse createDeliverable(long projectId, CreateDeliverableRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.post("/projects/" + projectId + "/deliverables", request, DeliverableResponse.class);
    }

    public DeliverableResponse updateDeliverable(long id, UpdateDeliverableRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.put("/deliverables/" + id, request, DeliverableResponse.class);
    }

    public DeliverableResponse finishDeliverable(long id) throws IOException, InterruptedException, ApiException {
        return apiClient.patch("/deliverables/" + id + "/finish", null, DeliverableResponse.class);
    }

    public List<TaskItemResponse> getTasks(long deliverableId) throws IOException, InterruptedException, ApiException {
        TaskItemResponse[] response = apiClient.get("/deliverables/" + deliverableId + "/tasks", TaskItemResponse[].class);
        return response == null ? List.of() : List.of(response);
    }

    public TaskItemResponse createTask(long deliverableId, CreateTaskItemRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.post("/deliverables/" + deliverableId + "/tasks", request, TaskItemResponse.class);
    }

    public TaskItemResponse updateTask(long id, UpdateTaskItemRequest request) throws IOException, InterruptedException, ApiException {
        return apiClient.put("/tasks/" + id, request, TaskItemResponse.class);
    }

    public TaskItemResponse finishTask(long id) throws IOException, InterruptedException, ApiException {
        return apiClient.patch("/tasks/" + id + "/finish", null, TaskItemResponse.class);
    }
}
