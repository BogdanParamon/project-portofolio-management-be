package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectsToCollaboratorsTest {

    @Test
    void testConstructor() {
        Collaborator collaborator = new Collaborator("Test");
        Project project = new Project("Title","Test","Test",false);
        ProjectsToCollaborators ptc = new ProjectsToCollaborators(project,collaborator);
        assertEquals(ptc.getCollaborator(), collaborator);
        assertEquals(ptc.getProject(), project);
    }

}