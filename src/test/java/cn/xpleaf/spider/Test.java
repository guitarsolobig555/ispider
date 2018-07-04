package cn.xpleaf.spider;

import cn.xpleaf.spider.core.pojo.Page;
import cn.xpleaf.spider.utils.HtmlUtil;
import cn.xpleaf.spider.utils.HttpUtil;
import cn.xpleaf.spider.utils.SpiderUtil;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class Test {
    private static org.slf4j.Logger logger= LoggerFactory.getLogger(Test.class);
    public static void main(String[] args)
    {
        String url="https://item.jd.hk/29250740057.html";
        String content = HttpUtil.getHttpContent(url);
        //System.out.println(content);
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode tagNode=cleaner.clean(content);
        System.out.println(tagNode.getText());
        Page page=new Page();
        page.setUrl(url);
        String id = HtmlUtil.getIdByUrl(url);
        page.setId(id);

        // 2.source; 商品来源
        String domain = SpiderUtil.getTopDomain(page.getUrl());
        System.out.println(domain);
        page.setSource(domain);

        // 3.title; 商品标题
        String title = HtmlUtil.getTextByXpath(tagNode, "//div[@class='sku-name']");
        System.out.println(title);
        page.setTitle(title);

        // 4.price; 商品价格
        String priceUrl = "https://p.3.cn/prices/mgets?pduid=1504781656858214892980&skuIds=J_" + id;
        /**
         * 上面的价格url中，pduid每隔一段时间都会改变，所以需要定时更新一下，特别是价格无法获取到时则都是这个问题
         * 下面就来解决这个问题吧，如果获取不到价格，会返回jsonp字符串：{"error":"pdos_captcha"}1504781656858214892980
         * 当出现这个情况时，就提示更换pduid
         */
        String priceJson = HttpUtil.getHttpContent(priceUrl);
        /*if (priceJson != null) {
            // 解析json [{"op":"4899.00","m":"9999.00","id":"J_3133843","p":"4799.00"}] 将该json字符串封装成json对象
            if (priceJson.contains("error")) {   // 返回{"error":"pdos_captcha"}，说明价格url已经不可用，更换pduid再做解析
                logger.info("价格url已经不可用，请及时更换pduid--->" + priceJson);
            } else {
                JSONArray priceJsonArray = new JSONArray(priceJson);
                JSONObject priceJsonObj = priceJsonArray.getJSONObject(0);
                String priceStr = priceJsonObj.getString("p").trim();
                Float price = Float.valueOf(priceStr);
                page.setPrice(price);
            }
        }
        */

        // 5.imgUrl; 商品图片链接
        String imgUrl = HtmlUtil.getAttrByXpath(tagNode, "data-origin", "//img[@id='spec-img']");
        System.out.println(imgUrl);
        page.setImgUrl("http:" + imgUrl);
        System.out.println(imgUrl);

        // 6.params; 商品规格参数
        // Map<String, Map<String, String>>
        // {"主体": {"品牌": Apple}, "型号": "IPhone 7 Plus", "基本信息":{"机身颜色":"玫瑰金"}}
        JSONObject paramObj = HtmlUtil.getParams(tagNode, "//*[@id=\"detail\"]/div[2]/div[2]/div[1]/*", "//h3", "//dl");
        if (paramObj.has("主体")) {
            if (paramObj.getJSONObject("主体").has("品牌")) {
                String brand = paramObj.getJSONObject("主体").getString("品牌");
                page.setBrand(brand);
            }
        }
        page.setParams(paramObj.toString());

        // 7.商品评论数
        // 注意JsonObj和JsonArray的不同获取方法
        String commentCountUrl = "https://club.jd.com/comment/productCommentSummaries.action?referenceIds=" + id;
        String commentCountJson = HttpUtil.getHttpContent(commentCountUrl);
        if (commentCountJson != null) {
            JSONArray commentCountJsonArray = new JSONObject(commentCountJson).getJSONArray("CommentsCount");
            JSONObject commentCountJsonObj = commentCountJsonArray.getJSONObject(0);
            int commentCount = commentCountJsonObj.getInt("CommentCount");
            page.setCommentCount(commentCount);
        }
      //我无敌了
    }
}
