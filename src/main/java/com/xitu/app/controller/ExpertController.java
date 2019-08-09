package com.xitu.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.xitu.app.common.R;
import com.xitu.app.common.request.SaveExpertRequest;
import com.xitu.app.mapper.ItemMapper;
import com.xitu.app.model.Expert;
import com.xitu.app.model.Item;
import com.xitu.app.model.Org;
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
	
	@ResponseBody
	@RequestMapping(value = "expert/getByName", method = RequestMethod.POST,consumes = "application/json")
	public JSONObject getExpertByname(
			@RequestBody JSONObject insname) {
		//String id = "1f93a2c9b53c4b10ac68c330a9f23234";
		
		if(insname != null ) {
			//expert = expertRepository.findById(id).get();
			//expert = expertRepository.findByName(name).get();
			String name = insname.getString("name");
			JSONObject rs = new JSONObject();
			rs = expertService.executeOneFiled("anotherName",name);
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
	@GetMapping(value = "expert/delete")
	public String deleteExpert(@RequestParam(required=false,value="id") String id) {
		if(id != null) {
			expertRepository.deleteById(id);
		}
		return "redirect:/expert/list";
	}
	
	@GetMapping(value = "expert/update")
	public String updateExpert() {
		Iterator<Expert> experts = expertRepository.findAll().iterator();
		while(experts.hasNext()) {
			Expert expert = experts.next();
			System.out.println(expert.getId());
//			List<String> tags = new ArrayList<String>();
//			tags.add("测试");
			String seq = "";
			if(expert.getFrontend() != null && !expert.getFrontend().equals("")) {
				seq = expert.getFrontend();
			}
			expert.setSeq(seq);
			expertRepository.save(expert);
		}
		return "fasdf";
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
	
//	/**
//     * 文件解析
//     * */
//    @GetMapping(value="expert/import")
//    @ResponseBody 
//    public R importJson(){
//    	try {
//			String filePath = String.format("/Users/liubingchuan/git/xitu/src/main/resources/xyz.json");
//			File file = new File(filePath);
//			JSONReader reader=new JSONReader(new FileReader(file));
//			reader.startArray();
//			List<Expert> experts = new LinkedList<Expert>();
//			int i=1;
//			while (reader.hasNext()) {
//				if(i==1148){
//					System.out.println();
//				}
//				if(experts.size()>=1000) {
//					expertRepository.saveAll(experts);
//					experts.clear();
//				}
//				ExpertVO vo = new ExpertVO();
//				try{
//					vo = reader.readObject(ExpertVO.class);
//				}catch(Exception e) {
//					continue;
//				}
//				Expert expert = new Expert();
//				expert.setId(UUID.randomUUID().toString());
//				expert.setName(vo.getKey());
//				expert.setNow(System.currentTimeMillis());
//				experts.add(expert);
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
	
	/**
     * 文件解析
     * */
    @GetMapping(value="expert/import")
    @ResponseBody 
    public R importExcel(){
    	
    	Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String filePath = "/Users/liubingchuan/Desktop/hangzhou/zhuanjiaxinxixiugai/zhuanjiaxinxi-1.xlsx";
        String columns[] = {"id","name","alias","org","lingyu","resume","addr","email","phone","tel","duty","major","photo"};
        String photoLocalPath = "/Users/liubingchuan/Desktop/hangzhou/zhuanjiaxinxixiugai/zhuanjiazhaopin-1";
        wb = readExcel(filePath);
//        Scpclient client = Scpclient.getInstance("45.76.75.11", 22, "root", "6c]V=*z2bY=mRZ2)");
        Set<String> yanjiulingyus = new HashSet<String>();
        Set<String> zhiwus = new HashSet<String>();
        Set<String> zhichengs = new HashSet<String>();
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
            for (int i = 116; i<rownum; i++) {
            	Expert expert = new Expert();
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
                        		expert.setFrontend(uuid+"_"+fileName);
                        		expert.setFrontendFileName(fileName);
                        		expert.setFrontendSize(String.valueOf(Math.round(photo.length()/1000)));
//                        		client.putFile(photoLocalPath + File.separator + fileName, uuid+"_"+fileName, "/root/files", "0755");
                        	}else {
                        		fileName = cellData + ".png";
                        		photo = new File(photoLocalPath + File.separator + fileName);
                        		if(photo.exists()) {
                        			expert.setFrontend(uuid+"_"+fileName);
                        			expert.setFrontendFileName(fileName);
                        			expert.setFrontendSize(String.valueOf(Math.round(photo.length()/1000)));
//                        			client.putFile(photoLocalPath + File.separator + fileName, uuid+"_"+fileName, "/root/files", "0755");
                        		}
                        	}
                        }else if(j==1){
                        	expert.setName(cellData);
                        }else if(j==2) {
                        	List<String> alias = new ArrayList<String>();
                        	alias.add(cellData);
                        	expert.setAlias(alias);
                        }else if(j==3) {
                        	expert.setUnit(cellData);
                        }else if(j==4) {
                        	String[] str = cellData.split("；");
                        	List<String> areas = new ArrayList<String>();
                        	for(String s : str) {
                        		areas.add(s);
                        		yanjiulingyus.add(s);
                        	}
                        	expert.setArea(areas);
                        }else if(j==5) {
                        	expert.setResume(cellData);
                        }else if(j==6) {
                        	expert.setAddress(cellData);
                        }else if(j==7) {
                        	expert.setEmail(cellData);
                        }else if(j==8) {
                        	expert.setPhone(cellData);
                        }else if(j==9) {
                        	expert.setTel(cellData);
                        }else if(j==10) {
                        	List<String> duties = new ArrayList<String>();
                        	duties.add(cellData);
                        	expert.setDuty(duties);
                        	zhiwus.add(cellData);
                        }else if(j == 11) {
                        	List<String> titles = new ArrayList<String>();
                        	titles.add(cellData);
                        	expert.setTitle(titles);
                        	zhichengs.add(cellData);
                        }else {
                        	expert.setId(UUID.randomUUID().toString().replaceAll("\\-", ""));
                        }
                    }
//                    expertRepository.save(expert);
                }else{
                    break;
                }
            }
        }
        Item yjly = new Item();
        yjly.setService("yjly");
        StringBuilder bufferYjly = new StringBuilder();
		boolean append = false;
		for(String s: yanjiulingyus) {
			if("".equals(s)) {
				append=false;
				continue;
			}
			if(append) bufferYjly.append(";"); else append = true; 
			bufferYjly.append(s);
		}
		yjly.setItem(bufferYjly.toString().length()==0?"":bufferYjly.substring(0, bufferYjly.length()));
		itemMapper.insertItem(yjly);
		Item zw = new Item();
		zw.setService("zw");
		StringBuilder bufferZw = new StringBuilder();
		append = false;
		for(String s: zhiwus) {
			if("".equals(s)) {
				append=false;
				continue;
			}
			if(append) bufferZw.append(";"); else append = true; 
			bufferZw.append(s);
		}
		zw.setItem(bufferZw.toString().length()==0?"":bufferZw.substring(0, bufferZw.length()));
		itemMapper.insertItem(zw);
		
		
		Item zc = new Item();
		zc.setService("zc");
		StringBuilder bufferZc = new StringBuilder();
		append = false;
		for(String s: zhichengs) {
			if("".equals(s)) {
				append=false;
				continue;
			}
			if(append) bufferZc.append(";"); else append = true; 
			bufferZc.append(s);
		}
		zc.setItem(bufferZc.toString().length()==0?"":bufferZc.substring(0, bufferZc.length()));
		itemMapper.insertItem(zc);
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
		if(insname.containsKey("insname")) {
			rs = expertService.executeIns(insname.getString("insname"),0, pageSize, "unit",i);
		}else if(insname.containsKey("insname")) {
			
		}
		return R.ok().put("list", rs.get("list")).put("totalPages", rs.get("totalPages")).put("totalCount", rs.get("totalCount"));
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
