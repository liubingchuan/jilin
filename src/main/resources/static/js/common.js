$("#quit").click(function(){
	$.cookie("openId", "", {expires: -1});
	$.cookie("nickName", "", {expires: -1});
	$.cookie("headImg", "", {expires: -1});
	$.cookie("role", "", {expires: -1});
	window.location.href = "/";
});

$("#login").click(function(){
	$("#alertbd").css('display','none');
	$("#alertZh").css('display','block');
});

$("#chanyejiance").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/jiance/jiancelist";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#xituzhiku").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/org/list";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#zhishifuwu").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/zhishifuwu/zhishifuwu";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

function showLoginInfoFrontend(){
	$("#alertbd").css('display','none');
    if($.cookie("role")!=null) {
		$("#loginBefore").css('display','none'); 
		$("#headImg").attr("src", $.cookie("headImg"));
		$("#loginAfter").css('display','block');
		if($.cookie("role")!=null && $.cookie("role")==="visitor"){
			$(".vip").html("普通用户"); 
			$("#backgroud").css('display','none'); 
		}
		if($.cookie("role")!=null && $.cookie("role")==="admin"){
			$(".vip").html("admin"); 
			$("#backgroud").css('display','block'); 
		}
		$(".vip").html($.cookie("role"));
		$("#logoinName").html($.cookie("loginName"));
	}else {
		$("#loginBefore").css('display','block'); 
		$("#loginAfter").css('display','none'); 
		//document.getElementById("xituzhiku").removeAttribute("href");
	}
}