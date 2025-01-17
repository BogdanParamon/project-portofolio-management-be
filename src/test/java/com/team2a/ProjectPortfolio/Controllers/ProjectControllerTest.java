package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Template;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import com.team2a.ProjectPortfolio.WebSocket.ProjectWebSocketHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectControllerTest {

    private ProjectService projectService;
    private ProjectController projectController;
    @Mock
    private ProjectWebSocketHandler webSocketHandler;

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        webSocketHandler = Mockito.mock(ProjectWebSocketHandler.class);
        projectController = new ProjectController(projectService, webSocketHandler);
    }

    @Test
    void getProjectsEmpty() {
        List<Project> expected = new ArrayList<>();
        when(projectService.getProjects()).thenReturn(List.of());
        ResponseEntity<List<Project>> response = projectController.getProjects();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getProjectsNotEmpty() {
        Project project1 = new Project("Title1", "Description1",  false);
        Project project2 = new Project("Title2", "Description2",  false);
        Project project3 = new Project("Title3", "Description3",  false);
        List<Project> projects = List.of(project1, project2, project3);

        when(projectService.getProjects()).thenReturn(projects);

        ResponseEntity<List<Project>> response = projectController.getProjects();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projects, response.getBody());
    }

    @Test
    void updateProjectSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", false);
        when(projectService.updateProject(projectId, project1)).thenReturn(project1);
        ResponseEntity<Project> response = projectController.updateProject(projectId, project1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project1, response.getBody());
    }
    @Test
    void createProjectSuccess() {
        Project project = new Project("title1", "desc1", false);
        when(projectService.createProject(project)).thenReturn(project);
        ResponseEntity<Project> response = projectController.createProject(project);
        verify(webSocketHandler).broadcast(any());
        assertEquals(project, response.getBody());
    }

    @Test
    void getProjectByIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", false);
        when(projectService.getProjectById(projectId)).thenReturn(project1);
        ResponseEntity<Project> response = projectController.getProjectById(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project1, response.getBody());
    }

    @Test
    void deleteProjectSuccessful() {
        UUID projectId = UUID.randomUUID();
        doNothing().when(projectService).deleteProject(projectId);
        ResponseEntity<String> response = projectController.deleteProject(projectId);
        verify(webSocketHandler).broadcast(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateProjectTemplateSuccess() {
        UUID projectId = UUID.randomUUID();
        Template template = new Template("TempTitle",
                "StandardDescription", 6);
        Project project1 = new Project("Title1", "Description1", false, template);
        when(projectService.updateProjectTemplate(projectId, template)).thenReturn(project1);
        ResponseEntity<Project> response = projectController.updateProjectTemplate(projectId, template);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project1, response.getBody());
    }

    @Test
    void updateProjectTemplateNotFound() {
        UUID projectId = UUID.randomUUID();
        Template template = new Template("TempTitle",
                "StandardDescription", 6);
        when(projectService.updateProjectTemplate(projectId, template)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Project> response = projectController.updateProjectTemplate(projectId, template);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).updateProjectTemplate(projectId, template);
    }

    @Test
    void removeTemplateFromProjectSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", false, null);
        when(projectService.removeTemplateFromProject(projectId)).thenReturn(project1);
        ResponseEntity<Project> response = projectController.removeTemplateFromProject(projectId, "");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getTemplate());
    }

    @Test
    void removeTemplateFromProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        when(projectService.removeTemplateFromProject(projectId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Project> response = projectController.removeTemplateFromProject(projectId, "");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).removeTemplateFromProject(projectId);
    }

    @Test
    void getTemplateByProjectIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Template template = new Template("TempTitle",
                "StandardDescription", 6);
        Project project1 = new Project("Title1", "Description1", false, template);
        when(projectService.getTemplateByProjectId(projectId)).thenReturn(template);
        ResponseEntity<Template> response = projectController.getTemplateByProjectId(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(template, response.getBody());
    }

    @Test
    void getTemplateByProjectIdNotFound() {
        UUID projectId = UUID.randomUUID();
        Template template = new Template("TempTitle",
                "StandardDescription", 6);
        when(projectService.getTemplateByProjectId(projectId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Template> response = projectController.getTemplateByProjectId(projectId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(projectService, times(1)).getTemplateByProjectId(projectId);
    }
}