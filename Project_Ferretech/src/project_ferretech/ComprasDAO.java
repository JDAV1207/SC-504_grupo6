package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class ComprasDAO {
    public static DefaultTableModel obtenerCompras() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Proveedor");
        modelo.addColumn("Fecha Compra");
        modelo.addColumn("Total");

        try (Connection con = ConexionOracle.getConnection();
             CallableStatement stmt = con.prepareCall("{CALL LISTAR_COMPRAS(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_COMPRA"),
                    rs.getInt("ID_PROVEEDOR"),
                    rs.getDate("FECHA_COMPRA"),
                    rs.getDouble("TOTAL")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }
}
