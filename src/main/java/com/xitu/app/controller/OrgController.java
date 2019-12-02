package com.xitu.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.common.request.SaveOrgRequest;
import com.xitu.app.mapper.ItemMapper;
import com.xitu.app.model.Expert;
import com.xitu.app.model.Item;
import com.xitu.app.model.Org;
import com.xitu.app.repository.OrgRepository;
import com.xitu.app.service.es.ESHttpClient;
import com.xitu.app.service.es.OrgService;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.Scpclient;
import com.xitu.app.utils.ThreadLocalUtil;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class OrgController {

	private static final Logger logger = LoggerFactory.getLogger(OrgController.class);
	
	@Autowired
    private OrgRepository orgRepository;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
    private ItemMapper itemMapper;
	
	@Autowired
	private OrgService orgService;
	
	@PostMapping(value = "org/save")
	public String saveOrg(SaveOrgRequest request,Model model) {
		
		Org org = new Org();
		BeanUtil.copyBean(request, org);
		if(org.getId() == null || "".equals(org.getId())) {
			org.setId(UUID.randomUUID().toString());
		}
		List<String> area = new ArrayList<String>();
		List<String> classic = new ArrayList<String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//area.add(request.getArea());
		//classic.add(request.getClassic());
		if (request.getArea() != null && !request.getArea().equals("")) {
			String[] s = request.getArea().split(",");
			for(String id:s){
				area.add(id);
			}
		}
		org.setArea(area);
		if (request.getClassic() != null && !request.getClassic().equals("")) {
			String[] s = request.getClassic().split(",");
			for(String id:s){
				classic.add(id);
			}
		}
		org.setClassic(classic);
		org.setDescription(request.getInfo());
		org.setNow(System.currentTimeMillis());
		org.setCtime(df.format(new Date()));
		
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
		org.setAlias(alias);
		orgRepository.save(org);
		return "redirect:/org/list";
	}
	
	@GetMapping(value = "org/update")
	public String updateOrg() {
		
//		Iterator<Org> orgs = orgRepository.findAll().iterator();
//		while(orgs.hasNext()) {
//			Org org = orgs.next();
//			System.out.println(org.getId());
//			boolean flag = false;
//			if(org.getFrontend() == null || org.getFrontend().equals("")) {
//				org.setFrontend("");
//				flag = true;
//			}
//			if(org.getFrontendFileName()==null || org.getFrontendFileName().equals("")) {
//				org.setFrontendFileName("");
//				flag = true;
//			}
//			if(org.getFrontendSize()==null || org.getFrontendSize().equals("")) {
//				org.setFrontendSize("");
//				flag = true;
//			}
//			if(flag) {
//				orgRepository.save(org);
//			}
//		}
		
		
		Iterator<Org> orgs = orgRepository.findAll().iterator();
		while(orgs.hasNext()) {
			Org org = orgs.next();
			System.out.println(org.getId());
			String anotherName = "";
			if(org.getName() != null && !org.getName().equals("")) {
				anotherName = org.getName();
			}
			org.setAnotherName(anotherName);
			orgRepository.save(org);
		}
		return "fasdf";
	}
	
	@ResponseBody
	@RequestMapping(value = "org/getByName", method = RequestMethod.POST,consumes = "application/json")
	public JSONObject getExpertByname(
			@RequestBody JSONObject insname) {
		//String id = "1f93a2c9b53c4b10ac68c330a9f23234";
		Org org = new Org();
		if(insname != null ) {
			//expert = expertRepository.findById(id).get();
			//expert = expertRepository.findByName(name).get();
			String name = insname.getString("name");
			JSONObject rs = new JSONObject();
			rs = orgService.executeOneFiled("anotherName",name);
			List sources = new LinkedList();
			if(rs != null) {
				sources = (List) rs.get("list");
				if(sources != null) {
					//return "redirect:/expert/get?front=0&id="+expert.getId();
					return (JSONObject) sources.get(0);
				}
			}
		}
		return null;
	}
	@GetMapping(value = "org/get")
	public String getOrg(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="disable") String disable,
			@RequestParam(required=false,value="id") String id, Model model) {
		String view = "qiyezhikuzhongdianjigouxiangqing";
		Org org = new Org();
		if(id != null) {
			org = orgRepository.findById(id).get();
			String name = org.getName();
			model.addAttribute("name", name);
			String bieming = null;
			List<String> alias = org.getAlias();
			if (alias != null && alias.size() > 0) {
				bieming =  StringUtils.join(alias.toArray(), ",");
			}
			if (bieming != null) {
				name = name+","+bieming;
			}
			model.addAttribute("namebieming", name);
			model.addAttribute("frontendId", "".equals(org.getFrontend())?null:org.getFrontend());
			model.addAttribute("frontendFileName", "".equals(org.getFrontendFileName())?null:org.getFrontendFileName());
			model.addAttribute("frontendSize", "".equals(org.getFrontendSize())?null:org.getFrontendSize());
			model.addAttribute("area", org.getArea());
			model.addAttribute("classic", org.getClassic());
		}
		if(front != null) {
			view = "qiyezhikujigouxiangqing";
		}
		
		if(disable !=null) {
			model.addAttribute("disable", "0");
		}else {
			model.addAttribute("disable", "1");
		}
		
		Item yjlyitem = itemMapper.selectItemByService("yjly");
		List<String> yjlyitemitems = new ArrayList<String>();
		if(yjlyitem != null && yjlyitem.getItem() != null) {
			for(String s: yjlyitem.getItem().split(";")) {
				yjlyitemitems.add(s);
			}
		}
		model.addAttribute("yjlyitems", yjlyitemitems);
		
		Item jglxitems = itemMapper.selectItemByService("jglx");
		List<String> jglxitemitems = new ArrayList<String>();
		if(jglxitems != null && jglxitems.getItem() != null) {
			for(String s: jglxitems.getItem().split(";")) {
				jglxitemitems.add(s);
			}
		}
		model.addAttribute("jglxitems", jglxitemitems);
		
		Item gjitems = itemMapper.selectItemByService("gj");
		List<String> gjitemitems = new ArrayList<String>();
		if(gjitems != null && gjitems.getItem() != null) {
			for(String s: gjitems.getItem().split(";")) {
				gjitemitems.add(s);
			}
		}
		model.addAttribute("gjitems", gjitemitems);
		
		Item cylitems = itemMapper.selectItemByService("cyl");
		List<String> cylitemitems = new ArrayList<String>();
		if(cylitems != null && cylitems.getItem() != null) {
			for(String s: cylitems.getItem().split(";")) {
				cylitemitems.add(s);
			}
		}
		model.addAttribute("cylitems", cylitemitems);
		
		Item cplxitems = itemMapper.selectItemByService("cplx");
		List<String> cplxitemitems = new ArrayList<String>();
		if(cplxitems != null && cplxitems.getItem() != null) {
			for(String s: cplxitems.getItem().split(";")) {
				cplxitemitems.add(s);
			}
		}
		model.addAttribute("cplxitems", cplxitemitems);
		
		model.addAttribute("org", org);
		return view;
	}
	
	@GetMapping(value = "org/delete")
	public String deleteOrg(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			orgRepository.deleteById(id);
		}
		
		return "redirect:/org/list";
	}
	
	@GetMapping(value = "org/list")
	public String patents(@RequestParam(required=false,value="front") String front,
			@RequestParam(required=false,value="q") String q,
			@RequestParam(required=false,value="type") String type,
			@RequestParam(required=false,value="link") String link,
			@RequestParam(required=false,value="classic") String classic,
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
		int i = 4;//0代表专利；1代表论文；2代表项目；3代表监测;4代表机构
		// TODO 静态变量未环绕需调整
		ThreadLocalUtil.set(model);
		orgService.execute(pageIndex, pageSize, i,q,type,link,classic);
		ThreadLocalUtil.remove();
		String view = "qiyezhikuzhongdianjigouliebiao";
		if (front != null) {
			view = "qiyezhikuzhongdianjigou";
		}
		return view;
	}
	
