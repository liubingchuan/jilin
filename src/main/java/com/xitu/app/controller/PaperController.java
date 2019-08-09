package com.xitu.app.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.xitu.app.common.R;
import com.xitu.app.common.request.SavePaperRequest;
import com.xitu.app.mapper.PatentMapper;
import com.xitu.app.model.AggVO;
import com.xitu.app.model.Paper;
import com.xitu.app.model.PaperVO;
import com.xitu.app.model.Project;
import com.xitu.app.repository.PaperRepository;
import com.xitu.app.service.es.PaperService;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class PaperController {

	private static final Logger logger = LoggerFactory.getLogger(PaperController.class);
	
	@Autowired
    private PaperRepository paperRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
	private PaperService paperService;
	
	
	@GetMapping(value = "paper/get")
	public String getPaper(@RequestParam(required=false,value="id") String id,@RequestParam(required=false,value="q") String q, Model model) {
		Paper paper = new Paper();
		if(id != null) {
			paper = paperRepository.findById(id).get();
		}
		if(paper != null) {
			List<String> keywords = paper.getKeywords();
			if (keywords != null && keywords.size() > 0) {
				String str = StringUtils.join(keywords, ",");
		        System.out.println("第一种方法" + str);
		        model.addAttribute("keywords", str);
			}
			model.addAttribute("title", paper.getTitle());
		}
		model.addAttribute("paper", paper);
		model.addAttribute("query", q);
		model.addAttribute("id", id);
		Integer pageIndex = 0;
		Integer pageSize = 10;
		long totalCount = 0L;
		long totalPages = 0L;
		List<Paper> paperList = new ArrayList<Paper>();
		if(esTemplate.indexExists(Paper.class) && paper.getTitle() != null) {
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

			Page<Paper> searchPageResults = paperRepository.search(searchQuery);
			paperList = searchPageResults.getContent();
			totalCount = esTemplate.count(searchQuery, Paper.class);
			
			
			totalPages = Math.round(totalCount/pageSize);
			if(paperList.size()<2) {
				paperList = new ArrayList<Paper>();
			}else {
				paperList = paperList.subList(1, paperList.size());
			}
			model.addAttribute("paperList", paperList);
		}
		return "result-wxCon";
	}
	
//	@GetMapping(value = "paper/list")
//	public String projects(@RequestParam(required=false,value="q") String q,
//			@RequestParam(required=false,value="year") String year,
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
//		List<Paper> paperList = new ArrayList<Paper>();
//		String view = "result-wx";
//		if(esTemplate.indexExists(Paper.class)) {
//			if(q == null) {
//				totalCount = paperRepository.count();
//				if(totalCount >0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Paper> projectsPage = paperRepository.search(searchQuery);
//					paperList = projectsPage.getContent();
//				}
//			}else {
//				// 分页参数
//				Pageable pageable = new PageRequest(pageIndex, pageSize);
//
//				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("title", q)).should(QueryBuilders.matchPhraseQuery("subject", q));
//				if(year != null) {
//					String[] years = year.split("-");
//					queryBuilder.filter(QueryBuilders.termsQuery("year", years));
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
//				Page<Paper> searchPageResults = paperRepository.search(searchQuery);
//				paperList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Paper.class);
//				
//				
//				BoolQueryBuilder queryBuilderAgg = QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery("title", q));
//				FunctionScoreQueryBuilder functionScoreQueryBuilderAgg = QueryBuilders.functionScoreQuery(queryBuilderAgg, ScoreFunctionBuilders.weightFactorFunction(1000));
//				List<String> pList=new ArrayList<>();
//				SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
//						.withQuery(functionScoreQueryBuilderAgg)
//						.withSearchType(SearchType.QUERY_THEN_FETCH)
//						.withIndices("paper").withTypes("pr")
//						.addAggregation(AggregationBuilders.terms("agyear").field("year").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("aginstitution").field("institution").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agjournal").field("journal").order(Terms.Order.count(false)).size(10))
//						.addAggregation(AggregationBuilders.terms("agauthor").field("author").order(Terms.Order.count(false)).size(10))
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
//					StringTerms institutionTerms = (StringTerms) aggregations.asMap().get("aginstitution");
//					Iterator<Bucket> institutionbit = institutionTerms.getBuckets().iterator();
//					Map<String, Long> institutionMap = new HashMap<String, Long>();
//					while(institutionbit.hasNext()) {
//						Bucket institutionBucket = institutionbit.next();
//						institutionMap.put(institutionBucket.getKey().toString(), Long.valueOf(institutionBucket.getDocCount()));
//					}
//					model.addAttribute("aginstitution", institutionMap);
//					
//					StringTerms journalTerms = (StringTerms) aggregations.asMap().get("agjournal");
//					Iterator<Bucket> journalbit = journalTerms.getBuckets().iterator();
//					Map<String, Long> journalMap = new HashMap<String, Long>();
//					while(journalbit.hasNext()) {
//						Bucket journalBucket = journalbit.next();
//						journalMap.put(journalBucket.getKey().toString(), Long.valueOf(journalBucket.getDocCount()));
//					}
//					model.addAttribute("agjournal", journalMap);
//					
//					StringTerms authorTerms = (StringTerms) aggregations.asMap().get("agauthor");
//					Iterator<Bucket> authorbit = authorTerms.getBuckets().iterator();
//					Map<String, Long> authorMap = new HashMap<String, Long>();
//					while(authorbit.hasNext()) {
//						Bucket authorBucket = authorbit.next();
//						authorMap.put(authorBucket.getKey().toString(), Long.valueOf(authorBucket.getDocCount()));
//					}
//					model.addAttribute("agauthor", authorMap);
//					
//				}
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
//				
//				
//			}
//		}
//		model.addAttribute("paperList", paperList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("query", q);
//			
//		return view;
//	}
	@GetMapping(value = "paper/list")
	public String projects(@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="author") String author,
			@RequestParam(required=false,value="institution") String institution,
			@RequestParam(required=false,value="journal") String journal,
			@RequestParam(required=false,value="year") String year,
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
		int i = 1;//0代表专利；1代表论文；2代表项目；3代表监测
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		paperService.execute(pageIndex, pageSize, i,q,author,institution,journal,year);
		ThreadLocalUtil.remove();
		String view = "result-wx";	
		return view;
	}
	 /**
     * 实现文件上传
     * */
    @RequestMapping(value="paper/fileUpload",method = RequestMethod.POST)
    @ResponseBody 
    public R fileUpload(MultipartFile file){
    	
        if(file==null || file.isEmpty()){
            return R.error();
        }
        String fileName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String path = System.getProperty("user.dir") + "/src/main/resources/static/images" ;
        File dest = new File(path + File.separator + uuid + "_" + fileName);
        if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest); //保存文件
            return R.ok().put("img", "/images/" + uuid+"_"+fileName);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return R.error();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return R.error();
        }
    }
    
    
