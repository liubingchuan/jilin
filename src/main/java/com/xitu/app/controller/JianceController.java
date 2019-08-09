package com.xitu.app.controller;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
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

import com.alibaba.fastjson.JSONObject;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.xitu.app.common.R;
import com.xitu.app.mapper.TaskMapper;
import com.xitu.app.model.Jiance;
import com.xitu.app.model.Paper;
import com.xitu.app.model.Task;
import com.xitu.app.repository.JianceRepository;
import com.xitu.app.repository.PaperRepository;
import com.xitu.app.service.es.JianceService;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class JianceController {

	private static final Logger logger = LoggerFactory.getLogger(JianceController.class);
	
	@Autowired
    private JianceRepository paperRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
	private JianceService jianceService;
	
	@Autowired
    private JianceRepository jianceRepository;
	
	
	
//	@GetMapping(value = "jiance/jiancelist")
//	public String projects(@RequestParam(required=false,value="q") String q,
//			@RequestParam(required=false,value="year") String year,
//			@RequestParam(required=false,value="institution") String institution,
//			@RequestParam(required=false,value="lanmu") String lanmu,
//			@RequestParam(required=false,value="pageSize") Integer pageSize, 
//			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
//			Model model) {
//		if(pageSize == null) {
//			pageSize = 10;
//		}
//		if(pageIndex == null) {
//			pageIndex = 0;
//		}
//		
//		long lb = System.currentTimeMillis();
//		//long lc = lb-la;
//		//System.out.println("查询后时间 ====  "+lc );
//		long totalCount = 0L;
//		long totalPages = 0L;
//		List<Jiance> paperList = new ArrayList<Jiance>();
//		String view = "T-jiance";
//		if(esTemplate.indexExists(Jiance.class)) {
//			if(q == null || q.equals("null") || q.equals("")) {
//				//totalCount = paperRepository.count();
//				//if(totalCount >0) {
//					Sort sort = new Sort(Direction.DESC, "pubtime");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					//Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//					BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
////					if(year != null) {
////						String[] years = year.split(",");
////						queryBuilder.filter(QueryBuilders.termsQuery("year", years));
////					}
//					if(institution != null && !institution.equals("")) {
//						try {
//							institution = URLDecoder.decode(institution, "UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						String[] institutions = institution.split(",");
//						queryBuilder.filter(QueryBuilders.termsQuery("institution", institutions));
//					}
//					if(lanmu != null && !lanmu.equals("")) {
//						try {
//							lanmu = URLDecoder.decode(lanmu, "UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						String[] lanmus = lanmu.split(",");
//						queryBuilder.filter(QueryBuilders.termsQuery("lanmu", lanmus));
//					}
//					
//					// 分数，并自动按分排序
//					FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).withQuery(functionScoreQueryBuilder).build();
//					Page<Jiance> projectsPage = paperRepository.search(searchQuery);
//					paperList = projectsPage.getContent();
//					
//					totalCount = esTemplate.count(searchQuery, Jiance.class);
//					if(totalCount % pageSize == 0){
//						totalPages = totalCount/pageSize;
//					}else{
//						totalPages = totalCount/pageSize + 1;
//					}
//					//totalPages = Math.round(totalCount/pageSize);
//					SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
//							//.withQuery(functionScoreQueryBuilderAgg)
//							.withQuery(functionScoreQueryBuilder)
//							.withSearchType(SearchType.QUERY_THEN_FETCH)
//							.withIndices("jiance").withTypes("jc")
//							//.addAggregation(AggregationBuilders.terms("agpubyear").field("pubyear").order(Terms.Order.count(false)).size(10))
//							.addAggregation(AggregationBuilders.terms("aglanmu").field("lanmu").order(Terms.Order.count(false)).size(10))
//							.addAggregation(AggregationBuilders.terms("aginstitution").field("institution").order(Terms.Order.count(false)).size(10))
//							//.addAggregation(AggregationBuilders.terms("agauthor").field("author").order(Terms.Order.count(false)).size(10))
//							.build();
//					Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
//				        @Override
//				        public Aggregations extract(SearchResponse response) {
//				            return response.getAggregations();
//				        }
//				    });
//					long ld=System.currentTimeMillis();
//					long le = ld-lb;
//					System.out.println("查询后时间2 ====  "+le );
//					
//					if(aggregations != null) {
////						StringTerms yearTerms = (StringTerms) aggregations.asMap().get("agpubyear");
////						Iterator<Bucket> yearbit = yearTerms.getBuckets().iterator();
////						Map<String, Long> yearMap = new HashMap<String, Long>();
////						while(yearbit.hasNext()) {
////							Bucket yearBucket = yearbit.next();
////							yearMap.put(yearBucket.getKey().toString(), Long.valueOf(yearBucket.getDocCount()));
////						}
////						model.addAttribute("agyear", yearMap);
//						
//						StringTerms institutionTerms = (StringTerms) aggregations.asMap().get("aginstitution");
//						Iterator<Bucket> institutionbit = institutionTerms.getBuckets().iterator();
//						Map<String, Long> institutionMap = new HashMap<String, Long>();
//						while(institutionbit.hasNext()) {
//							Bucket institutionBucket = institutionbit.next();
//							institutionMap.put(institutionBucket.getKey().toString(), Long.valueOf(institutionBucket.getDocCount()));
//						}
//						model.addAttribute("aginstitution", institutionMap);
//						
//						StringTerms journalTerms = (StringTerms) aggregations.asMap().get("aglanmu");
//						Iterator<Bucket> journalbit = journalTerms.getBuckets().iterator();
//						Map<String, Long> journalMap = new HashMap<String, Long>();
//						while(journalbit.hasNext()) {
//							Bucket journalBucket = journalbit.next();
//							journalMap.put(journalBucket.getKey().toString(), Long.valueOf(journalBucket.getDocCount()));
//						}
//						model.addAttribute("aglanmu", journalMap);
//						
//					}
//				//}
//			}else {
//				// 分页参数
//				//Pageable pageable = new PageRequest(pageIndex, pageSize);
//				Sort sort = new Sort(Direction.DESC, "pubtime");
//				Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("title", q)).should(QueryBuilders.matchPhraseQuery("description", q));
////				if(year != null) {
////					String[] years = year.split(",");
////					queryBuilder.filter(QueryBuilders.termsQuery("year", years));
////				}
//				if(institution != null && !institution.equals("")) {
//					try {
//						institution = URLDecoder.decode(institution, "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					String[] institutions = institution.split(",");
//					queryBuilder.filter(QueryBuilders.termsQuery("institution", institutions));
//				}
//				if(lanmu != null && !lanmu.equals("")) {
//					try {
//						lanmu = URLDecoder.decode(lanmu, "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					String[] lanmus = lanmu.split(",");
//					queryBuilder.filter(QueryBuilders.termsQuery("lanmu", lanmus));
//				}
//				
//				// 分数，并自动按分排序
//				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//
//				// 分数、分页
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//						.withQuery(functionScoreQueryBuilder).build();
//
//				Page<Jiance> searchPageResults = paperRepository.search(searchQuery);
//				paperList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Jiance.class);
//				
//				
//				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery().filter(QueryBuilders.matchPhraseQuery("title", q)).should(QueryBuilders.matchPhraseQuery("description", q));
//				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
//				List<String> pList=new ArrayList<>();
//				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
//						.withQuery(functionScoreQueryBuilderAgg)
//						.withSearchType(SearchType.QUERY_THEN_FETCH)
//						.withIndices("jiance").withTypes("jc")
//						//.addAggregation(AggregationBuilders.terms("agpubyear").field("pubyear").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("aglanmu").field("lanmu").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("aginstitution").field("institution").order(Terms.Order.count(false)).size(10))
//						//.addAggregation(AggregationBuilders.terms("agauthor").field("author").order(Terms.Order.count(false)).size(10))
//						.build();
//				Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
//			        @Override
//			        public Aggregations extract(SearchResponse response) {
//			            return response.getAggregations();
//			        }
//			    });
//				long ld=System.currentTimeMillis();
//				long le = ld-lb;
//				System.out.println("查询后时间3 ====  "+le );
//				if(aggregations != null) {
////					StringTerms yearTerms = (StringTerms) aggregations.asMap().get("agpubyear");
////					Iterator<Bucket> yearbit = yearTerms.getBuckets().iterator();
////					Map<String, Long> yearMap = new HashMap<String, Long>();
////					while(yearbit.hasNext()) {
////						Bucket yearBucket = yearbit.next();
////						yearMap.put(yearBucket.getKey().toString(), Long.valueOf(yearBucket.getDocCount()));
////					}
////					model.addAttribute("agyear", yearMap);
//					
//					StringTerms institutionTerms = (StringTerms) aggregations.asMap().get("aginstitution");
//					Iterator<Bucket> institutionbit = institutionTerms.getBuckets().iterator();
//					Map<String, Long> institutionMap = new HashMap<String, Long>();
//					while(institutionbit.hasNext()) {
//						Bucket institutionBucket = institutionbit.next();
//						institutionMap.put(institutionBucket.getKey().toString(), Long.valueOf(institutionBucket.getDocCount()));
//					}
//					model.addAttribute("aginstitution", institutionMap);
//					
//					StringTerms journalTerms = (StringTerms) aggregations.asMap().get("aglanmu");
//					Iterator<Bucket> journalbit = journalTerms.getBuckets().iterator();
//					Map<String, Long> journalMap = new HashMap<String, Long>();
//					while(journalbit.hasNext()) {
//						Bucket journalBucket = journalbit.next();
//						journalMap.put(journalBucket.getKey().toString(), Long.valueOf(journalBucket.getDocCount()));
//					}
//					model.addAttribute("aglanmu", journalMap);
//					
//				}
//				//totalPages = Math.round(totalCount/pageSize);
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
//				//totalPages = Math.round(totalCount/pageSize);
//				
//			}
//		}
//		for(Jiance jc: paperList) {
//			jc.setDescription(jc.getDescription().replace("&lt;", "<"));
//			jc.setDescription(jc.getDescription().replace("&gt;", ">"));
//		}
//		long la = System.currentTimeMillis();
//		//System.out.println("查询前时间 ====  "+la );
//		DataDiscoveryServiceImpl dataDiscoveryService = new DataDiscoveryServiceImpl();
//		JSONObject json_send= dataDiscoveryService.jiance(q,year,institution,lanmu,pageIndex);
//		System.out.println("我的  " + (System.currentTimeMillis() - la));
//		model.addAttribute("paperList", paperList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
//		model.addAttribute("ins", institution);
//		model.addAttribute("lanmu", lanmu);
//			
//		return view;
//	}
	
	@GetMapping(value = "jiance/jiancelist")
	public String projects(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="pubtime") String pubtime,
			@RequestParam(required=false,value="institution") String institutions,
			@RequestParam(required=false,value="lanmu") String lanmus,
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
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		jianceService.execute(pageIndex, pageSize, 3,q,institutions,lanmus,pubtime);
		ThreadLocalUtil.remove();
//		DataDiscoveryServiceImpl dataDiscoveryService = new DataDiscoveryServiceImpl();
//		JSONObject json_send= dataDiscoveryService.jiance(q,year,institutions,lanmus,pageIndex);
//		String total = json_send.getString("total");
//		JSONObject js = json_send.getJSONObject("hits");
		
//		System.out.println(response.toString());
//		model.addAttribute("json_send", response);
		String view = "T-jiance";
		return view;
	}
	
	@GetMapping(value = "jiance/get")
	public String getPaper(@RequestParam(required=false,value="id") String id, Model model) {
		Jiance paper = new Jiance();
		if(id != null) {
			paper = paperRepository.findById(id).get();
		}
		model.addAttribute("paper", paper);
		Integer pageIndex = 0;
		Integer pageSize = 10;
		long totalCount = 0L;
		long totalPages = 0L;
		List<Jiance> paperList = new ArrayList<Jiance>();
		if(esTemplate.indexExists(Jiance.class) && paper.getTitle() != null) {
			// 分页参数
			Pageable pageable = new PageRequest(pageIndex, pageSize);

			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("title", paper.getTitle()));
//			if(entrust != null) {
//				String[] entrusts = entrust.split("-");
//				queryBuilder.filter(QueryBuilders.termsQuery("entrust", entrusts));
//			}
			
			// 分数，并自动按分排序
			FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));

			// 分数、分页
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
					.withQuery(functionScoreQueryBuilder).build();

			Page<Jiance> searchPageResults = paperRepository.search(searchQuery);
			paperList = searchPageResults.getContent();
			totalCount = esTemplate.count(searchQuery, Paper.class);
			
			
			totalPages = Math.round(totalCount/pageSize);
			if(paperList.size()<2) {
				paperList = new ArrayList<Jiance>();
			}else {
				paperList = paperList.subList(1, paperList.size());
			}
			model.addAttribute("paperList", paperList);
		}
		return "T-jianceCon";
	}
	
	@GetMapping(value = "jiance/subcribe")
	public String subcribe() {
		List<Jiance> objs = new LinkedList<Jiance>();
    	try {
    		Map<String, String> map = new TreeMap<String, String>();
    		map.put("http://35.201.235.191:3000/users/1/web_requests/15/xituzixun.xml", "新闻动态");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/18/xituguojiazhengce.xml", "国家政策");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/37/xituzaixian.xml", "新闻动态");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/40/ruidaoxitu.xml", "新闻动态");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/42/SCI.xml", "科研进展");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int i=1;
			for(Map.Entry<String, String> kv: map.entrySet()) {
				try (XmlReader reader = new XmlReader(new URL(kv.getKey()))) {
					SyndFeed feed = new SyndFeedInput().build(reader);
					System.out.println(feed.getTitle());
					System.out.println("***********************************");
					for (SyndEntry entry : feed.getEntries()) {
						System.out.println(entry.getPublishedDate());
						if(this.isNow(sdf.format(entry.getPublishedDate()))) {
							System.out.println();
						}else {
							System.out.println();
						}
						Jiance jiance = new Jiance();
						jiance.setId(UUID.randomUUID().toString());
						jiance.setTitle(entry.getTitle());
						jiance.setDescription(entry.getDescription().getValue());
						jiance.setPubtime(sdf.format(entry.getPublishedDate()));
						jiance.setLanmu(kv.getValue());
						if(i==3){
							jiance.setInstitution("稀土在线");
						}else if(i==4) {
							jiance.setInstitution("瑞道稀土");
						}else if(i==5) {
							jiance.setInstitution("科睿唯安");
						}else {
							jiance.setInstitution("中国稀土网");
						}
						objs.add(jiance);
						System.out.println("***********************************");
					}
					System.out.println("Done");
				}
				i++;
			}
//			jianceRepository.saveAll(objs);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "success";
	}
	
	private boolean isNow(String date) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //获取今天的日期
        String nowDay = sf.format(now);
        //对比的时间
        return date.equals(nowDay);
    }
	
	@ResponseBody
	@RequestMapping(value = "jiance/jianceInsList", method = RequestMethod.POST,consumes = "application/json")
	public R expertInsList(@RequestBody JSONObject insname,Model model) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		ThreadLocalUtil.set(model);
		//rs = jianceService.executeIns(insname.getString("insname"),pageIndex, pageSize, "institution",i);
		rs = jianceService.execute(pageIndex, pageSize, 3,insname.getString("insname"),"","","");
		ThreadLocalUtil.remove();
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
}
