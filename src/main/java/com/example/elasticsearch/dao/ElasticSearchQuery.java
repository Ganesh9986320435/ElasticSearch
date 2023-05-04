package com.example.elasticsearch.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.objenesis.instantiator.SerializationInstantiatorHelper;
import org.springframework.stereotype.Repository;
import com.example.elasticsearch.model.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Repository
public class ElasticSearchQuery {

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	private final String indexName = "products";

	public String createOrUpdateDocument(Product product) throws IOException {

		IndexResponse response = elasticsearchClient
				.index(i -> i.index(indexName).id(product.getId()).document(product));
		if (response.result().name().equals("Created")) {
			return new StringBuilder("Document has been successfully created.").toString();
		} else if (response.result().name().equals("Updated")) {
			return new StringBuilder("Document has been successfully updated.").toString();
		}
		return new StringBuilder("Error while performing the operation.").toString();
	}

	public Product getDocumentById(String productId) throws IOException {
		Product product = null;
		GetResponse<Product> response = elasticsearchClient.get(g -> g.index(indexName).id(productId), Product.class);

		if (response.found()) {
			product = response.source();
			System.out.println("Product name " + product.getName());
		} else {
			System.out.println("Product not found");
		}

		return product;
	}

	public String deleteDocumentById(String productId) throws IOException {

		DeleteResponse deleteResponse = elasticsearchClient.delete(n -> n.index("products").id(productId));
		if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
			return new StringBuilder("Product with id " + deleteResponse.id() + " has been deleted.").toString();
		}
		System.out.println("Product not found");
		return new StringBuilder("Product with id " + deleteResponse.id() + " does not exist.").toString();

	}

	public List<Product> searchAllDocuments() throws IOException {

		SearchResponse<Product> response = elasticsearchClient.search(s -> s.index("products"), Product.class);
		List<Hit<Product>> hits = response.hits().hits();
		List<Product> products = new ArrayList<>();
		for (Hit<Product> object : hits) {
			products.add(object.source());

		}
		return products;
	}

	public List<Product> findMultiMatch(String some) throws ElasticsearchException, IOException {
////        SearchRequest searchRequest =new  SearchRequest(indexName);
//        String[] feild = {"ds","dsf"};
////        ObjectBuilder<MsearchTemplateRequest> builder=  QueryBuilders.multiMatchQuery(searchRequest, feild);
////        
//////    	SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();
//////    	sourceBuilder.query(QueryBuilders.multiMatchQuery(some, "name","fname"));
//////    	searchRequest.source();//source(sourceBuilder);
////    	SearchResponse searchResponse =  elasticsearchClient.msearchTemplate(builder, Product.class);
//        ObjectBuilder<Product> objectBuilder=(ObjectBuilder<Product>) QueryBuilders.boolQuery();
//        objectBuilder.
//    	MultiMatchQueryBuilder builder=QueryBuilders.multiMatchQuery(indexName, feild);
//    	builder.minimumShouldMatch();
//    	QueryBuilder  queryBuilder=builder.boost();
//    	

//    	NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//    			  .withQuery(multiMatchQuery("tutorial")
//    			    .field("title")
//    			    .field("tags")
//    			    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
//    			  .build();
//    	
//    	
//    	
//    	NativeSearchQuery nativeSearchQuery=new NativeSearchQuery(QueryBuilders.multiMatchQuery(some, "name","age"));

		SearchResponse<Product> response = elasticsearchClient.search(
				s -> s.index("products").query(q -> q.multiMatch(t -> t.fields("name", "description").query(some))),
				Product.class);
		List<Product> list = new ArrayList<>();
		for (Hit<Product> hits : response.hits().hits()) {
			list.add(hits.source());
		}
		return list;

	}

	public List<Product> searh() throws ElasticsearchException, IOException {
		String queryString = "{\"query\":{\"match\":{\"{{name}}\":\"{{gani}}\"}}}";

		co.elastic.clients.elasticsearch.core.SearchRequest searchRequest = co.elastic.clients.elasticsearch.core.SearchRequest
				.of(i -> i.index(indexName));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString));
		SearchResponse<Product> response = elasticsearchClient.search(searchRequest, Product.class);
		List<Product> list = new ArrayList<>();
		for (Hit<Product> hits : response.hits().hits()) {
			list.add(hits.source());
		}
		return list;
	}

	public List<Product> byName(String name) throws ElasticsearchException, IOException {
		SearchResponse<Product> response = elasticsearchClient
				.search(s -> s.index(indexName).query(q -> q.match(t -> t.field("name").query(name))), Product.class);
		List<Product> list = new ArrayList<>();
		for (Hit<Product> hits : response.hits().hits()) {
			list.add(hits.source());
		}
		return list;
	}

	public List<Product> byQueryString(String name) throws ElasticsearchException, IOException {
		SearchResponse<Product> response = elasticsearchClient.search(
				s -> s.index(indexName).query(k -> k.queryString(o -> o.query("name:%1$s OR description:%1$s"))),
				Product.class);
		List<Product> list = new ArrayList<>();
		for (Hit<Product> hits : response.hits().hits()) {
			list.add(hits.source());
		}
		return list;
	}

	public List<Product> boolQuery(String name, String description) throws ElasticsearchException, IOException {

		Query query = Query.of(a -> a.match(m -> m.field("name").query(name)));
		Query query1 = Query.of(a -> a.match(m -> m.field("description").query(description)));

		List<Query> list2 = new ArrayList<>();
		list2.add(query);
		list2.add(query1);

		SearchResponse<Product> searchResponse = elasticsearchClient
				.search(s -> s.index("products").query(q -> q.bool(b -> b.must(list2).filter(list2))), Product.class);
		List<Product> list = new ArrayList<>();
		for (Hit<Product> hits : searchResponse.hits().hits()) {
			list.add(hits.source());
		}
		return list;

	}

}
