package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JFrame;
import project_ferretech.ConexionOracle;

public class EmpleadoDAO {

    public static DefaultTableModel obtenerEmpleados() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Empleado");
        modelo.addColumn("Nombre");
        modelo.addColumn("Cargo");
        modelo.addColumn("Telefono");
        modelo.addColumn("Correo");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_EMPLEADOS(?)}")) {
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

    private static int obtenerNuevoID() {
        int nuevoID = 1;
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_EMPLEADO), 0) + 1 FROM EMPLEADOS")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

    //CREAR EMPLEADO
    public static void crearEmpleado(JFrame parentFrame, JTable tablaEmpleados) {
        String nombre;
        String cargo;
        String telefono;
        String correo;

        try {
            int idEmpleado = obtenerNuevoID();

            nombre = JOptionPane.showInputDialog(parentFrame, "Ingrese el nombre y apellido del empleado:");
            cargo = JOptionPane.showInputDialog(parentFrame, "Ingrese el cargo del empleado:");
            telefono = JOptionPane.showInputDialog(parentFrame, "Ingrese el teléfono del empleado, separado por un guion (8888-8888):");
            correo = JOptionPane.showInputDialog(parentFrame, "Ingrese el correo del empleado:");

            if (nombre != null && !nombre.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Empleado.INSERTAR_EMPLEADO(?, ?, ?, ?, ?)}")) {

                    stmt.setInt(1, idEmpleado);
                    stmt.setString(2, nombre);
                    stmt.setString(3, cargo);
                    stmt.setString(4, telefono);
                    stmt.setString(5, correo);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Empleado registrado.");
                    tablaEmpleados.setModel(obtenerEmpleados());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al registrar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //ACTIALIZAR EMPLEADO
    public static void editarEmpleado(JFrame parentFrame, JTable tablaEmpleados) {
        int fila = tablaEmpleados.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un empleado.");
            return;
        }

        int idEmpleado = (int) tablaEmpleados.getValueAt(fila, 0);
        String nombre;
        String cargo;
        String telefono;
        String correo;

        try {
            nombre = JOptionPane.showInputDialog(parentFrame, "Ingrese el nuevo nombre y apellido del empleado:", tablaEmpleados.getValueAt(fila, 1));
            cargo = JOptionPane.showInputDialog(parentFrame, "Ingrese el nuevo cargo del empleado:", tablaEmpleados.getValueAt(fila, 2));
            telefono = JOptionPane.showInputDialog(parentFrame, "Ingrese el nuevo teléfono del empleado (8888-8888):", tablaEmpleados.getValueAt(fila, 3));
            correo = JOptionPane.showInputDialog(parentFrame, "Ingrese el nuevo correo del empleado:", tablaEmpleados.getValueAt(fila, 4));

            if (nombre != null && !nombre.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Empleado.ACTUALIZAR_EMPLEADO(?, ?, ?, ?, ?)}")) {

                    stmt.setInt(1, idEmpleado);
                    stmt.setString(2, nombre);
                    stmt.setString(3, cargo);
                    stmt.setString(4, telefono);
                    stmt.setString(5, correo);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Empleado actualizado.");
                    tablaEmpleados.setModel(obtenerEmpleados());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al actualizar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //ELIMINAR EMPLEADO
    public static void eliminarEmpleado(JFrame parentFrame, JTable tablaEmpleados) {
        int fila = tablaEmpleados.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un empleado.");
            return;
        }

        int id = (int) tablaEmpleados.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar este empleado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Empleado.ELIMINAR_EMPLEADO(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Empleado eliminado.");
                tablaEmpleados.setModel(obtenerEmpleados());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
