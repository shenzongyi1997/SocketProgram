package ClientProgram;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.*;

/**
 * Created by Administrator on 2017/11/14.
 */
public class test {
    public static void main(String args[])
    {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("开始连接数据库！");
            String user = "test";
            String pwd = "dachuang2017";
            Connection connection = DriverManager.getConnection("jdbc:mysql://47.95.204.80:3306/test",user,pwd);
            System.out.println("成功连接！");
            Statement statement = connection.createStatement();
            /*String sql = "select * from custumor";
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("开始获得数据！");
            while(!rs.next())
            {
                System.out.println("获得一条数据！");
                System.out.println("名字是"+rs.getString(1)+rs.getString(2));
            }*/
            String name="hello!";
            String txt = "fuck!";
            String sql = "insert into sqltest (name,txt) values ('"+name+"','"+txt+"')";
            statement.execute(sql);
            System.out.println("成功插入！");
            sql = "select * from sqltest";
            ResultSet rs =statement.executeQuery(sql);
            while(rs.next())
            {
                System.out.println("获得一条数据！");
                System.out.println("名字是"+rs.getString(1)+rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
