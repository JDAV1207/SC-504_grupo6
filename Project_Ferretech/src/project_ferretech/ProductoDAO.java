/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_ferretech;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.Component;

public class ProductoDAO {

    // Método para obtener todos los productos
    public static DefaultTableModel obtenerProductos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Descripción");
        modelo.addColumn("Precio");
        modelo.addColumn("Stock");
        modelo.addColumn("ID Categoría");

        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos.");
                return modelo;
            }

            try (CallableStatement stmt = con.prepareCall("{CALL LISTAR_PRODUCTOS(?)}")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();

                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                            rs.getInt("ID_PRODUCTO"),
                            rs.getString("NOMBRE"),
                            rs.getString("DESCRIPCION"),
                            rs.getDouble("PRECIO"),
                            rs.getInt("STOCK"),
                            rs.getInt("ID_CATEGORIA")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return modelo;
    }

    //INSERTAR
    public static boolean insertarProducto(String nombre, String descripcion, double precio, int stock,
            int idCategoria) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{call INSERTAR_PRODUCTO(?, ?, ?, ?, ?)}")) {
                //stmt.setInt(1, id);
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setDouble(3, precio);
                stmt.setInt(4, stock);
                stmt.setInt(5, idCategoria);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public static void insertarProductoDesdeFormulario(JTable tablaProductos, Component parent) {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
        String descripcion = JOptionPane.showInputDialog("Ingrese descripción del producto:");
        String precioStr = JOptionPane.showInputDialog("Ingrese el precio:");
        String stockStr = JOptionPane.showInputDialog("Ingrese la cantidad en stock:");
        String idCategoriaStr = JOptionPane.showInputDialog("Ingrese el ID de la categoría:");

        if (nombre != null && !nombre.trim().isEmpty()
                && descripcion != null && !descripcion.trim().isEmpty()
                && precioStr != null && !precioStr.trim().isEmpty()
                && stockStr != null && !stockStr.trim().isEmpty()
                && idCategoriaStr != null && !idCategoriaStr.trim().isEmpty()) {

            try {
                double precio = Double.parseDouble(precioStr);
                int stock = Integer.parseInt(stockStr);
                int idCategoria = Integer.parseInt(idCategoriaStr);

                boolean exito = insertarProducto(nombre, descripcion, precio, stock, idCategoria);

                if (exito) {
                    JOptionPane.showMessageDialog(parent, "Producto agregado con éxito.");
                    tablaProductos.setModel(obtenerProductos());
                } else {
                    JOptionPane.showMessageDialog(parent, "Error al agregar el producto.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Error: Ingrese datos numéricos válidos.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Por favor, complete todos los campos.");
        }
    }

    //EDITAR
    public static boolean actualizarProducto(int id, String nombre, String descripcion, double precio, int stock, int idCategoria) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_PRODUCTO(?, ?, ?, ?, ?, ?)}")) {
                stmt.setInt(1, id);
                stmt.setString(2, nombre);
                stmt.setString(3, descripcion);
                stmt.setDouble(4, precio);
                stmt.setInt(5, stock);
                stmt.setInt(6, idCategoria);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
            return false;
        }
    }

    public static void actualizarProductoDesdeTabla(JTable tablaProducto, Component parent) {
        int fila = tablaProducto.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parent, "Seleccione un producto.");
            return;
        }

        int id = (int) tablaProducto.getValueAt(fila, 0);
        String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", tablaProducto.getValueAt(fila, 1));
        String nuevaDescripcion = JOptionPane.showInputDialog("Nueva descripción:", tablaProducto.getValueAt(fila, 2));
        String nuevoPrecioStr = JOptionPane.showInputDialog("Nuevo precio:", tablaProducto.getValueAt(fila, 3));
        String nuevoStockStr = JOptionPane.showInputDialog("Nuevo stock:", tablaProducto.getValueAt(fila, 4));
        String nuevaCategoriaStr = JOptionPane.showInputDialog("Nuevo ID de categoría:", tablaProducto.getValueAt(fila, 5));

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()
                && nuevaDescripcion != null && !nuevaDescripcion.trim().isEmpty()
                && nuevoPrecioStr != null && !nuevoPrecioStr.trim().isEmpty()
                && nuevoStockStr != null && !nuevoStockStr.trim().isEmpty()
                && nuevaCategoriaStr != null && !nuevaCategoriaStr.trim().isEmpty()) {

            try {
                double precio = Double.parseDouble(nuevoPrecioStr);
                int stock = Integer.parseInt(nuevoStockStr);
                int idCategoria = Integer.parseInt(nuevaCategoriaStr);

                boolean exito = actualizarProducto(id, nuevoNombre, nuevaDescripcion, precio, stock, idCategoria);

                if (exito) {
                    JOptionPane.showMessageDialog(parent, "Producto actualizado con éxito.");
                    tablaProducto.setModel(obtenerProductos());
                } else {
                    JOptionPane.showMessageDialog(parent, "Error al actualizar el producto.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Ingrese datos válidos.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Por favor, complete todos los campos.");
        }
    }

    //ELIMINAR
    public static boolean eliminarProducto(int id) {
        try (Connection con = ConexionOracle.getConnection()) {
            if (con == null) {
                System.err.println("No se pudo establecer conexión.");
                return false;
            }
            try (CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_PRODUCTO(?)}")) {
                stmt.setInt(1, id);
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }

    public static void eliminarProductoDesdeTabla(JTable tablaProducto, Component parent) {
        int fila = tablaProducto.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(parent, "Seleccione un producto.");
            return;
        }

        int id = (int) tablaProducto.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(parent, "¿Seguro que desea eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = eliminarProducto(id);

            if (exito) {
                JOptionPane.showMessageDialog(parent, "Producto eliminado correctamente.");
                tablaProducto.setModel(obtenerProductos());
            } else {
                JOptionPane.showMessageDialog(parent, "Error al eliminar el producto.");
            }
        }

    }
}
