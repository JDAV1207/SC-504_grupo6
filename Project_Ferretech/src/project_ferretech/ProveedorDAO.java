package project_ferretech;

import java.sql.*;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class ProveedorDAO {

    public static DefaultTableModel obtenerProveedores() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Telefono");
        modelo.addColumn("Direccion");
        modelo.addColumn("Correo");

        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos.");
                return modelo;
            }

            try (CallableStatement stmt = con.prepareCall("{CALL LISTAR_PROVEEDORES(?)}")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();

                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getInt("ID_PROVEEDOR"),
                            rs.getString("NOMBRE"),
                            rs.getString("TELEFONO"),
                            rs.getString("DIRECCION"),
                            rs.getString("CORREO")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return modelo;
    }

    //INSERTAR PROVEEDOR
    public static boolean insertarProveedor(int id, String nombre, String telefono, String direccion, String correo) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión con la base de datos.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_PROVEEDOR(?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, telefono);
                stmt.setString(4, direccion);
                stmt.setString(5, correo);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    public static int obtenerNuevoID() {
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_PROVEEDOR), 0) + 1 FROM PROVEEDORES")) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de proveedor: " + e.getMessage());
        }
        return 1;
    }

    public static void crearProveedorDesdeFormulario(JTable tablaProveedor, Component parent) {
        try {
            String nombre = JOptionPane.showInputDialog(parent, "Nombre del proveedor:");
            String telefono = JOptionPane.showInputDialog(parent, "Teléfono:");
            String direccion = JOptionPane.showInputDialog(parent, "Dirección:");
            String correo = JOptionPane.showInputDialog(parent, "Correo:");

            if (nombre == null || telefono == null || direccion == null || correo == null
                    || nombre.trim().isEmpty() || telefono.trim().isEmpty() || direccion.trim().isEmpty() || correo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Por favor, complete todos los campos.");
                return;
            }

            int id = obtenerNuevoID();

            if (insertarProveedor(id, nombre, telefono, direccion, correo)) {
                JOptionPane.showMessageDialog(parent, "Proveedor agregado correctamente.");
                tablaProveedor.setModel(obtenerProveedores());
            } else {
                JOptionPane.showMessageDialog(parent, "Error al agregar el proveedor.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Datos inválidos o cancelados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //ACTUALIZAR PROVEEDOR
    public static boolean actualizarProveedor(int id, String nombre, String telefono, String direccion, String correo) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call ACTUALIZAR_PROVEEDOR(?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, telefono);
                stmt.setString(4, direccion);
                stmt.setString(5, correo);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    public static void actualizarProveedorDesdeTabla(JTable tablaProveedor, Component parentComponent) {
        int fila = tablaProveedor.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentComponent, "Seleccione un proveedor.");
            return;
        }

        int id = (int) tablaProveedor.getValueAt(fila, 0);
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del proveedor:");
        String telefono = JOptionPane.showInputDialog("Ingrese el teléfono del proveedor:");
        String direccion = JOptionPane.showInputDialog("Ingrese la dirección del proveedor:");
        String correo = JOptionPane.showInputDialog("Ingrese el correo del proveedor:");

        if (nombre != null && !nombre.trim().isEmpty()
                && telefono != null && !telefono.trim().isEmpty()
                && direccion != null && !direccion.trim().isEmpty()
                && correo != null && !correo.trim().isEmpty()) {

            boolean resultado = actualizarProveedor(id, nombre, telefono, direccion, correo);

            if (resultado) {
                JOptionPane.showMessageDialog(parentComponent, "Proveedor actualizado correctamente.");
                tablaProveedor.setModel(obtenerProveedores());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Error al actualizar el proveedor.");
            }
        } else {
            JOptionPane.showMessageDialog(parentComponent, "Por favor, complete todos los campos.");
        }
    }

    //BOORAR PROVEEDOR
    public static boolean eliminarProveedor(int id) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }

            try (CallableStatement stmt = con.prepareCall("{call ELIMINAR_PROVEEDOR(?)}")) {
                stmt.setInt(1, id);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }

    public static void eliminarProveedorDesdeTabla(JTable tablaProveedor, Component parentComponent) {
        int fila = tablaProveedor.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentComponent, "Seleccione un proveedor.");
            return;
        }

        int id = (int) tablaProveedor.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(
                parentComponent,
                "¿Está seguro que desea eliminar este proveedor?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = eliminarProveedor(id);

            if (eliminado) {
                JOptionPane.showMessageDialog(parentComponent, "Proveedor eliminado correctamente.");
                tablaProveedor.setModel(obtenerProveedores());
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Error al eliminar el proveedor.");
            }
        }

    }
}
