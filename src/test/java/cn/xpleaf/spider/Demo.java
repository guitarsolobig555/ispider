package cn.xpleaf.spider;

import cn.xpleaf.spider.core.download.IDownload;
import cn.xpleaf.spider.core.download.impl.HttpGetDownloadImpl;
import cn.xpleaf.spider.core.parser.IParser;
import cn.xpleaf.spider.core.parser.Impl.JDHtmlParserImpl;
import cn.xpleaf.spider.core.parser.Impl.SNHtmlParserImpl;
import cn.xpleaf.spider.core.pojo.Page;
import cn.xpleaf.spider.core.repository.IRepository;
import cn.xpleaf.spider.core.repository.impl.RandomRedisRepositoryImpl;
import cn.xpleaf.spider.core.store.IStore;
import cn.xpleaf.spider.core.store.impl.MySQLStoreImpl;
import cn.xpleaf.spider.utils.HtmlUtil;
import cn.xpleaf.spider.utils.HttpUtil;
import cn.xpleaf.spider.utils.SpiderUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;

import static cn.xpleaf.spider.utils.HBaseUtil.store;

public class Demo
{
    //??完全可以把所有的页面全部爬取下来
    private static  Logger logger= LoggerFactory.getLogger(Demo.class);
   static   List<String> all=new ArrayList<>();
   static String higher="https://list.jd.com";
   static volatile String url="https://list.jd.com/list.html?cat=9987,653,655&page=1";
   static HttpGetDownloadImpl httpGetDownload=new HttpGetDownloadImpl();
    private static IDownload download;
    // 爬虫解释器map: key-->需要爬取的网页的顶级域名     value-->该顶级域名的解释器实现类对象
    private static  Map<String, IParser> parsers = new HashMap<>();
    // 爬虫存储器
    private static  IStore store1=new MySQLStoreImpl();
    // 域名高低优先级url标识器map: key-->domain   value-->Map<Level, url> 其中level为higher或lower即高低优先级的意思
    private static Map<String, Map<String, String>> urlLevelMarker = new HashMap<>();
    // 域名列表
    private static List<String> domains = new ArrayList<>();
    // 种子url
    /**
     * 这是初始启动爬虫程序时的种子url，当将该种子url的数据爬取完成后就没有数据爬取了
     * 那如何解决呢？这就需要使用我们的url调度系统，我们另外启动了一个url调度程序
     * 该程序会定时从redis的种子url列表中获取种子url，然后再添加到高优先级url列表中
     * 这样我们的爬虫程序就不会停下来，达到了定时爬取特别网页数据的目的
     */
    private static List<String> seedUrls = new ArrayList<>();
    // url仓库
    private static IRepository repository=new RandomRedisRepositoryImpl();
    public static void parser(Page page, String domain) {
        IParser domainParser = parsers.get(domain);
        if (domainParser != null) {
            domainParser.parser(page);
        } else {
            logger.error("没有对应域名{}的解析器", domain);
        }
    }

    public static void main(String[] args)
    {
        domains.add("jd.com");
        domains.add("suning.com");
        parsers.put("jd.com", new JDHtmlParserImpl());
        // 2.2 设置高低优先级url标识器
        Map<String, String> jdMarker = new HashMap<>();
        jdMarker.put("higher", "https://list.jd.com/");
        jdMarker.put("lower", "https://item.jd.com");
        Map<String, String> snMarker = new HashMap<>();
        snMarker.put("higher", "https://list.suning.com");
        snMarker.put("lower", "https://product.suning.com");
        urlLevelMarker.put("jd.com", jdMarker);
        repository.offerHigher("https://list.jd.com/list.html?cat=9987,653,655&page=1");
        /*getAllHiger("https://list.jd.com/list.html?cat=9987,653,655&page=1");
        System.out.println(all.size());*/
      ScheduledExecutorService es = Executors.newScheduledThreadPool(5);
       for (int i = 0; i < 5; i++)
        {
            es.execute(new Runnable()
            {
                @Override
                public void run() {
                    while (true)
                    {  // 要想开启循环爬取商品，则必须是执行一个死循环
                       String url = repository.poll();
                        String domain = SpiderUtil.getTopDomain(url);
                        System.out.println(domain);// 获取url对应的顶级域名
                        if (url != null)
                        {  // 从url仓库中获取的url不为null
                            // 下载网页
                            Page page =httpGetDownload.download(url);
                            // 解析网页
                            if (page.getContent() != null)
                            { // 只有content不为null时才进行后面的操作，否则没有意义
                                parser(page, domain);
                                System.out.println(page.getId());
                                // 如果该url为列表url，从这里有可能解析出很多的url
                                for (String pUrl : page.getUrls())
                                { // 向url仓库中添加url
                                    String higherUrlMark = urlLevelMarker.get(domain).get("higher");
                                    String lowerUrlMark = urlLevelMarker.get(domain).get("lower");
                                    if (pUrl.startsWith(higherUrlMark)) {    // 高优先级
                                        repository.offerHigher(pUrl);
                                    } else if (pUrl.startsWith(lowerUrlMark)) { // 低优先级
                                        repository.offerLower(pUrl);
                                    }
                                }
                                if (page.getId() != null) {  // 当商品id不为null时，说明前面解析的url是商品url，而不是列表url，这时存储数据才有意义
                                    // 存储解析数据
                                    store1.store(page);
                                }
                            }

                            // 上面操作结束之后必须要休息一会，否则频率太高的话很有可能会被封ip
                            SpiderUtil.sleep(1000);
                        } else
                            {    // 从url仓库中没有获取到url
                            logger.info("没有url，请及时添加种子url");
                            SpiderUtil.sleep(2000);
                            }
                    }
                }
            });
        }
    }
}
