package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final ProjectRepository projectRepository;

    /**
     * Constructor for the Media Service
     * @param mediaRepository the Media Repository
     * @param projectRepository the Project Repository
     */
    @Autowired
    public MediaService(MediaRepository mediaRepository, ProjectRepository projectRepository) {
        this.mediaRepository = mediaRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Returns all the Medias corresponding to a specific Project
     * @param projectId the id of the Project for which we retrieve the Medias
     * @return the list of retrieved Medias
     * @throws RuntimeException - Project doesn't exist or the id is null
     */
    public List<Media> getMediaByProjectId (UUID projectId) throws RuntimeException {
        checkProjectExistence(projectId);
        return mediaRepository.findAllByProjectProjectId(projectId);
    }

    /**
     * Adds a Media to a specific Project
     * @param projectId the id of the Project that gets a new media
     * @param path the path of the given Media to be added
     * @return the Media that was added
     * @throws RuntimeException - Project doesn't exist or the id is null
     */
    public Media addMediaToProject (UUID projectId, String path) throws RuntimeException {
        Project p = checkProjectExistence(projectId);
        Media m1 = new Media(p, path);
        mediaRepository.save(m1);
        return m1;
    }

    /**
     * Deletes a Media from the database
     * @param mediaId the id of the Media to delete
     * @throws RuntimeException - Media doesn't exist or the id is null
     */
    public void deleteMedia (UUID mediaId) {
        checkMediaExistence(mediaId);
        mediaRepository.deleteById(mediaId);
    }

    /**
     * Checks whether the id is valid and the Media exists
     * @param mediaId the id of the Media to verify
     * @throws RuntimeException - Media doesn't exist or the id is null
     */
    public void checkMediaExistence (UUID mediaId) throws RuntimeException {
        if(mediaId == null) {
            throw new IdIsNullException("Null id not accepted.");
        }
        Optional<Media> m = mediaRepository.findById(mediaId);
        if(m.isEmpty()){
            throw new MediaNotFoundException("No media with the id " + mediaId + " could be found.");
        }
    }

    /**
     * Checks whether the id is valid and the Project exists
     * @param projectId the id of the Project to verify
     * @return the Project
     * @throws RuntimeException - Project doesn't exist or the id is null
     *
     */
    public Project checkProjectExistence (UUID projectId) throws RuntimeException {
        if(projectId == null) {
            throw new IdIsNullException("Null id not accepted.");
        }
        Optional<Project> p = projectRepository.findById(projectId);
        if(p.isEmpty()){
            throw new ProjectNotFoundException("No project with the id " + projectId + "could be found.");
        }
        return p.get();
    }
}