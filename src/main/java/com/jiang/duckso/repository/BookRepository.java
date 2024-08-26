package com.jiang.duckso.repository;

import com.jiang.duckso.model.dto.es.Books;
import org.springframework.data.repository.Repository;

/**
 * spring data es
 */
public interface BookRepository extends Repository<Books,Long> {
    
}
