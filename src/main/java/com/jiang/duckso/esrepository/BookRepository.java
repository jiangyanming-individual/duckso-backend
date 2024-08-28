package com.jiang.duckso.esrepository;

import com.jiang.duckso.model.dto.es.Books;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * spring data es
 */
public interface BookRepository extends ElasticsearchRepository<Books,Long> {

}
