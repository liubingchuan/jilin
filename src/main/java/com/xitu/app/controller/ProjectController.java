package com.xitu.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.common.request.SaveProjectRequest;
import com.xitu.app.mapper.ItemMapper;
import com.xitu.app.model.Item;
import com.xitu.app.model.Project;
import com.xitu.app.repository.ProjectRepository;
import com.xitu.app.service.es.ProjectService;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ProjectController {

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
	
	@Autowired
    private ProjectRepository projectRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private ItemMapper itemMapper;
	
	@Autowired
    private ProjectService projectService;
	
	@PostMapping(value = "project/save")
	public String saveProject(SaveProjectRequest request,Model model) {
		
		Project project = new Project();
		BeanUtil.copyBean(request, project);
		if(project.getId() == null || "".equals(project.getId())) {
			project.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
		}
		project.setDescription(request.getInfo());
		project.setNow(System.currentTimeMillis());
//		List<String> list = new ArrayList<String>();
//		list.add("sdf");
//		list.add("gasdf");
//		list.add("kkkkkk");
//		project.setList(list);
		projectRepository.save(project);
		return "redirect:/project/list";
	}
	
	@GetMapping(value = "project/get")
	public String getProject(@RequestParam(required=false,value="id") String id, 
			@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="disable") String disable,
			Model model) {
		Project project = new Project();
		if(id != null) {
			project = projectRepository.findById(id).get();
		}
		model.addAttribute("project", project);
		model.addAttribute("id", id);
		model.addAttribute("query", q);
		if(disable !=null) {
			model.addAttribute("disable", "0");
		}else {
			model.addAttribute("disable", "1");
		}
		Item item = itemMapper.selectItemByService("xmfl");
		List<String> items = new ArrayList<String>();
		if(item != null && item.getItem() != null) {
			for(String s: item.getItem().split(";")) {
				items.add(s);
			}
		}
		model.addAttribute("items", items);
		Integer pageIndex = 0;
		Integer pageSize = 10;
		long totalCount = 0L;
		long totalPages = 0L;
		List<Project> projectList = new ArrayList<Project>();
		String view = "manageProCon";
		if(esTemplate.indexExists(Project.class) && project.getName() != null) {
			// 分页参数
			Pageable pageable = new PageRequest(pageIndex, pageSize);

			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("name", project.getName()));
//			if(entrust != null) {
//				String[] entrusts = entrust.split("-");
//				queryBuilder.filter(QueryBuilders.termsQuery("entrust", entrusts));
//			}
			
			// 分数，并自动按分排序
			FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));

			// 分数、分页
			SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
					.withQuery(functionScoreQueryBuilder).build();

			Page<Project> searchPageResults = projectRepository.search(searchQuery);
			projectList = searchPageResults.getContent();
			totalCount = esTemplate.count(searchQuery, Project.class);
			
			
			totalPages = Math.round(totalCount/pageSize);
			if(projectList.size()<2) {
				projectList = new ArrayList<Project>();
			}else {
				projectList = projectList.subList(1, projectList.size());
			}
			model.addAttribute("projectList", projectList);
			if(front != null) {
				view = "result-xmCon";
			}
		}
		return view;
	}
	
