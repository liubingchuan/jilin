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
import java.util.TreeMap;
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
    private JianceRepository jianceRepository;
	
	
	
	@Async
	@Scheduled(cron = "0 0 13 * * ?")
//	@Scheduled(cron = "*/30 * * * * ?")  //间隔十五秒
	public void jiance2(){
		List<Jiance> objs = new LinkedList<Jiance>();
		try {
			Map<String, String> map = new TreeMap<String, String>();
			map.put("http://35.201.235.191:3000/users/1/web_requests/64/guojiazhengce.xml", "国家政策");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int i=1;
			for(Map.Entry<String, String> kv: map.entrySet()) {
				try (XmlReader reader = new XmlReader(new URL(kv.getKey()))) {
					SyndFeed feed = new SyndFeedInput().build(reader);
					System.out.println(feed.getTitle());
					System.out.println("***********************************");
					for (SyndEntry entry : feed.getEntries()) {
						if(!this.isNow(sdf.format(entry.getPublishedDate()))) {
							continue;
						}
						Jiance jiance = new Jiance();
						jiance.setId(UUID.randomUUID().toString());
						jiance.setTitle(entry.getTitle());
						jiance.setDescription(entry.getDescription().getValue());
						jiance.setPubtime(sdf.format(entry.getPublishedDate()));
						jiance.setLanmu(kv.getValue());
						jiance.setInstitution("吉林能源");
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
	
	private boolean isNow(String date) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //获取今天的日期
        String nowDay = sf.format(now);
        //对比的时间
        return date.equals(nowDay);
    }
	
	@Async
	@Scheduled(cron = "0 0 13 * * ?")
//	@Scheduled(cron = "*/30 * * * * ?")  //间隔十五秒
	public void jiance3(){
		List<Jiance> objs = new LinkedList<Jiance>();
		try {
			Map<String, String> map = new TreeMap<String, String>();
			map.put("http://35.201.235.191:3000/users/1/web_requests/58/sohuyiyao.xml", "行业新闻");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for(Map.Entry<String, String> kv: map.entrySet()) {
				try (XmlReader reader = new XmlReader(new URL(kv.getKey()))) {
					SyndFeed feed = new SyndFeedInput().build(reader);
					System.out.println(feed.getTitle());
					System.out.println("***********************************");
					for (SyndEntry entry : feed.getEntries()) {
						if(!this.isNow(sdf.format(entry.getPublishedDate()))) {
							continue;
						}
						Jiance jiance = new Jiance();
						jiance.setId(UUID.randomUUID().toString());
						jiance.setTitle(entry.getTitle());
						jiance.setDescription(entry.getDescription().getValue());
						jiance.setPubtime(sdf.format(entry.getPublishedDate()));
						jiance.setLanmu(kv.getValue());
						jiance.setInstitution("吉林能源");
						objs.add(jiance);
						System.out.println("***********************************");
					}
					System.out.println("Done");
				}
			}
			jianceRepository.saveAll(objs);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Async
	@Scheduled(cron = "0 0 13 * * ?")
//	@Scheduled(cron = "*/30 * * * * ?")  //间隔十五秒
	public void jiance4(){
		List<Jiance> objs = new LinkedList<Jiance>();
		try {
			Map<String, String> map = new TreeMap<String, String>();
			map.put("http://35.201.235.191:3000/users/1/web_requests/61/chanyedongtai.xml", "产业动态");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for(Map.Entry<String, String> kv: map.entrySet()) {
				try (XmlReader reader = new XmlReader(new URL(kv.getKey()))) {
					SyndFeed feed = new SyndFeedInput().build(reader);
					System.out.println(feed.getTitle());
					System.out.println("***********************************");
					for (SyndEntry entry : feed.getEntries()) {
						if(!this.isNow(sdf.format(entry.getPublishedDate()))) {
							continue;
						}
						Jiance jiance = new Jiance();
						jiance.setId(UUID.randomUUID().toString());
						jiance.setTitle(entry.getTitle());
						jiance.setDescription(entry.getDescription().getValue());
						jiance.setPubtime(sdf.format(entry.getPublishedDate()));
						jiance.setLanmu(kv.getValue());
						jiance.setInstitution("吉林能源");
						objs.add(jiance);
						System.out.println("***********************************");
					}
					System.out.println("Done");
				}
			}
			jianceRepository.saveAll(objs);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Async
	@Scheduled(cron = "0 0 13 * * ?")
//	@Scheduled(cron = "*/30 * * * * ?")  //间隔十五秒
	public void jiance5(){
		List<Jiance> objs = new LinkedList<Jiance>();
		try {
			Map<String, String> map = new TreeMap<String, String>();
			map.put("http://35.201.235.191:3000/users/1/web_requests/66/keyanqianyan.xml", "科研进展");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for(Map.Entry<String, String> kv: map.entrySet()) {
				try (XmlReader reader = new XmlReader(new URL(kv.getKey()))) {
					SyndFeed feed = new SyndFeedInput().build(reader);
					System.out.println(feed.getTitle());
					System.out.println("***********************************");
					for (SyndEntry entry : feed.getEntries()) {
						if(!this.isNow(sdf.format(entry.getPublishedDate()))) {
							continue;
						}
						Jiance jiance = new Jiance();
						jiance.setId(UUID.randomUUID().toString());
						jiance.setTitle(entry.getTitle());
						jiance.setDescription(entry.getDescription().getValue());
						jiance.setPubtime(sdf.format(entry.getPublishedDate()));
						jiance.setLanmu(kv.getValue());
						jiance.setInstitution("吉林能源");
						objs.add(jiance);
						System.out.println("***********************************");
					}
					System.out.println("Done");
				}
			}
			jianceRepository.saveAll(objs);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
 }
