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
    private static Set<String> hashset=new HashSet<>();
    private static Jedis getJedis=null;
    static Pattern pattern=new Pattern();
    static page page;


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
    public static page parserEnd(String content)
    {
        Document doc= Jsoup.parse(content);
        Elements elements=doc.getElementsByClass("post_content_main");
        Elements elements1=doc.getElementsByClass("post_time_source");
        for(Element element :elements)
        {
            page.setTitle(element.getElementsByTag("h1").text());
        }
        for(Element element :elements1)
        {
            page.setUrl(element.getElementsByTag("a").attr("href"));
        }
        return page;
    }
}
