package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    /**
     * The constructor for the Project Service
     * @param projectRepository the repository of projects
     */
    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Returns a list of all Projects
     * @return the list of all projects
     */
    public List<Project> getProjects () {
        return projectRepository.findAll();
    }

    /**
     * Deletes a project based on its ID
     * @param projectId the id of the project to be deleted
     * @return a string stating that the deletion was successful
     */
    public String deleteProject (UUID projectId) {
        if(projectId == null) {
            throw new IllegalArgumentException();
        }
        Project project = projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
        projectRepository.delete(project);
        return "Deleted project with specified ID";
    }

    /**
     * Instantiates a new project and returns it
     * @param project A json deserialized object with the attributes for the project
     * @return the project added
     */
    public Project createProject (Project project) {
        if (project == null) {
            throw new IllegalArgumentException();
        }
        Optional<Project> existing = projectRepository.findFirstByTitleAndDescriptionAndBibtex(project.getTitle(),
                project.getDescription(), project.getBibtex());
        if (existing.isPresent()) {
            return existing.get();
        }
        Project result = new Project(project.getTitle(), project.getDescription(),
                project.getBibtex(), project.getArchived());
        result = projectRepository.save(result);
        return result;
    }

    /**
     * Returns a project given an id
     * @param projectId the id of the project
     * @return a project queried by its id
     */
    public Project getProjectById (UUID projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException();
        }
        return projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
    }
}
