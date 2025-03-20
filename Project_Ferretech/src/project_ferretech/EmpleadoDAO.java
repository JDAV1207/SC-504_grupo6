package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class EmpleadoDAO {
    public static DefaultTableModel obtenerEmpleados() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Empleado");
        modelo.addColumn("Nombre");
        modelo.addColumn("Cargo");
        modelo.addColumn("Telefono");
        modelo.addColumn("Correo");

        try (Connection con = ConexionOracle.getConnection();
             CallableStatement stmt = con.prepareCall("{CALL LISTAR_EMPLEADOS(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_EMPLEADO"),
                    rs.getString("NOMBRE"),
                    rs.getString("CARGO"),
                    rs.getString("TELEFONO"),
                    rs.getString("CORREO")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }
}