//    /**
//     * 文件解析
//     * */
//    @GetMapping(value="paper/import")
//    @ResponseBody 
//    public R importJson(){
//    	try {
//    		
//    		StringBuffer buffer = new StringBuffer();
//    		/** CSV文件列分隔符 */
//    	    String CSV_COLUMN_SEPARATOR = ",";
//
//    	    /** CSV文件列分隔符 */
//    	    String CSV_RN = "\r\n";
//    	    buffer.append("author").append(CSV_COLUMN_SEPARATOR).append("org").append(CSV_RN);
//			String filePath = String.format("/Users/liubingchuan/git/bdm/meta/xitu.json");
//			File file = new File(filePath);
//			JSONReader reader=new JSONReader(new FileReader(file));
//			reader.startArray();
//			List<Paper> papers = new LinkedList<Paper>();
//			int i=1;
//			String[] titles = new String[]{"author","org"};
//			List<String> orgs= new ArrayList<String>();
//			while (reader.hasNext()) {
//				
//	            PaperVO vo = reader.readObject(PaperVO.class);
////	            Paper paper = new Paper();
////	            paper.setId(vo.get_id());
////	            paper.setNow(System.currentTimeMillis());
////	            List<String> links = new ArrayList<String>();
////	            links.add(vo.getUrl());
////	            paper.setLink(links);
////	            paper.setTitle(vo.getTitle());
////	            paper.setIssue(vo.getOnlineDate());
//	            
////	            String year = "".equals(vo.getOnlineDate())?"预发布":vo.getOnlineDate().substring(0, 4);
////	            paper.setYear(year);
////	            List<String> authors = vo.getAuthor();
////	            List<String> paperAuthors = new ArrayList<String>();
////	            boolean isAuthor = false;
////	            for(String author: authors) {
////	            	if(isAuthor) {
////	            		paperAuthors.add(author);
////	            	}
////	            	if("|".equals(author)){
////	            		isAuthor = true;
////	            	}
////	            }
////	            paper.setAuthor(paperAuthors);
////	            paper.setSubject(vo.getAbc());
////	            List<String> keywords = new ArrayList<String>();
////	            for(String keyword: vo.getKeyWord()) {
////	            	keywords.add(keyword);
////	            }
////	            paper.setKeywords(keywords);
////	            List<String> orgs= new ArrayList<String>();
//	            
//	            for(String org : vo.getOrganization()) {
//	            	if(org.contains(",")) {
//	            		String[] orgArray = org.split(",");
//	            		for(String newOrg : orgArray) {
//	            			if(newOrg.contains("!")) {
//	            				if(!orgs.contains(newOrg.split("!")[0])){
//	            					orgs.add(newOrg.split("!")[0]);
//	            				}
//	            			}else {
//	            				if(!orgs.contains(newOrg)){
//	            					orgs.add(newOrg);
//	            				}
//	            			}
//	            		}
//	            	}else {
//	            		if(!orgs.contains(org)) {
//	            			orgs.add(org);
//	            		}
//	            	}
//	            }
//	            
////	            for(String auth: paperAuthors) {
////	            	buffer.append(auth).append(CSV_COLUMN_SEPARATOR);
////	            	for(String org :orgs) {
////	            		buffer.append(org).append(";");
////	            	}
////	            	buffer.deleteCharAt(buffer.length()-1);
////	            	buffer.append(CSV_RN);
////	            }
////	            paper.setInstitution(orgs);
////	            paper.setJournal(vo.getJournal());
////	            papers.add(paper);
//	            System.out.println("当前id----》" + i);
//	            i++;
//	        }
//			
//			try{
//				for(String s : orgs) {
//					buffer.append(s).append(CSV_RN);
//				}
//			      String data = buffer.toString();
//
//			      File outFile =new File("/Users/liubingchuan/git/xitu/org.json");
//
//			      //if file doesnt exists, then create it
//			      if(!outFile.exists()){
//			    	  outFile.createNewFile();
//			      }
//
//			      //true = append file
//			      FileWriter fileWritter = new FileWriter(outFile.getName(),true);
//			      fileWritter.write(data);
//			      fileWritter.close();
//
//			      System.out.println("Done");
//
//			     }catch(IOException e){
//			      e.printStackTrace();
//			 }
//			reader.endArray();
//	        reader.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    	return R.ok();
//    }
    /**
     * 文件解析
     * */
    @GetMapping(value="paper/import")
    @ResponseBody 
    public R importJson(){
    	try {
    		
    		StringBuffer buffer = new StringBuffer();
    		/** CSV文件列分隔符 */
    		String CSV_COLUMN_SEPARATOR = ",";
    		
    		/** CSV文件列分隔符 */
    		String CSV_RN = "\r\n";
    		buffer.append("author").append(CSV_COLUMN_SEPARATOR).append("org").append(CSV_RN);
    		String filePath = String.format("/Users/liubingchuan/git/bdm/meta/xitu.json");
    		File file = new File(filePath);
    		JSONReader reader=new JSONReader(new FileReader(file));
    		reader.startArray();
    		List<Paper> papers = new LinkedList<Paper>();
    		int i=1;
//    		String[] titles = new String[]{"author","org"};
//    		List<String> orgs= new ArrayList<String>();
    		List<String> totalOrgs = new LinkedList<String>();
    		while (reader.hasNext()) {
    			
    			PaperVO vo = reader.readObject(PaperVO.class);
//	            Paper paper = new Paper();
//	            paper.setId(vo.get_id());
//	            paper.setNow(System.currentTimeMillis());
//	            List<String> links = new ArrayList<String>();
//	            links.add(vo.getUrl());
//	            paper.setLink(links);
//	            paper.setTitle(vo.getTitle());
//	            paper.setIssue(vo.getOnlineDate());
    			
//	            String year = "".equals(vo.getOnlineDate())?"预发布":vo.getOnlineDate().substring(0, 4);
//	            paper.setYear(year);
	            List<String> authors = vo.getAuthor();
	            List<String> paperAuthors = new ArrayList<String>();
	            boolean isAuthor = false;
	            if(i==42){
	            	System.out.println();
	            }
	            for(String author: authors) {
	            	if(author.contains("谭培功")){
	            		System.out.println();
	            	}
	            	if(isAuthor) {
	            		paperAuthors.add(author);
	            	}
	            	if("|".equals(author)){
	            		isAuthor = true;
	            	}
	            }
//	            paper.setAuthor(paperAuthors);
//	            paper.setSubject(vo.getAbc());
//	            List<String> keywords = new ArrayList<String>();
//	            for(String keyword: vo.getKeyWord()) {
//	            	keywords.add(keyword);
//	            }
//	            paper.setKeywords(keywords);
	            List<String> orgs= new ArrayList<String>();
    			
    			for(String org : vo.getOrganization()) {
    				org = org.trim();
    				if(org.contains(",")) {
    					String[] orgArray = org.split(",");
    					//trim一下
    					String[] newArray = new String[orgArray.length];
    					for(int n=0;n<orgArray.length;n++) {
    						newArray[n] = orgArray[n].trim();
    					}
    					// 只保留子串
    					newArray = subString(newArray);
    					
    					for(String newOrg : newArray) {
    						String s = filter(newOrg);
    						if(s.length()==0){
    							continue;
    						}
    						if(s.contains("!")) {
    							String orgName = s.split("!")[0];
    							orgName = orgName.replaceAll(";", "");
    							if(!orgs.contains(orgName)){
    								orgs.add(orgName);
    							}
    						}else {
    							s = s.replaceAll(";", "");
    							if(!orgs.contains(s)){
    								orgs.add(s);
    							}
    						}
    						if(s.split(" ").length>1) {
    							s = s.split(" ")[0];
    						}
    						if(s.contains("教授")){
    							s = "";
    						}
    					}
    				}else {
    					org = org.replaceAll(";", "");
    					if(org.split(" ").length>1) {
    						org = org.split(" ")[0];
    					}
    					if(org.contains("教授")){
    						org = "";
    						continue;
    					}
    					if(!orgs.contains(org)) {
    						orgs.add(org);
    					}
    				}
    			}
    			
	            for(String auth: paperAuthors) {
	            	buffer.append(auth).append(CSV_COLUMN_SEPARATOR);
	            	for(String org :orgs) {
	            		buffer.append(org).append(";");
	            	}
	            	buffer.deleteCharAt(buffer.length()-1);
	            	buffer.append(CSV_RN);
	            }
//	            paper.setInstitution(orgs);
//	            paper.setJournal(vo.getJournal());
//	            papers.add(paper);
    			System.out.println("当前id----》" + i);
    			i++;
    			for(String org: orgs) {
    				if(!totalOrgs.contains(org)) {
    					totalOrgs.add(org);
    				}
    			}
    		}
    		
    		try{
//    			for(String s : orgs) {
//    				buffer.append(s).append(CSV_RN);
//    			}
    			String data = buffer.toString();
    			
    			File aoFile =new File("/Users/liubingchuan/git/xitu/ao.csv");
    			
    			//if file doesnt exists, then create it
    			if(!aoFile.exists()){
    				aoFile.createNewFile();
    			}
    			
    			//true = append file
    			FileWriter fileWritter = new FileWriter(aoFile.getName(),true);
    			fileWritter.write(data);
    			fileWritter.close();
    			
    			StringBuffer sb = new StringBuffer();
    			for(String s: totalOrgs) {
    				sb.append(s).append(CSV_RN);
    			}
    			String orgData = sb.toString();
    			
    			File orgFile =new File("/Users/liubingchuan/git/xitu/organization.csv");
    			
    			//if file doesnt exists, then create it
    			if(!orgFile.exists()){
    				orgFile.createNewFile();
    			}
    			
    			//true = append file
    			FileWriter orgFileWritter = new FileWriter(orgFile.getName(),true);
    			orgFileWritter.write(orgData);
    			orgFileWritter.close();
    			
    			System.out.println("Done");
    			
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    		reader.endArray();
    		reader.close();
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return R.ok();
    }
    
    
    /**
     * 文件合并
     * */
    @GetMapping(value="paper/merge")
    @ResponseBody 
    public R merge(){
//    	try {
    		
    		try {
    			StringBuffer buffer = new StringBuffer();
    			/** CSV文件列分隔符 */
        		String CSV_COLUMN_SEPARATOR = ",";
        		
        		/** CSV文件列分隔符 */
        		String CSV_RN = "\r\n";
        		buffer.append("author").append(CSV_COLUMN_SEPARATOR).append("org").append(CSV_RN);
    			
    			String filePath = String.format("/Users/liubingchuan/git/xitu/ao.csv");
				List<String> scanList= readFile(filePath);
				Map<String,String> paperMap = new HashMap<String,String>();
				for(int i=1;i<scanList.size();i++) {
					String content = scanList.get(i);
					if(content.contains(",")){
						String[] ao = content.split(",");
						paperMap.put(ao[0], ao[1]);
					}
				}
				
				SAXReader reader = new SAXReader();
		        File file = new File("/Users/liubingchuan/git/xitu/xitu_patent.xml");
		        Document document = reader.read(file);
		        Element root = document.getRootElement();
		        List<Element> childElements = root.elements();
		        for (Element child : childElements) {
		            //未知属性名情况下
		            /*List<Attribute> attributeList = child.attributes();
		            for (Attribute attr : attributeList) {
		                System.out.println(attr.getName() + ": " + attr.getValue());
		            }*/
		              
		            //已知属性名情况下
//		            System.out.println("person: " + child.attributeValue("person"));
		              
		            //未知子元素名情况下
		            /*List<Element> elementList = child.elements();
		            for (Element ele : elementList) {
		                System.out.println(ele.getName() + ": " + ele.getText());
		            }
		            System.out.println();*/
		              
		            //已知子元素名的情况下
//		            System.out.println("person" + child.elementText("person"));
//		            System.out.println("creator" + child.elementText("creator"));
		            
		            String creator = child.elementText("creator");
		            String person = child.elementText("person");
		            String[] authors = creator.split(";");
		            String[] orgs = person.split(";");
		            for(String author: authors) {
		            	String exsitOrg = paperMap.get(author);
		            	boolean crossing = false;
		            	for(String org:orgs) {
		            		if(paperMap.containsKey(author)) {
		            			if(exsitOrg.contains(org)) {
		            				crossing = true;
		            				break;
		            			}
		            		}
		            	}
		            	if(!crossing) {
		            		buffer.append(author).append(CSV_COLUMN_SEPARATOR).append(person).append(CSV_RN);
		            	}
		            }
		            //这行是为了格式化美观而存在
		            System.out.println();
		        }
		        
		        String orgData = buffer.toString();
    			
    			File orgFile =new File("/Users/liubingchuan/git/xitu/patent_organization.csv");
    			
    			//if file doesnt exists, then create it
    			if(!orgFile.exists()){
    				orgFile.createNewFile();
    			}
    			
    			//true = append file
    			FileWriter orgFileWritter = new FileWriter(orgFile.getName(),true);
    			orgFileWritter.write(orgData);
    			orgFileWritter.close();
				
			} catch (IOException | DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//    		File file = new File(filePath);
//    		JSONReader reader=new JSONReader(new FileReader(file));
//    		reader.startArray();
//    		List<Paper> papers = new LinkedList<Paper>();
//    		int i=1;
////    		String[] titles = new String[]{"author","org"};
////    		List<String> orgs= new ArrayList<String>();
//    		List<String> totalOrgs = new LinkedList<String>();
//    		while (reader.hasNext()) {
//    			
//    			PaperVO vo = reader.readObject(PaperVO.class);
////	            Paper paper = new Paper();
////	            paper.setId(vo.get_id());
////	            paper.setNow(System.currentTimeMillis());
////	            List<String> links = new ArrayList<String>();
////	            links.add(vo.getUrl());
////	            paper.setLink(links);
////	            paper.setTitle(vo.getTitle());
////	            paper.setIssue(vo.getOnlineDate());
//    			
////	            String year = "".equals(vo.getOnlineDate())?"预发布":vo.getOnlineDate().substring(0, 4);
////	            paper.setYear(year);
//    			List<String> authors = vo.getAuthor();
//    			List<String> paperAuthors = new ArrayList<String>();
//    			boolean isAuthor = false;
//    			if(i==42){
//    				System.out.println();
//    			}
//    			for(String author: authors) {
//    				if(author.contains("谭培功")){
//    					System.out.println();
//    				}
//    				if(isAuthor) {
//    					paperAuthors.add(author);
//    				}
//    				if("|".equals(author)){
//    					isAuthor = true;
//    				}
//    			}
////	            paper.setAuthor(paperAuthors);
////	            paper.setSubject(vo.getAbc());
////	            List<String> keywords = new ArrayList<String>();
////	            for(String keyword: vo.getKeyWord()) {
////	            	keywords.add(keyword);
////	            }
////	            paper.setKeywords(keywords);
//    			List<String> orgs= new ArrayList<String>();
//    			
//    			for(String org : vo.getOrganization()) {
//    				org = org.trim();
//    				if(org.contains(",")) {
//    					String[] orgArray = org.split(",");
//    					//trim一下
//    					String[] newArray = new String[orgArray.length];
//    					for(int n=0;n<orgArray.length;n++) {
//    						newArray[n] = orgArray[n].trim();
//    					}
//    					// 只保留子串
//    					newArray = subString(newArray);
//    					
//    					for(String newOrg : newArray) {
//    						String s = filter(newOrg);
//    						if(s.length()==0){
//    							continue;
//    						}
//    						if(s.contains("!")) {
//    							String orgName = s.split("!")[0];
//    							orgName = orgName.replaceAll(";", "");
//    							if(!orgs.contains(orgName)){
//    								orgs.add(orgName);
//    							}
//    						}else {
//    							s = s.replaceAll(";", "");
//    							if(!orgs.contains(s)){
//    								orgs.add(s);
//    							}
//    						}
//    						if(s.split(" ").length>1) {
//    							s = s.split(" ")[0];
//    						}
//    						if(s.contains("教授")){
//    							s = "";
//    						}
//    					}
//    				}else {
//    					org = org.replaceAll(";", "");
//    					if(org.split(" ").length>1) {
//    						org = org.split(" ")[0];
//    					}
//    					if(org.contains("教授")){
//    						org = "";
//    						continue;
//    					}
//    					if(!orgs.contains(org)) {
//    						orgs.add(org);
//    					}
//    				}
//    			}
//    			
//    			for(String auth: paperAuthors) {
//    				buffer.append(auth).append(CSV_COLUMN_SEPARATOR);
//    				for(String org :orgs) {
//    					buffer.append(org).append(";");
//    				}
//    				buffer.deleteCharAt(buffer.length()-1);
//    				buffer.append(CSV_RN);
//    			}
////	            paper.setInstitution(orgs);
////	            paper.setJournal(vo.getJournal());
////	            papers.add(paper);
//    			System.out.println("当前id----》" + i);
//    			i++;
//    			for(String org: orgs) {
//    				if(!totalOrgs.contains(org)) {
//    					totalOrgs.add(org);
//    				}
//    			}
//    		}
//    		
//    		try{
////    			for(String s : orgs) {
////    				buffer.append(s).append(CSV_RN);
////    			}
//    			String data = buffer.toString();
//    			
//    			File aoFile =new File("/Users/liubingchuan/git/xitu/ao.csv");
//    			
//    			//if file doesnt exists, then create it
//    			if(!aoFile.exists()){
//    				aoFile.createNewFile();
//    			}
//    			
//    			//true = append file
//    			FileWriter fileWritter = new FileWriter(aoFile.getName(),true);
//    			fileWritter.write(data);
//    			fileWritter.close();
//    			
//    			StringBuffer sb = new StringBuffer();
//    			for(String s: totalOrgs) {
//    				sb.append(s).append(CSV_RN);
//    			}
//    			String orgData = sb.toString();
//    			
//    			File orgFile =new File("/Users/liubingchuan/git/xitu/organization.csv");
//    			
//    			//if file doesnt exists, then create it
//    			if(!orgFile.exists()){
//    				orgFile.createNewFile();
//    			}
//    			
//    			//true = append file
//    			FileWriter orgFileWritter = new FileWriter(orgFile.getName(),true);
//    			orgFileWritter.write(orgData);
//    			orgFileWritter.close();
//    			
//    			System.out.println("Done");
//    			
//    		}catch(IOException e){
//    			e.printStackTrace();
//    		}
//    		reader.endArray();
//    		reader.close();
//    		
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    	}
    	return R.ok();
    }
    
    /**
     * 机构合并
     * */
    @GetMapping(value="org/merge")
    @ResponseBody 
    public R orgMerge(){
//    	try {
    	JSONReader reader = null;
    	try {
    		StringBuffer buffer = new StringBuffer();
    		/** CSV文件列分隔符 */
    		String CSV_COLUMN_SEPARATOR = ",";
    		
    		/** CSV文件列分隔符 */
    		String CSV_RN = "\r\n";
    		buffer.append("org").append(CSV_RN);
    		
    		String filePath = String.format("/Users/liubingchuan/git/xitu/organization.csv");
    		List<String> scanList= readFile(filePath);
    		
    		String filePath2 = String.format("/Users/liubingchuan/git/xitu/patent_applyer.json");
    		File file = new File(filePath2);
    		reader=new JSONReader(new FileReader(file));
    		reader.startArray();
    		int i=1;
    		while (reader.hasNext()) {
    			System.out.println(i);
//    			if (i==19652){
//    				System.out.println();
//    			}
    			AggVO vo = reader.readObject(AggVO.class);
    			System.out.println(vo.getKey());
    			System.out.println(vo.getDoc_count());
    			if(!scanList.contains(vo.getKey())){
    				scanList.add(vo.getKey());
    			}
    			i++;
    		}
    		for(String s: scanList) {
    			buffer.append(s).append(CSV_RN);
    		}
    		String orgData = buffer.toString();
    		
    		File orgFile =new File("/Users/liubingchuan/git/xitu/total_organization.csv");
    		
    		//if file doesnt exists, then create it
    		if(!orgFile.exists()){
    			orgFile.createNewFile();
    		}
    		
    		//true = append file
    		FileWriter orgFileWritter = new FileWriter(orgFile.getName(),true);
    		orgFileWritter.write(orgData);
    		orgFileWritter.close();
    		if(reader != null) {
    			reader.endArray();
    			reader.close();
    		}
    		
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		if(reader != null) {
    			reader.endArray();
    			reader.close();
    		}
    	}
    	return R.ok();
    }
    
    public List<String> readFile(String path) throws IOException {
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new LinkedList<String>();
        FileInputStream fis = new FileInputStream(path);
        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
            if (line.lastIndexOf("---") < 0) {
                list.add(line);
            }
        }
        br.close();
        isr.close();
        fis.close();
        return list;
    }
    
    //只保留子串
    public String[] subString(String[] s) {
    	int m = 0;
    	List<Integer> k = new ArrayList<Integer>(s.length);
    	for(int i=0;i<s.length;i++) {
    		if(s[i].length()<=3) {
    			k.add(i);
    			continue;
    		}
    		if(k.contains(i)) {
    			continue;
    		}
    		for(int j=0;j<s.length;j++) {
    			if(i==j || k.contains(i)) {
    				continue;
    			}
    			if(s[j].contains(s[i])) {
    				if(!k.contains(j)){
    					k.add(j);
    					m++;
    				}
    			}
    		}
    	}
    	if(m>0) {
    		List<String> newList = new LinkedList<String>();
    		for(int i=0;i<s.length;i++) {
    			if(!k.contains(i)) {
    				newList.add(s[i]);
    			}
    		}
    		String[] array = new String[s.length-m];
    		return newList.toArray(array);
    	}
    	return s;
    }
    
    //6位数字连续的，独立串、前方连带其他字符的串、前方有空格的串，去除该子串以后，判断前半部分是否含有空格，如果含有空格则用空格分开，取第一部分，备选名字要大于等于三个字
    public String filter(String s) {
    	if(s==null) {
    		s="";
    	}
    	if(s.length()>=6) {
    		int start = s.length()-6;
    		Pattern pattern = Pattern.compile("\\d{6}");
    		boolean matches = false;
    		while(start>=0 && (!matches)){
    			matches = pattern.matcher(s.substring(start, start+6)).matches();
    			if(!matches) {
    				start--;
    			}else {
    				while(start != 0 && (' ' != s.charAt(start-1))) {
    					start--;
    					if(start>=1 && '!' == s.charAt(start-1)){
    						break;
    					}
    				}
    			}
    		}
    		
    		if(matches) {
    			if(start<=0) {
    				s = "";
    			}else {
    				s = s.substring(0,start);
    				s = s.split(" ")[0];
    			}
    			
    		}
    		
    	}
    	
    	if(s.contains("研究生") || s.contains("博士生") || s.length()<=3) {
    		s = "";
    	}
    	return s;
    }
    
    
    /**
     * 文件解析
     * */
    @GetMapping(value="paper/extract")
    @ResponseBody 
    public R extract(){
    	SearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
				.withSearchType(SearchType.QUERY_THEN_FETCH)
				.withIndices("paper").withTypes("pr")
				.addAggregation(AggregationBuilders.terms("aginstitution").field("institution").order(Terms.Order.count(false)).size(60))
//				.addAggregation(AggregationBuilders.terms("agauthor").field("author").order(Terms.Order.count(false)).size(2))
				.build();
		Aggregations aggregations = esTemplate.query(nativeSearchQueryBuilder, new ResultsExtractor<Aggregations>() {
	        @Override
	        public Aggregations extract(SearchResponse response) {
	            return response.getAggregations();
	        }
	    });
		
		if(aggregations != null) {
			
			StringTerms institutionTerms = (StringTerms) aggregations.asMap().get("aginstitution");
			Iterator<Bucket> institutionbit = institutionTerms.getBuckets().iterator();
			Map<String, Long> institutionMap = new HashMap<String, Long>();
			while(institutionbit.hasNext()) {
				Bucket institutionBucket = institutionbit.next();
				institutionMap.put(institutionBucket.getKey().toString(), Long.valueOf(institutionBucket.getDocCount()));
			}
			System.out.println(institutionMap.size());
			
			
//			StringTerms authorTerms = (StringTerms) aggregations.asMap().get("agauthor");
//			Iterator<Bucket> authorbit = authorTerms.getBuckets().iterator();
//			Map<String, Long> authorMap = new HashMap<String, Long>();
//			while(authorbit.hasNext()) {
//				Bucket authorBucket = authorbit.next();
//				authorMap.put(authorBucket.getKey().toString(), Long.valueOf(authorBucket.getDocCount()));
//			}
			
		}
    	return R.ok();
    }
    @ResponseBody
	@RequestMapping(value = "paper/paperInsList", method = RequestMethod.POST,consumes = "application/json")
	public R expertInsList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		//rs.put("list", sources);
		//rs.put("totalPages", totalPages);
		//rs.put("totalCount", totalCount);
		rs = paperService.executeIns(insname.getString("insname"),pageIndex, pageSize, "institution",i);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
    @ResponseBody
	@RequestMapping(value = "paper/paperExpList", method = RequestMethod.POST,consumes = "application/json")
	public R expertpaperList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		rs = paperService.executeIns(insname.getString("insname"),pageIndex, pageSize, "author",i);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
    
    @ResponseBody
	@RequestMapping(value = "paper/xiangguanpaperList", method = RequestMethod.POST,consumes = "application/json")
	public R xiangguanpaperList(@RequestBody JSONObject insname) {
    	int pageSize = 10;
//		if(pageIndex == null) {
//		   pageIndex = 0;
//		}
    	int pageIndex = (int) insname.get("pageIndex");
    	String query = (String) insname.get("query");
    	String id=(String) insname.get("id");
    	String keywords=(String) insname.get("keywords");
    	String title=(String) insname.get("title");
    	List<String> ss = new ArrayList<String>();
    	if (keywords != null && keywords.length() >0) {
			if (keywords.contains(",")) {
				for(String key : keywords.split(",")){
					ss.add(key);
				}
			}else {
				ss.add(keywords);
			}
    	}else if (query != null && query.length() > 0 ) {
    		ss.add(query);
		}else {
			ss.add(title);
		}
		int i = 1;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		rs = paperService.executeXiangguan(pageIndex, pageSize, i,id,ss);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount")).put("pageIndex", pageIndex);
    }
	
}
