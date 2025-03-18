
package project_ferretech;

import java.sql.*;

public class Project_Ferretech {

    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//localhost:1521/XE"; // Cambia según tu configuración
        String usuario = "SYSTEM"; // Reemplaza con tu usuario
        String contraseña = "1234"; // Reemplaza con tu contraseña

        try {
            // Cargar el driver de Oracle
            Class.forName("oracle.jdbc.OracleDriver");

            // Establecer conexión con Oracle
            Connection conn = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos Ferretech.");

            // Consulta SQL a la tabla CATEGORIAS
            String sql = "SELECT * FROM CATEGORIAS";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Mostrar los resultados
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID") + ", Nombre: " + rs.getString("NOMBRE"));
            }

            // Cerrar recursos
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error en la conexión SQL.");
            e.printStackTrace();
        }
    }
}

