package com.xitu.app.service.es;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.test.http.Request;
/**
 * 创建索引
 *
 * @author dell
 */

public class ESHttpClient implements Serializable {
    /**
     * 是否开启debug模式，debug模式下过程语句将会输出
     */
    public static boolean debug = false;
    /**
     * 日志对象
     */
    //private static Logger logger = Logger.getLogger(ESHttpClient.class);
    /**
     * 建索引的url
     */
    private String indexUrl;
    private String deleteUrl;
    /**
     * 查询索引的url
     */
    private String queryUrl;
    /**
     * 查询的返回值
     */
    private JSONObject queryJsonResult = null;
    /**
     * 编码
     */
    private String charset = "utf-8";

    /**
     * http访问对象
     */
    private Request request;
	protected static int nums;

    /**
     * 构造函数
     *
     * @param ip:port
     * @param
     */
    public ESHttpClient(String ipAndport, String indexName, String typeName) {

        if (indexName == null) {
            //System.out.println("indexName must not be null");
            return;
        }
        //构造查询indexurl
        this.indexUrl = "http://" + ipAndport;
        if (indexName != null)
            this.indexUrl = this.indexUrl + "/" + indexName;

        if (typeName != null)
            this.indexUrl = this.indexUrl + "/" + typeName;

        this.indexUrl = this.indexUrl + "/_bulk";

        //构造查询url
        this.queryUrl = "http://" + ipAndport;
        if (indexName != null)
            this.queryUrl = this.queryUrl + "/" + indexName;

        if (typeName != null)
            this.queryUrl = this.queryUrl + "/" + typeName;
        //preference=_primary_first&
        this.queryUrl = this.queryUrl + "/_search?search_type=dfs_query_then_fetch";

        //构造查询deleteurl
        this.deleteUrl = "http://" + ipAndport;
        if (indexName != null)
            this.deleteUrl = this.deleteUrl + "/" + indexName;

        if (typeName != null)
            this.deleteUrl = this.deleteUrl + "/" + typeName;

        this.deleteUrl = this.deleteUrl + "/_query";

        request = new Request(charset);
    }

    public ESHttpClient(String ipAndport, String indexName) {

        if (indexName == null) {
            ////System.out.println("indexName must not be null");
        }
        //构造查询indexurl
        this.indexUrl = "http://" + ipAndport;
        if (indexName != null)
            this.indexUrl = this.indexUrl + "/" + indexName;

        //this.indexUrl = this.indexUrl + "/_bulk";

        //构造查询deleteurl
        this.deleteUrl = "http://" + ipAndport;
        if (indexName != null)
            this.deleteUrl = this.deleteUrl + "/" + indexName;

        //this.deleteUrl = this.deleteUrl + "/_query";

        request = new Request(charset);
    }

    public ESHttpClient(String ipAndport) {


        //构造查询indexurl
        this.indexUrl = "http://" + ipAndport;

        //构造查询deleteurl
        this.deleteUrl = "http://" + ipAndport;

        request = new Request(charset);
    }


    /**
     * 提交请求
     *
     * @param fields 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
     * @return
     */
    public JSONObject execute(String queryStr) {

        //String queryStr = "{\"query\":{\"term\":{\"art_id\":\"11284091\"}}}";
        String queryResult = request.postRequest(this.queryUrl, queryStr, charset);

        System.out.println("query: curl " + this.queryUrl + " -d " + queryStr);
        //System.out.println(queryResult);

        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);

