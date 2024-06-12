package com.team2a.ProjectPortfolio.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Services.MediaService;
import java.util.List;
import java.util.UUID;

import com.team2a.ProjectPortfolio.WebSocket.MediaProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.MediaWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.MediaFileContent;
import jakarta.persistence.Tuple;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class MediaControllerTest {

  @Mock
  private MediaService mediaService;


  @Mock
  private MediaProjectWebSocketHandler mediaProjectWebSocketHandler;

  private MediaController mediaController;

  @BeforeEach
  void setup() {
    mediaService = Mockito.mock(MediaService.class);
    mediaProjectWebSocketHandler = Mockito.mock(MediaProjectWebSocketHandler.class);
    mediaController = new MediaController(mediaService, mediaProjectWebSocketHandler);
  }


  @Test
  void TestGetImagesContentByProjectIdSuccess() {
    when(mediaService.getImagesContentByProjectId(any(UUID.class))).thenReturn(List.of(new MediaFileContent("name1","path1","content1")));
    ResponseEntity<List<MediaFileContent>> entity = mediaController.getImagesContentByProjectId(UUID.randomUUID());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(List.of(new MediaFileContent("name1","path1","content1")).toString(), entity.getBody().toString());
  }

  @Test
  void TestGetDocumentContentByMediaIdSuccess() {
    MediaFileContent p1 = new MediaFileContent("test","test","test");
    when(mediaService.getDocumentByMediaId(any(UUID.class))).thenReturn(p1);
    ResponseEntity<MediaFileContent> entity = mediaController.getDocumentContentByMediaId(UUID.randomUUID());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(p1, entity.getBody());
  }

  @Test
  void TestGetDocumentsByProjectIdSuccess() {
    Media m1 = new Media("test","test");
    when(mediaService.getDocumentsByProjectId(any(UUID.class))).thenReturn(List.of(m1));
    ResponseEntity<List<Media>> entity = mediaController.getDocumentsByProjectId(UUID.randomUUID());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(List.of(m1), entity.getBody());
  }

  @Test
  void testAddMediaToProjectPathNotUnique() {
    when(mediaService.addMediaToProject(any(UUID.class), any(MultipartFile.class),any(String.class))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
    assertThrows(ResponseStatusException.class, () -> mediaController.addMediaToProject(UUID.randomUUID(),  new MockMultipartFile("file", "test.md", "text/plain", "test".getBytes()),"Test"));
  }

  @Test
  void testAddMediaToProjectSuccess() {
    UUID id = UUID.randomUUID();
    Media m1 = new Media("name", "path1");
    MultipartFile mp = new MockMultipartFile("file", "test.md", "text/plain", "test".getBytes());
    when(mediaService.addMediaToProject(id, mp,"test")).thenReturn(m1);
    ResponseEntity<Media> entity = mediaController.addMediaToProject(id,mp,"test");
    verify(mediaProjectWebSocketHandler).broadcast(any());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(m1, entity.getBody());
  }

  @Test
  void testDeleteMediaFromProjectMediaNotFound() {
    UUID i1 = UUID.randomUUID();
    doThrow(new MediaNotFoundException("No media with the id " + i1 + " could be found."))
        .when(mediaService).deleteMedia(i1);
    ResponseEntity<String> entity = mediaController.deleteMedia(UUID.randomUUID(), i1);
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals("No media with the id " + i1 + " could be found.", entity.getBody());
  }

  @Test
  void testDeleteMediaFromProjectSuccess() {
    Media m = new Media();
    Project p = new Project();
    p.setProjectId(UUID.randomUUID());
    m.setProject(p);
    when(mediaService.deleteMedia(any())).thenReturn(m);
    ResponseEntity<String> entity = mediaController.deleteMedia(UUID.randomUUID(), UUID.randomUUID());
    verify(mediaProjectWebSocketHandler).broadcast(any());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals("Media deleted successfully.", entity.getBody());
  }

  @Test
  void testEditMediaNotFound() {
    Media media = new Media();
    when(mediaService.editMedia(media)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
    assertThrows(ResponseStatusException.class, () -> mediaController.editMedia(media));
  }

  @Test
  void testEditMediaForbiddenPath() {
    Media media = new Media();
    when(mediaService.editMedia(media)).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
    assertThrows(ResponseStatusException.class, () -> mediaController.editMedia(media));
  }

  @Test
  void testMediaSuccess() {
    Media media = new Media();
    Project p = new Project();
    p.setProjectId(UUID.randomUUID());
    media.setProject(p);
    when(mediaService.editMedia(media)).thenReturn(media);
    ResponseEntity<Media> entity = mediaController.editMedia(media);
    verify(mediaProjectWebSocketHandler).broadcast(any());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(media, mediaService.editMedia(media));
  }
}
