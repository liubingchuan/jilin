package com.xitu.app.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.common.cache.CachePool;
import com.xitu.app.common.request.AgPersonRequest;
import com.xitu.app.common.request.AgTypeRequest;
import com.xitu.app.common.request.PatentPageListRequest;
import com.xitu.app.common.request.PriceAvgRequest;
import com.xitu.app.mapper.PatentMapper;
import com.xitu.app.mapper.PriceMapper;
import com.xitu.app.model.Expert;
import com.xitu.app.model.Org;
import com.xitu.app.model.Patent;
import com.xitu.app.model.PatentMysql;
import com.xitu.app.model.Price;
import com.xitu.app.repository.ExpertRepository;
import com.xitu.app.repository.OrgRepository;
import com.xitu.app.repository.PatentRepository;
import com.xitu.app.service.es.JianceService;
import com.xitu.app.service.es.PatentService;
import com.xitu.app.utils.DocUtil;
import com.xitu.app.utils.ImageUtil;
import com.xitu.app.utils.JsonUtil;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class PatentController {

	private static final Logger logger = LoggerFactory.getLogger(PatentController.class);
	
	@Autowired
    private PatentRepository patentRepository;
	@Autowired
	private ExpertRepository expertRepository;
	@Autowired
	private OrgRepository orgRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private PriceMapper priceMapper;
	
	@Autowired
	private PatentMapper patentMapper;
	
	@Autowired
	private PatentService patentService;
	
	// 代理隧道验证信息
    final static String ProxyUser = "H677S6B336VV189D";
    final static String ProxyPass = "8E65F1A7219B95AB";

    // 代理服务器
    final static String ProxyHost = "http-dyn.abuyun.com";
    final static Integer ProxyPort = 9020;

    // 设置IP切换头
    final static String ProxyHeadKey = "Proxy-Switch-Ip";
    final static String ProxyHeadVal = "yes";
    Map<String, String> countryName = new HashMap<String, String>();
	public PatentController(){
		 
		 countryName.put("中国","China");
		 countryName.put("韩国","South Korea");
		 countryName.put("加拿大","Canada");
		 countryName.put("俄罗斯","Russia");
		 countryName.put("墨西哥","Mexico");
		 countryName.put("日本","Japan");
		 countryName.put("澳大利亚","Australia");
		 countryName.put("美国","United States of America");
		 countryName.put("马来西亚","Malaysia");
		 countryName.put("乌克兰","Ukraine");
		 countryName.put("菲律宾","Philippines");
		 countryName.put("南非","South Africa");
		 
		 countryName.put("新加坡","Singapore");
		 countryName.put("印度","India");
		 countryName.put("葡萄牙","Portugal");
		 countryName.put("保加利亚","Bulgaria");
		 countryName.put("新西兰","New Zealand");
		 countryName.put("希腊","Greece");
		 
		 countryName.put("瑞典","Sweden");
		 countryName.put("匈牙利","Hungary");
		 countryName.put("波兰","Poland");
		 countryName.put("挪威","Norway");
		 countryName.put("英国","United Kingdom");
		 
		 countryName.put("以色列","Israel");
		 countryName.put("卢森堡","Luxembourg");
		 countryName.put("荷兰","Netherlands");
		 countryName.put("莫尔多瓦","Moldova");
		 countryName.put("土耳其","Turkey");
		 
		 countryName.put("德国","Germany");
		 countryName.put("芬兰","Finland");
		 countryName.put("克罗地亚","Croatia");
		 countryName.put("拉脱维亚","Latvia");
		 countryName.put("斯洛文尼亚","Slovenia");
		 countryName.put("立陶宛","Lithuania");
		 
		 countryName.put("格鲁吉亚","Georgia");
		 countryName.put("突尼斯","Tunisia");
		 countryName.put("约旦","Jordan");
		 countryName.put("法国","France");
		 
		 countryName.put("丹麦","Denmark");
		 countryName.put("爱尔兰","Ireland");
		 countryName.put("埃及","Egypt");
		 countryName.put("沙特阿拉伯","Saudi Arabia");
		 
		 
		 
		 
		 
	}
   
	
	@GetMapping(value = "patent/get")
	public String getPatent(@RequestParam(required=false,value="id") String id, Model model) {
		Patent patent = new Patent();
		if(id != null) {
			patent = patentRepository.findById(id).get();
		}
		model.addAttribute("patent", patent);
		return "result-zlCon";
	}
	
//	@RequestMapping(value = "patent/list")
//	public String patents(@RequestParam(required=false,value="q") String q,
//			@RequestParam(required=false,value="year") String year,
//			@RequestParam(required=false,value="ipc") String ipc,
//			@RequestParam(required=false,value="cpc") String cpc,
//			@RequestParam(required=false,value="person") String person,
//			@RequestParam(required=false,value="creator") String creator,
//			@RequestParam(required=false,value="lawstatus") String lawstatus,
//			@RequestParam(required=false,value="country") String country,
//			@RequestParam(required=false,value="pageSize") Integer pageSize, 
//			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
//			Model model) {
//		if(pageSize == null) {
//			pageSize = 10;
//		}
//		if(pageIndex == null) {
//			pageIndex = 0;
//		}
//		long totalCount = 0L;
//		long totalPages = 0L;
//		List<Patent> patentList = new ArrayList<Patent>();
//		String view = "result-zl";
//		if(esTemplate.indexExists(Patent.class)) {
//			if(q == null) {
//				totalCount = patentRepository.count();
//				if(totalCount >0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Patent> patentsPage = patentRepository.search(searchQuery);
//					patentList = patentsPage.getContent();
//				}
//			}else {
//				// 分页参数
//				Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
//						.should(QueryBuilders.matchPhraseQuery("title", q))
//						.should(QueryBuilders.matchPhraseQuery("subject", q));
//				if(year != null) {
//					String[] years = year.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("year", years));
//				}
//				if(ipc != null) {
//					String[] ipcs = ipc.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("ipc", ipcs));
//				}
//				if(cpc != null) {
//					String[] cpcs = cpc.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("cpc", cpcs));
//				}
//				if(person != null) {
//					String[] persons = person.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("person", persons));
//				}
//				if(lawstatus != null) {
//					String[] lawstatuses = lawstatus.split("%");
//					queryBuilder.filter(QueryBuilders.termsQuery("lawstatus", lawstatuses));
//				}
//				if(creator != null) {
//					String[] creators = creator.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("creator", creators));
//				}
//				if(country != null) {
//					String[] countries = country.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("country", countries));
//				}
//				
//				// 分数，并自动按分排序
//				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//
//				// 分数、分页
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//						.withQuery(functionScoreQueryBuilder).build();
//
//				Page<Patent> searchPageResults = patentRepository.search(searchQuery);
//				patentList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Patent.class);
//				
//				
//				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery()
//						.should(QueryBuilders.matchQuery("title", q))
//						.should(QueryBuilders.matchQuery("subject", q));
//				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
//				List<String> pList=new ArrayList<>();
//				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
//						.withQuery(functionScoreQueryBuilderAgg)
//						.withSearchType(SearchType.QUERY_THEN_FETCH)
//						.withIndices("patent").withTypes("pt")
//						.addAggregation(AggregationBuilders.terms("agyear").field("publicyear").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agcpc").field("cpc").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agperson").field("person").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agcreator").field("creator").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agcountry").field("country").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("aglawstatus").field("lawstatus").order(Terms.Order.count(false)).size(10))
//						.build();
//				Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
//			        @Override
//			        public Aggregations extract(SearchResponse response) {
//			            return response.getAggregations();
//			        }
//			    });
//				
//				if(aggregations != null) {
//					StringTerms yearTerms = (StringTerms) aggregations.asMap().get("agyear");
//					Iterator<Bucket> yearbit = yearTerms.getBuckets().iterator();
//					Map<String, Long> yearMap = new HashMap<String, Long>();
//					while(yearbit.hasNext()) {
//						Bucket yearBucket = yearbit.next();
//						yearMap.put(yearBucket.getKey().toString(), Long.valueOf(yearBucket.getDocCount()));
//					}
//					model.addAttribute("agyear", yearMap);
//					
//					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
//					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
//					Map<String, Long> ipcMap = new HashMap<String, Long>();
//					while(ipcbit.hasNext()) {
//						Bucket ipcBucket = ipcbit.next();
//						ipcMap.put(ipcBucket.getKey().toString(), Long.valueOf(ipcBucket.getDocCount()));
//					}
//					model.addAttribute("agipc", ipcMap);
//					
//					StringTerms lawstatusTerms = (StringTerms) aggregations.asMap().get("aglawstatus");
//					Iterator<Bucket> lawstatusbit = lawstatusTerms.getBuckets().iterator();
//					Map<String, Long> lawstatusMap = new HashMap<String, Long>();
//					while(lawstatusbit.hasNext()) {
//						Bucket lawstatusBucket = lawstatusbit.next();
//						lawstatusMap.put(lawstatusBucket.getKey().toString(), Long.valueOf(lawstatusBucket.getDocCount()));
//					}
//					model.addAttribute("aglawstatus", lawstatusMap);
//					
//					StringTerms cpcTerms = (StringTerms) aggregations.asMap().get("agcpc");
//					Iterator<Bucket> cpcbit = cpcTerms.getBuckets().iterator();
//					Map<String, Long> cpcMap = new HashMap<String, Long>();
//					while(cpcbit.hasNext()) {
//						Bucket cpcBucket = cpcbit.next();
//						cpcMap.put(cpcBucket.getKey().toString(), Long.valueOf(cpcBucket.getDocCount()));
//					}
//					
//					model.addAttribute("agcpc", cpcMap);
//					
//					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
//					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
//					Map<String, Long> personMap = new HashMap<String, Long>();
//					while(personbit.hasNext()) {
//						Bucket personBucket = personbit.next();
//						personMap.put(personBucket.getKey().toString(), Long.valueOf(personBucket.getDocCount()));
//					}
//					model.addAttribute("agperson", personMap);
//					
//					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
//					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
//					Map<String, Long> creatorMap = new HashMap<String, Long>();
//					while(creatorbit.hasNext()) {
//						Bucket creatorBucket = creatorbit.next();
//						creatorMap.put(creatorBucket.getKey().toString(), Long.valueOf(creatorBucket.getDocCount()));
//					}
//					model.addAttribute("agcreator", creatorMap);
//					
//					StringTerms countryTerms = (StringTerms) aggregations.asMap().get("agcountry");
//					Iterator<Bucket> countrybit = countryTerms.getBuckets().iterator();
//					Map<String, Long> countryMap = new HashMap<String, Long>();
//					while(countrybit.hasNext()) {
//						Bucket countryBucket = countrybit.next();
//						countryMap.put(countryBucket.getKey().toString(), Long.valueOf(countryBucket.getDocCount()));
//					}
//					model.addAttribute("agcountry", countryMap);
//					
//				}
////				nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
////				nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);
////				TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("aglist").field("list").order(Terms.Order.count(false)).size(10);
////				nativeSearchQueryBuilder.addAggregation(termsAggregation);
////		    	NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
////		    	Page<Project> search = projectRepository.search(nativeSearchQuery);
////		    	List<Project> content = search.getContent();
////		    	for (Project project : content) {
////		    		pList.add(esBlog.getUsername());
////				}
//				//totalPages = Math.round(totalCount/pageSize);
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
//				
//				
//			}
//		}
//		model.addAttribute("patentList", patentList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
//			
//		return view;
//	}
	
	@GetMapping(value = "patent/transfer")
	@ResponseBody
	public R transferPaper() {
		int currentPage = 0;
		int pageSize = 0;
		int index = 0;
		for(int i=544;i<957;i++){
			
			currentPage = (currentPage <= 0) ?1:currentPage;
			pageSize = (pageSize<=0) ? 100:pageSize;
			index = (currentPage - 1) * pageSize;
			System.out.println(i);
			List<PatentMysql> patents = patentMapper.getPatents(index, pageSize);
			currentPage++;
			List<Patent> tobesaved = new LinkedList<Patent>();
			for(PatentMysql patentMysql: patents) {
				Patent patentES = new Patent();
				
				patentES.setId(patentMysql.getId()+"");
				
				if (patentMysql.getTitle() != null) {
					patentES.setTitle(patentMysql.getTitle().trim());
				}
				
				String subject = patentMysql.getSubject();
				if (subject != null) {
					patentES.setSubject(subject);
				}
				
				String person = patentMysql.getPerson();
				if (person != null && !person.trim().equals("")) {
					person = person.trim();
					
					List<String> personlist = new ArrayList<String>();
					if (person.contains(";")) {
						String[] persons = person.split(";");
						for(String s:persons){
							personlist.add(s);
						}
					}else{
						personlist.add(person);
					}
					patentES.setPerson(personlist);
				}
				
				String creator = patentMysql.getCreator();
				if (creator != null && !creator.trim().equals("")) {
					creator = creator.trim();
					
					List<String> creatorlist = new ArrayList<String>();
					if (creator.contains(";")) {
						String[] creators = creator.split(";");
						for(String s:creators){
							creatorlist.add(s);
						}
					}else{
						creatorlist.add(creator);
					}
					patentES.setCreator(creatorlist);
				}
				
				if (patentMysql.getApplytime() != null) {
					patentES.setApplytime(patentMysql.getApplytime().trim());
				}
				
				if (patentMysql.getPublictime() != null) {
					patentES.setPublictime(patentMysql.getPublictime().trim());
				}
				
				if (patentMysql.getApplyyear() != null) {
					patentES.setApplyyear(patentMysql.getApplyyear().trim());
				}
				
				if (patentMysql.getPublicyear() != null) {
					patentES.setPublicyear(patentMysql.getPublicyear().trim());
				}
				
				if (patentMysql.getPtype() != null) {
					patentES.setType(patentMysql.getPtype().trim());
				}
				
				if (patentMysql.getDescription() != null) {
					patentES.setDescription(patentMysql.getDescription().trim());
				}
				
				if (patentMysql.getClaim() != null) {
					patentES.setClaim(patentMysql.getClaim().trim());
				}
				
				if (patentMysql.getPublicnumber() != null) {
					patentES.setPublicnumber(patentMysql.getPublicnumber().trim());
				}
				
				if (patentMysql.getApplynumber() != null) {
					patentES.setApplynumber(patentMysql.getApplynumber().trim());
				}
				
				String ipc = patentMysql.getIpc();
				if (ipc != null && !ipc.trim().equals("")) {
					ipc = ipc.trim();
					
					List<String> ipclist = new ArrayList<String>();
					if (ipc.contains(";")) {
						String[] ipcs = ipc.split(";");
						for(String s:ipcs){
							ipclist.add(s);
						}
					}else{
						ipclist.add(ipc);
					}
					patentES.setIpc(ipclist);
				}
				
				if (patentMysql.getPiroryear() != null) {
					patentES.setPiroryear(patentMysql.getPiroryear().trim());
				}
				
				if (patentMysql.getCountry() != null) {
					patentES.setCountry(patentMysql.getCountry().trim());
				}
				
				if (patentMysql.getLawstatus() != null) {
					patentES.setLawstatus(patentMysql.getLawstatus().trim());
				}
				
				if (patentMysql.getNow() != null) {
					patentES.setNow(patentMysql.getNow());
				}
				tobesaved.add(patentES);
//				paperRepository.save(paperES);
			}
			patentRepository.saveAll(tobesaved);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return R.ok();
	}
	
	@RequestMapping(value = "patent/list")
	public String patents(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="person") String person,
			@RequestParam(required=false,value="creator") String creator,
			@RequestParam(required=false,value="publicyear") String publicyear,
			@RequestParam(required=false,value="ipc") String ipc,
			@RequestParam(required=false,value="country") String country,
			@RequestParam(required=false,value="lawstatus") String lawstatus,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("pageSize", pageSize);
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
//		patentService.execute(pageIndex, pageSize, i,q,person,creator,publicyear,ipc,country,lawstatus);
		JSONObject jObject = patentService.execute(pageIndex, pageSize, i,q,person,creator,null,publicyear,ipc,country,lawstatus);
		if (jObject != null) {
			CachePool cache = CachePool.getInstance();
			if (q == null || "".equals(q)) {
				cache.putCacheItem("total", jObject);
			}else{
				cache.putCacheItem(q, jObject);
			}
		}
		ThreadLocalUtil.remove();
		String view = "result-zl";
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/pageList", method = RequestMethod.POST,consumes = "application/json")
	public R patentPageList(@RequestBody PatentPageListRequest request) {
		Integer pageSize = request.getPageSize();
		Integer pageIndex = request.getPageIndex();
		String year = request.getYear();
		String ipc = request.getIpc();
		String cpc = request.getCpc();
		String person = request.getPerson();
		String creator = request.getCreator();
		String country= request.getCountry();
		String q = request.getQ();
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		long totalCount = 0L;
		long totalPages = 0L;
		List<Patent> patentList = new ArrayList<Patent>();
		if(esTemplate.indexExists(Patent.class)) {
			if(q == null) {
				totalCount = patentRepository.count();
				if(totalCount >0) {
					Sort sort = new Sort(Direction.DESC, "now");
					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
					SearchQuery searchQuery = new NativeSearchQueryBuilder()
							.withPageable(pageable).build();
					Page<Patent> patentsPage = patentRepository.search(searchQuery);
					patentList = patentsPage.getContent();
					if(totalCount % pageSize == 0){
						totalPages = totalCount/pageSize;
					}else{
						totalPages = totalCount/pageSize + 1;
					}
				}
			}else {
				// 分页参数
				Pageable pageable = new PageRequest(pageIndex, pageSize);

				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
						.should(QueryBuilders.matchPhraseQuery("title", q))
						.should(QueryBuilders.matchPhraseQuery("subject", q));
				if(year != null) {
					String[] years = year.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("year", years));
				}
				if(ipc != null) {
					String[] ipcs = ipc.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("ipc", ipcs));
				}
				if(cpc != null) {
					String[] cpcs = cpc.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("cpc", cpcs));
				}
				if(person != null) {
					String[] persons = person.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("person", persons));
				}
				if(creator != null) {
					String[] creators = creator.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("creator", creators));
				}
				if(country != null) {
					String[] countries = country.split("-");
					queryBuilder.filter(QueryBuilders.termsQuery("country", countries));
				}
				
				// 分数，并自动按分排序
				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));

				// 分数、分页
				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
						.withQuery(functionScoreQueryBuilder).build();

				Page<Patent> searchPageResults = patentRepository.search(searchQuery);
				patentList = searchPageResults.getContent();
				totalCount = esTemplate.count(searchQuery, Patent.class);
				if(totalCount % pageSize == 0){
					totalPages = totalCount/pageSize;
				}else{
					totalPages = totalCount/pageSize + 1;
				}
				
				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery()
						.should(QueryBuilders.matchQuery("title", q))
						.should(QueryBuilders.matchQuery("subject", q));
				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
				List<String> pList=new ArrayList<>();
				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
						.withQuery(functionScoreQueryBuilderAgg)
						.withSearchType(SearchType.QUERY_THEN_FETCH)
						.withIndices("patent").withTypes("pt")
						.addAggregation(AggregationBuilders.terms("agyear").field("publicyear").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agcpc").field("cpc").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agperson").field("person").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agcreator").field("creator").order(Terms.Order.count(false)).size(10))
						.addAggregation(AggregationBuilders.terms("agcountry").field("country").order(Terms.Order.count(false)).size(10))
						.build();
				Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
			        @Override
			        public Aggregations extract(SearchResponse response) {
			            return response.getAggregations();
			        }
			    });
				
				if(aggregations != null) {
					StringTerms yearTerms = (StringTerms) aggregations.asMap().get("agyear");
					Iterator<Bucket> yearbit = yearTerms.getBuckets().iterator();
					Map<String, Long> yearMap = new HashMap<String, Long>();
					while(yearbit.hasNext()) {
						Bucket yearBucket = yearbit.next();
						yearMap.put(yearBucket.getKey().toString(), Long.valueOf(yearBucket.getDocCount()));
					}
//					model.addAttribute("agyear", yearMap);
					
					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
					Map<String, Long> ipcMap = new HashMap<String, Long>();
					while(ipcbit.hasNext()) {
						Bucket ipcBucket = ipcbit.next();
						ipcMap.put(ipcBucket.getKey().toString(), Long.valueOf(ipcBucket.getDocCount()));
					}
//					model.addAttribute("agipc", ipcMap);
					
					StringTerms cpcTerms = (StringTerms) aggregations.asMap().get("agcpc");
					Iterator<Bucket> cpcbit = cpcTerms.getBuckets().iterator();
					Map<String, Long> cpcMap = new HashMap<String, Long>();
					while(cpcbit.hasNext()) {
						Bucket cpcBucket = cpcbit.next();
						cpcMap.put(cpcBucket.getKey().toString(), Long.valueOf(cpcBucket.getDocCount()));
					}
					
//					model.addAttribute("agcpc", cpcMap);
					
					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
					Map<String, Long> personMap = new HashMap<String, Long>();
					while(personbit.hasNext()) {
						Bucket personBucket = personbit.next();
						personMap.put(personBucket.getKey().toString(), Long.valueOf(personBucket.getDocCount()));
					}
//					model.addAttribute("agperson", personMap);
					
					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
					Map<String, Long> creatorMap = new HashMap<String, Long>();
					while(creatorbit.hasNext()) {
						Bucket creatorBucket = creatorbit.next();
						creatorMap.put(creatorBucket.getKey().toString(), Long.valueOf(creatorBucket.getDocCount()));
					}
//					model.addAttribute("agcreator", creatorMap);
					
					StringTerms countryTerms = (StringTerms) aggregations.asMap().get("agcountry");
					Iterator<Bucket> countrybit = countryTerms.getBuckets().iterator();
					Map<String, Long> countryMap = new HashMap<String, Long>();
					while(countrybit.hasNext()) {
						Bucket countryBucket = countrybit.next();
						countryMap.put(countryBucket.getKey().toString(), Long.valueOf(countryBucket.getDocCount()));
					}
//					model.addAttribute("agcountry", countryMap);
					
				}
