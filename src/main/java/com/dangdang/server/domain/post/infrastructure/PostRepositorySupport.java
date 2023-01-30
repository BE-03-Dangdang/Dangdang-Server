package com.dangdang.server.domain.post.infrastructure;

import static com.dangdang.server.domain.post.domain.entity.QPost.post;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class PostRepositorySupport extends QuerydslRepositorySupport {

  private final JPAQueryFactory queryFactory;

  public PostRepositorySupport(JPAQueryFactory queryFactory) {
    super(Post.class);
    this.queryFactory = queryFactory;
  }

  public Slice<Post> searchBySearchOptionSlice(String query,
      PostSearchOptionRequest postSearchOptionRequest, List<Long> adjacentTownIds, Pageable pageable) {
    List<Post> results = queryFactory.selectFrom(post)
        .where(
            containsTitleOrContent(query),
            inCategory(postSearchOptionRequest.category()),
            greaterOrEqualThanMinPrice(postSearchOptionRequest.minPrice()),
            lowerOrEqualThanMaxPrice(postSearchOptionRequest.maxPrice()),
            inAdjacentTownIds(adjacentTownIds),
            eqTransactionAvailable(postSearchOptionRequest.isTransactionAvailableOnly())
        )
        .orderBy(post.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1)
        .fetch();

    return toSlice(results, pageable);
  }

  public List<Post> searchBySearchOption(String query,
      PostSearchOptionRequest postSearchOptionRequest, List<Long> adjacentTownIds) {
    return queryFactory.selectFrom(post)
        .where(
            containsTitleOrContent(query),
            inCategory(postSearchOptionRequest.category()),
            greaterOrEqualThanMinPrice(postSearchOptionRequest.minPrice()),
            lowerOrEqualThanMaxPrice(postSearchOptionRequest.maxPrice()),
            inAdjacentTownIds(adjacentTownIds),
            eqTransactionAvailable(postSearchOptionRequest.isTransactionAvailableOnly())
        )
        .orderBy(post.createdAt.desc())
        .fetch();
  }

  private BooleanExpression containsTitleOrContent(String query) {
    if(!StringUtils.hasText(query)) {
      return null;
    }
    return post.title.contains(query).or(post.content.contains(query));
  }

  private BooleanExpression inCategory(List<Category> categories) {
    if(categories == null || categories.isEmpty()) {
      return null;
    }
    return post.category.in(categories);
  }

  private BooleanExpression greaterOrEqualThanMinPrice(Long minPrice) {
    if(minPrice == null) {
      return null;
    }
    return post.price.goe(minPrice);
  }

  private BooleanExpression lowerOrEqualThanMaxPrice(Long maxPrice) {
    if(maxPrice == null) {
      return null;
    }
    return post.price.loe(maxPrice);
  }

  private BooleanExpression inAdjacentTownIds(List<Long> adjacentTownIds) {
    return post.town.id.in(adjacentTownIds);
  }

  private BooleanExpression eqTransactionAvailable(Boolean isTransactionAvailableOnly) {
    if (isTransactionAvailableOnly == null || !isTransactionAvailableOnly) {
      return null;
    }
    return post.status.eq(StatusType.SELLING);
  }

  private Slice<Post> toSlice(List<Post> contents, Pageable pageable) {
    boolean hasNext = isContentSizeGreaterThanPageSize(contents, pageable);
    return new SliceImpl<>(hasNext ? subtractLastContent(contents, pageable) : contents, pageable,
        hasNext);
  }

  private boolean isContentSizeGreaterThanPageSize(List<Post> contents, Pageable pageable) {
    return pageable.isPaged() && contents.size() > pageable.getPageSize();
  }

  private List<Post> subtractLastContent(List<Post> content, Pageable pageable) {
    return content.subList(0, pageable.getPageSize());
  }
}
