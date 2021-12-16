import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/query")
public class Query extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String URL =
            "jdbc:mysql://180.76.157.67:3306/linux_final?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
    static final String USER = "root";
    static final String PASS = "Txy0326!";
    static final String SQL_QUERY_NOTEPAD = "SELECT * FROM `notepad`";

    static Connection conn = null;
    static Jedis jedis = null;

    // servlet创建时 初始化的东西
    public void init() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            jedis = new Jedis("180.76.157.67");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 当再次调用servlet销毁之前
    public void destroy() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 重写doGet方法
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response 返回值类型
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // response 返回
        PrintWriter out = response.getWriter();


        // 通过key获取redis中的value
        String notepadList = jedis.get("notepadlist");

        if (notepadList == null){ // 如果缓存没有，从数据库查询，并放入缓存
            // 调用业务方法得到返回值
            List<Notepad> list = getNotepad();
            Gson gson = new Gson();
            // 使用gson将对象解析成 某格式json字符串
            String json = gson.toJson(list, new TypeToken<List<Notepad>>() {}.getType());
            jedis.set("notepadlist",json);
            // 返回结果
            out.println(json);
            System.out.println("走数据库，存入缓存");
        }else{ // 如果缓存有，直接从缓存中查询
            out.println(notepadList);
            System.out.println("走缓存");
        }





        out.flush();
        out.close();

    }

    // 查询 返回一个泛型为实体类的List集合
    private List<Notepad> getNotepad() {
        List<Notepad> list = new ArrayList<Notepad>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL_QUERY_NOTEPAD);
            while (rs.next()) {
                Notepad notepad = new Notepad();
                notepad.setId(rs.getString("id"));
                notepad.setNotepadContent(rs.getString("notepadContent"));
                notepad.setNotepadeTime(rs.getString("notepadTime"));
                list.add(notepad);
            }
            rs.close();
            stmt.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return list;
    }

    class Notepad {
        private String id;
        private String notepadContent;
        private String notepadTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNotepadContent() {
            return notepadContent;
        }

        public void setNotepadContent(String notepadContent) {
            this.notepadContent = notepadContent;
        }

        public String getNotepadeTime() {
            return notepadTime;
        }

        public void setNotepadeTime(String notepadeTime) {
            this.notepadTime = notepadeTime;
        }
    }
}
