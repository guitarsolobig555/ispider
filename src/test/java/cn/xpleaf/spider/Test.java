package cn.xpleaf.spider;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*将字符串 "PAYPALISHIRING" 以Z字形排列成给定的行数：
P   A   H   N
A P L S I I G
Y   I   R
之后从左往右，逐行读取字符："PAHNAPLSIIGYIR"
实现一个将字符串进行指定行数变换的函数:
string convert(string s, int numRows);
示例 1:
输入: s = "PAYPALISHIRING", numRows = 3
输出: "PAHNAPLSIIGYIR"
示例 2:
输入: s = "PAYPALISHIRING", numRows = 4
输出: "PINALSIGYAHRPI"
解释:
P     I    N
A   L S  I G
Y A   H R
P     I*/
public class Test
{
  public static void main(String[] args) throws IOException {
      Set<String> list=new HashSet<>();
      Set<String> hashset=new HashSet<>();
      CloseableHttpClient closeableHttpClient= HttpClients.custom().build();
      String url="http://news.163.com/";
      HttpGet httpGet=new HttpGet(url);
      HttpResponse httpResponse=closeableHttpClient.execute(httpGet);
      HttpEntity entity=httpResponse.getEntity();
      String content= EntityUtils.toString(entity);
      Document doc= Jsoup.parse(content);
      Elements elements=doc.select("a");
      list.add("http://www.163.com/");
      list.add("http://war.163.com");
      list.add("http://sports.163.com");
      list.add("http://money.163.com");
      list.add("http://auto.163.com");
      list.add("http://tech.163.com");
      list.add("http://fashion.163.com");
      list.add("http://digi.163.com/");
      list.add("http://house.163.com");
      list.add("http://home.163.com");
      list.add("http://daxue.163.com");
      list.add("http://tech.163.com");
      list.add("http://mobile.163.com/");
      list.add("http://lady.163.com/");
      list.add("http://travel.163.com/");
      list.add("http://art.163.com/");
      for(Element e:elements) {
         String str= e.attr("href");
         hashset.add(str);
      }
  }
}
