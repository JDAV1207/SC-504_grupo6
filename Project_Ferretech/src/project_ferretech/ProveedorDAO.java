package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class ProveedorDAO {

    public static DefaultTableModel obtenerProveedores() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Telefono");
        modelo.addColumn("Direccion");
        modelo.addColumn("Correo");

        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos.");
                return modelo;
            }

            try (CallableStatement stmt = con.prepareCall("{CALL LISTAR_PROVEEDORES(?)}")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();

                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getInt("ID_PROVEEDOR"),
                            rs.getString("NOMBRE"),
                            rs.getString("TELEFONO"),
                            rs.getString("DIRECCION"),
                            rs.getString("CORREO")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return modelo;
    }

    //INSERTAR PROVEEDOR
    public static boolean insertarProveedor(int id, String nombre, String telefono, String direccion, String correo) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_PROVEEDOR(?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, telefono);
                stmt.setString(4, direccion);
                stmt.setString(5, correo);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    //ACTUALIZAR PROVEEDOR
    public static boolean actualizarProveedor(int id, String nombre, String telefono, String direccion, String correo) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call ACTUALIZAR_PROVEEDOR(?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, telefono);
                stmt.setString(4, direccion);
                stmt.setString(5, correo);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    //BOORAR PROVEEDOR
    public static boolean eliminarProveedor(int id) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{call ELIMINAR_PROVEEDOR(?)}")) {
                stmt.setInt(1, id);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }

}
