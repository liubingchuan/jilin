package com.xitu.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.cluster.metadata.AliasAction.NewAliasValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xitu.app.common.R;
import com.xitu.app.common.SystemConstant;
import com.xitu.app.common.request.LoginRequest;
import com.xitu.app.common.request.RegisterRequest;
import com.xitu.app.common.request.SaveItemRequest;
import com.xitu.app.common.request.SaveOrderRequest;
import com.xitu.app.common.request.UpdateUserRequest;
import com.xitu.app.constant.Constant;
import com.xitu.app.mapper.UserMapper;
import com.xitu.app.mapper.ZhishifuwuMapper;
import com.xitu.app.model.Linkuser;
import com.xitu.app.model.Order;
import com.xitu.app.model.Relation;
import com.xitu.app.model.User;
import com.xitu.app.utils.BeanUtil;
import com.xitu.app.utils.HttpClientUtils;
import com.xitu.app.utils.JwtUtils;



@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
@Controller
public class ZhishifuwuController {

	private static final Logger logger = LoggerFactory.getLogger(ZhishifuwuController.class);
	
	private static final String MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={TOKEN}";
	
	@Autowired
    private ZhishifuwuMapper zhishifuwuMapper;
	
	@GetMapping(value = "zhishifuwu/zhishifuwu")
	public String Kejichaxin(Model model) {
		SaveOrderRequest saveOrderRequest = new SaveOrderRequest();
		model.addAttribute("saveOrderRequest", saveOrderRequest);
		return "T-zhishi";
	}
	@GetMapping(value = "zhishifuwu/wenxianchuandi")
	public String WenxianChuandi(Model model) {
		SaveOrderRequest saveOrderRequest = new SaveOrderRequest();
		model.addAttribute("saveOrderRequest", saveOrderRequest);
		return "T_wenxian";
	}
	
