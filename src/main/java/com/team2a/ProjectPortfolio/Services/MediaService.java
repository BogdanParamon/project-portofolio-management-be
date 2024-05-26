package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Setter;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final ProjectRepository projectRepository;
    @Setter
    private FileOutputStreamFactory fileOutputStreamFactory;

    /**
     * Constructor for the Media Service
     * @param mediaRepository the Media Repository
     * @param projectRepository the Project Repository
     */
    @Autowired
    public MediaService(MediaRepository mediaRepository, ProjectRepository projectRepository) {
        this.mediaRepository = mediaRepository;
        this.projectRepository = projectRepository;
        fileOutputStreamFactory = null;
    }

    /**
     * Returns all the Medias corresponding to a specific Project
     * @param projectId the project UUID
     * @return a List of Tuples that contain the media, the media name and the media description
     * @throws RuntimeException- Project doesn't exist or the id is null
     */
    public List<Triple<String,String,String>> getMediaByProjectId (UUID projectId) throws RuntimeException {
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        checkProjectExistence(projectId);
        String fileUploadPath = System.getProperty("user.dir") + "/assets";
        List<String> filenames = java.util.Arrays.stream(this.getFiles()).toList();
        List<Media> mediaToGetObject = mediaRepository.findAllByProjectProjectId(projectId);
        String filePath = fileUploadPath + File.separator;

        Map<String, Media> filenameToMediaMap = mediaToGetObject.stream()
                .collect(Collectors.toMap(Media::getPath, Function.identity()));

        List<Triple<String, String, String>> mediaFiles = new ArrayList<>();
        for (String filename : filenames) {
            Media media = filenameToMediaMap.get(filename);
            if (media != null) {
                try {
                    Path path = Paths.get(filePath + filename);
                    byte[] content = Files.readAllBytes(path);
                    String encodedContent = Base64.getEncoder().encodeToString(content); // Convert to Base64
                    String mediaName = media.getName();
                    mediaFiles.add(new Triple<>(filename, encodedContent, mediaName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return mediaFiles;
    }

    /**
     * Gets the files located on the server
     * @return the file names
     */
    public String[] getFiles ()
    {
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        String folderPath = System.getProperty("user.dir") +"/assets";
        File directory= new File(folderPath);
        return directory.list();
    }
    /**
     * Adds a Media to a specific Project
     * @param projectId the id of the Project that gets a new media
     * @param file the Media to be added
     * @param name the name of the media
     * @return the Media that was added
     * @throws RuntimeException - Project doesn't exist or the id is null
     */
    public Media addMediaToProject (UUID projectId, MultipartFile file,String name) throws RuntimeException {
        Project p = checkProjectExistence(projectId);
        checkPathUniqueness(file.getOriginalFilename());
        String filePath = System.getProperty("user.dir") + "/assets" + File.separator + file.getOriginalFilename();
        Media media = new Media(name,file.getOriginalFilename());
        media.setProject(p);
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        // Try block to check exceptions
        try {
            myMethod(filePath,file);
        }

        // Catch block to handle exceptions
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mediaRepository.save(media);
    }
    public void myMethod (String path,MultipartFile file) throws IOException {
        try (FileOutputStream fout = fileOutputStreamFactory.create(path)) {
            fout.write(file.getBytes());
        }
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
     * @throws RuntimeException - Media doesn't exist
     */
    public void checkMediaExistence (UUID mediaId) throws RuntimeException {
        Optional<Media> m = mediaRepository.findById(mediaId);
        if(m.isEmpty()){
            throw new MediaNotFoundException("No media with the id " + mediaId + " could be found.");
        }
    }

    /**
     * Checks whether the id is valid and the Project exists
     * @param projectId the id of the Project to verify
     * @return the Project
     * @throws RuntimeException - Project doesn't exist
     *
     */
    public Project checkProjectExistence (UUID projectId) throws RuntimeException {
        Optional<Project> p = projectRepository.findById(projectId);
        if(p.isEmpty()){
            throw new ProjectNotFoundException("No project with the id " + projectId + "could be found.");
        }
        return p.get();
    }

    /**
     * Checks that a path is unique
     * @param path - the path to be added to the database
     * @throws RuntimeException - the path is already in use
     */
    public void checkPathUniqueness (String path) throws RuntimeException {
        if(!mediaRepository.findAll().stream()
                .filter(x -> x.getPath().equals(path)).toList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Edits a Media in the database
     * @param media - the Media with all the new fields
     * @return - the Media that was edited
     */
    public Media editMedia (Media media) {
        Optional<Media> o = mediaRepository.findById(media.getMediaId());
        if(o.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        else if(!media.getPath().equals(o.get().getPath())){
            checkPathUniqueness(media.getPath());
        }
        return mediaRepository.save(media);
    }
}
