package com.dangdang.server.domain.postImage.application;

import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PostImageService {

  private final PostImageRepository postImageRepository;

  public PostImageService(PostImageRepository postImageRepository) {
    this.postImageRepository = postImageRepository;
  }

  public void savePostImage(Post post, PostImageRequest postImageRequest) {
    List<String> urls = postImageRequest.getUrl();
    urls.stream().map(url -> PostImageRequest.toPostImage(post, url))
        .forEach(postImageRepository::save);
  }

  public List<String> findPostImagesByPostId(Long postId) {
    List<PostImage> postImages = postImageRepository.findPostImagesByPostId(postId);
    return postImages.stream().map(PostImage::getUrl)
        .collect(Collectors.toList());

  }
}
