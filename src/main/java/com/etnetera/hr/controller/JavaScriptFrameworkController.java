package com.etnetera.hr.controller;

import com.etnetera.hr.controller.exception.FrameworkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple REST controller for accessing application logic.
 *
 * @author Etnetera
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

    private final JavaScriptFrameworkRepository repository;

    @Autowired
    public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/frameworks")
    public Iterable<JavaScriptFramework> getFrameworks(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "hypeLevel", required = false) Integer hypeLevel,
            @RequestParam(value = "hypeOp", required = false) String hypeOp,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "dateOp", required = false) String dateOp
    ) throws ParseException {
        if (name != null) {
            if (version != null) {
                return repository.findByNameAndVersion(name, version);
            }
            return repository.findByName(name);
        }
        if (hypeLevel != null) {
            if (hypeOp != null) {
                switch (hypeOp) {
                    case ">":
                        return repository.findByHypeLevelGreaterThan(hypeLevel);
                    case "<":
                        return repository.findByHypeLevelLessThan(hypeLevel);
                    default:
                        return repository.findByHypeLevel(hypeLevel);
                }

            }
            return repository.findByHypeLevel(hypeLevel);
        }
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date realDate = formatter.parse(date);
            if (dateOp != null) {
                switch (dateOp) {
                    case ">":
                        return repository.findByDeprecationDateAfter(realDate);
                    case "<":
                        return repository.findByDeprecationDateBefore(realDate);
                    default:
                        return repository.findByDeprecationDate(realDate);
                }
            }
            return repository.findByDeprecationDate(realDate);
        }
        return repository.findAll();
    }

    @PostMapping("/frameworks")
    public JavaScriptFramework addFramework(@RequestBody JavaScriptFramework framework) {
//		try {
        return repository.save(framework);
//		} catch (DataIntegrityViolationException ex) {
//
//		}
    }

    //single item
    @GetMapping("/frameworks/{id}")
    public JavaScriptFramework getFramework(@PathVariable Long id) throws FrameworkNotFoundException {
        return repository.findById(id).orElseThrow(() -> new FrameworkNotFoundException(id));
    }

    @PutMapping("/frameworks/{id}")
    JavaScriptFramework updateFramework(@RequestBody JavaScriptFramework updatedFramework, @PathVariable Long id) {

        return repository.findById(id)
                .map(oldFramework -> {
                    oldFramework.setName(updatedFramework.getName());
                    oldFramework.setVersion(updatedFramework.getVersion());
                    oldFramework.setDeprecationDate(updatedFramework.getDeprecationDate());
                    oldFramework.setHypeLevel(updatedFramework.getHypeLevel());
                    return repository.save(oldFramework);
                })
                .orElseGet(() -> {
                    updatedFramework.setId(id);
                    return repository.save(updatedFramework);
                });
    }

    @DeleteMapping("/frameworks/{id}")
    void deleteFramework(@PathVariable Long id) throws FrameworkNotFoundException {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new FrameworkNotFoundException(id);
        }
    }
}
