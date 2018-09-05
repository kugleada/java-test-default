package com.etnetera.hr;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.etnetera.hr.controller.exception.FrameworkNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Class used for Spring Boot/MVC based tests.
 * 
 * @author Etnetera
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkTests {

	@Autowired
	private MockMvc mockMvc;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private JavaScriptFrameworkRepository repository;


//	@Rule
//	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void prepareData() {
		JavaScriptFramework react = new JavaScriptFramework("ReactJS", "16.1.1", new Date(), 5);
		JavaScriptFramework vue = new JavaScriptFramework("Vue.js", "2.1.10", new Date(), 7);
		
		repository.save(react);
		repository.save(vue);
	}

//	@Before
//	public void setUp() throws Exception {
//		if (setUpIsDone) {
//			return;
//		}
//		prepareData();
//		setUpIsDone = true;
//	}

	@Test
	public void frameworksTest() throws Exception {
		//prepareData();

		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("ReactJS")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));
	}

	@Test
	public void addFrameworkInvalid() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.message", is("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement")));
//				.andExpect(jsonPath("$.errors", hasSize(1)))
//				.andExpect(jsonPath("$.errors[0].field", is("name")))
//				.andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
		
		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error.message", is("could not execute statement; SQL [n/a]; nested exception is org.hibernate.exception.DataException: could not execute statement")));
//			.andExpect(jsonPath("$.errors", hasSize(1)))
//			.andExpect(jsonPath("$.errors[0].field", is("name")))
//			.andExpect(jsonPath("$.errors[0].message", is("Size")));
		
	}

	@Test
	public void addFrameworkTest() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework("AngularJS", "1.5.0", new Date(), 10);
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isOk());//created?
	}

	@Test
	public void getFrameworkTest() throws Exception {
		//prepareData();
		mockMvc.perform(get("/frameworks/1")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}

	@Test
	public void getInvalidFrameworkTest() throws Exception {
		//prepareData();
		mockMvc.perform(get("/frameworks/100")).andExpect(status().isNotFound());
	}

	@Test
	public void updateFrameworkTest() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework("AngularJS", "1.5.0", new Date(), 10);
		mockMvc.perform(put("/frameworks/2").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(2)))
				.andExpect(jsonPath("$.name", is("AngularJS")))
				.andExpect(jsonPath("$.version", is("1.5.0")))
				//TODO .andExpect(jsonPath("$.deprecationDate", is(2)))
				.andExpect(jsonPath("$.hypeLevel", is(10)));
	}

	@Test
	public void putFrameworkTest() throws JsonProcessingException, Exception {
		JavaScriptFramework framework = new JavaScriptFramework("AngularJS", "1.5.0", new Date(), 10);
		mockMvc.perform(put("/frameworks/100").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
				.andExpect(status().isOk())
				//.andExpect(jsonPath("$.id", is(100)))
				.andExpect(jsonPath("$.name", is("AngularJS")))
				.andExpect(jsonPath("$.version", is("1.5.0")))
				//TODO .andExpect(jsonPath("$.deprecationDate", is(2)))
				.andExpect(jsonPath("$.hypeLevel", is(10)));
	}

	@Test
	public void deleteFrameworkTest() throws Exception {
		mockMvc.perform(delete("/frameworks/1")).andExpect(status().isOk());
		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void deleteInvalidFrameworkTest() throws Exception {
		mockMvc.perform(delete("/frameworks/100")).andExpect(status().isNotFound());
	}

}