	@PostMapping(value = "order/save")
	public String saveOrder(SaveOrderRequest request,Model model, RedirectAttributes redirectAttributes) throws Exception {
		Order order = new Order();
		if (request.getTitle() != null) {
			order.setTitle(request.getTitle());
		}
		if (request.getUserId() != null) {
			order.setUserId(request.getUserId());
		}
		if (request.getOrdernumber() != null) {
			order.setOrdernumber(request.getOrdernumber());
		}
		if (request.getChaxinfanwei() != null && !request.getChaxinfanwei().equals("请选择查新范围")) {
			order.setChaxinfanwei(request.getChaxinfanwei());
		}
		
		if (request.getMudi() != null && !request.getMudi().equals("请选择项目目的")) {
			order.setMudi(request.getMudi());
		}
		
		if (request.getKexueyaodian() != null) {
			order.setKexueyaodian(request.getKexueyaodian());
		}
		
		if (request.getJiansuodian() != null) {
			order.setJiansuodian(request.getJiansuodian());
		}
		if (request.getBeizhu() != null) {
			order.setBeizhu(request.getBeizhu());
		}
		
		if (request.getJiansuoci() != null) {
			order.setJiansuoci(request.getJiansuoci());
		}
		if (request.getShenqingfujianId() != null) {
			order.setShenqingfujianId(request.getShenqingfujianId());
		}
		if (request.getXueke() != null && !request.getXueke().equals("请选择学科分类")) {
			order.setXueke(request.getXueke());
		}
		if (request.getChanye() != null && !request.getChanye().equals("请选择产业分类")) {
			order.setChanye(request.getChanye());
		}
		if (request.getInstitution()!= null) {
			order.setInstitution(request.getInstitution());
		}
		//order.setUserId(1);
		order.setChulizhuangtai("待处理");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		//System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		order.setShenqingshijian(df.format(new Date()));
		
		String orderuuid = UUID.randomUUID().toString();
		order.setUuid(orderuuid);
		zhishifuwuMapper.insertOrder(order);
		//刘冰川
		//微信发送给通知给用户  待处理
		//微信发送通知给操作员
		String url = MESSAGE_URL.replace("{TOKEN}", Constant.ACCESS_TOKEN);
		System.out.println("order userId------->" + order.getUserId());
		JSONObject jsonObject = new JSONObject();
//        jsonObject.put("touser", order.getUserId());   // openid
		jsonObject.put("touser", "okT7G03g6kRUX7BI0FY048D0BA9o");
        jsonObject.put("template_id", "xp9DfByRBXy5znxzyNW9o5TJh4_1zDRSCaV3BJhz3Sg");
//        jsonObject.put("url", "http://www.baidu.com");
 
        JSONObject data = new JSONObject();
        JSONObject first = new JSONObject();
        first.put("value", "hello");
        first.put("color", "#173177");
        JSONObject keyword1 = new JSONObject();
        keyword1.put("value", "hello");
        keyword1.put("color", "#173177");
//        JSONObject keyword2 = new JSONObject();
//        keyword2.put("value", "hello");
//        keyword2.put("color", "#173177");
//        JSONObject keyword3 = new JSONObject();
//        keyword3.put("value", "hello");
//        keyword3.put("color", "#173177");
//        JSONObject remark = new JSONObject();
//        remark.put("value", "hello");
//        remark.put("color", "#173177");
        
        data.put("first",first);
        data.put("keyword1",keyword1);
//        data.put("keyword2",keyword2);
//        data.put("keyword3",keyword3);
//        data.put("remark",remark);
 
        jsonObject.put("data", data);
 
        String string = HttpClientUtils.sendPostJsonStr(url, jsonObject.toJSONString());
        JSONObject result = JSON.parseObject(string);
        System.out.println("response 结果------>" + string);
        int errcode = result.getIntValue("errcode");
        if(errcode == 0){
            // 发送成功
            System.out.println("发送成功");
        } else {
            // 发送失败
            System.out.println("发送失败");

        }		
		
		Linkuser linkuser = new Linkuser();
		if (request.getName() != null) {
			linkuser.setName(request.getName());
		}
		if (request.getEmail() != null) {
			linkuser.setEmail(request.getEmail());
		}
		if (request.getInstitution()!= null) {
			linkuser.setInstitution(request.getInstitution());
		}
		if (request.getTelephone() != null) {
			linkuser.setTelephone(request.getTelephone());
		}
		String linkuseruuid = UUID.randomUUID().toString();
		linkuser.setUuid(linkuseruuid);
		
		zhishifuwuMapper.insertLinkuser(linkuser);
		
		Relation relation = new Relation();
		relation.setLinkuserId(linkuseruuid);
		relation.setOrderId(orderuuid);
		//主联系是0，其他联系人是1
		relation.setTag(0);
		
		
		zhishifuwuMapper.insertRelation(relation);
		
		Linkuser fulinkuser = new Linkuser();
		if (request.getFuna()!= null && !request.getFuna().equals("")) {
			if (request.getFuna()!= null) {
				fulinkuser.setName(request.getFuna().substring(1, request.getFuna().length()));
			}
			if (request.getFuema()!= null) {
				fulinkuser.setEmail(request.getFuema().substring(1, request.getFuema().length()));
			}
			if (request.getFuins()!= null) {
				fulinkuser.setInstitution(request.getFuins().substring(1, request.getFuins().length()));
			}
			if (request.getFutele()!= null) {
				fulinkuser.setTelephone(request.getFutele().substring(1, request.getFutele().length()));
			}
			String linkuseruuid1 = UUID.randomUUID().toString();
			fulinkuser.setUuid(linkuseruuid1);
			
			zhishifuwuMapper.insertLinkuser(fulinkuser);
			
			Relation relation1 = new Relation();
			relation1.setLinkuserId(linkuseruuid1);
			relation1.setOrderId(orderuuid);
			relation1.setTag(1);
			
			zhishifuwuMapper.insertRelation(relation1);
			
		}
		model.addAttribute("display", "1");
		redirectAttributes.addAttribute("token", request.getToken());
		return "T-zhishi";
	}
	
//	@ResponseBody
//	@RequestMapping(value = "order/save", method = RequestMethod.POST,consumes = "application/json")
//	public R saveOrder(@RequestBody SaveOrderRequest request) throws Exception {
//	Order order = new Order();
//	if (request.getTitle() != null) {
//		order.setTitle(request.getTitle());
//	}
//	
//	if (request.getChaxinfanwei() != null && !request.getChaxinfanwei().equals("请选择查新范围")) {
//		order.setChaxinfanwei(request.getChaxinfanwei());
//	}
//	
//	if (request.getMudi() != null && !request.getMudi().equals("请选择项目目的")) {
//		order.setMudi(request.getMudi());
//	}
//	
//	if (request.getKexueyaodian() != null) {
//		order.setKexueyaodian(request.getKexueyaodian());
//	}
//	
//	if (request.getJiansuodian() != null) {
//		order.setJiansuodian(request.getJiansuodian());
//	}
//	
//	if (request.getJiansuoci() != null) {
//		order.setJiansuoci(request.getJiansuoci());
//	}
//	if (request.getXueke() != null && !request.getXueke().equals("请选择学科分类")) {
//		order.setXueke(request.getXueke());
//	}
//	if (request.getChanye() != null && !request.getChanye().equals("请选择产业分类")) {
//		order.setChanye(request.getChanye());
//	}
//	if (request.getInstitution()!= null) {
//		order.setInstitution(request.getInstitution());
//	}
//	order.setUserId(1);
//	order.setChulizhuangtai("待处理");
//	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//	//System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
//	order.setShenqingshijian(df.format(new Date()));
//	
//	String orderuuid = UUID.randomUUID().toString();
//	order.setUuid(orderuuid);
//	zhishifuwuMapper.insertOrder(order);
//	
//	Linkuser linkuser = new Linkuser();
//	if (request.getName() != null) {
//		linkuser.setName(request.getName());
//	}
//	if (request.getEmail() != null) {
//		linkuser.setEmail(request.getEmail());
//	}
//	if (request.getInstitution()!= null) {
//		linkuser.setInstitution(request.getInstitution());
//	}
//	if (request.getTelephone() != null) {
//		linkuser.setTelephone(request.getTelephone());
//	}
//	String linkuseruuid = UUID.randomUUID().toString();
//	linkuser.setUuid(linkuseruuid);
//	
//	zhishifuwuMapper.insertLinkuser(linkuser);
//	
//	Relation relation = new Relation();
//	relation.setLinkuserId(linkuseruuid);
//	relation.setOrderId(orderuuid);
//	relation.setTag(0);
//	zhishifuwuMapper.insertRelation(relation);
//	return R.ok();
//}
		
