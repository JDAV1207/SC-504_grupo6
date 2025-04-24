package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
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
        try (Connection con = ConexionOracle.getConnection(); PreparedStatement stmt = con.prepareStatement("SELECT MAX(ID_PRODUCTO) + 1 FROM PRODUCTOS"); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de PRODUCTOS: " + e.getMessage());
        }
        return nuevoID;
    }

    //Crear VENTA
    public static void insertarVenta(int idVenta, int idCliente, int idEmpleado, String fechaVenta, double total) throws SQLException {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Venta.INSERTAR_VENTA(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, idVenta);
            stmt.setInt(2, idCliente);
            stmt.setInt(3, idEmpleado);
            stmt.setDate(4, Date.valueOf(fechaVenta));
            stmt.setDouble(5, total);
            stmt.execute();
        }
    }

    public static void crearVentaDesdeFormulario(JTable tablaVentas, Component parent) {
        try {
            int idCliente = Integer.parseInt(JOptionPane.showInputDialog(parent, "Ingrese el ID del cliente al que se le realizó la venta:"));
            int idEmpleado = Integer.parseInt(JOptionPane.showInputDialog(parent, "Ingrese el ID del empleado que realizó la venta:"));
            String fechaVenta = JOptionPane.showInputDialog(parent, "Ingrese la fecha de la venta (YYYY-MM-DD):");
            double total = Double.parseDouble(JOptionPane.showInputDialog(parent, "Ingrese el total de la venta:"));

            if (fechaVenta != null && !fechaVenta.trim().isEmpty()) {
                int nuevoID = obtenerNuevoID();
                insertarVenta(nuevoID, idCliente, idEmpleado, fechaVenta, total);

                tablaVentas.setModel(obtenerVentas());
                JOptionPane.showMessageDialog(parent, "Venta registrada.");
            } else {
                JOptionPane.showMessageDialog(parent, "Debe completar todos los campos.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error al registrar la venta.");
        }
    }

    //Actualizar VENTA
    public static void actualizarVenta(int idVenta, int idCliente, int idEmpleado, String fechaVenta, double total) throws SQLException {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Venta.ACTUALIZAR_VENTA(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, idVenta);
            stmt.setInt(2, idCliente);
            stmt.setInt(3, idEmpleado);
            stmt.setDate(4, Date.valueOf(fechaVenta));
            stmt.setDouble(5, total);
            stmt.execute();
        }
    }

    public static void actualizarVentaDesdeFormulario(JTable tablaVentas, Component parent) {
        try {
            int fila = tablaVentas.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(parent, "Seleccione una venta.");
                return;
            }

            int idVenta = (int) tablaVentas.getValueAt(fila, 0);
            int idCliente = Integer.parseInt(JOptionPane.showInputDialog(parent, "Ingrese el nuevo ID del cliente al que se le realizó la venta:", tablaVentas.getValueAt(fila, 1)));
            int idEmpleado = Integer.parseInt(JOptionPane.showInputDialog(parent, "Ingrese el nuevo ID del empleado que realizó la venta:", tablaVentas.getValueAt(fila, 2)));
            String fechaVenta = JOptionPane.showInputDialog(parent, "Ingrese la nueva fecha de la venta (YYYY-MM-DD):", tablaVentas.getValueAt(fila, 3));
            double total = Double.parseDouble(JOptionPane.showInputDialog(parent, "Ingrese el nuevo total de la venta:", tablaVentas.getValueAt(fila, 4)));

            if (fechaVenta != null && !fechaVenta.trim().isEmpty()) {
                actualizarVenta(idVenta, idCliente, idEmpleado, fechaVenta, total);

                tablaVentas.setModel(obtenerVentas());
                JOptionPane.showMessageDialog(parent, "Venta actualizada.");
            } else {
                JOptionPane.showMessageDialog(parent, "Debe completar todos los campos.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error al actualizar la venta.");
        }
    }

    //eliminar VENTA
    public static void eliminarVentaDesdeTabla(JTable tablaVentas, Component parent) {
        try {
            int fila = tablaVentas.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(parent, "Seleccione una venta.");
                return;
            }

            int idVenta = (int) tablaVentas.getValueAt(fila, 0);

            int confirm = JOptionPane.showConfirmDialog(parent, "¿Seguro que desea eliminar esta venta?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarVenta(idVenta);
                tablaVentas.setModel(obtenerVentas());
                JOptionPane.showMessageDialog(parent, "Venta eliminada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error al eliminar la venta.");
        }
    }

    public static void eliminarVenta(int idVenta) throws SQLException {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_venta.ELIMINAR_VENTA(?)}")) {

            stmt.setInt(1, idVenta);
            stmt.execute();
        }
    }
}
