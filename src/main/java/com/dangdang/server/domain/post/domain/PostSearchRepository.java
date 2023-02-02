package com.dangdang.server.domain.post.domain;

import com.dangdang.server.domain.post.domain.entity.PostSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostSearchRepository extends ElasticsearchRepository<PostSearch, Long> {

}