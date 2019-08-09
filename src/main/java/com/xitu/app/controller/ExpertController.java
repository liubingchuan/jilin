package com.xitu.app.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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
import com.alibaba.fastjson.JSONReader;
import com.xitu.app.common.R;
import com.xitu.app.common.request.PatentPageListRequest;
import com.xitu.app.common.request.SaveExpertRequest;
import com.xitu.app.mapper.ItemMapper;
import com.xitu.app.model.Expert;
import com.xitu.app.model.ExpertVO;
import com.xitu.app.model.Item;
import com.xitu.app.model.Org;
import com.xitu.app.model.OrgVO;
import com.xitu.app.repository.ExpertRepository;
import com.xitu.app.service.es.ExpertService;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ExpertController {

	private static final Logger logger = LoggerFactory.getLogger(ExpertController.class);
	
	@Autowired
    private ExpertRepository expertRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private ItemMapper itemMapper;
	
	@Autowired
	private ExpertService expertService;
	
	@PostMapping(value = "expert/save")
	public String saveExpert(SaveExpertRequest request,Model model) {
		
		Expert expert = new Expert();
		BeanUtil.copyBean(request, expert);
		if(expert.getId() == null || "".equals(request.getId())) {
			expert.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
		}
		List<String> areaList =new ArrayList<String>();
		List<String> dutyList =new ArrayList<String>();
		List<String> titleList =new ArrayList<String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (request.getArea() != null && !request.getArea().equals("")) {
			String[] s = request.getArea().split(",");
			for(String id:s){
				areaList.add(id);
			}
		}
		//areaList.add(request.getArea());
		dutyList.add(request.getDuty());
		titleList.add(request.getTitle());
		expert.setArea(areaList);;
		expert.setDuty(dutyList);
		expert.setResume(request.getInfo());
		expert.setNow(System.currentTimeMillis());
		expert.setCtime(df.format(new Date()));
		expert.setTitle(titleList);
		//expert.setUnit("中国科学院青岛生物能源与过程研究所");
		expert.setUnit(request.getUnit());
		List<String> alias = new ArrayList<String>();
		if (request.getAlias() != null && !request.getAlias().equals("")) {
			if(request.getAlias().contains(";")){
				String[] s = request.getAlias().split(";");
				for(String id:s){
					alias.add(id);
				}
			}else if (request.getAlias().contains("；")) {
				String[] s = request.getAlias().split("；");
				for(String id:s){
					alias.add(id);
				}
			}else {
				alias.add(request.getAlias());
			}
			
			
		}
		expert.setAlias(alias);
		expertRepository.save(expert);
		return "redirect:/expert/list";
	}
	
	
	@GetMapping(value = "expert/delete")
	public String deleteExpert(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			expertRepository.deleteById(id);
		}
		return "redirect:/expert/list";
	}
	
	
	@GetMapping(value = "expert/get")
	public String getExpert(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="disable") String disable,
			@RequestParam(required=false,value="id") String id, Model model) {
		String view = "qiyezhikuhangyerencaixiangqing";
		Expert expert = new Expert();
		if(id != null) {
			expert = expertRepository.findById(id).get();
			String name = expert.getName();
			String bieming = null;
			List<String> alias = expert.getAlias();
			if (alias != null && alias.size() > 0) {
				bieming =  StringUtils.join(alias.toArray(), ",");
			}
			if (bieming != null) {
				name = name+","+bieming;
			}
			model.addAttribute("namebieming", name);
			model.addAttribute("frontendId", "".equals(expert.getFrontend())?null:expert.getFrontend());
			model.addAttribute("frontendFileName", "".equals(expert.getFrontendFileName())?null:expert.getFrontendFileName());
			model.addAttribute("frontendSize", "".equals(expert.getFrontendSize())?null:expert.getFrontendSize());
			model.addAttribute("area", expert.getArea());
		}
		if(front != null) {
			view = "qiyezhikuhangyerencaizhuanjiaxiangqingyemian";
		}
		if(disable !=null) {
			model.addAttribute("disable", "0");
		}else {
			model.addAttribute("disable", "1");
		}
		
		Item yjlyitem = itemMapper.selectItemByService("yjly");
		List<String> yjlyitemitems = new ArrayList<String>();
		if(yjlyitem != null) {
			for(String s: yjlyitem.getItem().split(";")) {
				yjlyitemitems.add(s);
			}
		}
		model.addAttribute("yjlyitems", yjlyitemitems);
		
		Item zcitems = itemMapper.selectItemByService("zc");
		List<String> zcitemitems = new ArrayList<String>();
		if(zcitems != null) {
			for(String s: zcitems.getItem().split(";")) {
				zcitemitems.add(s);
			}
		}
		model.addAttribute("zcitems", zcitemitems);
		
		Item zwitems = itemMapper.selectItemByService("zw");
		List<String> zwitemitems = new ArrayList<String>();
		if(zwitems != null) {
			for(String s: zwitems.getItem().split(";")) {
				zwitemitems.add(s);
			}
		}
		model.addAttribute("zwitems", zwitemitems);
		
		model.addAttribute("expert", expert);
		return view;
	}
	