//	@GetMapping(value = "org/list")
//	public String orgs(@RequestParam(required=false,value="front") String front,
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
//		List<Org> orgList = new ArrayList<Org>();
//		String view = "qiyezhikuzhongdianjigouliebiao";
//		if (front != null) {
//			view = "qiyezhikuzhongdianjigou";
//		}
//		if(esTemplate.indexExists(Project.class)) {
//			if(q == null || q.equals("")) {
//				totalCount = orgRepository.count();
//				if(totalCount > 0) {
//					Sort sort = new Sort(Direction.DESC, "now");
//					Pageable pageable = new PageRequest(pageIndex, pageSize,sort);
//					SearchQuery searchQuery = new NativeSearchQueryBuilder()
//							.withPageable(pageable).build();
//					Page<Org> orgPage = orgRepository.search(searchQuery);
//					orgList = orgPage.getContent();
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
//				Page<Org> searchPageResults = orgRepository.search(searchQuery);
//				orgList = searchPageResults.getContent();
//				totalCount = esTemplate.count(searchQuery, Org.class);
//				//totalPages = Math.round(totalCount/pageSize);
//				if(totalCount % pageSize == 0){
//					totalPages = totalCount/pageSize;
//				}else{
//					totalPages = totalCount/pageSize + 1;
//				}
////				view = "qiyezhikuzhongdianjigou";
//			}
//		}
//		int max = 800;
//		int min = 100;
//		Random random = new Random();
//		for(Org org: orgList) {
//			int x = random.nextInt(max-min) + min;
//			org.setProfessors(String.valueOf(x));
//			int y = random.nextInt(max-min) + min;
//			org.setPatents(String.valueOf(y));
//			int z = random.nextInt(max-min) + min;
//			org.setDymanics(String.valueOf(z));
//		}
//		model.addAttribute("orgList", orgList);
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("totalCount", totalCount);
//		model.addAttribute("pageIndex", pageIndex);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("query", q);
//			
//		return view;
//	}
	
	 /**
     * 文件解析
     * */
