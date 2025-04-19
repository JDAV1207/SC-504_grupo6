package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class DetalleVentasDAO {

    public static DefaultTableModel obtenerDetalleVentas() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Detalle");
        modelo.addColumn("ID Venta");
        modelo.addColumn("ID Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Precio Unitario");

        try (Connection con = ConexionOracle.getConnection();
            CallableStatement stmt = con.prepareCall("{CALL LISTAR_DETALLE_VENTA(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

                
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("ID_DETALLE"),
                        rs.getInt("ID_VENTA"),
                        rs.getInt("ID_PRODUCTO"),
                        rs.getInt("CANTIDAD"),
                        rs.getDouble("PRECIO_UNITARIO")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }
}
