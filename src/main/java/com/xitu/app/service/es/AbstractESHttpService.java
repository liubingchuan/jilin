package com.xitu.app.service.es;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.ui.Model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.annotation.AggQuery;
import com.xitu.app.annotation.CrossQuery;
import com.xitu.app.annotation.DontMapping;
import com.xitu.app.annotation.SingleQuery;
import com.xitu.app.model.Jiance;
import com.xitu.app.utils.ThreadLocalUtil;

public abstract class AbstractESHttpService implements ESHttpService {

	@Value("${es.endpoint}")
	private String endpoint;

	protected abstract Class<?> getEntityClass();

	@Override
	public void execute(int pageIndex, int pageSize, int type,String...args) {
		convert(getHttpClient().execute(composeDSL(pageIndex, pageSize, type,args)));
	}
	
	@Override
	public JSONObject executeIns(String insNamearr,int pageIndex, int pageSize, String field, int type) {
		return convertIns(getHttpClient().execute(composeInsDSL(insNamearr,pageIndex, pageSize, field, type)),pageSize);
	}
	
	@Override
	public JSONObject executeXiangguan(int pageIndex, int pageSize,int type,String uuid,List<String> args) {
		return convertIns(getHttpClient().execute(composeXiangguanDSL(pageIndex, pageSize, type,uuid,args)),pageSize);
	}
	
	@Override
	public void executefamingren(int pageIndex, int pageSize, int type,String q,String person,String creator) {
		convert(getHttpClient().execute(composeDSL(pageIndex, pageSize, type,person,creator)));
	}
	
