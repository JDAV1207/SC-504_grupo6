package project_ferretech.daos;

import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import project_ferretech.ConexionOracle;

public class DetalleVentasDAO {

    public static DefaultTableModel obtenerDetalleVentas() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Detalle");
        modelo.addColumn("ID Venta");
        modelo.addColumn("ID Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Subtotal");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_DETALLE_VENTA(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("ID_DETALLE"),
                        rs.getInt("ID_VENTA"),
                        rs.getInt("ID_PRODUCTO"),
                        rs.getInt("CANTIDAD"),
                        rs.getDouble("SUBTOTAL")
                    });
                }
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

    //CREAR DETALLE vENTAS
    public static void crearDetalleVenta(JFrame parentFrame, JTable tablaDetVentas) {
        try {
            int idVenta = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID de la venta:"));
            int idProducto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del producto:"));
            int cantidad = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad:"));
            double precioUnitario = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio unitario:"));

            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_DETALLE_VENTA(?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, obtenerNuevoID()); // Asegurate de tener este método disponible
                stmt.setInt(2, idVenta);
                stmt.setInt(3, idProducto);
                stmt.setInt(4, cantidad);
                stmt.setDouble(5, precioUnitario);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Detalle de venta agregado.");
                tablaDetVentas.setModel(obtenerDetalleVentas());

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al agregar el detalle de venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Entrada inválida. Por favor ingrese solo números.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //EDITAR DETALLE vETNAS
    public static void editarDetalleVenta(JFrame parentFrame, JTable tablaDetVentas) {
        int fila = tablaDetVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un detalle de venta.");
            return;
        }

        try {
            int id = (int) tablaDetVentas.getValueAt(fila, 0);
            int nuevaCantidad = Integer.parseInt(JOptionPane.showInputDialog("Nueva cantidad:", tablaDetVentas.getValueAt(fila, 3)));
            double nuevoPrecioUnitario = Double.parseDouble(JOptionPane.showInputDialog("Nuevo precio unitario:", tablaDetVentas.getValueAt(fila, 4)));

            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_DETALLE_VENTA(?, ?, ?)}")) {

                stmt.setInt(1, id);
                stmt.setInt(2, nuevaCantidad);
                stmt.setDouble(3, nuevoPrecioUnitario);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Detalle de venta actualizado.");
                tablaDetVentas.setModel(obtenerDetalleVentas());

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al actualizar el detalle de venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Entrada inválida. Ingrese solo números.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //ELIMINAR DET VENTAS
    public static void eliminarDetalleVenta(JFrame parentFrame, JTable tablaDetVentas) {
        int fila = tablaDetVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un detalle de venta.");
            return;
        }

        int id = (int) tablaDetVentas.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar este detalle de venta?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_DETALLE_VENTA(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Detalle de venta eliminado.");
                tablaDetVentas.setModel(obtenerDetalleVentas());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar el detalle de venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
