package com.dangdang.server.domain.post.domain;

import com.dangdang.server.domain.post.domain.entity.Post;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query(value = "select p from Post p join fetch p.town where p.town.id in (:adjacency)")
  public Slice<Post> findPostsByTownIdFetchJoinSortByCreatedAt(
      @Param("adjacency") List<Long> adjacency, Pageable pageable);
}
