import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDatabase {
    public static void main(String[] args) throws Exception {
        // 尝试不同密码连接 MySQL
        String[] passwords = {"", "root", "123456", "password"};
        boolean connected = false;

        for (String pwd : passwords) {
            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true",
                    "root", pwd);
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE DATABASE IF NOT EXISTS `请假系统` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                System.out.println("SUCCESS: Database '请假系统' created (password: " + pwd + ")");
                stmt.close();
                conn.close();
                connected = true;
                break;
            } catch (Exception e) {
                System.out.println("FAIL with password '" + pwd + "': " + e.getMessage());
            }
        }

        if (!connected) {
            System.out.println("Could not connect to MySQL. Please check credentials.");
            System.exit(1);
        }
    }
}
