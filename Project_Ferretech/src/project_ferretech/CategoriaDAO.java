
package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class CategoriaDAO {
    public static DefaultTableModel obtenerCategorias() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");

        try (Connection con = ConexionOracle.getConnection();
             CallableStatement stmt = con.prepareCall("{CALL LISTAR_CATEGORIAS(?)}")) {
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
}
