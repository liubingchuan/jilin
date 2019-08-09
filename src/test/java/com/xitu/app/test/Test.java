package com.xitu.app.test;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.xitu.app.model.Jiance;
import com.xitu.app.model.Patent;
import com.xitu.app.model.Price;

public class Test {
	
//	public static void main(String[] args) {
//		final String url="http://nm.sci99.com/news/s8784.html" ;
//		String single = "http://nm.sci99.com";
//
//        try {
//
//            Document doc = Jsoup.connect(url).get();
//
//
//            Elements module = doc.getElementsByClass("ul_w690");
//
//            Document moduleDoc = Jsoup.parse(module.toString());
//
//            //Elements clearfix = moduleDoc.getElementsByClass("clearfix");  //DOM的形式
//
//            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式
//
//jump:
//            for (Element li : lis){
//                Document liDoc = Jsoup.parse(li.toString());
//                Elements hrefs = liDoc.select("a[href]");
//                for(Element elem: hrefs) {
//                	if(!"".equals(elem.attr("href"))){
//                		String href = elem.attr("href");
//                		single = single + href;
//                		break jump;
//                	}
//                }
//
//            }
//            
//            Document singleDoc = Jsoup.connect(single).get();
//            Element zoom = singleDoc.getElementById("zoom");
//            Elements trElements = zoom.select("tr");
//            boolean ignore = true;
//            for(Element tdelement : trElements) {
//            	if(ignore) {
//            		ignore = false;
//            		continue;
//            	}
//            	Elements tdes = tdelement.select("td");
//            	for(int i = 0; i < tdes.size(); i++){
//            		if(i==0) {
//            			
//            		}
//					System.out.println(tdes.get(i).text());
//				}
//
//            }
//              //  String title = clearfixli.getElementsByTag("a").text();
//            System.out.println("fasdf");
//
//          //  System.out.println(clearfix);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//	}
//	public static void main(String[] args) {
////		disableSslVerification();
////		int timer = 0;
////		while (timer < 10) {
////			java.awt.Toolkit.getDefaultToolkit().beep();
////			try {
////				Thread.sleep(1000);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}finally{
////				timer++;
////			}
////			
////		}
//		final String url="http://www2.soopat.com/Home/Result" ;
////		final String url="https://patentscope2.wipo.int/search/en/result.jsf?currentNavigationRow=8&prevCurrentNavigationRow=7&office=&prevFilter=&maxRec=63961&listLengthOption=10" ;
////		final String url="http://www.soopat.com/Home/Result" ;
//		String base = "http://www.soopat.com";
//		Map<String,String> innerPathMap = new HashMap<String,String>();
//////		Map<String, String> header = new HashMap<String, String>();
//////		header.put("Cookie", "JSESSIONID=607650CA1463FA889761A4DA246B499C; SERVERID=ps1-jp; _pk_ses.5.5313=1; _pk_id.5.5313=23643550a8ef3c45.1554595917.1.1554598461.1554595917.");
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("SearchWord", "稀土");
//		map.put("FMZL", "Y");
//		map.put("SYXX", "Y");
//		map.put("WGZL", "Y");
//		map.put("FMSQ", "Y");
//		map.put("PatentIndex", "10");
//		try {
//			Connection conn = Jsoup.connect(url);
//			conn.data(map);
////			conn.headers(header);
//			Document doc = conn.get();
//			
//			System.out.println(doc.toString());
//			
//			
////			try{
////			      String data = " This content will append to the end of the file";
////
////			      File file =new File("javaio-appendfile.txt");
////
////			      //if file doesnt exists, then create it
////			      if(!file.exists()){
////			       file.createNewFile();
////			      }
////
////			      //true = append file
////			      FileWriter fileWritter = new FileWriter(file.getName(),true);
////			      fileWritter.write(doc.toString());
////			      fileWritter.close();
////
////			      System.out.println("Done");
////
////			     }catch(IOException e){
////			      e.printStackTrace();
////			     }
//			
//			Elements patentBlocks = doc.getElementsByClass("PatentBlock");
//			
//			for(Element patentBlock: patentBlocks) {
//				Document patentDoc = Jsoup.parse(patentBlock.toString());
//				Elements patentTypeElements = patentDoc.getElementsByClass("PatentTypeBlock");
//				if(patentTypeElements.size() == 0) {
//					continue;
//				}
//jump:
//				for(Element pte: patentTypeElements) {
//					// FIXME
//					System.out.println(pte.text());
//					String type = pte.text().split(" ")[0];
//					Document pteDoc = Jsoup.parse(pte.toString());
//					Elements hrefs = pteDoc.select("a[href]");
//					for(Element elem: hrefs) {
//	                	if(!"".equals(elem.attr("href"))){
//	                		String href = elem.attr("href");
//	                		innerPathMap.put(href, type);
////	                		innerPaths.add(href);
//	                		break jump;
//	                	}
//	                }
//				}
//			}
//			
//			System.out.println("fasdf");
////
////		for(String path: innerPaths) {
////			String target = base + path;
////			Connection singlePageConn = Jsoup.connect(target);
////			Document singleDoc = singlePageConn.get();
////			System.out.println(singleDoc.getElementsByTag("h1").val());
////			System.out.println(singleDoc);
////		}
////			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		Patent patent = new Patent();
//		String singleUrl = "http://www.soopat.com/Patent/201280048571";
//		Connection singlePageConn = Jsoup.connect(singleUrl);
//		try {
//			Document singleDoc = singlePageConn.get();
////			System.out.println(singleDoc);
//			Elements h1Elements = singleDoc.getElementsByTag("h1");
//			for(Element h1Element: h1Elements) {
//				String title = h1Element.text();
//				if(title != null) {
//					patent.setTitle(title.split(" ")[0]);
//					patent.setLawstatus(title.split(" ")[1]);
//					break;
//				}
//			}
//			
//			Elements grayElements = singleDoc.getElementsByClass("gray");
//			for(Element grayElement: grayElements) {
//				String appliance = grayElement.text();
//				if(appliance != null) {
//					String[] s = appliance.split(" ");
//					if(s.length>=2) {
//						patent.setApplynumber(s[0].substring(4, s[0].length()));
//						patent.setApplytime(s[1].substring(4, s[1].length()));
//						break;
//					}
//				}
//			}
//			
//			Elements datainfoElements = singleDoc.getElementsByClass("datainfo");
//			for(Element dataInfo : datainfoElements) {
//				Elements tdelements = dataInfo.getElementsByTag("td");
//				for(Element td: tdelements) {
//					if(td.text().contains("摘要：")){
//						patent.setSubject(td.text());
//					}else if(td.text().contains("申请人：")){
//						List<String> persons = new ArrayList<String>();
//						persons.add(td.text().replace("申请人：", ""));
//						patent.setPerson(persons);
//					}else if(td.text().contains("发明(设计)人：")) {
//						List<String> creators = new ArrayList<String>();
//						creators.add(td.text().replace("发明(设计)人：", ""));
//						patent.setCreator(creators);
//					}else if(td.text().contains("分类号：") && (!td.text().contains("主分类号："))) {
//						List<String> ipcs = new ArrayList<String>();
//						String ipc = td.text().replace("分类号：", "");
//						String[] ipcArray = ipc.split(" ");
//						for(String s: ipcArray) {
//							ipcs.add(s);
//						}
//						patent.setIpc(ipcs);
//					}
//					System.out.println(td.text());
//				}
//			}
//			Elements vipcomElements = singleDoc.getElementsByClass("vipcom");
//			for(Element vipcom: vipcomElements) {
//				String s = vipcom.toString();
//				if(s.contains("其他信息")) {
//					int i = 0;
//					Elements tdelements = vipcom.getElementsByTag("td");
//					for(Element td: tdelements) {
//						if(i==1) {
//							patent.setClaim(td.text());
//						}else if(i==3) {
//							patent.setPublicnumber(td.text());
//						}else if(i==5) {
//							patent.setPublictime(td.text());
//							if(td.text().contains("-")) {
//								patent.setPublicyear(td.text().split("-")[0]);
//							}
//						}
//						i++;
//					}
//				}
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
//	public static void main(String[] args) {
//		try {
//			String url = "http://35.201.235.191:3000/users/1/web_requests/15/xituzixun.xml";
//			String type = "新闻动态";
// 
//			try (XmlReader reader = new XmlReader(new URL(url))) {
//				SyndFeed feed = new SyndFeedInput().build(reader);
//				System.out.println(feed.getTitle());
//				System.out.println("***********************************");
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				for (SyndEntry entry : feed.getEntries()) {
//					Jiance jiance = new Jiance();
//					jiance.setTitle(entry.getTitle());
//					jiance.setDescription(entry.getDescription().getValue());
//					jiance.setPubtime(sdf.format(entry.getPublishedDate()));
//					
//					System.out.println("***********************************");
//				}
//				System.out.println("Done");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
	
//	public static void main(String[] args) {
////		final String url="http://nm.sci99.com/news/s8784.html" ;
//        
//		for(int i=1;i<5;i++) {
//			String url="http://nm.sci99.com/news/?page=" + i + "&sid=8784&siteid=10" ;
//			String base = "http://nm.sci99.com";
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//			Date date = new Date();
//			String ymd = formatter.format(date);
//			try {
//	        	
////	        	Price exist = priceMapper.getPriceByUpdateTime(ymd);
//	        	
////	        	if(exist != null) {
////	        		return;
////	        	}
//
//	            Document doc = Jsoup.connect(url).get();
//
//	            Elements module = doc.getElementsByClass("ul_w690");
//
//	            Document moduleDoc = Jsoup.parse(module.toString());
//
//	            Elements lis = moduleDoc.getElementsByTag("li");  //选择器的形式
//
//	            Map<String,String> urls = new HashMap<String,String>();
//	            for (Element li : lis){
//	                Document liDoc = Jsoup.parse(li.toString());
//	                Elements hrefs = liDoc.select("a[href]");
//	                for(Element elem: hrefs) {
//	                	System.out.println(elem.text().substring(elem.text().length()-9,elem.text().length()-1));
//	                	
//	                	if(!"".equals(elem.attr("href"))){
//	                		String href = elem.attr("href");
//	                		base = base + href;
//	                		urls.put(base + href, elem.text().substring(elem.text().length()-9,elem.text().length()-1));
//	                	}
//	                }
//
//	            }
//	            for(Map.Entry<String, String> entry: urls.entrySet()) {
//	            	
//	            	Document singleDoc = Jsoup.connect(entry.getKey()).get();
////	            if(!singleDoc.toString().contains(ymd)){
////	            	return;
////	            }
//	            	Element zoom = singleDoc.getElementById("zoom");
//	            	Elements trElements = zoom.select("tr");
//	            	boolean ignore = true;
//	            	for(Element tdelement : trElements) {
//	            		if(ignore) {
//	            			ignore = false;
//	            			continue;
//	            		}
//	            		Elements tdes = tdelement.select("td");
//	            		Price price = new Price();
//	            		price.setUpdateTime(entry.getValue());
//	            		for(int j = 0; j < tdes.size(); j++){
//	            			if(j==0) {
//	            				price.setName(tdes.get(j).text());
//	            			}else if(j==1) {
//	            				price.setDescription(tdes.get(j).text());
//	            			}else if(j==6) {
//	            				price.setUnit(tdes.get(j).text());
//	            			}else if(j==3) {
//	            				price.setPrice(tdes.get(j).text());
//	            			}else if(j==5) {
//	            				price.setFloating(tdes.get(j).text());
//	            			}
//	            		}
////	            	priceMapper.insertPrice(price);
//	            	}
//	            	//  String title = clearfixli.getElementsByTag("a").text();
//	            	System.out.println("fasdf");
//	            }
//
//
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//		}
//
//        
//	}
	
	
	
	
//	private static void disableSslVerification() {
//		try {
//			// Create a trust manager that does not validate certificate chains
//			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//
//				public void checkClientTrusted(X509Certificate[] certs, String authType) {
//				}
//
//				public void checkServerTrusted(X509Certificate[] certs, String authType) {
//				}
//			} };
//
//			// Install the all-trusting trust manager
//			SSLContext sc = SSLContext.getInstance("SSL");
//			sc.init(null, trustAllCerts, new java.security.SecureRandom());
//			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//			// Create all-trusting host name verifier
//			HostnameVerifier allHostsValid = new HostnameVerifier() {
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}
//			};
//
//			// Install the all-trusting host verifier
//			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (KeyManagementException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	
	public static void main(String[] args) {
//		String[] s = new String[4];
//		s[0] = "liubings     ";
//		s[1] = "asdfasf";
//		s[2] = " liubingcshuan";
//		s[3] = "s";
//		s = filter(s);
//		for(String a : s) {
//			System.out.println(a);
//		}
//		for(String str: s) {
//			System.out.println(str.length());
//		}
//		String[] newStr = new String[s.length];
//		for(int i=0;i<s.length;i++) {
//			newStr[i] = s[i].trim();
//		}
//		for(String str: newStr) {
//			System.out.println(str.length());
//		}
		String s = "";
		System.out.println(s.length());
//		String[] str = s.split(" ");
//		System.out.println(str[0].length());
		System.out.println(filter(s));
//		System.out.println(filter(s).length());
	}
	//只保留子串
    public static String[] filter(String[] s) {
    	int m = 0;
    	List<Integer> k = new ArrayList<Integer>(s.length);
    	for(int i=0;i<s.length;i++) {
    		for(int j=0;j<s.length;j++) {
    			if(i==j || k.contains(i)) {
    				continue;
    			}
    			if(s[j].contains(s[i])) {
    				k.add(j);
    				m++;
    			}
    		}
    	}
    	if(m>0) {
    		List<String> newList = new LinkedList<String>();
    		for(int i=0;i<s.length;i++) {
    			if(!k.contains(i)) {
    				newList.add(s[i]);
    			}
    		}
    		String[] array = new String[s.length-m];
    		return newList.toArray(array);
    	}
    	return s;
    }
    
    public static String filter(String s) {
    	if(s.length()<=3) {
    		return "";
    	}else if(s.length()<6) {
    		return s;
    	}else {
    		int start = s.length()-6;
    		Pattern pattern = Pattern.compile("\\d{6}");
    		boolean matches = false;
    		while(start>=0 && (!matches)){
    			matches = pattern.matcher(s.substring(start, start+6)).matches();
    			if(!matches) {
    				start--;
    			}else {
    				while(start != 0 && (' ' != s.charAt(start-1))) {
    					System.out.println(s.charAt(start-1));
    					start--;
    				}
    			}
    		}
    		
    		if(matches) {
    			if(start<=0) {
    				s = "";
    			}else {
    				s = s.substring(0,start);
    				s = s.split(" ")[0];
    			}
    			
    		}
    		
    	}
    	
    	if(s.contains("研究生") || s.contains("博士生")) {
    		s = "";
    	}
    	return s;
    }
}
