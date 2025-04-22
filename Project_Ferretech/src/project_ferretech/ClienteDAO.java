package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;

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

    //CREAR CLIENTE
    public static void insertarCliente(int idCliente, String nombre, String telefono, String direccion, String correo) throws SQLException {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_CLIENTE(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, idCliente);
            stmt.setString(2, nombre);
            stmt.setString(3, telefono);
            stmt.setString(4, direccion);
            stmt.setString(5, correo);
            stmt.execute();
        }
    }

    public static void crearClienteDesdeFormulario(JTable tablaClientes, Component parent) {
        String nombre = JOptionPane.showInputDialog(parent, "Ingrese el nombre y apellido del cliente:");
        String telefono = JOptionPane.showInputDialog(parent, "Ingrese el teléfono del cliente (8888-8888):");
        String direccion = JOptionPane.showInputDialog(parent, "Ingrese la dirección del cliente:");
        String correo = JOptionPane.showInputDialog(parent, "Ingrese el correo del cliente:");

        if (nombre != null && !nombre.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_CLIENTE(?, ?, ?, ?, ?)}")) {

                stmt.setString(1, nombre);
                stmt.setString(2, telefono);
                stmt.setString(3, direccion);
                stmt.setString(4, correo);
                stmt.execute();

                tablaClientes.setModel(ClienteDAO.obtenerClientes());
                JOptionPane.showMessageDialog(parent, "Cliente registrado.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error al registrar el cliente.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Debe completar el nombre.");
        }
    }

    //Editar CLIENTE
    public static void actualizarCliente(int idCliente, String nombre, String telefono, String direccion, String correo) throws SQLException {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_CLIENTE(?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, idCliente);
            stmt.setString(2, nombre);
            stmt.setString(3, telefono);
            stmt.setString(4, direccion);
            stmt.setString(5, correo);
            stmt.execute();
        }
    }

    public static void editarClienteDesdeTabla(JTable tablaClientes, Component parent) {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parent, "Seleccione un cliente.");
            return;
        }

        int idCliente = (int) tablaClientes.getValueAt(fila, 0);
        String nombre = JOptionPane.showInputDialog(parent, "Ingrese el nuevo nombre del cliente:", tablaClientes.getValueAt(fila, 1));
        String telefono = JOptionPane.showInputDialog(parent, "Ingrese el nuevo teléfono del cliente:", tablaClientes.getValueAt(fila, 2));
        String direccion = JOptionPane.showInputDialog(parent, "Ingrese la nueva dirección del cliente:", tablaClientes.getValueAt(fila, 3));
        String correo = JOptionPane.showInputDialog(parent, "Ingrese el nuevo correo del cliente:", tablaClientes.getValueAt(fila, 4));

        if (nombre != null && !nombre.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_CLIENTE(?, ?, ?, ?, ?)}")) {

                stmt.setInt(1, idCliente);
                stmt.setString(2, nombre);
                stmt.setString(3, telefono);
                stmt.setString(4, direccion);
                stmt.setString(5, correo);
                stmt.execute();

                tablaClientes.setModel(ClienteDAO.obtenerClientes());
                JOptionPane.showMessageDialog(parent, "Cliente actualizado.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error al actualizar el cliente.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Debe completar el nombre.");
        }
    }

    //ELIMINAR CLIENTE
    public static void eliminarCliente(int idCliente) throws SQLException {
        try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_CLIENTE(?)}")) {

            stmt.setInt(1, idCliente);
            stmt.execute();
        }
    }

    public static void eliminarClienteDesdeTabla(JTable tablaClientes, Component parent) {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parent, "Seleccione un cliente.");
            return;
        }

        int idCliente = (int) tablaClientes.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(parent, "¿Seguro que desea eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_CLIENTE(?)}")) {

                stmt.setInt(1, idCliente);
                stmt.execute();
                tablaClientes.setModel(ClienteDAO.obtenerClientes());
                JOptionPane.showMessageDialog(parent, "Cliente eliminado.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error al eliminar el cliente.");
            }
        }
    }

}
