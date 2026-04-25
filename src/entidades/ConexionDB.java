package entidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // Reemplaza con tu string de conexión JDBC de Supabase y tu contraseña
    private static final String URL = "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.xjagectkljlgkhxxmjnu&password=1xumIbmP7ZqgCDIB";
    private static final String USER = "xjagectkljlgkhxxmjnu";
    private static final String PASSWORD = "1xumIbmP7ZqgCDIB";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}