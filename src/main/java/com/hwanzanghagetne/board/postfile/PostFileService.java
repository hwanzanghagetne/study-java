package com.hwanzanghagetne.board.postfile;

import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.post.Post;
import com.hwanzanghagetne.board.post.PostRepository;
import com.hwanzanghagetne.board.postfile.dto.PostFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostFileService {

    private final PostFileRepository postFileRepository;
    private final PostRepository postRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public List<Long> uploadFiles(Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String absoluteUploadDir = dir.getAbsolutePath();
        List<Long> savedIds = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFileName = UUID.randomUUID() + extension;
            String filePath = absoluteUploadDir + "/" + storedFileName;

            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException("파일 저장에 실패했습니다.", e);
            }

            PostFile postFile = PostFile.builder()
                    .post(post)
                    .originalFileName(originalFilename)
                    .storedFileName(storedFileName)
                    .filePath(filePath)
                    .build();

            PostFile saved = postFileRepository.save(postFile);
            savedIds.add(saved.getId());
        }
        return savedIds;
    }

    @Transactional(readOnly = true)
    public List<PostFileResponse> getFiles(Long postId) {
        List<PostFile> files = postFileRepository.findByPostId(postId);

        return files.stream()
                .map(file -> new PostFileResponse(
                        file.getId(),
                        file.getOriginalFileName()
                ))
                .toList();
    }

    public PostFile getFile(Long fileId) {
        return postFileRepository.findById(fileId).orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    }

    @Transactional
    public void deleteFilesByPost(Long postId) {
        List<PostFile> files = postFileRepository.findByPostId(postId);
        for (PostFile file : files) {
            new File(file.getFilePath()).delete();
        }
        postFileRepository.deleteAll(files);
    }

    @Transactional
    public void deleteFile(Long fileId, String loginId) {
        PostFile postFile = postFileRepository.findById(fileId).orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        if (!postFile.getPost().getMember().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.NOT_AUTHOR);
        }
        new File(postFile.getFilePath()).delete();
        postFileRepository.delete(postFile);
    }
}
