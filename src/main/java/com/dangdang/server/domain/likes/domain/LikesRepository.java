package com.dangdang.server.domain.likes.domain;

import com.dangdang.server.domain.likes.domain.entity.Likes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikesRepository extends JpaRepository<Likes, Long> {

  @Query("select l from Likes l where l.post.id = :postId and l.member.id = :memberId")
  Optional<Likes> findByPostIdAndMemberId(@Param("postId") Long postId,
      @Param("memberId") Long memberId);
}
