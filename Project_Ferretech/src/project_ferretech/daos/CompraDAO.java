package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.Date;
import javax.swing.JFrame;
import project_ferretech.ConexionOracle;

public class CompraDAO {

    public static DefaultTableModel obtenerCompras() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Proveedor");
        modelo.addColumn("Fecha Compra");
        modelo.addColumn("Total");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_COMPRAS(?)}")) {
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

    private static int obtenerNuevoID() {
        int nuevoID = 1;
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_COMPRA), 0) + 1 FROM COMPRAS")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

    //CREAR COMPRA
    public static void crearCompra(JFrame parentFrame, JTable tablaCompras) {
        int idProveedor;
        double total;
        String fechaCompra;

        try {
            idProveedor = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del proveedor:"));
            fechaCompra = JOptionPane.showInputDialog("Ingrese la fecha de compra (YYYY-MM-DD):");
            total = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el total de la compra:"));

            if (fechaCompra != null && !fechaCompra.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Compra.INSERTAR_COMPRA(?, ?, ?, ?)}")) {

                    stmt.setInt(1, obtenerNuevoID()); // Este método debe estar definido en CompraDAO o accesible desde ahí
                    stmt.setInt(2, idProveedor);
                    stmt.setDate(3, Date.valueOf(fechaCompra));
                    stmt.setDouble(4, total);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Compra creada.");
                    tablaCompras.setModel(obtenerCompras());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al crear la compra: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //EDITAR COMPRA
    public static void editarCompra(JFrame parentFrame, JTable tablaCompras) {
        int fila = tablaCompras.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione una compra.");
            return;
        }

        int id = (int) tablaCompras.getValueAt(fila, 0);
        int idProveedor;
        double total;
        String fechaCompra;

        try {
            idProveedor = Integer.parseInt(JOptionPane.showInputDialog("Nuevo ID del proveedor:", tablaCompras.getValueAt(fila, 1)));
            fechaCompra = JOptionPane.showInputDialog("Nueva fecha de compra (YYYY-MM-DD):", tablaCompras.getValueAt(fila, 2));
            total = Double.parseDouble(JOptionPane.showInputDialog("Nuevo total de la compra:", tablaCompras.getValueAt(fila, 3)));

            if (fechaCompra != null && !fechaCompra.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Compra.ACTUALIZAR_COMPRA(?, ?, ?, ?)}")) {

                    stmt.setInt(1, id);
                    stmt.setInt(2, idProveedor);
                    stmt.setDate(3, Date.valueOf(fechaCompra));
                    stmt.setDouble(4, total);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Compra actualizada.");
                    tablaCompras.setModel(obtenerCompras());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al actualizar la compra: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //ELIMIAR COMPRA
    public static void eliminarCompra(JFrame parentFrame, JTable tablaCompras) {
        int fila = tablaCompras.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione una compra.");
            return;
        }

        int id = (int) tablaCompras.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar esta compra?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Compra.ELIMINAR_COMPRA(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Compra eliminada.");
                tablaCompras.setModel(obtenerCompras());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar la compra: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
