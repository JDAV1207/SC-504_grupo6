/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class ProductoDAO {

    // Método para obtener todos los productos
    public static DefaultTableModel obtenerProductos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Descripción");
        modelo.addColumn("Precio");
        modelo.addColumn("Stock");
        modelo.addColumn("ID Categoría");

        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos.");
                return modelo;
            }

            try (CallableStatement stmt = con.prepareCall("{CALL LISTAR_PRODUCTOS(?)}")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();

                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getInt("ID_PRODUCTO"),
                            rs.getString("NOMBRE"),
                            rs.getString("DESCRIPCION"),
                            rs.getDouble("PRECIO"),
                            rs.getInt("STOCK"),
                            rs.getInt("ID_CATEGORIA")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return modelo;
    }

    //INSERTAR
    public static boolean insertarProducto(int id, String nombre, String descripcion, double precio, int stock,
             int idCategoria) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_PRODUCTO(?, ?, ?, ?, ?)}")) {
                setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, descripcion);
                stmt.setDouble(4, precio);
                setInt(5, stock);
                setInt(6, idCategoria);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    //Editar PRODUCTOS
    public static boolean actualizarProducto(int id, String nombre, String descripcion, double precio, int stock,
             int idCategoria) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_PRODUCTO(?, ?, ?, ?, ?)}")) {
                setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, descripcion);
                stmt.setDouble(4, precio);
                setInt(5, stock);
                setInt(6, idCategoria);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
            return false;
        }
    }

    //ELIMINAR PROD
    public static boolean eliminarProducto(int id) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call ELIMINAR_PRODUCTO(?, ?, ?, ?, ?)}")) {
                setInt(1, id);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
            return false;
        }
    }

}
