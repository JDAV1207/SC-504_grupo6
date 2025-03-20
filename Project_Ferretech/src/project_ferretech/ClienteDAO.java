package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class ClienteDAO {
    public static DefaultTableModel obtenerClientes() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Cliente");
        modelo.addColumn("Nombre");
        modelo.addColumn("Telefono");
        modelo.addColumn("Direccion");
        modelo.addColumn("Correo");

        try (Connection con = ConexionOracle.getConnection();
             CallableStatement stmt = con.prepareCall("{CALL LISTAR_CLIENTES(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_CLIENTE"),
                    rs.getString("NOMBRE"),
                    rs.getString("TELEFONO"),
                    rs.getString("DIRECCION"),
                    rs.getString("CORREO")
                    
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }
}
