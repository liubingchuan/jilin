package com.xitu.app.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xitu.app.common.R;
import com.xitu.app.constant.Constant;
import com.xitu.app.mapper.UserMapper;
import com.xitu.app.model.Message;
import com.xitu.app.model.QrCodeParam;
import com.xitu.app.model.User;
import com.xitu.app.model.WeChatUserInfo;
import com.xitu.app.service.Cache;
import com.xitu.app.service.IMsgService;
import com.xitu.app.utils.GsonUtil;
import com.xitu.app.utils.HttpRequest;
import com.xitu.app.utils.XmlUtil;
import com.xitu.app.utils.aes.AesException;
import com.xitu.app.utils.aes.WXBizMsgCrypt;

@Controller
public class WeChatController {

	@Autowired
	private IMsgService msgService;

	@Autowired
	private Message message;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private Cache cache;

	// 获取ticket
	private static final String GET_QRCODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token={TOKEN}";
	// 换取二维码
	private static final String QR_CODE_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket={TICKET}";
	// 获取用户信息
	private static final String GETUSERINFOURL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={ACCESS_TOKEN}&openid={OPENID}";

	@RequestMapping(value = "/wx", produces = "application/json;charset=utf-8")
	public void entry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println(request.getSession().getId());
		// 微信加密签名
		String msgSignature = request.getParameter("signature");
		// 时间戳
		String timeStamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echoStr = request.getParameter("echostr");
		Map<String, String> map = null;
		String msgType = null;