//	@GetMapping(value = "project/list")
//	public String projects(@RequestParam(required=false,value="q") String q,
//			@RequestParam(required=false,value="entrust") String entrust,
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
//		List<Project> projectList = new ArrayList<Project>();
//		String view = "manage_pro";
//		if(esTemplate.indexExists(Project.class)) {
//			if(q == null) {
//				totalCount = projectRepository.count();
//				if(totalCount >0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Project> projectsPage = projectRepository.search(searchQuery);
//					projectList = projectsPage.getContent();
//				}
//			}else {
//				// 分页参数
//				Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("name", q));
//				if(entrust != null) {
//					String[] entrusts = entrust.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("entrust", entrusts));
//				}
//				
//				
//				// 分数，并自动按分排序
//				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//
//				// 分数、分页
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//						.withQuery(functionScoreQueryBuilder).build();
//
//				Page<Project> searchPageResults = projectRepository.search(searchQuery);
//				projectList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Project.class);
//				
//				
//				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q));
//				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
//				List<String> pList=new ArrayList<>();
//				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
//						.withQuery(functionScoreQueryBuilderAgg)
//						.withSearchType(SearchType.QUERY_THEN_FETCH)
//						.withIndices("project").withTypes("pt")
//						.addAggregation(AggregationBuilders.terms("agentrust").field("entrust").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agbudget").field("budget").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agclassis").field("classis").order(Terms.Order.count(false)).size(10))
//						.build();
//				Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
//			        @Override
//			        public Aggregations extract(SearchResponse response) {
//			            return response.getAggregations();
//			        }
//			    });
//				
//				if(aggregations != null) {
//					StringTerms entrustTerms = (StringTerms) aggregations.asMap().get("agentrust");
//					Iterator<Bucket> enbit = entrustTerms.getBuckets().iterator();
//					Map<String, Long> entrustMap = new HashMap<String, Long>();
//					while(enbit.hasNext()) {
//						Bucket enBucket = enbit.next();
//						entrustMap.put(enBucket.getKey().toString(), Long.valueOf(enBucket.getDocCount()));
//					}
//					model.addAttribute("agentrust", entrustMap);
//					
//					StringTerms budgetTerms = (StringTerms) aggregations.asMap().get("agbudget");
//					Iterator<Bucket> bubit = budgetTerms.getBuckets().iterator();
//					Map<String, Long> budgetMap = new HashMap<String, Long>();
//					while(bubit.hasNext()) {
//						Bucket buBucket = bubit.next();
//						budgetMap.put(buBucket.getKey().toString(), Long.valueOf(buBucket.getDocCount()));
//					}
//					model.addAttribute("agbudget", budgetMap);
//					
//					StringTerms classisTerms = (StringTerms) aggregations.asMap().get("agclassis");
//					Iterator<Bucket> classisbit = classisTerms.getBuckets().iterator();
//					Map<String, Long> classisMap = new HashMap<String, Long>();
//					while(classisbit.hasNext()) {
//						Bucket classisBucket = classisbit.next();
//						classisMap.put(classisBucket.getKey().toString(), Long.valueOf(classisBucket.getDocCount()));
//					}
//					model.addAttribute("agclassis", classisMap);
//				}
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
//				view = "result-xm";
//			}
//		}
//		totalPages = Double.valueOf(Math.ceil(Double.valueOf(totalCount)/Double.valueOf(pageSize))).intValue();
//		model.addAttribute("projectList", projectList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
//			
//		return view;
//	}
	
	@GetMapping(value = "project/list")
	public String projects(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="classis") String classis,
			@RequestParam(required=false,value="budget") String budget,
			@RequestParam(required=false,value="entrust") String entrust,
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
		int i = 2;//0代表专利；1代表论文；2代表项目；3代表监测
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		projectService.execute(pageIndex, pageSize, i,q,classis,budget,entrust);
		ThreadLocalUtil.remove();
		String view = "manage_pro";
		if (q!=null) {
			view = "result-xm";
		}
		return view;
	}
	
	@GetMapping(value = "project/delete")
	public String deleteProject(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			projectRepository.deleteById(id);
		}
		
		return "redirect:/project/list";
	}
	@ResponseBody
	@RequestMapping(value = "project/xiangguanprojectList", method = RequestMethod.POST,consumes = "application/json")
	public R xiangguanpaperList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
    	String query = (String) insname.get("query");
    	String id = (String) insname.get("id");
		int i = 1;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		List<String> ss = new ArrayList<String>();
		ss.add(query);
		rs = projectService.executeXiangguan(pageIndex, pageSize, i,id,ss);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
}
