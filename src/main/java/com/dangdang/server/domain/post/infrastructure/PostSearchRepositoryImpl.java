package com.dangdang.server.domain.post.infrastructure;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.PostSearch;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

@Repository
public class PostSearchRepositoryImpl {

  private final ElasticsearchOperations operations;

  public PostSearchRepositoryImpl(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  public void bulkInsertOrUpdate(List<PostSearch> postSearches) {
    List<UpdateQuery> updates = postSearches.stream().map(postSearch ->
        UpdateQuery.builder(postSearch.getId())
            .withDocument(operations.getElasticsearchConverter().mapObject(postSearch))
            .withDocAsUpsert(true)
            .build()).collect(Collectors.toList());
    operations.bulkUpdate(updates, operations.getIndexCoordinatesFor(PostSearch.class));
  }

  public Slice<PostSearch> searchBySearchOptionSlice(String searchKeyword,
      PostSearchOptionRequest postSearchOptionRequest, List<Long> adjacentTownIds,
      Pageable pageable) {
    Query nativeSearchQuery = createNativeSearchQuery(searchKeyword,
        postSearchOptionRequest, adjacentTownIds, pageable);
    SearchHits<PostSearch> results = operations.search(nativeSearchQuery, PostSearch.class);
    List<PostSearch> postSearches = results.stream().map(SearchHit::getContent).toList();
    return toSlice(postSearches, pageable);
  }

  private Slice<PostSearch> toSlice(List<PostSearch> contents, Pageable pageable) {
    boolean hasNext = isContentSizeGreaterThanPageSize(contents, pageable);
    if (hasNext) {
      return new SliceImpl<>(subtractLastContent(contents, pageable), pageable, true);
    }
    return new SliceImpl<>(contents, pageable, false);
  }

  private boolean isContentSizeGreaterThanPageSize(List<PostSearch> contents, Pageable pageable) {
    return pageable.isPaged() && contents.size() > pageable.getPageSize() - 1;
  }

  private List<PostSearch> subtractLastContent(List<PostSearch> content, Pageable pageable) {
    return content.subList(0, pageable.getPageSize() - 1);
  }

  private Query createNativeSearchQuery(String searchKeyword,
      PostSearchOptionRequest postSearchOptionRequest, List<Long> adjacentTownIds,
      Pageable pageable) {

    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchKeyword,
        "title", "content").operator(Operator.AND);
    multiMatchQueryBuilder.type(Type.BEST_FIELDS);
    boolQueryBuilder.must(multiMatchQueryBuilder);
    if (!(postSearchOptionRequest.category() == null || postSearchOptionRequest.category()
        .isEmpty())) {

      TermsQueryBuilder categoryBuilder = QueryBuilders.termsQuery("category",
          postSearchOptionRequest.category().stream().map(Category::toString).toList());
      boolQueryBuilder.filter(categoryBuilder);
    }
    if (postSearchOptionRequest.maxPrice() != null) {
      RangeQueryBuilder maxPriceBuilder = QueryBuilders.rangeQuery("price")
          .to(postSearchOptionRequest.maxPrice()).includeLower(true);
      boolQueryBuilder.filter(maxPriceBuilder);
    }
    if (postSearchOptionRequest.minPrice() != null) {
      RangeQueryBuilder minPriceBuilder = QueryBuilders.rangeQuery("price")
          .from(postSearchOptionRequest.minPrice()).includeUpper(true);
      boolQueryBuilder.filter(minPriceBuilder);
    }
    TermsQueryBuilder adjacentTownsBuilder = QueryBuilders.termsQuery("townId", adjacentTownIds);
    boolQueryBuilder.filter(adjacentTownsBuilder);
    if (!(postSearchOptionRequest.isTransactionAvailableOnly() == null
        || !postSearchOptionRequest.isTransactionAvailableOnly())) {
      TermQueryBuilder transactionalOnlyBuilder = QueryBuilders.termQuery("status",
          StatusType.SELLING);
      boolQueryBuilder.filter(transactionalOnlyBuilder);
    }
    return new NativeSearchQueryBuilder()
        .withQuery(boolQueryBuilder)
        .withPageable(pageable)
        .build();
  }
}
