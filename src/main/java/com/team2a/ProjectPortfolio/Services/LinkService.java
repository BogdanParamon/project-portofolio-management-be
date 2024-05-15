package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class LinkService {

    private final ProjectRepository projectRepository;
    private final LinkRepository linkRepository;

    /**
     * Constructor for the link repository
     * @param linkRepository the Link repository
     * @param projectRepository the Project repository
     */
    @Autowired
    public LinkService(LinkRepository linkRepository, ProjectRepository projectRepository) {
        this.linkRepository = linkRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Add a link to the project
     * @param link the link entity
     * @return the new link entity
     */
    public Link addLinkToProject (Link link) {
        if(!projectRepository.existsById(link.getProject().getProjectId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        if(linkRepository.existsByProjectProjectIdAndUrl(link.getProject().getProjectId(), link.getUrl())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Link already exists");
        }
        return linkRepository.saveAndFlush(link);
    }

    /**
     * Edit the link of the project
     * @param link the link entity
     * @return the new link entity
     */
    public Link editLinkOfProject (Link link) {
        if(link == null) {
            throw new IllegalArgumentException();
        }
        if(linkRepository.findById(link.getLinkId()).isPresent()) {
            linkRepository.save (link);
        }
        else
            throw new EntityNotFoundException();
        return link;
    }

}