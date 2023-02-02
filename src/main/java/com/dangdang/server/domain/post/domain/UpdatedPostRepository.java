package com.dangdang.server.domain.post.domain;

import com.dangdang.server.domain.post.domain.entity.UpdatedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdatedPostRepository extends JpaRepository<UpdatedPost, Long> {

}
