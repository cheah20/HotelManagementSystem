package hms;

import java.sql.*;

// one shared connection
public class Database {
    private static final String URL = "jdbc:mariadb://localhost:3306/hotel_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection conn;

    public static Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) conn = DriverManager.getConnection(URL, USER, PASSWORD);
        return conn;
    }

    // caller closes the result set
    public static ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement ps = prepare(sql, params);
        ps.closeOnCompletion();
        return ps.executeQuery();
    }

    public static int update(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepare(sql, params)) {
            return ps.executeUpdate();
        }
    }

    // returns the new id
    public static int insert(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    private static PreparedStatement prepare(String sql, Object... params) throws SQLException {
        PreparedStatement ps = get().prepareStatement(sql);
        bind(ps, params);
        return ps;
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p instanceof Date) p = ((Date) p).toLocalDate();
            if (p instanceof Time) p = ((Time) p).toLocalTime();
            ps.setObject(i + 1, p);
        }
    }
}
