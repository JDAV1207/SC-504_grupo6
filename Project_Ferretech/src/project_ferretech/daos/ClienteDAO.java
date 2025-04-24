package project_ferretech.daos;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JFrame;
import project_ferretech.ConexionOracle;

public class ClienteDAO {

    public static DefaultTableModel obtenerClientes() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Cliente");
        modelo.addColumn("Nombre");
        modelo.addColumn("Telefono");
        modelo.addColumn("Direccion");
        modelo.addColumn("Correo");

        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL LISTAR_CLIENTES(?)}")) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_CLIENTE"),
                    rs.getString("NOMBRE"),
                    rs.getString("TELEFONO"),
                    rs.getString("DIRECCION"),
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
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_CLIENTE), 0) + 1 FROM CLIENTES")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

    //CREAR CLIENTE
    public static void crearCliente(JFrame parentFrame, JTable tablaClientes) {
        String nombre;
        String telefono;
        String direccion;
        String correo;

        try {
            int idCliente = obtenerNuevoID();

            nombre = JOptionPane.showInputDialog(parentFrame, "Ingrese el nombre y apellido del cliente:");
            telefono = JOptionPane.showInputDialog(parentFrame, "Ingrese el teléfono del cliente, separado por un guion (8888-8888):");
            direccion = JOptionPane.showInputDialog(parentFrame, "Ingrese la direccion del cliente");
            correo = JOptionPane.showInputDialog(parentFrame, "Ingrese el correo del cliente:");

            if (nombre != null && !nombre.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Cliente.INSERTAR_CLIENTE(?, ?, ?, ?, ?)}")) {

                    stmt.setInt(1, idCliente);
                    stmt.setString(2, nombre);
                    stmt.setString(3, telefono);
                    stmt.setString(4, telefono);
                    stmt.setString(5, correo);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Cliente registrado.");
                    tablaClientes.setModel(obtenerClientes());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al registrar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Editar CLIENTE
    public static void editarCliente(JFrame parentFrame, JTable tablaClientes) {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un cliente.");
            return;
        }

        int idCliente = (int) tablaClientes.getValueAt(fila, 0);
        String nombre;
        String telefono;
        String direccion;
        String correo;

        try {
            nombre = JOptionPane.showInputDialog("Ingrese el nuevo nombre y apellido del cliente:", tablaClientes.getValueAt(fila, 1));
            telefono = JOptionPane.showInputDialog("Ingrese el nuevo telefono del cliente, separado por un guion (8888-8888):", tablaClientes.getValueAt(fila, 2));
            direccion = JOptionPane.showInputDialog("Ingrese la nueva direccion del cliente:", tablaClientes.getValueAt(fila, 3));
            correo = JOptionPane.showInputDialog("Ingrese el nuevo correo del cliente:", tablaClientes.getValueAt(fila, 4));

            if (nombre != null && !nombre.trim().isEmpty()) {
                try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Cliente.ACTUALIZAR_CLIENTE(?, ?, ?, ?, ?)}")) {

                    stmt.setInt(1, idCliente);
                    stmt.setString(2, nombre);
                    stmt.setString(3, telefono);
                    stmt.setString(4, direccion);
                    stmt.setString(5, correo);
                    stmt.execute();

                    JOptionPane.showMessageDialog(parentFrame, "Cliente actualizado.");
                    tablaClientes.setModel(obtenerClientes());
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Error al actualizar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error: Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //ELIMINAR CLIENTE
    public static void eliminarCliente(JFrame parentFrame, JTable tablaClientes) {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Seleccione un cliente.");
            return;
        }

        int id = (int) tablaClientes.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Seguro que desea eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL Pk_Procedimiento_Cliente.ELIMINAR_CLIENTE(?)}")) {

                stmt.setInt(1, id);
                stmt.execute();

                JOptionPane.showMessageDialog(parentFrame, "Cliente eliminado.");
                tablaClientes.setModel(obtenerClientes());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error al eliminar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
