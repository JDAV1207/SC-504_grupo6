package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class VentaDAO {
    public static DefaultTableModel obtenerVentas() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Venta");
        modelo.addColumn("ID Cliente");
        modelo.addColumn("ID Empleado");
        modelo.addColumn("Fecha Venta");
        modelo.addColumn("Total");

        try (Connection con = ConexionOracle.getConnection();
             CallableStatement stmt = con.prepareCall("{CALL LISTAR_VENTAS(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_VENTA"),
                    rs.getInt("ID_CLIENTE"),
                    rs.getInt("ID_EMPLEADO"),
                    rs.getDate("FECHA_VENTA"),
                    rs.getDouble("TOTAL")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }
}
