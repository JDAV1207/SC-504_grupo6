package project_ferretech.daos;

import java.sql.*;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import project_ferretech.ConexionOracle;

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

    private static int obtenerNuevoID() {
        int nuevoID = 1;
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_PROVEEDOR), 0) + 1 FROM PROVEEDORES")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

    //INSERTAR PROVEEDOR
    public static void crearProveedor(JFrame parentFrame, JTable tablaProveedor) {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del proveedor:");
        String telefono = JOptionPane.showInputDialog("Ingrese el teléfono del proveedor:");
        String direccion = JOptionPane.showInputDialog("Ingrese la dirección del proveedor:");
        String correo = JOptionPane.showInputDialog("Ingrese el correo del proveedor:");

        if (nombre != null && !nombre.trim().isEmpty()
                && telefono != null && !telefono.trim().isEmpty()
                && direccion != null && !direccion.trim().isEmpty()
                && correo != null && !correo.trim().isEmpty()) {

            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_PROVEEDOR(?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, obtenerNuevoID()); // Este método debe estar disponible en ProveedorDAO o llamarse de una clase utilitaria
                stmt.setString(2, nombre);
                stmt.setString(3, telefono);
                stmt.setString(4, direccion);
                stmt.setString(5, correo);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Proveedor agregado.");
                tablaProveedor.setModel(obtenerProveedores());

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al agregar el proveedor.");
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Por favor, complete todos los campos.");
        }
    }

    //ACTUALIZAR PROVEEDOR
    public static void editarProveedor(JFrame parentFrame, JTable tablaProveedor) {
        int fila = tablaProveedor.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un proveedor.");
            return;
        }

        int id = (int) tablaProveedor.getValueAt(fila, 0);
        String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", tablaProveedor.getValueAt(fila, 1));
        String nuevoTelefono = JOptionPane.showInputDialog("Nuevo teléfono:", tablaProveedor.getValueAt(fila, 2));
        String nuevoDireccion = JOptionPane.showInputDialog("Nueva Dirección:", tablaProveedor.getValueAt(fila, 3));
        String nuevoCorreo = JOptionPane.showInputDialog("Nuevo Correo:", tablaProveedor.getValueAt(fila, 4));

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty() && nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_PROVEEDOR(?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, id);
                stmt.setString(2, nuevoNombre);
                stmt.setString(3, nuevoTelefono);
                stmt.setString(4, nuevoDireccion);
                stmt.setString(5, nuevoCorreo);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Proveedor actualizado.");
                tablaProveedor.setModel(obtenerProveedores());

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al actualizar el proveedor.");
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Por favor, complete al menos el nombre y teléfono.");
        }
    }

    //BOORAR PROVEEDOR
    public static void borrarProveedor(JFrame parentFrame, JTable tablaProveedor) {
        int fila = tablaProveedor.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un Proveedor.");
            return;
        }

        int id = (int) tablaProveedor.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_PROVEEDOR(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Proveedor eliminado.");
                tablaProveedor.setModel(obtenerProveedores());

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar el proveedor.");
            }
        }
    }

}
