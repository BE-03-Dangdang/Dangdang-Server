package com.dangdang.server.domain.review.domain;

import com.dangdang.server.domain.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
