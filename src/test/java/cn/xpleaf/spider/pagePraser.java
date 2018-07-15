package cn.xpleaf.spider;

import cn.xpleaf.spider.utils.JedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class pagePraser
{
    private static Set<String> hashset=new HashSet<>();
    private static Jedis getJedis=null;
    static Pattern pattern=new Pattern();
    public static void parserBegin(String content)
    {
        getJedis= JedisUtil.getJedis();
        Document doc= Jsoup.parse(content);
        Elements elements=doc.select("a");
        for(Element e:elements) {
            String str= e.attr("href");
            if(Pattern.isRight(str)&&!hashset.contains(str)) {
                getJedis.lpush("url", str);
            }
        }
    }
    public static void parserEnd(String content)
    {
        Document doc= Jsoup.parse(content);
    }
    public static void  main(String[] args) throws IOException {
        String url="http://news.163.com/18/0715/17/DMP8LNF60001875N.html";
        String con= HttpUtils.getContent(url);
        Document doc=Jsoup.parse(con);
        Elements elements=doc.getElementsByClass("post_content_main");

        Elements elements1=doc.getElementsByClass("post_time_source");
        for(Element element :elements)
        {
            System.out.println(element.getElementsByTag("h1").text());
        }
    }


}