//	@GetMapping(value = "expert/list")
//	public String experts(@RequestParam(required=false,value="front") String front,
//			@RequestParam(required=false,value="q") String q,
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
//		List<Expert> expertList = new ArrayList<Expert>();
//		String view = "qiyezhikuhangyerencailiebiao";
//		if (front != null) {
//			view = "qiyezhikuhangyerencaiqiantai";
//		}
//		if(esTemplate.indexExists(Expert.class)) {
//			if(q == null || q.equals("")) {
//				totalCount = expertRepository.count();
//				if(totalCount > 0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Expert> expertPage = expertRepository.search(searchQuery);
//					expertList = expertPage.getContent();
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
//				// 分数，并自动按分排序
//				FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", q)), ScoreFunctionBuilders.weightFactorFunction(1000));
//
//				// 分数、分页
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//						.withQuery(functionScoreQueryBuilder).build();
//
//				Page<Expert> searchPageResults = expertRepository.search(searchQuery);
//				expertList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Expert.class);
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
////				if (front != null) {
////					view = "qiyezhikuhangyerencaiqiantai";
////				}
//			}
//		}
//		model.addAttribute("expertList", expertList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("totalPages", totalPages);
//			
//		return view;
//	}
	@GetMapping(value = "expert/list")
    public String experts(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="unit") String unit,
			@RequestParam(required=false,value="area") String area,
			@RequestParam(required=false,value="duty") String duty,
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
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		expertService.execute(pageIndex, pageSize, i,q,unit,area,duty);
		ThreadLocalUtil.remove();
		String view = "qiyezhikuhangyerencailiebiao";
		if (front != null) {
			view = "qiyezhikuhangyerencaiqiantai";
		}
		return view;
	}
	
	/**
     * 文件解析
     * */
    @GetMapping(value="expert/import")
    @ResponseBody 
    public R importJson(){
    	try {
			String filePath = String.format("/Users/liubingchuan/git/xitu/src/main/resources/xyz.json");
			File file = new File(filePath);
			JSONReader reader=new JSONReader(new FileReader(file));
			reader.startArray();
			List<Expert> experts = new LinkedList<Expert>();
			int i=1;
			while (reader.hasNext()) {
				if(i==1148){
					System.out.println();
				}
				if(experts.size()>=1000) {
					expertRepository.saveAll(experts);
					experts.clear();
				}
				ExpertVO vo = new ExpertVO();
				try{
					vo = reader.readObject(ExpertVO.class);
				}catch(Exception e) {
					continue;
				}
				Expert expert = new Expert();
				expert.setId(UUID.randomUUID().toString());
				expert.setName(vo.getKey());
				expert.setNow(System.currentTimeMillis());
				experts.add(expert);
				i++;
				System.out.println("当前id---------》" + i);
			}
			reader.endArray();
	        reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
    	return R.ok();
    }
    @ResponseBody
	@RequestMapping(value = "expert/expertInsList", method = RequestMethod.POST,consumes = "application/json")
	public R expertInsList(@RequestBody JSONObject insname) {
    	int pageSize = 20;
		//if(pageIndex == null) {
		int pageIndex = 0;
		//}
		int i = 5;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构；5代表专家；
		// TODO 静态变量未环绕需调整
		JSONObject rs = new JSONObject();
		//rs.put("list", sources);
		//rs.put("totalPages", totalPages);
		//rs.put("totalCount", totalCount);
		rs = expertService.executeIns(insname.getString("insname"),0, pageSize, "unit",i);
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount"));
    }
	
}
