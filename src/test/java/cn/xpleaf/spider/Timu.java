package cn.xpleaf.spider;

public class Timu {
    public  static int uniquepath(int m,int n)
    {
        int[][] dp=new int[m][n];
        int x=0;
        while(x<n)
        {
            dp[0][x]=1;
            x++;
        }
        x=0;
        while(x<m)
        {
            dp[x][0]=1;
            x++;
        }
        for(int i=1;i<m;i++)
        {
            for(int j=1;j<n;j++)
            {
                dp[i][j]=dp[i-1][j]+dp[i][j-1];
            }
        }
        return dp[m-1][n-1];
    }
    public static void main(String[] args)
    {
        System.out.println(uniquepath(9,8));
    }
}
