package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class CategoriaDAO {

    public static DefaultTableModel obtenerCategorias() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_CATEGORIAS(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{rs.getInt("ID_CATEGORIA"), rs.getString("NOMBRE_CATEGORIA")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }

//ELIMINAR CATEGORIA
    public static boolean eliminarCategoria(int id) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{call ELIMINAR_CATEGORIA(?)}")) {
                stmt.setInt(1, id);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

//ACTUALIZAR CATEGORIA
    public static boolean actualizarCategoria(int id, String nuevoNombre) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call ACTUALIZAR_CATEGORIA(?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nuevoNombre);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    //Crear CATEGORIA
    public static boolean insertarCategoria(int id, String Nombre) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_CATEGORIA(?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, Nombre);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar la categoría: " + e.getMessage());
            return false;
        }
    }

}
