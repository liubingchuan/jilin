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
		$("#loginBefore").css('display','none'); 
		$("#headImg").attr("src", $.cookie("headImg"));
		$("#loginAfter").css('display','block');
		if(($.cookie("role")==null) || ($.cookie("role")!=null && $.cookie("role")==="普通用户")){
			$(".vip").html("普通用户"); 
			$("#backgroud").css('display','none'); 
			document.getElementById("xituzhiku").removeAttribute("href");
			document.getElementById("chanyejiance").removeAttribute("href");
			document.getElementById("zhishifuwu").removeAttribute("href");
			document.getElementById("zhishifuwu2").removeAttribute("href");
			
		}
		if($.cookie("role")!=null && $.cookie("role")==="admin"){
			$(".vip").html("admin"); 
			$("#backgroud").css('display','block'); 
		}
		$("#logoinName").html($.cookie("nickName"));
	}else {
		$("#loginBefore").css('display','block'); 
		$("#loginAfter").css('display','none'); 
		
		document.getElementById("xituzhiku").removeAttribute("href");
		document.getElementById("chanyejiance").removeAttribute("href");
		document.getElementById("zhishifuwu").removeAttribute("href");
		document.getElementById("zhishifuwu2").removeAttribute("href");
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