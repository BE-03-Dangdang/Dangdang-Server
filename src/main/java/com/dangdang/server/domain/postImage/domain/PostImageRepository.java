package com.dangdang.server.domain.postImage.domain;

import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

  List<PostImage> findPostImagesByPostId(@Param("postId") Long postId);

  @Modifying
  @Query("delete from PostImage pi where pi.post.id = :postId")
  void deletePostImageByPostId(@Param("postId") Long postId);


}
