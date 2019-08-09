$("#quit").click(function(){
	$.cookie("openId", "", {expires: -1});
	$.cookie("nickName", "", {expires: -1});
	$.cookie("headImg", "", {expires: -1});
	$.cookie("role", "", {expires: -1});
	window.location.href = "/";
});

function showLoginInfo(){
	//$.cookie("openId","2");
	//$.cookie("role","管理员");
	//$.cookie("role","操作员");
	//$.cookie("nickName","测试人");
	if($.cookie("openId")!=null) {
		$("#headImg").attr("src", $.cookie("headImg"));
		$("#logoinName").html($.cookie("nickName"));
		$("#logoinNames").html($.cookie("nickName"));
		$("#logoinRole").html($.cookie("role"));
		
		if($.cookie("role")!=null && $.cookie("role")==="操作员"){
			document.getElementById("fenleishezhi").removeAttribute("href");
			document.getElementById("hangyerencai").removeAttribute("href");
			document.getElementById("zhogndianjigou").removeAttribute("href");
			document.getElementById("fenxibaogao").removeAttribute("href");
			document.getElementById("jieguoliebiao").removeAttribute("href");
			document.getElementById("xiangmuxinxi").removeAttribute("href");
		}
	}
}

function showLoginInfoFrontend(){
	//$.cookie("openId","1");
	if($.cookie("openId")!=null) {
		$("#headImg").attr("src", $.cookie("headImg"));
		if($.cookie("role")!=null && $.cookie("role")==="普通用户"){
			$(".vip").html("普通用户"); 
			$("#backgroud").css('display','none'); 
			document.getElementById("xituzhiku").removeAttribute("href");
			document.getElementById("chanyejiance").removeAttribute("href");
			document.getElementById("zhishifuwu").removeAttribute("href");
		}
		if($.cookie("role")!=null && $.cookie("role")==="管理员"){
			$(".vip").html("管理员"); 
			$("#backgroud").css('display','block'); 
		}
		$("#logoinName").html($.cookie("nickName"));
	}
}