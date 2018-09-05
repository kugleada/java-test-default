package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkQueryTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JavaScriptFrameworkRepository repository;

    @Before
    public void prepareData() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        JavaScriptFramework react = new JavaScriptFramework("ReactJS", "16.1.1", formatter.parse("22-1-2020"), 7);
        JavaScriptFramework vue = new JavaScriptFramework("Vue.js", "2.1.10", formatter.parse("22-1-2019"), 5);

        repository.save(react);
        repository.save(vue);
        repository.save(new JavaScriptFramework("ReactJS", "16.1.0", formatter.parse("22-5-2019"), 5));
        repository.save(new JavaScriptFramework("Vue.js", "1.1.10", formatter.parse("20-1-2016"), 3));
    }

    @Test
    public void findByNameTest() throws Exception {
        mockMvc.perform(get("/frameworks").param("name", "Vue.js")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Vue.js")))
                .andExpect(jsonPath("$[1].name", is("Vue.js")));
    }

    @Test
    public void findByNameAndVersionTest() throws Exception {
        mockMvc.perform(get("/frameworks").param("name", "ReactJS").param("version", "16.1.0")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("ReactJS")))
                .andExpect(jsonPath("$[0].version", is("16.1.0")));
    }

    @Test
    public void findByDeprecationDateTest() throws Exception {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        Date date = formatter.parse("22-1-2019");
        mockMvc.perform(get("/frameworks").param("date", "22-1-2019")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
        mockMvc.perform(get("/frameworks").param("date", "22-1-2019").param("dateOp", ">")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(3)));;
        mockMvc.perform(get("/frameworks").param("date", "22-1-2019").param("dateOp", "<")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4)));;
    }

    @Test
    public void findByHypeLevelTest() throws Exception {
        mockMvc.perform(get("/frameworks").param("hypeLevel", "5")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].hypeLevel", is(5)))
                .andExpect(jsonPath("$[1].hypeLevel", is(5)));
        mockMvc.perform(get("/frameworks").param("hypeLevel", "5").param("hypeOp", ">")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].hypeLevel", Matchers.greaterThan(5)));
        mockMvc.perform(get("/frameworks").param("hypeLevel", "5").param("hypeOp", "<")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].hypeLevel", Matchers.lessThan(5)));
    }


}