		try {
			// 解析xml数据，将解析结果存储在HashMap中
			map = new HashMap<>();
			// 读取输入流
			SAXReader reader = new SAXReader();
			Document document = reader.read(request.getInputStream());
			// 得到xml根元素
			Element root = document.getRootElement();
			XmlUtil.parserXml(root, map);
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println("key:" + entry.getKey() + "-value:" + entry.getValue());
			}
			if (null != map && !map.isEmpty()) {
				// 消息类型
				msgType = map.get("MsgType");
			}
		} catch (DocumentException e) {
		} catch (IOException e) {
		}

		String result = null;
		try {
			if (StringUtils.isEmpty(msgType)) {
				PrintWriter out = null;
				try {
					// 创建加密类
					WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(Constant.WechatAccount.TOKEN,
							Constant.WechatAccount.ENCODINGAESKEY, Constant.WechatAccount.APPID);
					out = response.getWriter();
					// 比对msgSignature 用token, timeStamp,
					// nonce加密的参数是否一致，一致证明该接口来自微信，异常则不是来自微信
					out.write(wxcpt.verifyUrl_WXGZ(msgSignature, Constant.WechatAccount.TOKEN, timeStamp, nonce,
							echoStr));
					System.out.println("验证成功");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (out != null) {
						out.close();
					}
				}

			} else {
				if (msgType.equals(Constant.MsgType.TEXT)) {
					result = msgService.returnText(map);
				} else if (msgType.equals(Constant.MsgType.EVENT)) { // 事件处理
					String event = map.get("Event");
					if (event.equals(Constant.Event.SUBSCRIBE)) { // 关注公众号
						result = msgService.returnText(map, message.getSubscribe());
						String eventKeyValue = map.get("EventKey");
						String openId = map.get("FromUserName");
						if (eventKeyValue.contains("_")) {
							String[] ek = eventKeyValue.split("_");
							cache.save(ek[1], openId + "%unbind");
							String getUserInfoUrl = GETUSERINFOURL.replace("{ACCESS_TOKEN}", Constant.ACCESS_TOKEN).replace("{OPENID}", openId);
							String userInfoStr = HttpRequest.get(getUserInfoUrl, null, false);
					        WeChatUserInfo weChatUserInfo = GsonUtil.fromJson(userInfoStr, WeChatUserInfo.class);
					        String nickName = weChatUserInfo.getNickName();
					        User user = userMapper.getUserByOpenId(openId);
					        if(user == null) {
					        	user = new User();
					        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					        	user.setWechat(nickName);
					        	user.setOpenId(openId);
					        	user.setRole("普通用户");
					        	user.setStamp(df.format(new Date()));
					        	userMapper.insertUser(user);
					        	System.out.println("插入新的用户，wechat 名字为 " + nickName);
					        	System.out.println("插入新的用户，openId 为 " + openId);
					        }else {
					        	System.out.println("已有用户无需新插入该openId" + openId);
					        }
					        
						}
					} else if (event.equals(Constant.Event.CLICK)) { // 自定义菜单点击事件
						String eventKey = map.get("EventKey");
						if (eventKey.equals("V1001_TODAY_MUSIC")) {
							result = msgService.returnText(map, "今日歌曲如下：");
						}
					} else if (event.equals(Constant.Event.SCAN)) { // 扫码事件
						String eventKey = map.get("EventKey");
						String openId = map.get("FromUserName");
						cache.save(eventKey, openId + "%binded");
						result = msgService.returnText(map, "扫描临时二维码");
						// if (eventKey.equals("temp_qrcode_test")) { //临时二维码
						// result = msgService.returnText(map, "扫描临时二维码");
						// } else if (eventKey.equals("permanent_qrcode_test"))
						// {
						// result = msgService.returnText(map, "扫描永久二维码");
						// }
					}
				}
			}
			System.out.println(result);
		} catch (AesException e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@GetMapping(value = "wechat/getLoginQcode")
	public R getLoginQcode(HttpServletRequest request) {
		String sessionId = request.getSession().getId();
		QrCodeParam qrCodeParam = new QrCodeParam();
		QrCodeParam.ActionInfo actionInfo = qrCodeParam.new ActionInfo();
		QrCodeParam.ActionInfo.Scene scene = actionInfo.new Scene();
		// 设置场景值
		scene.setSceneStr(sessionId);
		actionInfo.setScene(scene);
		qrCodeParam.setActionInfo(actionInfo);
		qrCodeParam.setActionName("QR_STR_SCENE");
		qrCodeParam.setExpireSeconds(604800);

		String param = GsonUtil.toJson(qrCodeParam);
		String qrCodeUrl = QR_CODE_URL.replace("{TICKET}", getTicket(param));
		System.out.println(qrCodeUrl);
		return R.ok().put("img", qrCodeUrl);
	}

	/**
	 * 获取ticket
	 *
	 * @param param
	 * @return
	 */
	private String getTicket(String param) {
		String url = GET_QRCODE_URL.replace("{TOKEN}", Constant.ACCESS_TOKEN);
		String jsonStr = HttpRequest.post(url, param, null, Constant.ContentType.APPLICATION_JSON, false);
		Map<String, Object> map = GsonUtil.fromJson(jsonStr, Map.class);
		return map.get("ticket").toString();
	}

	/**
	 * 判断是否登录，用于微信扫码后
	 *
	 * @return
	 */
	@RequestMapping("wechat/islogin")
	@ResponseBody
	public R isLogin(HttpServletRequest request) {
		String sessionId = request.getSession().getId();
		System.out.println("试图获取的sessionId   " + sessionId);
		String openIdInfo = cache.get(sessionId);
		
		if (openIdInfo == null) {
			System.out.println("openIdInfo 未找到");
			return R.notFound();
		}
		String openId = "";
		String bind = "true";
		openId = openIdInfo.split("%")[0];
		if (openIdInfo.contains("unbind")) {
			bind = "false";
		} else {
			bind = "true";
		}
		String getUserInfoUrl = GETUSERINFOURL.replace("{ACCESS_TOKEN}", Constant.ACCESS_TOKEN).replace("{OPENID}", openId);
		String userInfoStr = HttpRequest.get(getUserInfoUrl, null, false);
        WeChatUserInfo weChatUserInfo = GsonUtil.fromJson(userInfoStr, WeChatUserInfo.class);
        String nickName = weChatUserInfo.getNickName();
        String headUrl = weChatUserInfo.getHeadImgUrl();
        int retry = 3;
        if(nickName == null || headUrl == null) {
        	while(retry != 0) {
        		userInfoStr = HttpRequest.get(getUserInfoUrl, null, false);
        		weChatUserInfo = GsonUtil.fromJson(userInfoStr, WeChatUserInfo.class);
        		nickName = weChatUserInfo.getNickName();
        		headUrl = weChatUserInfo.getHeadImgUrl();
        		if(nickName == null || headUrl == null) {
        			retry--;
        			System.out.println("获取用户信息失败，重试1次");
        			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}else {
        			break;
        		}
        	}
        }
        User user = userMapper.getUserByOpenId(openId);
        String role = user==null?"普通用户":user.getRole();
        System.out.println("nickName--"+ nickName);
        System.out.println("headUrl---" + headUrl);
        System.out.println("openId---" + openId);
        System.out.println("role---" + role);
		return R.ok().put("openId", openId).put("bind", bind).put("nickName", nickName).put("headUrl", headUrl).put("role", role);

	}

	// @RequestMapping(value = "wx",method=RequestMethod.GET)
	// public void login(HttpServletRequest request,HttpServletResponse
	// response){
	// System.out.println("success");
	// String signature = request.getParameter("signature");
	// String timestamp = request.getParameter("timestamp");
	// String nonce = request.getParameter("nonce");
	// String echostr = request.getParameter("echostr");
	// System.out.println("signature------->" + signature);
	// System.out.println("timestamp------->" + timestamp);
	// System.out.println("nonce------->" + nonce);
	// System.out.println("echostr------->" + echostr);
	// PrintWriter out = null;
	// try {
	// out = response.getWriter();
	// if(CheckUtil.checkSignature(signature, timestamp, nonce)){
	// System.out.println("验证成功");
	// out.write(echostr);
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }finally{
	// out.close();
	// }
	//
	// }

}
