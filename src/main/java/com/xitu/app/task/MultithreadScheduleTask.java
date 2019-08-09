package com.xitu.app.task;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.xitu.app.mapper.PriceMapper;
import com.xitu.app.model.Jiance;
import com.xitu.app.model.Price;
import com.xitu.app.repository.JianceRepository;

@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync        // 2.开启多线程
public class MultithreadScheduleTask {

	@Autowired
    private PriceMapper priceMapper;
	
	@Autowired
    private JianceRepository jianceRepository;
	
	@Async
    @Scheduled(cron = "0 0 0,12,14,21 * * ?") 
//	@Async
//	@Scheduled(cron = "*/15 * * * * ?")  //间隔十五秒
    public void price() throws InterruptedException {

		final String url="http://nm.sci99.com/news/s8784.html" ;
		String single = "http://nm.sci99.com";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String ymd = formatter.format(date);

        try {
        	
        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
        	
        	if(exist != null) {
        		return;
        	}

            Document doc = Jsoup.connect(url).get();

            Elements module = doc.getElementsByClass("ul_w690");
            if(!module.text().contains(ymd)) {
            	return;
            }
            System.out.println(module.text());

            Document moduleDoc = Jsoup.parse(module.toString());

            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式

jump:
            for (Element li : lis){
                Document liDoc = Jsoup.parse(li.toString());
                Elements hrefs = liDoc.select("a[href]");
                for(Element elem: hrefs) {
                	if(!"".equals(elem.attr("href"))){
                		String href = elem.attr("href");
                		single = single + href;
                		break jump;
                	}
                }

            }
            
            Document singleDoc = Jsoup.connect(single).get();
//            if(!singleDoc.toString().contains(ymd)){
//            	return;
//            }
            Element zoom = singleDoc.getElementById("zoom");
            Elements trElements = zoom.select("tr");
            boolean ignore = true;
            for(Element tdelement : trElements) {
            	if(ignore) {
            		ignore = false;
            		continue;
            	}
            	Elements tdes = tdelement.select("td");
            	Price price = new Price();
            	price.setUpdateTime(formatter.format(date));
        		price.setName(tdes.get(0).text());
        		price.setDescription(tdes.get(1).text());
        		price.setUnit(tdes.get(6).text());
        		price.setPrice(tdes.get(3).text());
        		price.setAvg(tdes.get(4).text());
        		Price yesterday = priceMapper.getLatestPrice(price.getName());
        		if(yesterday == null) {
        			price.setFloating("100%");
        		}else {
        			float before = Float.valueOf(yesterday.getAvg());
        			float now = Float.valueOf(tdes.get(4).text());
        			float delta = now - before;
        			if(delta != 0) {
        				System.out.println();
        			}
        			NumberFormat numberFormat = NumberFormat.getInstance();
        			numberFormat.setMaximumFractionDigits(2);
        			String result = numberFormat.format(delta / before * 100);
        			price.setFloating(result + "%");
        		}
            	priceMapper.insertPrice(price);
            }
              //  String title = clearfixli.getElementsByTag("a").text();
            System.out.println("fasdf");

          //  System.out.println(clearfix);

        } catch (IOException e) {
            e.printStackTrace();
        }
	
    }
	
	@Async
    @Scheduled(cron = "0 0 13 * * ?")
//	@Scheduled(cron = "*/30 * * * * ?")  //间隔十五秒
	public void jiance(){
    	List<Jiance> objs = new LinkedList<Jiance>();
    	try {
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("http://35.201.235.191:3000/users/1/web_requests/15/xituzixun.xml", "新闻动态");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/18/xituguojiazhengce.xml", "国家政策");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/37/xituzaixian.xml", "新闻动态");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/40/ruidaoxitu.xml", "新闻动态");
    		map.put("http://35.201.235.191:3000/users/1/web_requests/42/SCI.xml", "科研进展");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int i=1;
			for(Map.Entry<String, String> kv: map.entrySet()) {
				try (XmlReader reader = new XmlReader(new URL(kv.getKey()))) {
					SyndFeed feed = new SyndFeedInput().build(reader);
					System.out.println(feed.getTitle());
					System.out.println("***********************************");
					for (SyndEntry entry : feed.getEntries()) {
						Jiance jiance = new Jiance();
						jiance.setId(UUID.randomUUID().toString());
						jiance.setTitle(entry.getTitle());
						jiance.setDescription(entry.getDescription().getValue());
						jiance.setPubtime(sdf.format(entry.getPublishedDate()));
						jiance.setLanmu(kv.getValue());
						if(i==3){
							jiance.setInstitution("稀土在线");
						}else if(i==4) {
							jiance.setInstitution("瑞道稀土");
						}else if(i==5) {
							jiance.setInstitution("科睿唯安");
						}else {
							jiance.setInstitution("中国稀土网");
						}
						objs.add(jiance);
						System.out.println("***********************************");
					}
					System.out.println("Done");
				}
				i++;
			}
			jianceRepository.saveAll(objs);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 }
