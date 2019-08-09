package com.xitu.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xitu.app.common.R;
import com.xitu.app.common.request.SaveReportRequest;
import com.xitu.app.mapper.ItemMapper;
import com.xitu.app.model.Item;
import com.xitu.app.model.Report;
import com.xitu.app.repository.ReportRepository;
import com.xitu.app.service.es.ReportService;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ReportController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
	
	@Autowired
    private ReportRepository reportRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private ItemMapper itemMapper;
	
	@Autowired
    private ReportService reportService;
	
	@Value("${files.path}")
	private String fileRootPath;
	
	@PostMapping(value = "report/save")
	public String saveReport(SaveReportRequest request,Model model) {
		
		Report report = new Report();
		BeanUtil.copyBean(request, report);
		if(report.getId() == null || "".equals(report.getId())) {
			report.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
		}
		report.setNow(System.currentTimeMillis());
		report.setYear(request.getPtime().substring(0, 4));
		report.setSubject(request.getInfo());
		
		List<String> authors = new ArrayList<String>();
		if (request.getAuthor() != null && !request.getAuthor().equals("")) {
			if(request.getAuthor().contains(";")){
				String[] s = request.getAuthor().split(";");
				for(String id:s){
					authors.add(id);
				}
			}else if (request.getAuthor().contains("；")) {
				String[] s = request.getAuthor().split("；");
				for(String id:s){
					authors.add(id);
				}
			}else {
				authors.add(request.getAuthor());
			}
			
			
		}
		report.setAuthor(authors);
		reportRepository.save(report);
		return "redirect:/report/list";
	}
	
	@GetMapping(value = "report/delete")
	public String deleteReport(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			reportRepository.deleteById(id);
		}
		
		return "redirect:/report/list";
	}
	
	@GetMapping(value = "report/get")
	public String getReport(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="id") String id,
			@RequestParam(required=false,value="disable") String disable, Model model) {
		Report report = new Report();
		if(id != null) {
			report = reportRepository.findById(id).get();
			model.addAttribute("pdfId", "".equals(report.getPdf())?null:report.getPdf());
			model.addAttribute("pdfFileName", "".equals(report.getPdfFileName())?null:report.getPdfFileName());
			model.addAttribute("pdfSize", "".equals(report.getPdfSize())?null:report.getPdfSize());
			model.addAttribute("frontendId", "".equals(report.getFrontend())?null:report.getFrontend());
			model.addAttribute("frontendFileName", "".equals(report.getFrontendFileName())?null:report.getFrontendFileName());
			model.addAttribute("frontendSize", "".equals(report.getFrontendSize())?null:report.getFrontendSize());
		}
		model.addAttribute("report", report);
		if(disable !=null) {
			model.addAttribute("disable", "0");
		}else {
			model.addAttribute("disable", "1");
		}
		
		Item item = itemMapper.selectItemByService("xmfl");
		List<String> items = new ArrayList<String>();
		for(String s: item.getItem().split(";")) {
			items.add(s);
		}
		model.addAttribute("items", items);
		
		String view = "qiyezhikufenxibaogaoxiangqing";
		if(front != null) {
			view = "qiyezhikufenxibaogaoxiangqingqiantai";
		}
//		if(esTemplate.indexExists(Report.class)) {
//			// 分页参数
//			Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", project.getName()));
////			if(entrust != null) {
////				String[] entrusts = entrust.split("-");
////				queryBuilder.filter(QueryBuilders.termsQuery("entrust", entrusts));
////			}
//			
//			// 分数，并自动按分排序
//			FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//
//			// 分数、分页
//			SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//					.withQuery(functionScoreQueryBuilder).build();
//
//			Page<Project> searchPageResults = projectRepository.search(searchQuery);
//			projectList = searchPageResults.getContent();
//			totalCount = esTemplate.count(searchQuery, Project.class);
//			
//			
//			totalPages = Math.round(totalCount/pageSize);
//			model.addAttribute("projectList", projectList);
//			view = "result-xmCon";
//		}
		return view;
	}
	
//	@GetMapping(value = "report/list")
//	public String projects(@RequestParam(required=false,value="q") String q,
//			@RequestParam(required=false,value="front") String front,
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
//		List<Report> reportList = new ArrayList<Report>();
//		String view = "qiyezhikufenxibaogaoliebiao";
//		if (front != null) {
//			view = "qiyezhikufenxibaogaoqiantai";
//		}
//		if(esTemplate.indexExists(Report.class)) {
//			if(q == null || q.equals("")) {
//				totalCount = reportRepository.count();
//				if(totalCount >0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Report> reportsPage = reportRepository.search(searchQuery);
//					reportList = reportsPage.getContent();
//					if(totalCount % pageSize == 0){
//						totalPages = totalCount/pageSize;
//					}else{
//						totalPages = totalCount/pageSize + 1;
//					}
//				}
//			}else {
//				// 分页参数
//				Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q));
////				if(entrust != null) {
////					String[] entrusts = entrust.split("-");
////					queryBuilder.filter(QueryBuilders.termsQuery("entrust", entrusts));
////				}
//				
//				
//				// 分数，并自动按分排序
//				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.weightFactorFunction(1000));
//
//				// 分数、分页
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//						.withQuery(functionScoreQueryBuilder).build();
//
//				Page<Report> searchPageResults = reportRepository.search(searchQuery);
//				reportList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Report.class);
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
//				
//			}
//		}
//		//totalPages = Double.valueOf(Math.ceil(Double.valueOf(totalCount)/Double.valueOf(pageSize))).intValue();
//		model.addAttribute("reportList", reportList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("name", q);
//			
//		return view;
//	}
	
	@GetMapping(value = "report/list")
	public String projects(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="unit") String unit,
			@RequestParam(required=false,value="year") String year,
			@RequestParam(required=false,value="type") String type,
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
		int i = 6;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表人才；6代表报告
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		reportService.execute(pageIndex, pageSize, i,q,unit,year,type);
		ThreadLocalUtil.remove();
		String view = "qiyezhikufenxibaogaoliebiao";
		if (front != null) {
			view = "qiyezhikufenxibaogaoqiantai";
		}
		return view;
	}
	
}
