package org.serdar.deeplearning;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AjaxController {
	
	private static final Logger log = LoggerFactory.getLogger(AjaxController.class);

	@Autowired
    JdbcTemplate jdbcTemplate;
	
	@PostConstruct
	private void initialize() {
		// CREATE TABLE sentences(id int primary key auto_increment,name VARCHAR(16),key INT,type VARCHAR(16), timestamp INT8)
//		log.info("Intializing tables");
//		try {
//			jdbcTemplate.execute("CREATE TABLE sentences("
//					+ "id SERIAL, "
//					+ "name VARCHAR(16), "
//					+ "key INT, "
//					+ "type VARCHAR(16), "
//					+ "timestamp INT8, "
//					+ ")");			
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		}
	}
	
	@PostMapping(value="/sendToServer")
    public void sendToServer(@RequestBody List<String> arr, Principal principal) {
		
		List<Object> list = IntStream.range(0, arr.size())
				.mapToObj(i -> (i%3 == 0 ) ? Integer.valueOf(arr.get(i)) : arr.get(i)).collect(Collectors.toList());
		List<Object> collect = IntStream.range(0, list.size())
				.mapToObj(i -> (i%3 == 2 ) ? Long.valueOf(arr.get(i)) : arr.get(i)).collect(Collectors.toList());
		
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < collect.size(); i=i+3) {
//			jdbcTemplate.update("INSERT INTO sentences(name,key,type,timestamp) "
//					+ "VALUES (?,?,?,?)", principal.getName(),collect.get(i),collect.get(i+1),collect.get(i+2));
			params.add(new Object[]{principal.getName(),collect.get(i),collect.get(i+1),collect.get(i+2)});
			
		}
		//INSERT INTO SENTENCES(name,key,type,timestamp) VALUES ('test',1,'keydown',12342534)
		jdbcTemplate.batchUpdate("INSERT INTO sentences(name,key,type,timestamp) "
				+ "VALUES (?,?,?,?)", params);
		
		
		
	}
}
