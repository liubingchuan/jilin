package com.xitu.app.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.xitu.app.common.request.SaveTaskRequest;
import com.xitu.app.mapper.ItemMapper;
import com.xitu.app.mapper.TaskMapper;
import com.xitu.app.model.Item;
import com.xitu.app.model.Jiance;
import com.xitu.app.model.Project;
import com.xitu.app.model.Task;
import com.xitu.app.repository.JianceRepository;
import com.xitu.app.utils.BeanUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class TaskController {

	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
	@Autowired
    private JianceRepository taskRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private ItemMapper itemMapper;
	
	@GetMapping(value = "task/delete")
	public String deleteProject(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			taskRepository.deleteById(id);
		}
		
		return "redirect:/task/getTaskList";
	}
	
	@PostMapping(value = "task/save")
	public String saveTask(SaveTaskRequest request,Model model) {
		
		Jiance jiance = new Jiance();
		BeanUtil.copyBean(request, jiance);
		if(jiance.getId() == null || "".equals(jiance.getId())) {
			jiance.setId(UUID.randomUUID().toString());
		}
		jiance.setTitle(request.getTitle());
		jiance.setDescription(request.getInfo());
		jiance.setInstitution(request.getInstitution());
		jiance.setLanmu(request.getLanmu());
		jiance.setPubtime(request.getPubtime());
		
		//jiance.setNow(System.currentTimeMillis());
//		List<String> list = new ArrayList<String>();
//		list.add("sdf");
//		list.add("gasdf");
//		list.add("kkkkkk");
//		project.setList(list);
		taskRepository.save(jiance);
		return "redirect:/task/getTaskList";
	}
	
	@GetMapping(value = "task/get")
	public String getTask(@RequestParam(required=false,value="id") String id, 
			@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="disable") String disable,
			Model model) {
		Jiance jiance = new Jiance();
		if(id != null) {
			jiance = taskRepository.findById(id).get();
		}
		model.addAttribute("jiance", jiance);
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
		String view = "manageJianCon";
		return view;
	}
	
	@GetMapping(value = "task/getTaskList")
	public String getTaskList(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="year") String year,
			@RequestParam(required=false,value="institution") String institution,
			@RequestParam(required=false,value="lanmu") String lanmu,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		long totalCount = 0L;
		long totalPages = 0L;
		List<Jiance> paperList = new ArrayList<Jiance>();
		String view = "xinxicaijijieguoliebiao";
		if(esTemplate.indexExists(Jiance.class)) {
			if(q == null || q.equals("null") || q.equals("")) {
				//totalCount = paperRepository.count();
				//if(totalCount >0) {
					Sort sort = new Sort(Direction.DESC, "pubtime");
					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
					//Pageable pageable = new PageRequest(pageIndex, pageSize);

					BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
					if(institution != null && !institution.equals("")) {
						try {
							institution = URLDecoder.decode(institution, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String[] institutions = institution.split(",");
						queryBuilder.filter(QueryBuilders.termsQuery("institution", institutions));
					}
					if(lanmu != null && !lanmu.equals("")) {
						try {
							lanmu = URLDecoder.decode(lanmu, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String[] lanmus = lanmu.split(",");
						queryBuilder.filter(QueryBuilders.termsQuery("lanmu", lanmus));
					}
					
					// 分数，并自动按分排序
					FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
					SearchQuery searchQuery = new NativeSearchQueryBuilder()
							.withPageable(pageable).withQuery(functionScoreQueryBuilder).build();
					Page<Jiance> projectsPage = taskRepository.search(searchQuery);
					paperList = projectsPage.getContent();
					
					totalCount = esTemplate.count(searchQuery, Jiance.class);
					if(totalCount % pageSize == 0){
						totalPages = totalCount/pageSize;
					}else{
						totalPages = totalCount/pageSize + 1;
					}
			}else {
				// 分页参数
				Pageable pageable = new PageRequest(pageIndex, pageSize);

				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("title", q)).should(QueryBuilders.matchPhraseQuery("description", q));
				if(institution != null && !institution.equals("")) {
					try {
						institution = URLDecoder.decode(institution, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String[] institutions = institution.split(",");
					queryBuilder.filter(QueryBuilders.termsQuery("institution", institutions));
				}
				if(lanmu != null && !lanmu.equals("")) {
					try {
						lanmu = URLDecoder.decode(lanmu, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String[] lanmus = lanmu.split(",");
					queryBuilder.filter(QueryBuilders.termsQuery("lanmu", lanmus));
				}
				
				// 分数，并自动按分排序
				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));

				// 分数、分页
				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
						.withQuery(functionScoreQueryBuilder).build();

				Page<Jiance> searchPageResults = taskRepository.search(searchQuery);
				paperList = searchPageResults.getContent();
				totalCount = esTemplate.count(searchQuery, Jiance.class);
				if(totalCount % pageSize == 0){
					totalPages = totalCount/pageSize;
				}else{
					totalPages = totalCount/pageSize + 1;
				}
				
			}
		}
		for(Jiance jc: paperList) {
			if(jc.getDescription() != null){
				jc.setDescription(jc.getDescription().replace("&lt;", "<"));
				jc.setDescription(jc.getDescription().replace("&gt;", ">"));
			}
			
		}
		model.addAttribute("paperList", paperList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("query", q);
		model.addAttribute("ins", institution);
		model.addAttribute("lanmu", lanmu);
			
		return view;
	}
	
}
