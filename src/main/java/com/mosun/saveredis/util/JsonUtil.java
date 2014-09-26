package com.mosun.saveredis.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;
/**
 * @author Melon
 * 2013-5-11 下午05:27:10   
 * @version
 */
public class JsonUtil<T, ID extends Serializable> {

	private static  ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger=Logger.getLogger("json");
	private volatile static JsonUtil jsonFactory = null;
	private static final String getCaller(){
		StringBuilder sb = new StringBuilder();
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement ste = stack[i];
            sb.append(ste.getClassName());
            sb.append(":");
            sb.append(ste.getMethodName());
            sb.append(":");
            sb.append(ste.getFileName());
            sb.append(":");
            sb.append(ste.getLineNumber());
            
        }
        return sb.toString();
	}
	private static final String cutString(String str,int len){
		if (str==null) return null;
		int strlen = str.length();
		if (strlen<=len){
			return str;
		}
		return str.substring(0, len);
	}
	@SuppressWarnings("deprecation")
	private JsonUtil() {
		
		//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		
		// 设置默认日期格式
//		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//		mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
      /*  //提供其它默认设置
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
		mapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));*/
		mapper.disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
//		mapper.disable(SerializationConfig.Feature.WRITE_NULL_PROPERTIES);
		mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
		mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_EMPTY);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.getSerializationConfig().withSerializationInclusion(Inclusion.NON_NULL);
//		mapper.getSerializationConfig().withSerializationInclusion(Inclusion.NON_EMPTY);
//		mapper.getSerializationConfig().withSerializationInclusion(Inclusion.NON_DEFAULT);
		mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY) // auto-detect all member fields
        .setVisibility(JsonMethod.GETTER, JsonAutoDetect.Visibility.NONE) // but only public getters
        .setVisibility(JsonMethod.IS_GETTER, JsonAutoDetect.Visibility.NONE) // and none of "is-setters"
        .enable(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY)
  ;
				
	}

	public static JsonUtil getInstance() {
		if (jsonFactory==null){
			synchronized (JsonUtil.class) {
				if (jsonFactory==null){
					jsonFactory = new JsonUtil();
				}
			}
		}
		return jsonFactory;
	}

	public <T> T readValue(String value, Class<T> Object) {
		
		if (value != null && !value.trim().equals("")) {
			try {
				 T ret = mapper.readValue(value, Object);
				 return ret;
				
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public <T> T readValue(String value, TypeReference<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				T ret = mapper.readValue(value, Object);
				return ret;
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String writeValue(Object object) {
		if (object == null) {
			return null;
		}
//		StringWriter sw = new StringWriter();
		try {
//			mapper.writeValue(sw, object);
			String ret = mapper.writeValueAsString(object);
			return ret;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将json数据解析放到map
	 * 注：适用于多层的json数据
	 * @param json
	 * @return
	 */
	@Deprecated
	public HashMap<String, Object> changeJsonToMap(String json)
	{
		try{
			JsonFactory jsonFactory = new MappingJsonFactory();  
	        // Json解析器  
	        JsonParser jsonParser = jsonFactory.createJsonParser(json);  
	        // 跳到结果集的开始  
	        jsonParser.nextToken();  
	        // 接受结果的HashMap  
	        HashMap<String, Object> map = new HashMap<String, Object>();  
	        // while循环遍历Json结果  
	        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {  
//	        	 String key = jsonParser.getText();  
//	             JsonToken nextToken = jsonParser.nextToken();  
	        	
	        }  
	        return map;
		}catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将json数据解析放到map
	 * 注：适用于多层的json数据
	 * @author XMING
	 * @param json
	 * @return
	 */
	public HashMap<String, String> changeJsonToMap2(String json)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		
		
		try{
				
			map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
			
	       return map;
	          
	        
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	protected boolean isSimpleValue(JsonToken jsonToken) {  
        return !jsonToken.equals(JsonToken.START_ARRAY) && !jsonToken.equals(JsonToken.START_OBJECT);  
    }  
	/* public ValueBean parse(JsonParser jsonParser) {  
	        ValueBean vb = new ValueBean();  
	        try {  
	            JsonToken jsonToken = jsonParser.getCurrentToken();  
	            if (jsonToken == null) {  
	                jsonToken = jsonParser.nextToken();  
	            }  
	            if (JsonToken.START_OBJECT.equals(jsonToken)) {  
	                vb.setObject(parseObject(jsonParser));  
	                return vb;  
	            } else if (JsonToken.START_ARRAY.equals(jsonToken)) {  
	                vb.setArray(parseArray(jsonParser));  
	                return vb;  
	            } else {  
	                throw new RuntimeException("parser json error,jsonParser is" + jsonParser);  
	            }  
	        } catch (Exception e) {  
	            throw new RuntimeException(e);  
	        }  
	    }  */
	/* protected Map parseObject(JsonParser jsonParser) throws JsonParseException, IOException {  
	        JsonToken jsonToken = jsonParser.getCurrentToken();  
	        if (jsonToken == null) {  
	            jsonToken = jsonParser.nextToken();  
	        }  
	        Map<String, Object> object = new HashMap<String, Object>();  
	  
	        while ((jsonToken = jsonParser.nextToken()) != JsonToken.END_OBJECT) {  
	  
	            String key = jsonParser.getText();  
	            JsonToken nextToken = jsonParser.nextToken();  
	  
	            if (isSimpleValue(nextToken)) {  
	                String value = jsonParser.getText();  
	                object.put(key, value);  
	            } else {  
	                ValueBean vb = parse(jsonParser);  
	                Object value = vb.isObject() ? vb.getObject() : vb.getArray();  
	                object.put(key, value);  
	            }  
	        }  
	        return object;  
	    }  */
}
