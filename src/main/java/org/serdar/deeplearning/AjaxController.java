package org.serdar.deeplearning;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
		log.info("Intializing tables");
		try {
			jdbcTemplate.execute("CREATE TABLE sentences("
					+ "id int primary key auto_increment, "
					+ "name VARCHAR(16), "
					+ "key INT, "
					+ "type VARCHAR(16), "
					+ "timestamp INT8)");			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	@PostMapping(value="/sendToServer")
    public @ResponseBody String sendToServer(@RequestBody List<String> arr, Principal principal) {
		log.info("Received a sentence from " + principal.getName() + "!");
		
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
		
		return "{}";
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody List<KeyData> get(Principal principal) {
		return getKeyData();
	}
	
	@RequestMapping(value="/refined",method=RequestMethod.GET)
	public @ResponseBody List<KeyCompact> getRefined(Principal principal) {
		 // Dwell time (the time a key pressed) and Flight time (the time between "key up" and the next "key down")
		// u can not keydown the same key without keyupping it first 
		List<KeyData> rawData = getKeyData();
		
		Stack<KeyCompact> swapCache = new Stack<KeyCompact>();
		Stack<KeyCompact> keyDownStack = new Stack<KeyCompact>();
		List<KeyCompact> result = new ArrayList<KeyCompact>();
		
		for (KeyData raw : rawData) {
//			System.out.println(raw);
			if ("keydown".equals(raw.type)) {
				KeyCompact key = new KeyCompact(Label.valueOf(raw.name).asInt(),raw.key,raw.timestamp);
				keyDownStack.push(key);					
			} else {
				
				KeyCompact pop = null;
				do {
					//can't keyup a key without keydown but this happens on some keyboards
					if (keyDownStack.isEmpty()) {
						log.warn("keydown stack empty where it shouldn't be: " + raw );
						break;
					} 					
					pop = keyDownStack.pop();
					
					if (pop.key.equals(raw.key)) {
						pop.keyup = raw.timestamp;
						result.add(pop);
					} else {
						swapCache.push(pop);
					}
				} while (!pop.key.equals(raw.key));
				
				//now push em back in
				while (!swapCache.isEmpty()) {
					keyDownStack.push(swapCache.pop());
				}
			}
		}
		
		//now iterate result once and set dwell flight times
		KeyCompact current,next;
		for (int i = 0; i < result.size()-1; i++) {
			current = result.get(i);
			next = result.get(i+1);
			
			current.dwell = current.keyup - current.keydown;
			current.flight = next.keydown - current.keyup; // can be negative
			
		}
		
		// at this point we have every keys values set except for the last one which is an enter
		// also there could be long gaps between an enter and the next sentences first letter
		// since we wont use enters in our data set it wont matter.
		
		return result;
		
	}
	
	@RequestMapping(value="/deleteData/{password}",method=RequestMethod.GET)
    public @ResponseBody String sendToServer(@PathVariable String password, Principal principal) {
		if ("123qwe".equals(password)) {
			log.info("Deleting data");
			
			jdbcTemplate.execute("DELETE FROM SENTENCES");
		} else {
			throw new RuntimeException("Wrong password");
		}
		
		return "{}";
	}
	
	private List<KeyData> getKeyData() {
		
//		try {
//			FileReader fileReader = new FileReader("src/main/resources/static/data31.json");
//			ObjectMapper mapper = new ObjectMapper();
//			List<KeyData> result = mapper.readValue(fileReader, TypeFactory.defaultInstance()
//		            .constructCollectionType(List.class, KeyData.class));
//			
//			return result;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		} 
		
		List<KeyData> query = jdbcTemplate.query("SELECT * FROM SENTENCES ORDER BY name, timestamp", new RowMapper<KeyData>(){

			@Override
			public KeyData mapRow(ResultSet rs, int index) throws SQLException {
				KeyData k = new KeyData();
				k.setName(rs.getString("NAME"));
				k.setKey(rs.getInt("KEY"));
				k.setType(rs.getString("TYPE"));
				k.setTimestamp(rs.getLong("TIMESTAMP"));
				return k;
			}
			
		});
		
		return query;
	}
}
