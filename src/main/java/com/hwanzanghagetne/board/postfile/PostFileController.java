package com.hwanzanghagetne.board.postfile;

import com.hwanzanghagetne.board.postfile.dto.PostFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostFileController {

    private final PostFileService postFileService;

    @PostMapping("/api/posts/{postId}/files")
    public ResponseEntity<List<Long>> uploadFiles(@PathVariable Long postId, @RequestParam("files") List<MultipartFile> files) {
        List<Long> fileIds = postFileService.uploadFiles(postId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileIds);
    }

    @GetMapping("/api/posts/{postId}/files")
    public List<PostFileResponse> getFiles(@PathVariable Long postId) {
        return postFileService.getFiles(postId);
    }

    @GetMapping("/api/files/{fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long fileId) throws IOException {
        PostFile postFile = postFileService.getFile(fileId);

        Path path = Paths.get(postFile.getFilePath());
        UrlResource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @DeleteMapping("/api/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId, Authentication authentication) {
        postFileService.deleteFile(fileId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