	@GetMapping(value = "zhishifuwu/shenqinglist")
	public String ShenqingList(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="userid") String userid, 
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			@RequestParam(required=false,value="front") Integer front,
			Model model) {
		String view="T-shenqing";
		int totalCount = 0;
		if (front!= null && front == 0) {
			view ="T-manageOrder";
			totalCount = zhishifuwuMapper.getTotalOrderCount();
		}else{
			totalCount = zhishifuwuMapper.getOrderCount(userid);
		}
		if(pageSize == null) {
			pageSize = 10;
		}
		if(pageIndex == null) {
			pageIndex = 0;
		}
		if("-1".equals(String.valueOf(pageIndex))) {
			pageIndex = 0;
		}
		//刘冰川 *
		//获取当前登录用户的userid
		//userid = 1;
		int start = Integer.valueOf(pageIndex) * Integer.valueOf(pageSize);
		int end = totalCount-1;
		int totalPages = totalCount/Integer.valueOf(pageSize) + 1;
		if(pageIndex.equals(String.valueOf(totalPages))) {
			pageIndex = pageIndex - 1;
		}else {
			end = pageSize * (pageIndex+1);
		}
		List<Order> orderList = null;
		if (front!= null && front == 0) {
			orderList = zhishifuwuMapper.getTotalOrders(start, end);
		}else{
			orderList = zhishifuwuMapper.getOrders(userid,start, end);
		}
		
		model.addAttribute("orderList", orderList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("userid", userid);
			
		return view;
	}
	
	@GetMapping(value = "zhishifuwu/xiangqing")
	public String XiangQing(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="front") Integer front,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		
		Order order = zhishifuwuMapper.getOrderByUUID(uuid);
		model.addAttribute("order", order);
		List<String> ids = new ArrayList<String>();
		String shenqingfujainId = order.getShenqingfujianId();
		if (shenqingfujainId != null && !shenqingfujainId.equals("")) {
			String[] s = shenqingfujainId.split(";");
			for(String id:s){
				ids.add(id);
			}
		}
		model.addAttribute("ids", ids);
		String tag = "";
		if (order.getChulizhuangtai().equals("已完成") || order.getChulizhuangtai().equals("无法满足")  ) {
			tag = "1";
		}else {
			tag = "0";
		}
		model.addAttribute("tag", tag);
		String chulirenfujianId = order.getChulirenfujianId();
		if (chulirenfujianId != null && !chulirenfujianId.equals("")) {
			model.addAttribute("fujianneme", chulirenfujianId.split("_")[1]);
			model.addAttribute("chufujianId", chulirenfujianId);
		}
		List<Relation> ra= zhishifuwuMapper.getRelationByUUID(uuid);
		//String linkuseruuid = zhishifuwuMapper.getRelationByUUID(uuid);
		for(Relation r:ra){
			if (r.getTag() == 0) {
				Linkuser linkuser = zhishifuwuMapper.getLinkuserByUUID(r.getLinkuserId());
				model.addAttribute("linkuser", linkuser);
			}
			if (r.getTag() == 1) {
				Linkuser linkuser = zhishifuwuMapper.getLinkuserByUUID(r.getLinkuserId());
				model.addAttribute("fulinkuser", linkuser);
			}
		}
		
		String chuliren = null;
		if (zhishifuwuMapper.getUserByID(order.getChuliren()) != null) {
			chuliren = zhishifuwuMapper.getUserByID(order.getChuliren()).getWechat();
			model.addAttribute("chuliren", chuliren);
		}
		String view="T-orderCon";
		if (front!= null && front == 0) {
			view ="T-manageordercon";
		}
		model.addAttribute("front", front);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("uuid", uuid);
		return view;
	}
	
	@GetMapping(value = "zhishifuwu/jiedan")
	public String jiedan(@RequestParam(required=false, value="token") String token,
			@RequestParam(required=false,value="chuliren") String chuliren, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			Model model) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String chulishijian = df.format(new Date());
		zhishifuwuMapper.updateJieDanChulizhuangtaiByUUID(uuid,"处理中",chulishijian,null,chuliren,null);
		//刘冰川
		//获取当前操作员=chuliren
		return "redirect:/zhishifuwu/shenqinglist?front=0&pageIndex="+pageIndex+"&pageSize="+pageSize;
		
	}
	@GetMapping(value = "zhishifuwu/tijiao")
	public String tijiao(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			@RequestParam(required=false,value="chuliyijian") String chuliyijian, 
			@RequestParam(required=false, value="chulirenfujianId") String chulirenfujianId, 
			@RequestParam(required=false,value="front") Integer front,
			Model model,Order order) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String chulishijian = df.format(new Date());
		//刘冰川
	    //获取当前操作员=chuliren
		zhishifuwuMapper.updateChulizhuangtaiByUUID(uuid,"已完成",chulishijian,chuliyijian,chulirenfujianId);
		
		//刘冰川
		//状态：已完成，微信发送通知给用户
		return "redirect:/zhishifuwu/shenqinglist?front=0&pageIndex="+pageIndex+"&pageSize="+pageSize;
		
	}
	
	@GetMapping(value = "zhishifuwu/chexiao")
	public String chexiao(@RequestParam(required=false, value="token") String token, 
			@RequestParam(required=false,value="uuid") String uuid,
			@RequestParam(required=false,value="pageSize") Integer pageSize, 
			@RequestParam(required=false, value="pageIndex") Integer pageIndex, 
			@RequestParam(required=false,value="chuliyijian") String chuliyijian, 
			@RequestParam(required=false, value="chulirenfujianId") String chulirenfujianId, 
			@RequestParam(required=false,value="front") Integer front,
			Model model,Order order) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String chulishijian = df.format(new Date());
		
		//刘冰川
	    //获取当前操作员=chuliren
		zhishifuwuMapper.updateChulizhuangtaiByUUID(uuid,"无法满足",chulishijian,chuliyijian,chulirenfujianId);
		
		//刘冰川
		//状态：无法满足，微信发送通知给用户
		return "redirect:/zhishifuwu/shenqinglist?front=0&pageIndex="+pageIndex+"&pageSize="+pageSize;
		
	}
	public static void main(String[] args){
		String string = "1;";
		String[] s = string.split(";");
		for(String ss : s){
			System.out.println(ss);
		}
		System.out.println(s.length);
		
	}
	
}
