import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class InitMysql {
    public static void main(String[] args) throws Exception {
        String pwd = args.length > 0 ? args[0] : "123456";

        System.out.println("Connecting to MySQL...");
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai",
            "root", pwd
        );
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE DATABASE IF NOT EXISTS `leave_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        System.out.println("OK: Database 'leave_system' ready");
        stmt.close();
        conn.close();

        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/leave_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true",
            "root", pwd
        );
        stmt = conn.createStatement();

        String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.sql")), "UTF-8");
        String[] statements = sql.split(";\n");
        for (String s : statements) {
            String trimmed = s.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
            try {
                stmt.execute(trimmed);
            } catch (SQLException e) {
                System.out.println("WARN: " + e.getMessage());
            }
        }
        stmt.close();

        Statement check = conn.createStatement();
        ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM sys_user");
        rs.next(); System.out.println("OK: sys_user = " + rs.getInt(1) + " rows");
        rs = check.executeQuery("SELECT COUNT(*) FROM leave_request");
        rs.next(); System.out.println("OK: leave_request = " + rs.getInt(1) + " rows");
        rs = check.executeQuery("SELECT COUNT(*) FROM approval_record");
        rs.next(); System.out.println("OK: approval_record = " + rs.getInt(1) + " rows");
        check.close();
        conn.close();

        System.out.println("\nAll done! Refresh in Navicat to see the data.");
    }
}