//				nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
//				nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);
//				TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("aglist").field("list").order(Terms.Order.count(false)).size(10);
//				nativeSearchQueryBuilder.addAggregation(termsAggregation);
//		    	NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
//		    	Page<Project> search = projectRepository.search(nativeSearchQuery);
//		    	List<Project> content = search.getContent();
//		    	for (Project project : content) {
//		    		pList.add(esBlog.getUsername());
//				}
				totalPages = Math.round(totalCount/pageSize);
				
				
			}
		}
//		model.addAttribute("patentList", patentList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
			
		return R.ok().put("patentList", patentList).put("pageIndex", pageIndex);
	}
	
	
	
	@GetMapping(value = "patent/analysis")
	public String analysis() {
		return "zhuanlifenxifamingrenjizhuanliquanren";
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/agpersons", method = RequestMethod.POST,consumes = "application/json")
	public R persons(@RequestBody AgPersonRequest request) {
		List<List<Object>> personList = new ArrayList<List<Object>>();
//		String view = "zhuanlifenxifamingrenjizhuanliquanren";
		String person = request.getPerson();
		String creator = request.getCreator();
		if(esTemplate.indexExists(Patent.class)) {
				
			MatchAllQueryBuilder queryBuilderAgg = QueryBuilders.matchAllQuery();
			FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
					.withQuery(functionScoreQueryBuilderAgg)
					.withSearchType(SearchType.QUERY_THEN_FETCH)
					.withIndices("patent").withTypes("pt");
			if(person != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agperson").field("person").order(Terms.Order.count(true)).size(10));
			}
			if(creator != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agcreator").field("creator").order(Terms.Order.count(true)).size(10));
			}
			Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder.build(), new ResultsExtractor<Aggregations>() {
				@Override
				public Aggregations extract(SearchResponse response) {
					return response.getAggregations();
				}
			});
			
			if(aggregations != null) {
				if(person != null) {
					StringTerms personTerms = (StringTerms) aggregations.asMap().get("agperson");
					Iterator<Bucket> personbit = personTerms.getBuckets().iterator();
					List<Object> titleList = new ArrayList<Object>();
					titleList.add("score");
					titleList.add("amount");
					titleList.add("product");
					personList.add(titleList);
					while(personbit.hasNext()) {
						List<Object> list = new ArrayList<Object>();
						Bucket personBucket = personbit.next();
						list.add((int)(Math.random()*90)+10);
						list.add(personBucket.getDocCount());
						list.add(personBucket.getKey().toString());
						personList.add(list);
					}
				}
				
				if(creator != null) {
					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
					while(creatorbit.hasNext()) {
						List<Object> list = new ArrayList<Object>();
						Bucket creatorBucket = creatorbit.next();
						list.add((int)(Math.random()*90)+10);
						list.add(creatorBucket.getDocCount());
						list.add(creatorBucket.getKey().toString());
						personList.add(list);
					}
				}
			}
		}
		
		return R.ok().put("agpersons", personList);
	}
	
	
	@GetMapping(value = "patent/agtype")
	public String agtype(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="totalcount") String totalCount,
			Model model) {
		CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
	    Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
        String[] str=new String[5];
        int[] famingnumtotal={0,0,0,0,0};
	    int[] famingnum={0,0,0,0,0};
	    int[] famingshouquannum={0,0,0,0,0};
	    int[] shiyongnum={0,0,0,0,0};
	    int[] waiguannum={0,0,0,0,0};
	    int lastfive = year-4;
	    for(int i=0;i<5;i++){
	    	str[i] = lastfive+"";
	    	lastfive++;
	    }
	    
	    JSONObject agg = (JSONObject) obj.get("typeyear");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    
	    for(Object jsonObject : ar){
	    	for(int j = 0;j<str.length;j++){
	    		if(((JSONObject)jsonObject).get("key").toString().contains(str[j])){
	    			if(((JSONObject)jsonObject).get("key").toString().contains("发明_")){
	    				famingnum[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
	    			if(((JSONObject)jsonObject).get("key").toString().contains("发明授权_")){
	    				famingshouquannum[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
	    			if(((JSONObject)jsonObject).get("key").toString().contains("实用新型_")){
	    				shiyongnum[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
	    			if(((JSONObject)jsonObject).get("key").toString().contains("外观_")){
	    				waiguannum[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
		    	}
	    	}
	    	
	    }
	    for(int i=0; i<5; i++){
	    	famingnumtotal[i] = famingnum[i] + famingshouquannum[i];
		}
		//model.addAttribute(key, agg.get("buckets"));
	    model.addAttribute("famingnumtotal", famingnumtotal);
	    model.addAttribute("waiguannum", waiguannum);
	    model.addAttribute("shiyongnum", shiyongnum);
	    model.addAttribute("yearstr", str);
	    if(totalCount == null || totalCount.equals("")){
	    	totalCount = 129684+"";
	    }
	    model.addAttribute("totalCount", totalCount); 
	    model.addAttribute("query", q);
		return "zhuanlifenxizhuanlileixing";
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/patentTypeMonList", method = RequestMethod.POST,consumes = "application/json")
	public R patenttypemonthList(@RequestBody JSONObject queryobj) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	String query= (String)queryobj.get("querystring");
    	Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
    	
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		
		JSONObject num = patentService.executeTypeMonth(0, 10, i,query,null,null,""+year,null,null,null,null);
		return R.ok().put("famingnumtotal", num.get("famingnumtotal")).put("waiguannum", num.get("waiguannum")).put("shiyongnum", num.get("shiyongnum")).put("query", query);
    }
	
	/*@GetMapping(value = "patent/agmount")
	public String agmount(Model model) {
		String time = priceMapper.getLatestUpdateTime();
		if(time != null) {
			List<Price> prices = priceMapper.getPricesByUpdateTime(time);
			model.addAttribute("prices", prices);
		}else {
			model.addAttribute("prices", new ArrayList<String>());
		}
		List<String> items = priceMapper.getPricesGroupByName();
		model.addAttribute("items", items);
		return "zhuanlifenxizhuanlishenqingliang";
	}*/
	
	@GetMapping(value = "patent/agmount")
	public String agmount(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="totalcount") String totalCount,
			Model model) {
		CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
	    	 if (obj == null) {
	    		 
	    		ThreadLocalUtil.set(model);
    			JSONObject jObject = patentService.execute(0, 10, 0,q,null,null,null,null,null,null,null);
    			if (jObject != null) {
    				
    				cache.putCacheItem("total", jObject);
    				obj = (JSONObject) cache.getCacheItem("total");
    			}
    			ThreadLocalUtil.remove();
			}
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
	    System.out.println(JsonUtil.toJSONString(obj));
	    Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
        String[] str=new String[5];
	    int[] num={0,0,0,0,0};
	    int lastfive = year-4;
	    for(int i=0;i<5;i++){
	    	str[i] = lastfive+"";
	    	lastfive++;
	    }
	    
	    JSONObject agg = (JSONObject) obj.get("applyyear");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    
	    for(Object jsonObject : ar){
	    	for(int j = 0;j<str.length;j++){
	    		if(((JSONObject)jsonObject).get("key").equals(str[j])){
		    		num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	}
	    	}
	    	
	    }
		//model.addAttribute(key, agg.get("buckets"));
	    model.addAttribute("num", num);
	    model.addAttribute("yearstr", str);
	    if(totalCount == null || totalCount.equals("")){
	    	totalCount = 129684+"";
	    }
	    model.addAttribute("totalCount", totalCount); 
	    model.addAttribute("query", q);
		return "zhuanlifenxizhuanlishenqingliang";
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/patentMonList", method = RequestMethod.POST,consumes = "application/json")
	public R patentmonthList(@RequestBody JSONObject queryobj) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	String query= (String)queryobj.get("querystring");
    	Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
    	
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		
		int[] num = patentService.executeMonth(0, 10, i,query,null,null,""+year,null,null,null,null);
		return R.ok().put("num", num).put("query", query);
    }
	
	@ResponseBody
	@RequestMapping(value = "patent/patentLastMonList", method = RequestMethod.POST,consumes = "application/json")
	public R patentlastmonthList(@RequestBody JSONObject queryobj) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	String query= (String)queryobj.get("querystring");
    	Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
        int month = cale.get(Calendar.MONTH)+1;  
        String[] hengzhoushuju = {month-2+"月",month-1+"月",month+"月"};
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		
		int[] num = patentService.executeMonth(0, 10, i,query,null,null,""+year,null,null,null,null);
		int[] b = new int[3];
		System.arraycopy(num, month-3, b, 0, 3);
		return R.ok().put("b", b).put("query", query).put("hengzhoushuju", hengzhoushuju);
    }
	
//	@GetMapping(value = "patent/agcountry")
//	public String agcountry() {
//		return "zhuanlifenxizhuanlishenqingguo";
//	}
	
	@GetMapping(value = "patent/agcountry")
	public String agcountry(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="totalcount") String totalCount,
			Model model) {
		CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
	   
	    int[] num={0,0,0,0,0};
	    
	    JSONObject agg = (JSONObject) obj.get("country");
	    JSONObject[] strings = null;
	    
	    if (agg != null) {
	    	 JSONArray ar = (JSONArray) agg.get("buckets");
	 	    int j=0;
	 	    
	 	    List<JSONObject> joList = new ArrayList<JSONObject>();
	 	    
	 	    for(Object jsonObject : ar){
	 	    	//countryName
//	 	    	if(((JSONObject)jsonObject).get("key").equals("中国")){
//	 		    	num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
//	 		    }
//	 	    	j++;
	 	    	String countrycn = ((JSONObject)jsonObject).get("key").toString();
	 	    	if(countryName.containsKey(countrycn)){
	 	    		String countryen = countryName.get(countrycn);
	 	    		int nums= Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	 	    		JSONObject jo = new JSONObject();
	 	    	    jo.put("name", countryen);
	 	    	    jo.put("value", nums);
	 	    	    joList.add(jo);
	 	    	}
	 	    	
	 	    	
	 	    }
	 	    
	 	   strings = new JSONObject[joList.size()];

	 	   joList.toArray(strings);
	 	    
		}
	   
		//model.addAttribute(key, agg.get("buckets"));
	    model.addAttribute("num", num);
	    model.addAttribute("strings", strings);
	   // model.addAttribute("yearstr", str);
	    model.addAttribute("query", q);
	    model.addAttribute("totalCount", totalCount); 
		return "zhuanlifenxizhuanlishenqingguo";
	}
	
//	@GetMapping(value = "patent/agpeople")
//	public String agpeople() {
//		return "zhuanlifenxifamingrenjizhuanliquanren";
//	}
	@GetMapping(value = "patent/agpeoplecon")
	public String agpeoplecon() {
		return "T-hangyeCon";
	}
	
//	@GetMapping(value = "patent/agclassis")
//	public String agclassis() {
//		return "zhuanlifenxijishufenlei";
//	}
	
	@ResponseBody
	@RequestMapping(value = "patent/download", method = RequestMethod.POST,consumes = "application/json")
	public R xiangguanpaperList(@RequestBody JSONObject info) {
		String barBase64Info = (String) info.get("barBase64Info");
		DocUtil docUtil = new DocUtil();
	    //引入处理图片的工具类，包含将base64编码解析为图片并保存本地，获取图片本地路径
	    ImageUtil imageUtil = new ImageUtil();
	    //建立map存储所要导出到word的各种数据和图像，不能使用自己项目封装的类型，例如PageData
	    Map<String, Object> dataMap = new HashMap<String, Object>(); 
	    
	  //这一步，进行图片的处理，获取前台传过来的图片base64编码，在利用工具类解析图片保存到本地，然后利用工具类获取图片本地地址
	   
	    String path = "C:";
	    
	    //String image1  = imageUtil.getImageStr(image1);
	    
	    
		try {
			String image1 = ImageUtil.savePictoServer(barBase64Info, path);
			image1  = imageUtil.getImageStr(image1);
			
			dataMap.put("image1", image1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	    File file = null;
	    InputStream fin = null;
	    OutputStream out = null;
	    String filename = "文件名.doc";
	        //dataMap是上面处理完的数据，MODELPATH是模板文件的存储路径，"模板.xml"是相应的模板文件
	    file = docUtil.createWordFile(dataMap, "model.xml");
	    System.out.print(file.getAbsolutePath());
		return R.ok().put("filename", file.getName());
	}
	
	  @SuppressWarnings("unused")
	    @RequestMapping(value="pdf/fileDownload")
	    public void yzmDownload(@RequestParam("filename") String filename,HttpServletRequest request, HttpServletResponse response){
	    	
	    	
	    	File file=new File("C:\\Users\\abc\\git\\jilin" + File.separator + filename);
			String fileName=file.getName();
			String ext=fileName.substring(fileName.lastIndexOf(".")+1);
			String agent=(String)request.getHeader("USER-AGENT"); //判断浏览器类型
			try {  
	        	if(agent!=null && agent.indexOf("Fireforx")!=-1) {
	        		fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");   //UTF-8编码，防止输出文件名乱码
	        	}
	        	else {
	        		fileName=URLEncoder.encode(fileName,"UTF-8");
	        	}
			} catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
			}
			
			BufferedInputStream bis=null;
	        OutputStream os=null;
		response.reset();
	    	response.setCharacterEncoding("utf-8"); 
	    	if(ext=="docx") {
	    		response.setContentType("application/msword"); // word格式
	    	}else if(ext=="pdf") {
	    		response.setContentType("application/pdf"); // word格式
	    	}  
	        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	        
	        try {
		        bis=new BufferedInputStream(new FileInputStream(file));
			byte[] b=new byte[bis.available()+1000];
			int i=0;
	        	os = response.getOutputStream();   //直接下载导出
	        	while((i=bis.read(b))!=-1) {
	        		os.write(b, 0, i);
	        	}
	            os.flush();
	            os.close();
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }finally {
	        	if(os!=null) {
	        		try {
				     os.close();
				} catch (IOException e) {
				     e.printStackTrace();
					}
	        	}
	        }
	    }
	
	@GetMapping(value = "patent/agpeople")
	public String agpeople(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="totalcount") String totalCount,
			Model model) {
		
//		int pageSize = 10;
//		int pageIndex = 0;
//		
//		//model.addAttribute("pageIndex", pageIndex);
//		//model.addAttribute("pageSize", pageSize);
//		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测
//		// TODO 静态变量未环绕需调整
//		ThreadLocalUtil.set(model);
//		patentService.executefamingren(pageIndex, pageSize, i,null,"person","creator");
//		ThreadLocalUtil.remove();
		CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
        String[] str=new String[10];
	    int[] num=new int[10];
	    
	    
	    JSONObject agg = (JSONObject) obj.get("creator");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    int j = 0;
	    for(Object jsonObject : ar){
	    	
	    		str[j] = ((JSONObject)jsonObject).get("key").toString();
		    	num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	j++;
		    	if(j>=10){
		    		break;
		    	}
	    	
	    }
	    model.addAttribute("num", num);
	    model.addAttribute("famingren", str);
	    
	    String[] zhuanliquanrenstr=new String[10];
	    int[] zhuanliquanrennum=new int[10];
	    
	    
	    JSONObject zhuanliquanrenagg = (JSONObject) obj.get("person");
	    JSONArray zhuanliquanrenar = (JSONArray) zhuanliquanrenagg.get("buckets");
	    int jj = 0;
	    for(Object jsonObject : zhuanliquanrenar){
	    	
	    	zhuanliquanrenstr[jj] = ((JSONObject)jsonObject).get("key").toString();
	    	zhuanliquanrennum[jj] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	jj++;
		    	if(jj>=10){
		    		break;
		    	}
	    	
	    }
	    model.addAttribute("zhuanliquanrennum", zhuanliquanrennum);
	    model.addAttribute("zhuanliquanrenstr", zhuanliquanrenstr);
	    
	    if(totalCount == null || totalCount.equals("")){
	    	totalCount = 129684+"";
	    }
	    model.addAttribute("totalCount", totalCount); 
	    model.addAttribute("query", q);
		return "zhuanlifenxifamingrenjizhuanliquanren";
	}
	
	@GetMapping(value = "patent/agclassis")
	public String agclassis(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="totalcount") String totalCount,
			Model model) {
		CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
        String[] str=new String[10];
	    int[] num=new int[10];
	    
	    
	    JSONObject agg = (JSONObject) obj.get("ipc");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    int j = 0;
	    for(Object jsonObject : ar){
	    	
	    		str[j] = ((JSONObject)jsonObject).get("key").toString();
		    	num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	j++;
		    	if(j>=10){
		    		break;
		    	}
	    	
	    }
	    model.addAttribute("num", num);
	    model.addAttribute("famingren", str);
	    model.addAttribute("query", q);
	    if(totalCount == null || totalCount.equals("")){
	    	totalCount = 129684+"";
	    }
	    model.addAttribute("totalCount", totalCount); 
	    model.addAttribute("query", q);
		return "zhuanlifenxijishufenlei";
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/patentTypeLastMonList", method = RequestMethod.POST,consumes = "application/json")
	public R patenttypelastmonthList(@RequestBody JSONObject queryobj) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	String query= (String)queryobj.get("querystring");
    	Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
        int month = cale.get(Calendar.MONTH)+1;  
        String[] hengzhoushuju = {month-2+"月",month-1+"月",month+"月"};
		int i = 0;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		
		JSONObject num = patentService.executeTypeMonth(0, 10, i,query,null,null,""+year,null,null,null,null);
		int[] b1 = new int[3];
		int[] b2 = new int[3];
		int[] b3 = new int[3];
		System.arraycopy(num.get("famingnumtotal"), month-3, b1, 0, 3);
		System.arraycopy(num.get("waiguannum"), month-3, b2, 0, 3);
		System.arraycopy(num.get("shiyongnum"), month-3, b3, 0, 3);
		
		return R.ok().put("famingnumtotal", b1).put("waiguannum",b2).put("shiyongnum", b3).put("query", query).put("hengzhoushuju", hengzhoushuju);
		
    }
	
	@ResponseBody
	@RequestMapping(value = "patent/patentJishuqushi", method = RequestMethod.POST,consumes = "application/json")
	public R patentjishuqushi(@RequestBody JSONObject queryobj) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	String q= (String)queryobj.get("querystring");
    	
    	CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
	    // TODO  ipc
        String[] str=new String[7];
	    int[] num=new int[7];
	    String[] yearstr=new String[24];
	    Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
        
	   // TODO year
	    int lastfive = year-23;
	    for(int i=0;i<24;i++){
	    	yearstr[i] = lastfive+"";
	    	lastfive++;
	    }
	    JSONObject agg = (JSONObject) obj.get("ipc");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    int j = 0;
	    for(Object jsonObject : ar){
	    	
	    		str[j] = ((JSONObject)jsonObject).get("key").toString();
		    	num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	j++;
		    	if(j>=7){
		    		break;
		    	}
	    	
	    }
	    Map qushinum = patentService.executeQushi(0, 10, 0,q,str); //key  ip_year
	    int[][] arr = new int[168][3];
		int i=0;
		for(int m=0;m<str.length;m++) {
			for(int k=0;k<yearstr.length;k++) {
				int[] tmp = new int[3];
				tmp[0] = m;
				tmp[1] = k;
				if(qushinum.containsKey(str[m] + "_" + yearstr[k])) {
					tmp[2] = (int) qushinum.get(str[m] + "_" + yearstr[k]);
				}else {
					tmp[2] = 0;
				}
				arr[i] = tmp;
				i++;
			}
		}
	    
//	    int[][] data ={{0,0,5},{0,1,1},{0,2,0},{0,3,0},{0,4,0},{0,5,0},{0,6,0},{0,7,0},{0,8,0},{0,9,0},{0,10,0},{0,11,2},{0,12,4},{0,13,1},{0,14,1},{0,15,3},{0,16,4},{0,17,6},{0,18,4},{0,19,4},{0,20,3},{0,21,3},{0,22,2},{0,23,5},{1,0,7},{1,1,0},{1,2,0},{1,3,0},{1,4,0},{1,5,0},{1,6,0},{1,7,0},{1,8,0},{1,9,0},{1,10,5},{1,11,2},{1,12,2},{1,13,6},{1,14,9},{1,15,11},{1,16,6},{1,17,7},{1,18,8},{1,19,12},{1,20,5},{1,21,5},{1,22,7},{1,23,2},{2,0,1},{2,1,1},{2,2,0},{2,3,0},{2,4,0},{2,5,0},{2,6,0},{2,7,0},{2,8,0},{2,9,0},{2,10,3},{2,11,2},{2,12,1},{2,13,9},{2,14,8},{2,15,10},{2,16,6},{2,17,5},{2,18,5},{2,19,5},{2,20,7},{2,21,4},{2,22,2},{2,23,4},{3,0,7},{3,1,3},{3,2,0},{3,3,0},{3,4,0},{3,5,0},{3,6,0},{3,7,0},{3,8,1},{3,9,0},{3,10,5},{3,11,4},{3,12,7},{3,13,14},{3,14,13},{3,15,12},{3,16,9},{3,17,5},{3,18,5},{3,19,10},{3,20,6},{3,21,4},{3,22,4},{3,23,1},{4,0,1},{4,1,3},{4,2,0},{4,3,0},{4,4,0},{4,5,1},{4,6,0},{4,7,0},{4,8,0},{4,9,2},{4,10,4},{4,11,4},{4,12,2},{4,13,4},{4,14,4},{4,15,14},{4,16,12},{4,17,1},{4,18,8},{4,19,5},{4,20,3},{4,21,7},{4,22,3},{4,23,0},{5,0,2},{5,1,1},{5,2,0},{5,3,3},{5,4,0},{5,5,0},{5,6,0},{5,7,0},{5,8,2},{5,9,0},{5,10,4},{5,11,1},{5,12,5},{5,13,10},{5,14,5},{5,15,7},{5,16,11},{5,17,6},{5,18,0},{5,19,5},{5,20,3},{5,21,4},{5,22,2},{5,23,0},{6,0,1},{6,1,0},{6,2,0},{6,3,0},{6,4,0},{6,5,0},{6,6,0},{6,7,0},{6,8,0},{6,9,0},{6,10,1},{6,11,0},{6,12,2},{6,13,1},{6,14,3},{6,15,4},{6,16,0},{6,17,0},{6,18,0},{6,19,0},{6,20,1},{6,21,2},{6,22,2},{6,23,6}}; 
	
		return R.ok().put("qushi", arr).put("ipc", str).put("year", yearstr);
		
    }
	
	@ResponseBody
	@RequestMapping(value = "patent/patentJishufenbu", method = RequestMethod.POST,consumes = "application/json")
	public R patentjishufenbu(@RequestBody JSONObject queryobj) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	String q= (String)queryobj.get("querystring");
    	
    	CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
	    // TODO  ipc
        String[] str=new String[7];
	    int[] num=new int[7];
	    String[] ipcstr=new String[18];
	   
	    JSONObject agg = (JSONObject) obj.get("person");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    int j = 0;
	    for(Object jsonObject : ar){
	    	
	    		str[j] = ((JSONObject)jsonObject).get("key").toString();
		    	num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	j++;
		    	if(j>=7){
		    		break;
		    	}
	    	
	    }
	    JSONObject aggipc = (JSONObject) obj.get("ipc");
	    JSONArray aripc = (JSONArray) aggipc.get("buckets");
	    int jj = 0;
	    for(Object jsonObject : aripc){
	    	
	    	ipcstr[jj] = ((JSONObject)jsonObject).get("key").toString();
		    
	    	jj++;
	    	if(jj>=18){
	    		break;
	    	}
	    	
	    }
	    Map qushinum = patentService.executeJishufenbu(0, 10, 0,q,str,ipcstr); //key  ip_year
	    int[][] arr = new int[126][3];
		int i=0;
		for(int m=0;m<str.length;m++) {
			for(int k=0;k<ipcstr.length;k++) {
				int[] tmp = new int[3];
				tmp[0] = m;
				tmp[1] = k;
				if(qushinum.containsKey(str[m] + "_" + ipcstr[k])) {
					tmp[2] = (int) qushinum.get(str[m] + "_" + ipcstr[k]);
				}else {
					tmp[2] = 0;
				}
				arr[i] = tmp;
				i++;
			}
		}
	    
	    //int[][] data ={{0,0,5},{0,1,1},{0,2,0},{0,3,0},{0,4,0},{0,5,0},{0,6,0},{0,7,0},{0,8,0},{0,9,0},{0,10,0},{0,11,2},{0,12,4},{0,13,1},{0,14,1},{0,15,3},{0,16,4},{0,17,6},{0,18,4},{0,19,4},{0,20,3},{0,21,3},{0,22,2},{0,23,5},{1,0,7},{1,1,0},{1,2,0},{1,3,0},{1,4,0},{1,5,0},{1,6,0},{1,7,0},{1,8,0},{1,9,0},{1,10,5},{1,11,2},{1,12,2},{1,13,6},{1,14,9},{1,15,11},{1,16,6},{1,17,7},{1,18,8},{1,19,12},{1,20,5},{1,21,5},{1,22,7},{1,23,2},{2,0,1},{2,1,1},{2,2,0},{2,3,0},{2,4,0},{2,5,0},{2,6,0},{2,7,0},{2,8,0},{2,9,0},{2,10,3},{2,11,2},{2,12,1},{2,13,9},{2,14,8},{2,15,10},{2,16,6},{2,17,5},{2,18,5},{2,19,5},{2,20,7},{2,21,4},{2,22,2},{2,23,4},{3,0,7},{3,1,3},{3,2,0},{3,3,0},{3,4,0},{3,5,0},{3,6,0},{3,7,0},{3,8,1},{3,9,0},{3,10,5},{3,11,4},{3,12,7},{3,13,14},{3,14,13},{3,15,12},{3,16,9},{3,17,5},{3,18,5},{3,19,10},{3,20,6},{3,21,4},{3,22,4},{3,23,1},{4,0,1},{4,1,3},{4,2,0},{4,3,0},{4,4,0},{4,5,1},{4,6,0},{4,7,0},{4,8,0},{4,9,2},{4,10,4},{4,11,4},{4,12,2},{4,13,4},{4,14,4},{4,15,14},{4,16,12},{4,17,1},{4,18,8},{4,19,5},{4,20,3},{4,21,7},{4,22,3},{4,23,0},{5,0,2},{5,1,1},{5,2,0},{5,3,3},{5,4,0},{5,5,0},{5,6,0},{5,7,0},{5,8,2},{5,9,0},{5,10,4},{5,11,1},{5,12,5},{5,13,10},{5,14,5},{5,15,7},{5,16,11},{5,17,6},{5,18,0},{5,19,5},{5,20,3},{5,21,4},{5,22,2},{5,23,0},{6,0,1},{6,1,0},{6,2,0},{6,3,0},{6,4,0},{6,5,0},{6,6,0},{6,7,0},{6,8,0},{6,9,0},{6,10,1},{6,11,0},{6,12,2},{6,13,1},{6,14,3},{6,15,4},{6,16,0},{6,17,0},{6,18,0},{6,19,0},{6,20,1},{6,21,2},{6,22,2},{6,23,6}}; 
	
		return R.ok().put("qushi", arr).put("ipc", str).put("year", ipcstr);
		
    }
	
	@GetMapping(value = "patent/agclassiscon")
	public String agclassiscon() {
		return "T-jigouCon";
	}
	
	@GetMapping(value = "patent/agtypecon")
	public String agtypecon() {
		return "T-rencaiCon";
	}
	
	@GetMapping(value = "price")
	public void price() {
		

		for(int i=7;i>0;i--) {
			String url="http://nm.sci99.com/news/?page=" + i + "&sid=8784&siteid=10" ;
			String base = "http://nm.sci99.com";
			try {
	        	
//	        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
	        	
//	        	if(exist != null) {
//	        		return;
//	        	}

	            Document doc = Jsoup.connect(url).get();

	            Elements module = doc.getElementsByClass("ul_w690");

	            Document moduleDoc = Jsoup.parse(module.toString());

	            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式

	            Map< String, String> urls= new TreeMap<String, String>();
	            for (Element li : lis){
	                Document liDoc = Jsoup.parse(li.toString());
	                Elements hrefs = liDoc.select("a[href]");
	                for(Element elem: hrefs) {
	                	System.out.println(elem.text().substring(elem.text().length()-9,elem.text().length()-1));
	                	
	                	if(!"".equals(elem.attr("href"))){
	                		String href = elem.attr("href");
	                		urls.put(base + href, elem.text().substring(elem.text().length()-9,elem.text().length()-1));
	                	}
	                }

	            }
	            for(Map.Entry<String, String> entry: urls.entrySet()) {
	            	
	            	Document singleDoc = Jsoup.connect(entry.getKey()).get();
//	            if(!singleDoc.toString().contains(ymd)){
//	            	return;
//	            }
	            	Element zoom = singleDoc.getElementById("zoom");
	            	Elements trElements = zoom.select("tr");
	            	boolean ignore = true;
	            	for(Element tdelement : trElements) {
	            		if(ignore) {
	            			ignore = false;
	            			continue;
	            		}
	            		Elements tdes = tdelement.select("td");
	            		System.out.println(tdes.size());
	            		Price price = new Price();
	            		if(tdes.size()==7) {
	            			price.setUpdateTime(entry.getValue());
	            			price.setName(tdes.get(0).text());
	            			price.setDescription(tdes.get(1).text());
	            			price.setUnit(tdes.get(6).text());
	            			price.setPrice(tdes.get(3).text());
	            			price.setAvg(tdes.get(4).text());
	            			price.setFloating(tdes.get(5).text());
	            		}else {
	            			price.setUpdateTime(entry.getValue());
	            			price.setName(tdes.get(0).text());
	            			price.setDescription(tdes.get(1).text());
	            			price.setUnit(tdes.get(7).text());
	            			price.setPrice(tdes.get(3).text()+"-"+tdes.get(4).text());
	            			price.setAvg(tdes.get(5).text());
	            			price.setFloating(tdes.get(6).text());
	            			
	            		}
//	            		Price yesterday = priceMapper.getLatestPrice(price.getName());
//	            		if(yesterday == null) {
//	            			price.setFloating("100%");
//	            		}else {
//	            			float before = Float.valueOf(yesterday.getAvg());
//	            			float now = Float.valueOf(tdes.get(4).text());
//	            			float delta = now - before;
//	            			if(delta != 0) {
//	            				System.out.println();
//	            			}
//	            			NumberFormat numberFormat = NumberFormat.getInstance();
//	            			numberFormat.setMaximumFractionDigits(2);
//	            			String result = numberFormat.format(delta / before * 100);
//	            			price.setFloating(result + "%");
//	            		}
	            		priceMapper.insertPrice(price);
	            		try {
	        				Thread.sleep(1000);
	        			} catch (InterruptedException e) {
	        				// TODO Auto-generated catch block
	        				e.printStackTrace();
	        			}
	            		System.out.println("插入成功" + price.getName());
	            	}
	            	//  String title = clearfixli.getElementsByTag("a").text();
	            	System.out.println("fasdf");
	            }


	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		final String url="http://nm.sci99.com/news/s8784.html" ;
//		String single = "http://nm.sci99.com";
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//		Date date = new Date();
//		String ymd = formatter.format(date);
//
//        try {
//        	
//        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
//        	
////        	if(exist != null) {
////        		return;
//////        		System.out.println("exist");
////        	}
//
//            Document doc = Jsoup.connect(url).get();
//
//            Elements module = doc.getElementsByClass("ul_w690");
//            if(!module.text().contains(ymd)) {
//            	return;
////            	System.out.println("");
//            }
//            System.out.println(module.text());
//
//            Document moduleDoc = Jsoup.parse(module.toString());
//
//            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式
//
//jump:
//            for (Element li : lis){
//                Document liDoc = Jsoup.parse(li.toString());
//                Elements hrefs = liDoc.select("a[href]");
//                for(Element elem: hrefs) {
//                	if(!"".equals(elem.attr("href"))){
//                		String href = elem.attr("href");
//                		single = single + href;
//                		break jump;
//                	}
//                }
//
//            }
//            
//            Document singleDoc = Jsoup.connect(single).get();
////            if(!singleDoc.toString().contains(ymd)){
////            	return;
////            }
//            Element zoom = singleDoc.getElementById("zoom");
//            Elements trElements = zoom.select("tr");
//            boolean ignore = true;
//            for(Element tdelement : trElements) {
//            	if(ignore) {
//            		ignore = false;
//            		continue;
//            	}
//            	Elements tdes = tdelement.select("td");
//            	Price price = new Price();
//            	price.setUpdateTime(formatter.format(date));
//        		price.setName(tdes.get(0).text());
//        		price.setDescription(tdes.get(1).text());
//        		price.setUnit(tdes.get(6).text());
//        		price.setPrice(tdes.get(3).text());
//        		price.setAvg(tdes.get(4).text());
//        		Price yesterday = priceMapper.getLatestPrice(price.getName());
//        		if(yesterday == null) {
//        			price.setFloating("100%");
//        		}else {
//        			float before = Float.valueOf(yesterday.getAvg());
//        			float now = Float.valueOf(tdes.get(4).text());
//        			float delta = now - before;
//        			if(delta != 0) {
//        				System.out.println();
//        			}
//        			NumberFormat numberFormat = NumberFormat.getInstance();
//        			numberFormat.setMaximumFractionDigits(2);
//        			String result = numberFormat.format(delta / before * 100);
//        			price.setFloating(result + "%");
//        		}
////            	priceMapper.insertPrice(price);
//            }
//              //  String title = clearfixli.getElementsByTag("a").text();
//            System.out.println("fasdf");
//
//          //  System.out.println(clearfix);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		return "T-rencaiCon";
	}
	
	@ResponseBody
	@RequestMapping(value = "price/avg", method = RequestMethod.POST,consumes = "application/json")
	public R priceAvg(@RequestBody PriceAvgRequest request) {
		String time = request.getTime();
		String name = request.getName();
		Map<String, String> map = new HashMap<String, String>();
		String date = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		if("3".equals(time)) {
			for(int i=0;i<3;i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.MONTH, -i);
				Date lastmonth = c.getTime();
				date = formatter.format(lastmonth);
				String start = date.substring(0, 6) + "00";
				String end = date.substring(0,6) + "32";
				String avg = priceMapper.getAvgPricesGroupByName(start, end , name);
				if(avg == null) {
					avg = "0";
				}else if(avg.contains(".")) {
					avg = avg.split("\\.")[0];
				}
				map.put(date.substring(4, 6), avg);
				
			}
		}else if("1".equals(time)) {
			for(int i=0;i<12;i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.MONTH, -i);
				Date lastmonth = c.getTime();
				date = formatter.format(lastmonth);
				String start = date.substring(0, 6) + "00";
				String end = date.substring(0,6) + "32";
				String avg = priceMapper.getAvgPricesGroupByName(start, end , name);
				if(avg == null) {
					continue;
//					avg = "0";
				}else if(avg.contains(".")) {
					avg = avg.split("\\.")[0];
				}
				map.put(date.substring(4, 6), avg);
				
			}
			
		}
		return R.ok().put("avg", map);
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/agtypes", method = RequestMethod.POST,consumes = "application/json")
	public R types(@RequestBody AgTypeRequest request) {
		List<List<Object>> typeList = new ArrayList<List<Object>>();
//		String view = "zhuanlifenxifamingrenjizhuanliquanren";
		String type = request.getType();
		String trend = request.getTrend();
		if(esTemplate.indexExists(Patent.class)) {
				
			MatchAllQueryBuilder queryBuilderAgg = QueryBuilders.matchAllQuery();
			FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
					.withQuery(functionScoreQueryBuilderAgg)
					.withSearchType(SearchType.QUERY_THEN_FETCH)
					.withIndices("patent").withTypes("pt");
			if(type != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(true)).size(10));
			}
			if(trend != null) {
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agipc").field("ipc").order(Terms.Order.count(true)).size(10).subAggregation(AggregationBuilders.terms("agyear").field("year").order(Terms.Order.count(true)).size(10)));
			}
			Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder.build(), new ResultsExtractor<Aggregations>() {
				@Override
				public Aggregations extract(SearchResponse response) {
					return response.getAggregations();
				}
			});
			
			if(aggregations != null) {
				if(type != null) {
					StringTerms ipcTerms = (StringTerms) aggregations.asMap().get("agipc");
					Iterator<Bucket> ipcbit = ipcTerms.getBuckets().iterator();
					List<Object> titleList = new ArrayList<Object>();
					titleList.add("score");
					titleList.add("amount");
					titleList.add("product");
					typeList.add(titleList);
					while(ipcbit.hasNext()) {
						List<Object> list = new ArrayList<Object>();
						Bucket ipcBucket = ipcbit.next();
						list.add((int)(Math.random()*90)+10);
						list.add(ipcBucket.getDocCount());
						list.add(ipcBucket.getKey().toString());
						typeList.add(list);
					}
				}
				
				if(trend != null) {
//					StringTerms creatorTerms = (StringTerms) aggregations.asMap().get("agcreator");
//					Iterator<Bucket> creatorbit = creatorTerms.getBuckets().iterator();
//					while(creatorbit.hasNext()) {
//						List<Object> list = new ArrayList<Object>();
//						Bucket creatorBucket = creatorbit.next();
//						list.add((int)(Math.random()*90)+10);
//						list.add(creatorBucket.getDocCount());
//						list.add(creatorBucket.getKey().toString());
//						personList.add(list);
//					}
				}
			}
		}
		
		return R.ok().put("agpersons", typeList);
	}
	
	
	
	
	
	
	private String getLastMonth(int month){
        Calendar curr = Calendar.getInstance(); 
        curr.set(Calendar.MONTH,curr.get(Calendar.MONTH)-month); //减少一月
        Date date=curr.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
        String dateNowStr = sdf.format(date);  
        return dateNowStr;
    }
	
	private List<String> extractMessageByRegular(String msg){
		
		List<String> list=new ArrayList<String>();
		Pattern p = Pattern.compile("(\\[[^\\]]*\\])");
		Matcher m = p.matcher(msg);
		while(m.find()){
			list.add(m.group().substring(1, m.group().length()-1));
		}
		return list;
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/patentInsList", method = RequestMethod.POST,consumes = "application/json")
	public R expertInsList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		rs = patentService.executeIns(insname.getString("insname"),pageIndex, pageSize, "person",i);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
	@ResponseBody
	@RequestMapping(value = "patent/patentExpList", method = RequestMethod.POST,consumes = "application/json")
	public R expertpatentList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		rs = patentService.executeIns(insname.getString("insname"),pageIndex, pageSize, "creator",i);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
	@GetMapping(value = "patent/transterToNewProp")
	public String eout() {
		Iterator<Patent> patents = patentRepository.findAll().iterator();
		int i=0;
		List<Patent> patentList = new LinkedList<Patent>();
		while(patents.hasNext()) {
			System.out.println(i);
			Patent patent = patents.next();
			List<String> persons = patent.getPerson();
			List<String> ipcs = patent.getIpc();
			List<String> applicantipc = new ArrayList<String>();
			if(persons != null) {
				for(String person : persons) {
					if(ipcs !=null) {
						for(String ipc: ipcs) {
							applicantipc.add(person+"_"+ipc);
						}
					}else {
						applicantipc.add(person+"_0");
					}
				}
			}else {
				if(ipcs != null) {
					for(String ipc: ipcs) {
						applicantipc.add("0_" + ipc);
					}
				}else {
					applicantipc.add("0_0");
				}
			}
			patent.setApplicantipc(applicantipc);
			
			String applyyear = patent.getApplyyear();
			List<String> ipcyear = new ArrayList<String>();
			if(ipcs != null) {
				for(String ipc: ipcs) {
					if(applyyear != null) {
						ipcyear.add(ipc+"_"+applyyear);
					}else {
						ipcyear.add(ipc+"_0");
					}
				}
			}else {
				if(applyyear != null) {
					ipcyear.add("0_"+ applyyear);
				}else {
					ipcyear.add("0_0");
				}
			}
			patent.setIpcyear(ipcyear);
			
			String month = "";
			String applytime = patent.getApplytime();
			if(applytime != null) {
				String[] mats = applytime.split("-");
				if(mats != null && mats.length>1) {
					month = applytime.split("-")[1];
				}else {
					month = "01";
				}
			}
			patent.setMonth(month);
			String type = patent.getType();
			if(type != null && applytime != null) {
				String[] args = applytime.split("-");
				if(args != null && args.length>1) {
					patent.setTypemonth(type+"_"+args[1]);
					patent.setTypeyear(type+"_"+args[0]);
				}else {
					patent.setTypemonth(type+"_01");
					patent.setTypeyear(type+"_"+args[0]);
				}
			}
			patentList.add(patent);
			i++;
		}
		System.out.println("");
		patentRepository.saveAll(patentList);
		return "bsdf";
	}
	
	@GetMapping(value = "patent/travel")
	public String updatePatent() {
		Iterator<Patent> patents = patentRepository.findAll().iterator();
		Iterator<Org> orgs = orgRepository.findAll().iterator();
//		Iterator<Expert> experts = expertRepository.findAll().iterator();
		Map<String, Expert> eMap = new HashMap<String, Expert>();
		Map<String, Org> oMap = new HashMap<String, Org>();
//		while(experts.hasNext()) {
//			Expert e = experts.next();
//			String ren = e.getName();
//			String jigou = e.getUnit();
//			eMap.put(ren + "%" + jigou, e);
//		}
		while(orgs.hasNext()) {
			Org o = orgs.next();
			String jigou = o.getName();
			oMap.put(jigou, o);
		}
//		List<ElementMaster> masters = elementMapper.selectAllMasters();
//		List<ElementSlave> slaves = elementMapper.selectAllSlaves();
		Map<String, Set<String>> expertMap = new HashMap<String, Set<String>>();
		Map<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
		int i=0;
		while(patents.hasNext()) {
			System.out.println(i);
			Set<String> tags = new HashSet<String>();
			Patent patent = patents.next();
			List<String> rens = patent.getCreator();
			List<String> jigous = patent.getPerson();
			
//			String title = patent.getTitle();
//			String subject = patent.getSubject();
//			for(ElementMaster master: masters) {
//				if( title.contains( master.getName())) {
//					tags.add(master.getName());
//				}else if(subject.contains(master.getName())) {
//					tags.add(master.getName());
//				}
//			}
			
//			for(ElementSlave slave: slaves) {
//				if( title.contains( slave.getName())) {
//					tags.add(slave.getName());
//				}else if(subject.contains(slave.getName())) {
//					tags.add(slave.getName());
//				}
//			}
			
			for(String jigou: jigous) {
				for(String ren: rens) {
					String key = ren + "%" + jigou;
					if (eMap.containsKey(key)) {
						Expert expert = eMap.get(key);
						List<String> expertTags = expert.getTags();
						for(String tag : tags) {
							if(expertTags == null) {
								expertTags = new ArrayList<String>();
							}
							if(!expertTags.contains(tag)) {
								expertTags.add(tag);
							}
						}
						expertRepository.save(expert);
					}else {
						expertMap.put(key, tags);
					}
				}
				if(oMap.containsKey(jigou)) {
					Org org = oMap.get(jigou);
					List<String> orgTags = org.getTags();
					for(String tag: tags) {
						if(orgTags == null) {
							orgTags = new ArrayList<String>();
						}
						if(!orgTags.contains(tag)) {
							orgTags.add(tag);
						}
					}
					orgRepository.save(org);
				}else {
					orgMap.put(jigou, tags);
				}
			}
			i++;
		}
//		for(Map.Entry<String, Set<String>> entry: expertMap.entrySet()) {
//			String key = entry.getKey();
//			String[] eo = key.split("%");
//			Expert e = new Expert();
//			e.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
//			e.setName(eo[0]);
//			e.setAnotherName(eo[0]);
//			e.setNow(System.currentTimeMillis());
//			e.setUnit(eo[1]);
//			e.setTop("0");
//			List<String> tags = new ArrayList<String>();
//			tags.addAll(entry.getValue());
//			e.setTags(tags);
//			expertRepository.save(e);
//		}
		
		for(Map.Entry<String, Set<String>> entry: orgMap.entrySet()) {
			if(entry.getValue()!= null) {
				System.out.println();
			}
			String key = entry.getKey();
			List<String> tags = new ArrayList<String>();
			tags.addAll(entry.getValue());
			Org o = new Org();
			o.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
			o.setName(key);
			o.setTags(tags);
			o.setNow(System.currentTimeMillis());
			o.setAnotherName(key);
			o.setTop("0");
			orgRepository.save(o);
		}
		return "bsdf";
	}
	
	@ResponseBody
	@RequestMapping(value = "patent/downloadtotal", method = RequestMethod.POST,consumes = "application/json")
	public R daochutotal(@RequestBody JSONObject info) {
		String q = info.get("q").toString();
		CachePool cache = CachePool.getInstance();
	    JSONObject obj = new JSONObject();
	    //cache.putCacheItem("abc", obj);
	    if (q == null || "".equals(q)) {
	    	 obj = (JSONObject) cache.getCacheItem("total");
	    	 if (obj == null) {
    			JSONObject jObject = patentService.execute(0, 10, 0,q,null,null,null,null,null,null,null);
    			if (jObject != null) {
    				
    				cache.putCacheItem("total", jObject);
    				obj = (JSONObject) cache.getCacheItem("total");
    			}
    			
			}
		}else{
			 obj = (JSONObject) cache.getCacheItem(q);
		}
	    System.out.println(JsonUtil.toJSONString(obj));
	    Calendar cale = null;  
        cale = Calendar.getInstance();  
        int year = cale.get(Calendar.YEAR);  
        String[] str=new String[5];
	    int[] shuliangnum={0,0,0,0,0};
	    int lastfive = year-4;
	    for(int i=0;i<5;i++){
	    	str[i] = lastfive+"";
	    	lastfive++;
	    }
	    
	    JSONObject agg = (JSONObject) obj.get("applyyear");
	    JSONArray ar = (JSONArray) agg.get("buckets");
	    
	    for(Object jsonObject : ar){
	    	for(int j = 0;j<str.length;j++){
	    		if(((JSONObject)jsonObject).get("key").equals(str[j])){
	    			shuliangnum[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	}
	    	}
	    	
	    }
		//model.addAttribute(key, agg.get("buckets"));
	    //model.addAttribute("num", num);
	   // model.addAttribute("yearstr", str)
	    
	    JSONObject shenqingguoagg = (JSONObject) obj.get("country");
	    JSONObject[] strings = null;
	    
	    if (shenqingguoagg != null) {
	    	 JSONArray shenqingguoar = (JSONArray) shenqingguoagg.get("buckets");
	 	    int j=0;
	 	    
	 	    List<JSONObject> joList = new ArrayList<JSONObject>();
	 	    
	 	    for(Object jsonObject : shenqingguoar){
	 	    	//countryName
//	 	    	if(((JSONObject)jsonObject).get("key").equals("中国")){
//	 		    	num[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
//	 		    }
//	 	    	j++;
	 	    	String countrycn = ((JSONObject)jsonObject).get("key").toString();
	 	    	if(countryName.containsKey(countrycn)){
	 	    		String countryen = countryName.get(countrycn);
	 	    		int nums= Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	 	    		JSONObject jo = new JSONObject();
	 	    	    jo.put("name", countryen);
	 	    	    jo.put("value", nums);
	 	    	    joList.add(jo);
	 	    	}
	 	    	
	 	    	
	 	    }
	 	    
	 	   strings = new JSONObject[joList.size()];

	 	   joList.toArray(strings);
	 	    
		}
	    
	    String[] famingrenstr=new String[10];
	    int[] famingrennum=new int[10];
	    
	    
	    JSONObject famingrenagg = (JSONObject) obj.get("creator");
	    JSONArray famingrenar = (JSONArray) famingrenagg.get("buckets");
	    int j = 0;
	    for(Object jsonObject : famingrenar){
	    	
	    	famingrenstr[j] = ((JSONObject)jsonObject).get("key").toString();
	    	famingrennum[j] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	j++;
		    	if(j>=10){
		    		break;
		    	}
	    	
	    }
	    //model.addAttribute("num", num);
	   // model.addAttribute("famingren", str);
	    
	    String[] classisstr=new String[10];
	    int[] classisnum=new int[10];
	    
	    
	    JSONObject classisagg = (JSONObject) obj.get("ipc");
	    JSONArray classisar = (JSONArray) classisagg.get("buckets");
	    int jj = 0;
	    for(Object jsonObject : ar){
	    	
	    	classisstr[jj] = ((JSONObject)jsonObject).get("key").toString();
	    	classisnum[jj] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
		    	jj++;
		    	if(jj>=10){
		    		break;
		    	}
	    	
	    }
	    //model.addAttribute("num", num);
	   // model.addAttribute("famingren", str);
	   
	   
       
        int[] famingnumtotal={0,0,0,0,0};
	    int[] famingnum={0,0,0,0,0};
	    int[] famingshouquannum={0,0,0,0,0};
	    int[] shiyongnum={0,0,0,0,0};
	    int[] waiguannum={0,0,0,0,0};
	    
	    
	    JSONObject typeagg = (JSONObject) obj.get("typeyear");
	    JSONArray typear = (JSONArray) typeagg.get("buckets");
	    
	    for(Object jsonObject : typear){
	    	for(int jjj = 0;jjj<str.length;j++){
	    		if(((JSONObject)jsonObject).get("key").toString().contains(str[j])){
	    			if(((JSONObject)jsonObject).get("key").toString().contains("发明_")){
	    				famingnum[jjj] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
	    			if(((JSONObject)jsonObject).get("key").toString().contains("发明授权_")){
	    				famingshouquannum[jjj] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
	    			if(((JSONObject)jsonObject).get("key").toString().contains("实用新型_")){
	    				shiyongnum[jjj] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
	    			if(((JSONObject)jsonObject).get("key").toString().contains("外观_")){
	    				waiguannum[jjj] = Integer.valueOf(((JSONObject)jsonObject).get("doc_count").toString());
	    			}
		    	}
	    	}
	    	
	    }
	    for(int i=0; i<5; i++){
	    	famingnumtotal[i] = famingnum[i] + famingshouquannum[i];
		}
		//model.addAttribute(key, agg.get("buckets"));
//	    model.addAttribute("famingnumtotal", famingnumtotal);
//	    model.addAttribute("waiguannum", waiguannum);
//	    model.addAttribute("shiyongnum", shiyongnum);
//	    model.addAttribute("yearstr", str);
		return R.ok().put("shuliangnum", shuliangnum).put("yearstr", str).put("strings", strings).put("famingrennum", famingrennum).put("famingrenstr", famingrenstr).put("classisnum", classisnum).put("classisstr", classisstr).put("famingnumtotal", famingnumtotal).put("waiguannum", waiguannum).put("shiyongnum", shiyongnum);
	}
	
}
