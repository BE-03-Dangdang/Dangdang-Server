package com.dangdang.server.domain.postImage.application;

import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.global.util.S3ImageUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostImageService {

  private final PostImageRepository postImageRepository;
  private final PostRepository postRepository;

  public PostImageService(PostImageRepository postImageRepository,
      PostRepository postRepository) {
    this.postImageRepository = postImageRepository;
    this.postRepository = postRepository;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public List<String> savePostImage(Post post, PostImageRequest postImageRequest) {
    List<String> urls = postImageRequest.urls();
    urls.stream().map(url -> PostImageRequest.toPostImage(post, url))
        .forEach(postImageRepository::save);
    return postImageRepository.findPostImagesByPostId(post.getId()).stream()
        .map(PostImage::getUrl)
        .collect(Collectors.toList());
  }

  public List<String> findImageUrlsByPostId(Long postId) {
    List<PostImage> postImages = postImageRepository.findPostImagesByPostId(postId);
    return postImages.stream().map(PostImage::getUrl)
        .map(S3ImageUtil::makeImageLink)
        .collect(Collectors.toList());
  }

  @Transactional
  public void renewPostImage(Post post, List<String> newUrls) {
    Set<String> checkPreviousUrl = new HashSet<>();
    List<PostImage> postImages = postImageRepository.findPostImagesByPostId(post.getId());

    for (PostImage postImage : postImages) {
      if (newUrls.contains(postImage.getUrl())) {
        checkPreviousUrl.add(postImage.getUrl());
        continue;
      }
      postImageRepository.deleteById(postImage.getId());
    }

    for (String url : newUrls) {
      if (checkPreviousUrl.contains(url)) {
        continue;
      }
      postImageRepository.save(new PostImage(post, url));
    }
  }
}
