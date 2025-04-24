package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JFrame;
import project_ferretech.ConexionOracle;

public class VentaDAO {

    public static DefaultTableModel obtenerVentas() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Venta");
        modelo.addColumn("ID Cliente");
        modelo.addColumn("ID Empleado");
        modelo.addColumn("Fecha Venta");
        modelo.addColumn("Total");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_VENTAS(?)}")) {
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

    public static int obtenerNuevoID() {
        int nuevoID = -1;
        try (Connection con = ConexionOracle.getConnection(); PreparedStatement stmt = con.prepareStatement("SELECT MAX(ID_VENTA) + 1 FROM VENTAS"); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de VENTAS: " + e.getMessage());
        }
        return nuevoID;
    }

    //Crear VENTA
    public static void crearVenta(JFrame parentFrame, JTable tablaVentas) {
        int idCliente;
        int idEmpleado;
        String fechaVenta;
        double total;

        try {
            idCliente = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del cliente al que se le realizó la venta:"));
            idEmpleado = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del empleado que realizó la venta:"));
            fechaVenta = JOptionPane.showInputDialog("Ingrese la fecha de la venta (YYYY-MM-DD):");
            total = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el total de la venta:"));

            if (fechaVenta != null && !fechaVenta.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Venta.INSERTAR_VENTA(?, ?, ?, ?, ?)}")) {

                    stmt.setInt(1, obtenerNuevoID()); // Asegúrate de que este método sea accesible aquí
                    stmt.setInt(2, idCliente);
                    stmt.setInt(3, idEmpleado);
                    stmt.setDate(4, Date.valueOf(fechaVenta));
                    stmt.setDouble(5, total);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Venta registrada.");
                    tablaVentas.setModel(obtenerVentas());

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al registrar la venta.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Actualizar VENTA
    public static void editarVenta(JFrame parentFrame, JTable tablaVentas) {
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione una venta.");
            return;
        }

        int id = (int) tablaVentas.getValueAt(fila, 0);
        int idCliente;
        int idEmpleado;
        String fechaVenta;
        double total;

        try {
            idCliente = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el nuevo ID del cliente:", tablaVentas.getValueAt(fila, 1)));
            idEmpleado = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el nuevo ID del empleado:", tablaVentas.getValueAt(fila, 2)));
            fechaVenta = JOptionPane.showInputDialog("Ingrese la nueva fecha (YYYY-MM-DD):", tablaVentas.getValueAt(fila, 3));
            total = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el nuevo total:", tablaVentas.getValueAt(fila, 4)));

            if (fechaVenta != null && !fechaVenta.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Venta.ACTUALIZAR_VENTA(?, ?, ?, ?, ?)}")) {

                    stmt.setInt(1, id);
                    stmt.setInt(2, idCliente);
                    stmt.setInt(3, idEmpleado);
                    stmt.setDate(4, Date.valueOf(fechaVenta));
                    stmt.setDouble(5, total);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Venta actualizada.");
                    tablaVentas.setModel(obtenerVentas());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al actualizar la venta.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //eliminar VENTA
    public static void eliminarVenta(JFrame parentFrame, JTable tablaVentas) {
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione una venta.");
            return;
        }

        int id = (int) tablaVentas.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar esta venta?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Venta.ELIMINAR_VENTA(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Venta eliminada.");
                tablaVentas.setModel(obtenerVentas());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar la venta.");
            }
        }
    }
}
