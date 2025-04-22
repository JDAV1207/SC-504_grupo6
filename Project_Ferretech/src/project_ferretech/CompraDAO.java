package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.Date;

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

    //CREAR COMPRA
    public static boolean insertarCompra(int idCompra, int idProveedor, Date fechaCompra, double total) {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_COMPRA(?, ?, ?, ?)}")) {

            stmt.setInt(1, idCompra);
            stmt.setInt(2, idProveedor);
            stmt.setDate(3, fechaCompra);
            stmt.setDouble(4, total);
            stmt.execute();

            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar compra: " + e.getMessage());
            return false;
        }
    }

    public static int obtenerNuevoID() {
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_COMPRA), 0) + 1 FROM COMPRAS")) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de compra: " + e.getMessage());
        }
        return 1;
    }

    public static void crearCompraDesdeFormulario(JTable tablaCompras, Component parent) {
        try {
            int id = obtenerNuevoID();
            int idProveedor = Integer.parseInt(JOptionPane.showInputDialog(parent, "ID del proveedor:"));
            Date fecha = Date.valueOf(JOptionPane.showInputDialog(parent, "Fecha (YYYY-MM-DD):"));
            double total = Double.parseDouble(JOptionPane.showInputDialog(parent, "Total de la compra:"));

            if (insertarCompra(id, idProveedor, fecha, total)) {
                JOptionPane.showMessageDialog(parent, "Compra registrada correctamente.");
                tablaCompras.setModel(obtenerCompras());
            } else {
                JOptionPane.showMessageDialog(parent, "Error al registrar la compra.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Datos inválidos o cancelados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //EDITAR COMPRA
    public static boolean actualizarCompra(int id, int idProveedor, Date fecha, double total) {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_COMPRA(?, ?, ?, ?)}")) {

            stmt.setInt(1, id);
            stmt.setInt(2, idProveedor);
            stmt.setDate(3, fecha);
            stmt.setDouble(4, total);
            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar compra: " + e.getMessage());
            return false;
        }
    }

    public static void editarCompraDesdeTabla(JTable tablaCompras, Component parentComponent) {
        int fila = tablaCompras.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentComponent, "Seleccione una compra.");
            return;
        }

        int id = (int) tablaCompras.getValueAt(fila, 0);

        try {
            int idProveedor = Integer.parseInt(JOptionPane.showInputDialog("Nuevo ID proveedor:", tablaCompras.getValueAt(fila, 1)));
            String fechaStr = JOptionPane.showInputDialog("Nueva fecha (YYYY-MM-DD):", tablaCompras.getValueAt(fila, 2));
            double total = Double.parseDouble(JOptionPane.showInputDialog("Nuevo total:", tablaCompras.getValueAt(fila, 3)));

            if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                Date fechaCompra = Date.valueOf(fechaStr);
                if (actualizarCompra(id, idProveedor, fechaCompra, total)) {
                    JOptionPane.showMessageDialog(parentComponent, "Compra actualizada correctamente.");
                    tablaCompras.setModel(obtenerCompras());
                } else {
                    JOptionPane.showMessageDialog(parentComponent, "Error al actualizar la compra.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentComponent, "Datos numéricos inválidos.");
        }
    }

    //ELIMIAR COMPRA
    public static boolean eliminarCompra(int idCompra) {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_COMPRA(?)}")) {

            stmt.setInt(1, idCompra);
            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar la compra: " + e.getMessage());
            return false;
        }
    }

    public static void eliminarCompraDesdeTabla(JTable tablaCompras, Component parentComponent) {
        int fila = tablaCompras.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentComponent, "Seleccione una compra.");
            return;
        }

        int id = (int) tablaCompras.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(parentComponent,
                "¿Está seguro que desea eliminar esta compra?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (CompraDAO.eliminarCompra(id)) {
                JOptionPane.showMessageDialog(parentComponent, "Compra eliminada correctamente.");
                tablaCompras.setModel(obtenerCompras());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "No se pudo eliminar la compra.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
