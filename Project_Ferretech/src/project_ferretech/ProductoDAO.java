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
}