//    @GetMapping(value="org/import")
//    @ResponseBody 
//    public R importJson(){
//    	try {
//			String filePath = String.format("/Users/liubingchuan/git/xitu/src/main/resources/efg.json");
//			File file = new File(filePath);
//			JSONReader reader=new JSONReader(new FileReader(file));
//			reader.startArray();
//			List<Org> orgs = new LinkedList<Org>();
//			int i=1;
//			while (reader.hasNext()) {
//				if(i==1148){
//					System.out.println();
//				}
//				if(orgs.size()>=1000) {
//					orgRepository.saveAll(orgs);
//					orgs.clear();
//				}
//				OrgVO vo = new OrgVO();
//				try{
//					vo = reader.readObject(OrgVO.class);
//				}catch(Exception e) {
//					continue;
//				}
//				Org org = new Org();
//				org.setId(UUID.randomUUID().toString());
//				org.setName(vo.getKey());
//				org.setNow(System.currentTimeMillis());
//				orgs.add(org);
//				i++;
//				System.out.println("当前id---------》" + i);
//			}
//			reader.endArray();
//	        reader.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    	return R.ok();
//    }
    @ResponseBody
	@PostMapping(value = "org/searchNum")
    public JSONObject searchNum(@RequestBody Map<String, Object> map) {
    	JSONObject reData = new JSONObject();
    	Map<String, Integer> expertaggcountMap = new HashMap<String, Integer>();
    	Map<String, Integer> expertinscountMap = new HashMap<String, Integer>();
    	
    	if(map != null) {
    		List<String> insNamearr = new LinkedList<String>();
    		for(Map.Entry<String, Object> entry: map.entrySet()) {
    			insNamearr.add(entry.getKey());
    			if(entry.getValue() != null && !entry.getValue().equals("[]")) {
    				insNamearr.addAll((List)entry.getValue());
    			}
    		}
    		int aggsize = insNamearr.size();
    		
    		JSONObject expertInsName = ESHttpClient.conexpertESIns(orgService.createQqueryByListIns(insNamearr,aggsize,"unit"));
    		JSONObject aggregations = expertInsName.getJSONObject("aggregations");
    		JSONObject agg = (JSONObject) aggregations.get("unit");
    		for(Object s:(JSONArray)agg.get("buckets")){
    			JSONObject ss = (JSONObject) s;
    			if (ss != null && !ss.toString().trim().equals("")) {
    				expertaggcountMap.put(ss.getString("key"), Integer.valueOf(ss.getString("doc_count")));
    	    		
				}
    			}
    		for(Map.Entry<String, Object> entry: map.entrySet()) {
    			String name = entry.getKey();
    			List<String> bieming = (List<String>) entry.getValue();
    			//bieming.add(name);
    			int count = 0;
    			if(bieming!=null &&  bieming.size()>0){
    			for (String ns:bieming) {
					if (!ns.equals("") && expertaggcountMap.containsKey(ns)) {
						count += expertaggcountMap.get(ns);
					}
				}
    			}
    			if (expertaggcountMap.containsKey(name)) {
					count += expertaggcountMap.get(name);
				}
    			expertinscountMap.put(name, count);
    		}
    		reData.put("ExpertNum", expertinscountMap);
    		
    		Map<String, Integer> patentaggcountMap = new HashMap<String, Integer>();
        	Map<String, Integer> patentinscountMap = new HashMap<String, Integer>();
    		JSONObject patentInsName = ESHttpClient.conpatentESIns(orgService.createQqueryByListIns(insNamearr,aggsize,"person"));
    		JSONObject patentaggregations = patentInsName.getJSONObject("aggregations");
    		JSONObject patentagg = (JSONObject) patentaggregations.get("person");
    		for(Object s:(JSONArray)patentagg.get("buckets")){
    			JSONObject ss = (JSONObject) s;
    			patentaggcountMap.put(ss.getString("key"), Integer.valueOf(ss.getString("doc_count")));
    		}
    		for(Map.Entry<String, Object> entry: map.entrySet()) {
    			String name = entry.getKey();
    			List<String> bieming = (List<String>) entry.getValue();
    			//bieming.add(name);
    			int count = 0;
    			if(bieming!=null &&  bieming.size()>0){
    			for (String ns:bieming) {
					if (!ns.equals("") && patentaggcountMap.containsKey(ns)) {
						count += patentaggcountMap.get(ns);
					}
				}
    			}
    			if (patentaggcountMap.containsKey(name)) {
					count += patentaggcountMap.get(name);
				}
    			patentinscountMap.put(name, count);
    		}
    		reData.put("PatentNum", patentinscountMap);
    		
    		Map<String, Integer> paperaggcountMap = new HashMap<String, Integer>();
        	Map<String, Integer> paperinscountMap = new HashMap<String, Integer>();
    		JSONObject paperInsName = ESHttpClient.conpaperESIns(orgService.createQqueryByListIns(insNamearr,aggsize,"institution"));
    		JSONObject paperaggregations = paperInsName.getJSONObject("aggregations");
    		JSONObject paperagg = (JSONObject) paperaggregations.get("institution");
    		for(Object s:(JSONArray)paperagg.get("buckets")){
    			JSONObject ss = (JSONObject) s;
    			paperaggcountMap.put(ss.getString("key"), Integer.valueOf(ss.getString("doc_count")));
    		}
    		for(Map.Entry<String, Object> entry: map.entrySet()) {
    			String name = entry.getKey();
    			List<String> bieming = (List<String>) entry.getValue();
    			//bieming.add(name);
    			int count = 0;
    			if(bieming!=null &&  bieming.size()>0){
    			for (String ns:bieming) {
					if (!ns.equals("") && paperaggcountMap.containsKey(ns)) {
						count += paperaggcountMap.get(ns);
					}
				}
    			}
    			if (paperaggcountMap.containsKey(name)) {
					count += paperaggcountMap.get(name);
				}
    			paperinscountMap.put(name, count);
    		}
    		reData.put("PaperNum", paperinscountMap);
    		
    		Map<String, Integer> jianceaggcountMap = new HashMap<String, Integer>();
        	Map<String, Integer> jianceinscountMap = new HashMap<String, Integer>();
    		JSONObject jianceInsName = ESHttpClient.conjianceESIns(orgService.createQqueryByListIns(insNamearr,aggsize,"institution"));
    		
    		JSONObject jianceaggregations = jianceInsName.getJSONObject("aggregations");
    		JSONObject jianceagg = (JSONObject) jianceaggregations.get("institution");
    		for(Object s:(JSONArray)jianceagg.get("buckets")){
    			JSONObject ss = (JSONObject) s;
    			jianceaggcountMap.put(ss.getString("key"), Integer.valueOf(ss.getString("doc_count")));
    		}
    		for(Map.Entry<String, Object> entry: map.entrySet()) {
    			String name = entry.getKey();
    			List<String> bieming = (List<String>) entry.getValue();
    			//bieming.add(name);
    			int count = 0;
    			if(bieming!=null &&  bieming.size()>0){
    			for (String ns:bieming) {
					if (!ns.equals("") && jianceaggcountMap.containsKey(ns)) {
						count += jianceaggcountMap.get(ns);
					}
				}
    			}
    			if (jianceaggcountMap.containsKey(name)) {
					count += jianceaggcountMap.get(name);
				}
    			jianceinscountMap.put(name, count);
    		}
    		reData.put("JianceNum", jianceinscountMap);
    	}
		return reData;
		
	}
    
    /**
     * 文件解析
     * */
    @GetMapping(value="org/import")
    @ResponseBody 
    public R importExcel(){
    	
    	Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String filePath = "/Users/liubingchuan/Desktop/hangzhou/dierpijigouxinxihuizong597/jigouxinxihuizongdierpi.xlsx";
        String photoLocalPath = "/Users/liubingchuan/Desktop/hangzhou/dierpijigouxinxihuizong597/jigouzhaopianhuizong0514";
        wb = readExcel(filePath);
        Scpclient client = Scpclient.getInstance("45.76.75.11", 22, "root", "6c]V=*z2bY=mRZ2)");
        Set<String> jigouleixings = new HashSet<String>();
        Set<String> suoshuguojias = new HashSet<String>();
        Set<String> chanyelians = new HashSet<String>();
        Set<String> chanpinleixings = new HashSet<String>();
        if(wb != null){
            //用来存放表中数据
            list = new ArrayList<Map<String,String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 1; i<rownum; i++) {
            	Org org = new Org();
            	System.out.println("id------>" + i);
                row = sheet.getRow(i);
                if(row !=null){
                    for (int j=0;j<colnum;j++){
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        if(cellData.contains(".0")) {
                        	cellData = cellData.split("\\.")[0];
                        }
                        if(j==0) {
                        	UUID uuid = UUID.randomUUID();
                        	String fileName = cellData;
                        	System.out.println("fileId ---------->" + cellData);
                        	fileName = fileName + ".jpg";
                        	File photo = new File(photoLocalPath + File.separator + fileName);
                        	if(photo.exists()) {
                        		org.setFrontend(uuid+"_"+fileName);
                        		org.setFrontendFileName(fileName);
                        		org.setFrontendSize(String.valueOf(Math.round(photo.length()/1000)));
                        		client.putFile(photoLocalPath + File.separator + fileName, uuid+"_"+fileName, "/root/files", "0755");
                        	}else {
                        		fileName = cellData + ".png";
                        		photo = new File(photoLocalPath + File.separator + fileName);
                        		if(photo.exists()) {
                        			org.setFrontend(uuid+"_"+fileName);
                        			org.setFrontendFileName(fileName);
                        			org.setFrontendSize(String.valueOf(Math.round(photo.length()/1000)));
                        			client.putFile(photoLocalPath + File.separator + fileName, uuid+"_"+fileName, "/root/files", "0755");
                        		}
                        	}
                        }else if(j==1){
                        	org.setName(cellData);
                        }else if(j==2) {
                        	String[] str = cellData.split("、");
                        	List<String> alias = new ArrayList<String>();
                        	for(String s: str) {
                        		alias.add(s);
                        	}
                        	org.setAlias(alias);
                        }else if(j==3) {
                        	org.setDescription(cellData);
                        }else if(j==4) {
                        	org.setType(cellData);
                        }else if(j==5) {
                        	org.setCountry(cellData);
                        }else if(j==6) {
                        	org.setLink(cellData);
                        }else if(j==7) {
                        	List<String> classices = new ArrayList<String>();
                        	classices.add(cellData);
                        	org.setClassic(classices);
                        }else if(j==8) {
                        	org.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
                        }
                    }
                    orgRepository.save(org);
                }else{
                    break;
                }
            }
        }
        Item jglx = new Item();
        jglx.setService("jglx");
        StringBuilder bufferJglx = new StringBuilder();
		boolean append = false;
		for(String s: jigouleixings) {
			if("".equals(s)) {
				append=false;
				continue;
			}
			if(append) bufferJglx.append(";"); else append = true; 
			bufferJglx.append(s);
		}
		jglx.setItem(bufferJglx.toString().length()==0?"":bufferJglx.substring(0, bufferJglx.length()));
		itemMapper.insertItem(jglx);
		Item gj = new Item();
		gj.setService("gj");
		StringBuilder bufferGj = new StringBuilder();
		append = false;
		for(String s: suoshuguojias) {
			if("".equals(s)) {
				append=false;
				continue;
			}
			if(append) bufferGj.append(";"); else append = true; 
			bufferGj.append(s);
		}
		gj.setItem(bufferGj.toString().length()==0?"":bufferGj.substring(0, bufferGj.length()));
		itemMapper.insertItem(gj);
		
		
		Item cyl = new Item();
		cyl.setService("cyl");
		StringBuilder bufferCyl = new StringBuilder();
		append = false;
		for(String s: chanyelians) {
			if("".equals(s)) {
				append=false;
				continue;
			}
			if(append) bufferCyl.append(";"); else append = true; 
			bufferCyl.append(s);
		}
		cyl.setItem(bufferCyl.toString().length()==0?"":bufferCyl.substring(0, bufferCyl.length()));
		itemMapper.insertItem(cyl);
    	return R.ok();
    }
    
  //读取excel
    private static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }
    private static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC:{
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            }
            case Cell.CELL_TYPE_FORMULA:{
                //判断cell是否为日期格式
                if(DateUtil.isCellDateFormatted(cell)){
                    //转换为日期格式YYYY-mm-dd
                    cellValue = cell.getDateCellValue();
                }else{
                    //数字
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            case Cell.CELL_TYPE_STRING:{
                cellValue = cell.getRichStringCellValue().getString();
                break;
            }
            default:
                cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
	
}
