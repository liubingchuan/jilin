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
public class ZilingyuController {

	private static final Logger logger = LoggerFactory.getLogger(ZilingyuController.class);
	
	private static final String MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={TOKEN}";
	
	@Autowired
    private ZhishifuwuMapper zhishifuwuMapper;
	
	@GetMapping(value = "zilingyu/zilingyu")
	public String zilingyu(Model model) {
		
		return "zilingyu";
	}
	
}
