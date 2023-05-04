package com.example.elasticsearch.controller;

import org.apache.lucene.util.StringHelper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.elasticsearch.dao.ElasticSearchQuery;
import com.example.elasticsearch.model.Product;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ElasticSearchController {

    @Autowired
    private ElasticSearchQuery elasticSearchQuery;
    
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @PostMapping("/createOrUpdateDocument")
    public ResponseEntity<Object> createOrUpdateDocument(@RequestBody Product product) throws IOException {
          String response = elasticSearchQuery.createOrUpdateDocument(product);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getDocument")
    public ResponseEntity<Object> getDocumentById(@RequestParam String productId) throws IOException {
       Product product =  elasticSearchQuery.getDocumentById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/deleteDocument")
    public ResponseEntity<Object> deleteDocumentById(@RequestParam String productId) throws IOException {
        String response =  elasticSearchQuery.deleteDocumentById(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchDocument")
    public ResponseEntity<Object> searchAllDocument() throws IOException {
        List<Product> products = elasticSearchQuery.searchAllDocuments();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @GetMapping("/find")
    public ResponseEntity<Object> name(@RequestParam String some) throws ElasticsearchException, IOException {
		List<Product> products=elasticSearchQuery.findMultiMatch(some);
		return new ResponseEntity<>(products,HttpStatus.OK);
	}
    
    
    @GetMapping("/findByName")
    public ResponseEntity<Object> byName(@RequestParam String name) throws ElasticsearchException, IOException
    {
    	return new ResponseEntity<>(elasticSearchQuery.byName(name),HttpStatus.OK);
    }
    
    @GetMapping("/QueryString")
    public ResponseEntity<Object> byQueryString(@RequestParam String name) throws ElasticsearchException, IOException
    {
    	return new ResponseEntity<>(elasticSearchQuery.byQueryString(name),HttpStatus.OK);
    }
    
    @GetMapping("/boolQuery")
    public ResponseEntity<Object> boolQuery(@RequestParam String name,@RequestParam String description) throws ElasticsearchException, IOException
    {
    	return new ResponseEntity<>(elasticSearchQuery.boolQuery(name,description),HttpStatus.OK);
    }
    
    
    
    
//    @GetMapping("/search")
//    public List<Product> search() throws ElasticsearchException, IOException
//    {
//    	return elasticSearchQuery.searh();
//    }
}
