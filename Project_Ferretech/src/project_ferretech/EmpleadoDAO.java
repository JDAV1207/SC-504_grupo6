package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;

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

    public static int obtenerNuevoID() {
        int id = 1;
        try (Connection con = ConexionOracle.getConnection(); PreparedStatement stmt = con.prepareStatement("SELECT MAX(ID_EMPLEADO) + 1 FROM EMPLEADO"); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                id = rs.getInt(1);
            }
            if (rs.wasNull()) {
                id = 1;
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular nuevo ID de empleado: " + e.getMessage());
        }
        return id;
    }

    //CREAR EMPLEADO
    public static boolean insertarEmpleado(int id, String nombre, String cargo, String telefono, String correo) {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_EMPLEADO(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, id);
            stmt.setString(2, nombre);
            stmt.setString(3, cargo);
            stmt.setString(4, telefono);
            stmt.setString(5, correo);
            stmt.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertarEmpleadoDesdeFormulario() {
        try {
            String nombre = JOptionPane.showInputDialog("Nombre y apellido:");
            String cargo = JOptionPane.showInputDialog("Cargo:");
            String telefono = JOptionPane.showInputDialog("Teléfono (8888-8888):");
            String correo = JOptionPane.showInputDialog("Correo:");

            if (nombre == null || nombre.trim().isEmpty()
                    || cargo == null || cargo.trim().isEmpty()
                    || telefono == null || telefono.trim().isEmpty()
                    || correo == null || correo.trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Complete todos los campos.");
                return false;
            }

            int id = Utilidades.obtenerNuevoID("EMPLEADOS", "ID_EMPLEADO");

            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_EMPLEADO(?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, cargo);
                stmt.setString(4, telefono);
                stmt.setString(5, correo);
                stmt.execute();

                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error SQL: " + e.getMessage());
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }

    public class Utilidades {

        public static int obtenerNuevoID(String tabla, String columna) throws SQLException {
            int nuevoID = 1;
            try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT MAX(" + columna + ") FROM " + tabla)) {

                if (rs.next()) {
                    nuevoID = rs.getInt(1) + 1;
                }
            }
            return nuevoID;
        }
    }

    //ACTIALIZAR EMPLEADO
    public static boolean actualizarEmpleado(int id, String nombre, String cargo, String telefono, String correo) {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_EMPLEADO(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, id);
            stmt.setString(2, nombre);
            stmt.setString(3, cargo);
            stmt.setString(4, telefono);
            stmt.setString(5, correo);
            stmt.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean editarEmpleadoDesdeTabla(DefaultTableModel modelo, int fila) {
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un empleado.");
            return false;
        }

        int id = (int) modelo.getValueAt(fila, 0);
        String nombre = JOptionPane.showInputDialog("Nuevo nombre:", modelo.getValueAt(fila, 1));
        String cargo = JOptionPane.showInputDialog("Nuevo cargo:", modelo.getValueAt(fila, 2));
        String telefono = JOptionPane.showInputDialog("Nuevo teléfono:", modelo.getValueAt(fila, 3));
        String correo = JOptionPane.showInputDialog("Nuevo correo:", modelo.getValueAt(fila, 4));

        if (nombre == null || nombre.trim().isEmpty()
                || cargo == null || cargo.trim().isEmpty()
                || telefono == null || telefono.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe completar todos los campos.");
            return false;
        }

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_EMPLEADO(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, id);
            stmt.setString(2, nombre);
            stmt.setString(3, cargo);
            stmt.setString(4, telefono);
            stmt.setString(5, correo);
            stmt.execute();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    //ELIMINAR EMPLEADO
    public static boolean eliminarEmpleado(int idEmpleado) {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_EMPLEADO(?)}")) {

            stmt.setInt(1, idEmpleado);
            stmt.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarEmpleadoDesdeTabla(DefaultTableModel modelo, int fila) {
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un empleado.");
            return false;
        }

        int idEmpleado = (int) modelo.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Seguro que desea eliminar este empleado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_EMPLEADO(?)}")) {
                stmt.setInt(1, idEmpleado);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
                return false;
            }
        }

        return false;
    }
}
