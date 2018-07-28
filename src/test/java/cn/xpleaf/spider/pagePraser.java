package cn.xpleaf.spider;

import cn.xpleaf.spider.core.pojo.Page;
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
    private static Jedis getJedis=null;
    static Pattern pattern=new Pattern();
    public static void parserBegin(Jedis jedis,String content)
    {
        Document doc= Jsoup.parse(content);
        Elements elements=doc.select("a");
        for(Element e:elements) {
            String str= e.attr("href");
            if(Pattern.isRight(str)&&!Util.hashSet.contains(str)) {
                jedis.lpush("url", str);
            }
        }
    }
    public static page parserEnd(String content)
    {
        page page=new page();
        Document doc= Jsoup.parse(content);
        Elements elements=doc.getElementsByClass("post_content_main");
        for(Element element :elements)
        {
            page.setTitle(element.getElementsByTag("h1").text());
        }
        return page;
    }
}
