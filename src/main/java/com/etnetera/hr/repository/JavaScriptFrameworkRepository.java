package com.etnetera.hr.repository;

import org.springframework.data.repository.CrudRepository;

import com.etnetera.hr.data.JavaScriptFramework;

import java.util.Date;
import java.util.List;

/**
 * Spring data repository interface used for accessing the data in database.
 * 
 * @author Etnetera
 *
 */
public interface JavaScriptFrameworkRepository extends CrudRepository<JavaScriptFramework, Long> {
    List<JavaScriptFramework> findByName(String name);
    List<JavaScriptFramework> findByNameAndVersion(String name, String version);
    List<JavaScriptFramework> findByDeprecationDate(Date date);
    List<JavaScriptFramework> findByDeprecationDateBefore(Date date);
    List<JavaScriptFramework> findByDeprecationDateAfter(Date date);
    List<JavaScriptFramework> findByHypeLevel(int hypeLevel);
    List<JavaScriptFramework> findByHypeLevelLessThan(int hypeLevel);
    List<JavaScriptFramework> findByHypeLevelGreaterThan(int hypeLevel);
}
