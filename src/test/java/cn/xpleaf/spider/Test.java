package cn.xpleaf.spider;
import org.slf4j.LoggerFactory;
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
    public static void sort(String str,int numsRows)
    {
        int len = str.length();int k = len / (2 * (numsRows - 1));int l = len % (2 * (numsRows - 1));
        int tmp = k * (numsRows - 1);
        int numsRows2 = 0;
        if (l == 0) {
            numsRows2 = tmp;
        }

            if (l <= numsRows) {
                numsRows2 = tmp + 1;
            }
            if (l > numsRows) {
                numsRows2 = tmp + 1 + l - numsRows;
            }
            char[][] ch = new char[numsRows][numsRows2];
            int j = 0;
            int count = 0;
            for (int i = 0; i < numsRows2&&count<len; i++)
            {
                if (i % (numsRows - 1) == 0) {
                    while (j < numsRows&&count<len) {
                        ch[j++][i] = str.charAt(count++);
                    }
                    j=numsRows-2;
                } else {
                    ch[j--][i] = str.charAt(count++);
                }
            }
            j=0;
            String s="";
            for(int i=0;i<numsRows;i++)
            {
                while(j<numsRows2)
                {
                    if(ch[i][j]=='\0')
                    {
                      j++;
                      continue;
                    }
                    else {
                        s = s + ch[i][j++];
                    }
                }
                j=0;
            }
            System.out.println(s);
    }

  public static void main(String[] args)
  {
    String str="PAYPALISHIRING";
    sort(str,3);


  }
}
