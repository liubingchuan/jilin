//首页切换
$('.indexLFTL a').click(function(){
		var index = $(this).index()
		 $(this).addClass('ahover').siblings().removeClass('ahover');
		 $(".indexLFBM").eq(index).show().addClass('active').siblings().removeClass('active').hide();
	});
//弹窗
$('.loginBtn').click(function(){
		$(".alertBg").fadeIn();
		$(".alertErw").fadeIn();
	});
$('.alertZHbtn').click(function(){
		$(".alertZh").fadeIn();
		$(".alertErw").fadeOut();
	});
//$('.chaxinSubmit').click(function(){
		//$(".alertBg").fadeIn();
		//$(".alertxuq").fadeIn();
	//});
$('.alertxuqaNext').click(function(){
		$(".alertBg").fadeOut();
		$(".alertxuq").fadeOut();
});
$('.alertReg a').click(function(){
		$(".alertZh").fadeOut();
		$(".alertErw").fadeIn();
	});
$('.alertBg').click(function(){
		$(".alertBg").fadeOut();
		$(".alertCon").fadeOut();
	});
//后台管理菜单
$('.manageMenuF').click(function(){
		$(this).next(".manageMenuS").slideToggle();
	});
//全选
$('.allInp').click(function(){
		if(this.checked){
			$(this).parents(".usermanageTable").find("input").prop("checked",true); 
		}
		else{
			$(this).parents(".usermanageTable").find("input").prop("checked",false); 
		}
	});
//基本信息切换
$('.M_messTwoTl a').click(function(){
		var index = $(this).index()
		 $(this).addClass('ahover').siblings().removeClass('ahover');
		 $(".M_messTwoBm").eq(index).show().addClass('active').siblings().removeClass('active').hide();
	});
//左侧展开更多
$('.jiansuoLfMore').toggle(function(){
		$(this).html("收起");
		$(this).parents(".jiansuoLfGp").find(".jiansuoLfGpList").addClass("jiansuoLfGpListh");
	},function(){
		$(this).html("展开更多");
		$(this).parents(".jiansuoLfGp").find(".jiansuoLfGpList").removeClass("jiansuoLfGpListh");
	});
$('.jiansuoLImore').toggle(function(){
		$(this).html("收起");
		$(this).parents(".jiansuoLiText").find(".jiansuoLiTextSpan").addClass("span");
	},function(){
		$(this).html("展开更多");
		$(this).parents(".jiansuoLiText").find(".jiansuoLiTextSpan").removeClass("span");
	});
//专利详情页切换
$('.zhuanliConTwo a').click(function(){
		var index = $(this).index()
		 $(this).addClass('ahover').siblings().removeClass('ahover');
		 $(".zhuanliConThreeM").eq(index).show().addClass('active').siblings().removeClass('active').hide();
	});
//第二批
//添加主要联系人
$('.chaxinThree').live("click",function(){
		var ochaxin = $(".chaxinTwoB1").html()
		$(".chaxinTwo").append("<div class='chaxinK chaxinTwoGp chaxinTwoqt'>"+"<em class='chaxintwoDel'>×</em>"+"<div class='chaxinTwoTl'>"+"其他联系人"+"</div>"+"<div class='chaxinTwoB'>"+ochaxin+"</div>"+"</div>");
		document.getElementById('add').style.display='none';
});
$('.chaxintwoDel').live("click",function(){
		$(this).parents(".chaxinTwoGp").remove();
		document.getElementById('add').style.display='block';
	});
$('.chaxinzjADD').live("click",function(){
		var ochaxinzj = $(this).parents(".chaxinzjGp").html()
		$(this).parents(".chaxinzj").append("<div class='clearfix chaxinzjGp'>"+ochaxinzj+"</div>");
	});
$('.chaxinzjdel').live("click",function(){
		$(this).parents(".chaxinzjGp").remove();
	});
//检测左侧分类
$('.jiansuoLfGpTl').click(function(){
		$(this).next(".jiancelfgpB").slideToggle();
	});
//排序
$('.paixuico').click(function(){
		$(this).toggleClass("paixuicoh");
	});
//专利
$('.zhuanlTwo a').click(function(){
		 $(this).addClass('ahover').siblings().removeClass('ahover');
	});