	public ESHttpClient getHttpClient() {
		String url = endpoint;
		String indexName = "";
		String type = "";
		ESHttpClient client = new ESHttpClient(url);
		Document anno = getEntityClass().getAnnotation(Document.class);
		if (anno != null) {
			Method[] met = anno.annotationType().getDeclaredMethods();
			for (Method me : met) {
				if (!me.isAccessible()) {
					me.setAccessible(true);
				}
				try {
					if (me.getName().equals("indexName")) {
						indexName = (String) me.invoke(anno, null);
					} else if (me.getName().equals("type")) {
						type = (String) me.invoke(anno, null);
					}
					System.out.println(me.invoke(anno, null));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			client = new ESHttpClient(url, indexName, type);
		}
		return client;
	}

	public String composeDSL(int pageIndex, int pageSize,int type,String...args) {
		List<Field> fields = getFields(getEntityClass());
		List<String> crossedFields = new ArrayList<String>();
		List<String> singleFields = new ArrayList<String>();
		JSONObject query = new JSONObject();
		JSONObject aggs = new JSONObject();
		for (Field f : fields) {
			AggQuery aggQuery = f.getAnnotation(AggQuery.class);
			CrossQuery crossQuery = f.getAnnotation(CrossQuery.class);
			SingleQuery singleQuery = f.getAnnotation(SingleQuery.class);
			String fieldName = f.getName();
			if (aggQuery != null) {
				JSONObject fz = new JSONObject();
				JSONObject terms = new JSONObject();
				int size = aggQuery.size();
				fz.put("field", fieldName);
				fz.put("size", size);
				terms.put("terms", fz);
				aggs.put(fieldName, terms);
			} 
			if (crossQuery != null) {
				crossedFields.add(fieldName);
			} 
			if(singleQuery != null) {
				singleFields.add(fieldName);
			}
		}
		query.put("aggs",aggs);
    	JSONArray sort = new JSONArray();
    	JSONObject _score = new JSONObject();
    	JSONObject order = new JSONObject();
    	order.put("order", "desc");//method=desc
    	_score.put("_score",order);//orderby=_score
    	sort.add(_score);
    	JSONObject pubtimes = new JSONObject();
    	String sortfield ="";
    	if (type == 3) {
    		sortfield = "pubtime";
		}
    	if (type == 0) {
    		sortfield = "publictime";
		}
    	if (type == 1) {
    		sortfield = "year";
		}
    	if (type == 2) {
    		sortfield = "now";
		}
    	if (type == 4) {
    		sortfield = "now";
		}
    	if (type == 5) {
    		sortfield = "now";
		}
    	if (type == 6) {
    		sortfield = "now";
		}
//    	JSONObject order1s = new JSONObject();
//    	order1s.put("order", "desc");
//    	pubtimes.put(sortfield,order1s);
//    	sort.add(pubtimes);
    	query.put("sort",sort);
		JSONObject param = new JSONObject();
		JSONArray should = new JSONArray();
		JSONObject multi_match = new JSONObject();
		JSONObject match_all = new JSONObject();
		JSONArray must = new JSONArray();
		JSONObject bool1 = new JSONObject();
    	JSONObject bool2 = new JSONObject();
		JSONObject bool4 = new JSONObject();
    	JSONObject bool3 = new JSONObject();
    	Model model = ThreadLocalUtil.get();
		if (args == null || args.length==0 || args[0] == null || "".equals(args[0]) || "null".equals(args[0])) {
		    match_all.put("match_all", param);
		    must.add(match_all);
		}else {
			param.put("query", args[0]);
			model.addAttribute("query", args[0]);
			param.put("operator", "and");
			param.put("type", "cross_fields");
			param.put("fields", crossedFields.toArray());
			//param.put("type", "best_fields");
			multi_match.put("multi_match", param);
			should.add(multi_match);
			bool4.put("should", should);
			bool3.put("bool", bool4);
			must.add(bool3);
		}
		for(int i=1;i<args.length;i++) {
			if(isNotBlank(args[i]) && !"null".equals(args[i])) {
				try {
					args[i] = URLDecoder.decode(args[i], "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (!args[i].equals("近一天") && !args[i].equals("近三天") && !args[i].equals("近一周") && !args[i].equals("近三周") && !args[i].equals("近一个月")) {
					if(args[i].contains(",")){
						String[] split = args[i].split(",");
						for(String s : split){
							JSONObject match = new JSONObject();
							JSONObject field = new JSONObject();
							field.put(singleFields.get(i-1), s);
							match.put("match", field);
							must.add(match);
						}
					}else{
						JSONObject match = new JSONObject();
						JSONObject field = new JSONObject();
						field.put(singleFields.get(i-1), args[i]);
						match.put("match", field);
						must.add(match);
					}
					model.addAttribute(singleFields.get(i-1) + "s", args[i]);
				}else{
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					 String pubtime = df.format(new Date());
					if (args[i].equals("近一天")) {
				        JSONObject match = new JSONObject();
						JSONObject field = new JSONObject();
						field.put("pubtime", pubtime);
						match.put("match", field);
						must.add(match);
					}
					if (args[i].equals("近三天")) {
				        JSONObject range = new JSONObject();
						JSONObject field = new JSONObject();
						JSONObject pub = new JSONObject();
						pub.put("gte", getThreeDay());
						pub.put("lte", pubtime);
						//pub.put("format", "yyyy-MM-dd");
						field.put("pubtime",pub);
						range.put("range", field);
						must.add(range);
					}
					if (args[i].equals("近一周")) {
				        JSONObject range = new JSONObject();
						JSONObject field = new JSONObject();
						JSONObject pub = new JSONObject();
						pub.put("gte", getOneWeek());
						pub.put("lte", pubtime);
						//pub.put("format", "yyyy-MM-dd");
						field.put("pubtime",pub);
						range.put("range", field);
						must.add(range);
					}
					if (args[i].equals("近三周")) {
				        JSONObject range = new JSONObject();
						JSONObject field = new JSONObject();
						JSONObject pub = new JSONObject();
						pub.put("gte", getThreeWeek());
						pub.put("lte", pubtime);
						//pub.put("format", "yyyy-MM-dd");
						field.put("pubtime",pub);
						range.put("range", field);
						must.add(range);
					}
					if (args[i].equals("近一个月")) {
				        JSONObject range = new JSONObject();
						JSONObject field = new JSONObject();
						JSONObject pub = new JSONObject();
						pub.put("gte", getLastMonth());
						pub.put("lte", pubtime);
						//pub.put("format", "yyyy-MM-dd");
						field.put("pubtime",pub);
						range.put("range", field);
						must.add(range);
					}
					model.addAttribute("pubtimes", args[i]);
				}
				
			}
			
		}
		
    	bool2.put("must",must);
    	bool1.put("bool", bool2);
    	query.put("query", bool1);
    	query.put("from",pageIndex*pageSize);
    	query.put("size", pageSize);
    	System.out.println("query ---  " + query.toString());
    	return query.toString();
	}

	private List<Field> getFields(Class<?> clazz) {
		List<Field> result = new ArrayList<Field>();
		if (clazz != null) {
			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != Object.class) {
				result.addAll(getFields(superClazz));
			}
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				DontMapping dontMapping = field.getAnnotation(DontMapping.class);
				if (dontMapping == null) {
					result.add(field);
				}
			}
		}
		return result;
	}
	
	public void convert(JSONObject response) {
		JSONObject hits = response.getJSONObject("hits");
		Integer totalCount = hits.getInteger("total"); 
		JSONArray list = hits.getJSONArray("hits");
		List sources = new LinkedList();
		for(int i=0;i<list.size();i++) {
			JSONObject obj = list.getJSONObject(i);
			JSONObject source = (JSONObject) obj.get("_source");
			sources.add(source);
		}
		Model model = ThreadLocalUtil.get();
		model.addAttribute("list", sources);
		model.addAttribute("totalCount", totalCount);
		long totalPages = 0L;
		if (totalCount > 0) {
			if(totalCount % 10 == 0){
				totalPages = totalCount/10;
			}else{
				totalPages = totalCount/10 + 1;
			}
		}
		model.addAttribute("totalPages", totalPages);
		JSONObject aggregations = response.getJSONObject("aggregations");
		Set<String> keys = aggregations.keySet();
		for(String key : keys) {
			JSONObject agg = (JSONObject) aggregations.get(key);
			model.addAttribute(key, agg.get("buckets"));
		}
	}
	
	public JSONObject convertIns(JSONObject response,int pageSize) {
		JSONObject hits = response.getJSONObject("hits");
		Integer totalCount = hits.getInteger("total"); 
		JSONArray list = hits.getJSONArray("hits");
		List sources = new LinkedList();
		for(int i=0;i<list.size();i++) {
			JSONObject obj = list.getJSONObject(i);
			JSONObject source = (JSONObject) obj.get("_source");
			sources.add(source);
		}
		Model model = ThreadLocalUtil.get();
		//model.addAttribute("list", sources);
		//model.addAttribute("totalCount", totalCount);
		long totalPages = 0L;
		if (totalCount > 0) {
			if(totalCount % pageSize == 0){
				totalPages = totalCount/pageSize;
			}else{
				totalPages = totalCount/pageSize + 1;
			}
		}
		//model.addAttribute("totalPages", totalPages);
		JSONObject rs = new JSONObject();
		rs.put("list", sources);
		rs.put("totalPages", totalPages);
		rs.put("totalCount", totalCount);
		return rs;
	}
	
	public static void main(String[] args) { 
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
	}
	
	public static String createQqueryByListIns(List<String> insNamearr,int aggsize,String type){
    	JSONObject query = new JSONObject();
    	JSONObject bool1 = new JSONObject();
    	JSONObject bool2 = new JSONObject();
    	JSONObject bool3 = new JSONObject();
    	JSONObject must = new JSONObject();
    	JSONArray should = new JSONArray();
    	for(String s:insNamearr){
    		JSONObject term = new JSONObject();
        	JSONObject param = new JSONObject();
        	param.put(type, s);
        	term.put("match", param);
        	should.add(term);
    	}
    	bool2.put("should", should);
    	bool3.put("bool", bool2);
    	must.put("must",bool3);
    	bool1.put("bool", must);
    	query.put("query", bool1);
    	
    	JSONObject args = new JSONObject();
    	JSONObject fz = new JSONObject();
		JSONObject terms = new JSONObject();
		
		fz.put("field", type);
		fz.put("size", aggsize);
		terms.put("terms", fz);
		args.put(type, terms);
		query.put("aggs",args);
    	//System.out.println("*****"+query.toString());
    	return query.toString();
    	
    }
	public String composeInsDSL(String insNamearr,int pageIndex, int pageSize, String field, int type){
		JSONObject query = new JSONObject();
    	JSONObject bool1 = new JSONObject();
    	JSONObject bool2 = new JSONObject();
    	JSONObject bool3 = new JSONObject();
    	JSONObject must = new JSONObject();
    	JSONArray should = new JSONArray();
    	if (insNamearr.contains(",")) {
    		for(String s:insNamearr.split(",")){
	    		JSONObject term = new JSONObject();
	        	JSONObject param = new JSONObject();
	        	param.put(field, s);
	        	term.put("match", param);
	        	should.add(term);
    	    }
		}else {
			JSONObject term = new JSONObject();
        	JSONObject param = new JSONObject();
        	param.put(field, insNamearr);
        	term.put("match", param);
        	should.add(term);
		}
    	
    	bool2.put("should", should);
    	bool3.put("bool", bool2);
    	must.put("must",bool3);
    	bool1.put("bool", must);
    	query.put("query", bool1);
    	query.put("from",pageIndex*pageSize);
    	query.put("size", pageSize);
//    	JSONObject args = new JSONObject();
//    	JSONObject fz = new JSONObject();
//		JSONObject terms = new JSONObject();
//		
//		fz.put("field", type);
//		fz.put("size", 15);
//		terms.put("terms", fz);
//		args.put(field, terms);
//		query.put("aggs",args);
    	//System.out.println("*****"+query.toString());
    	return query.toString();
	}
	public String composeXiangguanDSL(int pageIndex, int pageSize,int type,String uuid,List<String> args) {
		List<Field> fields = getFields(getEntityClass());
		List<String> crossedFields = new ArrayList<String>();
		
		JSONObject query = new JSONObject();
		
		for (Field f : fields) {
			
			CrossQuery crossQuery = f.getAnnotation(CrossQuery.class);
			
			String fieldName = f.getName();
			 
			if (crossQuery != null) {
				crossedFields.add(fieldName);
			} 
			
		}
		
    	JSONArray sort = new JSONArray();
    	JSONObject _score = new JSONObject();
    	JSONObject order = new JSONObject();
    	order.put("order", "desc");//method=desc
    	_score.put("_score",order);//orderby=_score
    	sort.add(_score);
    	JSONObject pubtimes = new JSONObject();
    	String sortfield ="";
    	if (type == 3) {
    		sortfield = "pubtime";
		}
    	if (type == 0) {
    		sortfield = "publictime";
		}
    	if (type == 1) {
    		sortfield = "year";
		}
    	if (type == 2) {
    		sortfield = "now";
		}
    	if (type == 4) {
    		sortfield = "now";
		}
    	if (type == 5) {
    		sortfield = "now";
		}
    	if (type == 6) {
    		sortfield = "now";
		}
//    	JSONObject order1s = new JSONObject();
//    	order1s.put("order", "desc");
//    	pubtimes.put(sortfield,order1s);
//    	sort.add(pubtimes);
    	query.put("sort",sort);
		
		JSONArray should = new JSONArray();
		//JSONObject multi_match = new JSONObject();
		JSONObject match_all = new JSONObject();
		JSONArray must = new JSONArray();
		JSONObject bool1 = new JSONObject();
    	JSONObject bool2 = new JSONObject();
		JSONObject bool4 = new JSONObject();
    	JSONObject bool3 = new JSONObject();
		if (args == null || args.size()==0 ) {
			JSONObject param = new JSONObject();
		    match_all.put("match_all", param);
		    must.add(match_all);
		}else {
			
			for(int i=0;i<args.size();i++) {
				JSONObject multi_match = new JSONObject();
				JSONObject param = new JSONObject();
				param.put("query", args.get(i));
				
				param.put("operator", "and");
				param.put("type", "cross_fields");
				param.put("fields", crossedFields.toArray());
				//param.put("type", "best_fields");
				multi_match.put("multi_match", param);
				should.add(multi_match);
			}
			
			bool4.put("should", should);
			bool3.put("bool", bool4);
			must.add(bool3);
		}
		
    	bool2.put("must",must);
    	
    	JSONObject must_not = new JSONObject();
    	JSONObject term = new JSONObject();
    	must_not.put("term", term);
    	term.put("id", uuid);
    	bool2.put("must_not",must_not);
    	
    	bool1.put("bool", bool2);
    	query.put("query", bool1);
    	query.put("from",pageIndex*pageSize);
    	query.put("size", pageSize);
    	System.out.println("query ---  " + query.toString());
    	return query.toString();
	}
	public String composefamingrenDSL(int pageIndex, int pageSize,int type,String q,String person,String creator) {
		List<String> crossedFields = new ArrayList<String>();
		JSONObject query = new JSONObject();
		JSONObject aggs = new JSONObject();
		
		JSONObject fz = new JSONObject();
		JSONObject terms = new JSONObject();
		int size = 10;
		fz.put("field", "person");
		fz.put("size", size);
		terms.put("terms", fz);
		aggs.put("person", terms);
		JSONObject fz1 = new JSONObject();
		JSONObject terms1 = new JSONObject();
		fz1.put("field", "creator");
		fz1.put("size", size);
		terms1.put("terms", fz1);
		aggs.put("creator", terms1);
		query.put("aggs",aggs);
    	
		JSONObject param = new JSONObject();
		JSONArray should = new JSONArray();
		JSONObject multi_match = new JSONObject();
		JSONObject match_all = new JSONObject();
		JSONArray must = new JSONArray();
		JSONObject bool1 = new JSONObject();
    	JSONObject bool2 = new JSONObject();
		JSONObject bool4 = new JSONObject();
    	JSONObject bool3 = new JSONObject();
    	Model model = ThreadLocalUtil.get();
		if (q == null || q.length()==0 || q == null || "".equals(q) || "null".equals(q)) {
		    match_all.put("match_all", param);
		    must.add(match_all);
		}else {
			param.put("query", q);
			model.addAttribute("query",q);
			param.put("operator", "and");
			param.put("type", "cross_fields");
			param.put("fields", crossedFields.toArray());
			multi_match.put("multi_match", param);
			should.add(multi_match);
			bool4.put("should", should);
			bool3.put("bool", bool4);
			must.add(bool3);
		}
		
    	bool2.put("must",must);
    	bool1.put("bool", bool2);
    	query.put("query", bool1);
    	query.put("from",pageIndex*pageSize);
    	query.put("size", pageSize);
    	System.out.println("query ---  " + query.toString());
    	return query.toString();
	}
	public String getLastMonth(){
        Calendar curr = Calendar.getInstance(); 
        curr.set(Calendar.MONTH,curr.get(Calendar.MONTH)-1); //减少一月
        Date date=curr.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(date);  
        return dateNowStr;
    }
	public String getThreeDay(){
        Calendar curr = Calendar.getInstance(); 
        curr.set(Calendar.DAY_OF_YEAR,curr.get(Calendar.DAY_OF_YEAR)-2); //减少2天
        Date date=curr.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(date);  
        return dateNowStr;
    }
	public String getOneWeek(){
        Calendar curr = Calendar.getInstance(); 
        curr.set(Calendar.DAY_OF_YEAR,curr.get(Calendar.DAY_OF_YEAR)-6); //减少6天
        Date date=curr.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(date);  
        return dateNowStr;
    }
	public String getThreeWeek(){
        Calendar curr = Calendar.getInstance(); 
        curr.set(Calendar.DAY_OF_YEAR,curr.get(Calendar.DAY_OF_YEAR)-20); //减少6天
        Date date=curr.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(date);  
        return dateNowStr;
    }
}