        return this.queryJsonResult;
    }

    /**
     * 返回检索结果，返回的检索字段以及字段顺序由{@link #execute(String[])} 方法中的参数fields指定
     *
     * @return 检索的结果列表
     */
    public List<String> getResults() {
        List<String> list = new LinkedList<String>();
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return list;

        JSONArray hitJsons = this.queryJsonResult.getJSONObject("hits").getJSONArray("hits");
        for (int index = 0; index < hitJsons.size(); index++) {

            JSONObject hitJson = hitJsons.getJSONObject(index);
            ////System.out.println(hitJson.toString());
            list.add(hitJson.toString());
        }

        return list;
    }

    /**
     * 索引数据
     *
     * @param dataList      数据列表
     * @param uniqueKeyName 数据在索引中的主键名
     */
    public boolean index(List<JSONObject> dataList, String uniqueKeyName) {

        boolean rt = true;

        StringBuffer indexStrBuffer = new StringBuffer();

        if (dataList == null || dataList.size() == 0)
            return rt;

        JSONObject indexJson = new JSONObject();
        for (JSONObject dataJson : dataList) {
            //System.out.println(dataJson.toJSONString());
            //构造插入语句
            if (dataJson.containsKey(uniqueKeyName)) {
                //构造插入语句
                if (dataJson.containsKey(uniqueKeyName)) {
                    indexJson.put("_id", dataJson.get(uniqueKeyName));
                }
                indexStrBuffer.append("{\"index\":").append(indexJson.toString()).append("}\n");
                indexStrBuffer.append(dataJson.toString()).append("\n");

                indexJson.clear();

            }
        }

        String queryResultStr = request.postRequest(this.indexUrl, indexStrBuffer.toString(), charset);
        //System.out.println("index=" + queryResultStr);
        if (queryResultStr != null) {
            rt = true;
        } else {
            rt = false;
        }

        return rt;
    }

    public boolean index(String typeName, List<JSONObject> dataList, String uniqueKeyName) {
        /**
         *  构建索引url
         */
        String indexUrl = this.indexUrl;
        if (typeName != null)
            indexUrl = indexUrl + "/" + typeName;
        indexUrl = indexUrl + "/_bulk";

        boolean rt = true;

        StringBuffer indexStrBuffer = new StringBuffer();

        if (dataList == null || dataList.size() == 0)
            return rt;

        JSONObject indexJson = new JSONObject();
        for (JSONObject dataJson : dataList) {
            //构造插入语句
            if (dataJson.containsKey(uniqueKeyName)) {
                //构造插入语句
                if (dataJson.containsKey(uniqueKeyName)) {
                    indexJson.put("_id", dataJson.get(uniqueKeyName));
                }
                indexStrBuffer.append("{\"index\":").append(indexJson.toString()).append("}\n");
                indexStrBuffer.append(dataJson.toString()).append("\n");

                indexJson.clear();

            }
        }

        ////System.out.println(indexStrBuffer.toString());
        //System.out.println("indexUrl=" + indexUrl);
        //System.out.println("indexUrl json=" + indexStrBuffer.toString());
        String queryResultStr = request.postRequest(indexUrl, indexStrBuffer.toString(), charset);

        if (queryResultStr != null) {
            rt = true;
        } else {
            rt = false;
        }

        return rt;
    }


    public boolean update(String indexName, String typeName, List<JSONObject> dataList, String uniqueKeyName) {
        /**
         *  构建索引url
         */
        String indexUrl = this.indexUrl;
        if (typeName != null)
            indexUrl = indexUrl + "/" + indexName + "/" + typeName;
        indexUrl = indexUrl + "/_bulk";

        boolean rt = true;

        StringBuffer indexStrBuffer = new StringBuffer();

        if (dataList == null || dataList.size() == 0)
            return rt;

        JSONObject indexJson = new JSONObject();
        for (JSONObject dataJson : dataList) {
            //构造插入语句
            if (dataJson.containsKey(uniqueKeyName)) {
                //构造插入语句
                if (dataJson.containsKey(uniqueKeyName)) {
                    indexJson.put("_id", dataJson.get(uniqueKeyName));
                }
                indexStrBuffer.append("{\"index\":").append(indexJson.toString()).append("}\n");
                indexStrBuffer.append(dataJson.toString()).append("\n");

                indexJson.clear();
            }
        }

        //System.out.println("indexUrl=" + indexUrl);
        //System.out.println("indexUrl json=" + indexStrBuffer.toString());
        String queryResultStr = request.postRequest(indexUrl, indexStrBuffer.toString(), charset);

        if (queryResultStr != null) {
            rt = true;
        } else {
            rt = false;
        }

        return rt;
    }


    /**
     * 获取搜索结果总长度
     *
     * @return 检索结果的总长度
     */
    public long getTotal() {

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return 0;

        return this.queryJsonResult.getJSONObject("hits").getInteger("total");

    }


    public static String createQqueryByUuid(String value){
    	JSONObject query = new JSONObject();
    	JSONObject term = new JSONObject();
    	JSONObject param = new JSONObject();
//    	value="8bc8ad0d53d5b8499ec64a4a88c1ad08";
    	param.put("uuid", value);
    	term.put("term", param);
    	query.put("query", term);
    	//System.out.println(query.toString());
    	return query.toString();
    	
    }
  
    public static String createQqueryExpertRgzn(String uuid,int pageNo){
    	JSONObject query = new JSONObject();
    	JSONObject match_all = new JSONObject();
    	JSONObject param = new JSONObject();
    	//value="8bc8ad0d53d5b8499ec64a4a88c1ad08";
    	param.put("uuid", "zzzzzzzzzzztestdata.*");
    	match_all.put("regexp", param);
    	query.put("query", match_all);
    	int pageSize = 10;
    	if(pageNo<=0){
    		pageNo = 1;
    	}
    	query.put("from",(pageNo-1)*pageSize);
    	query.put("size", pageSize);
    	//System.out.println(query.toString());
    	return query.toString();
    }
    	
	 public static JSONObject conexpertESIns(String conn){
	    	ESHttpClient indexer = new ESHttpClient("45.77.86.209:9200", "expertyiyao","et");
	    	ESHttpClient.debug = true;
	        JSONObject json = indexer.execute(conn);
	        return json;
	    }
	 public static JSONObject conpatentESIns(String conn){
		ESHttpClient indexer = new ESHttpClient("45.77.86.209:9200", "patentyiyao","pt");
		ESHttpClient.debug = true;
	    JSONObject json = indexer.execute(conn);
	    return json;
	}
	 public static JSONObject conpaperESIns(String conn){
		ESHttpClient indexer = new ESHttpClient("45.77.86.209:9200", "paperyiyao","pr");
		ESHttpClient.debug = true;
	    JSONObject json = indexer.execute(conn);
	    return json;
	}
	 
	 public static JSONObject conjianceESIns(String conn){
			ESHttpClient indexer = new ESHttpClient("45.77.86.209:9200", "jianceyiyao","jc");
			ESHttpClient.debug = true;
		    JSONObject json = indexer.execute(conn);
		    return json;
		}
    
   
    
}
