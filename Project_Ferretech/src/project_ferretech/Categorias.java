/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package project_ferretech;

import java.sql.*;
import javax.swing.JOptionPane;
import java.sql.Connection;

public class Categorias extends javax.swing.JFrame {

    public Categorias() {
        initComponents();

    }

    // Método para obtener un nuevo ID
    private int obtenerNuevoID() {
        int nuevoID = 1;
        try (Connection con = ConexionOracle.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NVL(MAX(ID_CATEGORIA), 0) + 1 FROM CATEGORIAS")) {
            if (rs.next()) {
                nuevoID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nuevoID;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCategorias = new javax.swing.JTable();
        CrearCategoria = new javax.swing.JButton();
        EditarCategoria = new javax.swing.JButton();
        BorrarCategoria = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tablaCategorias.setModel(CategoriaDAO.obtenerCategorias());
        jScrollPane1.setViewportView(tablaCategorias);

        CrearCategoria.setText("Crear");
        CrearCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearCategoriaActionPerformed(evt);
            }
        });

        EditarCategoria.setText("Editar");
        EditarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditarCategoriaActionPerformed(evt);
            }
        });

        BorrarCategoria.setText("Eliminar");
        BorrarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarCategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BorrarCategoria, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CrearCategoria)
                            .addComponent(EditarCategoria))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CrearCategoria)
                .addGap(18, 18, 18)
                .addComponent(EditarCategoria)
                .addGap(18, 18, 18)
                .addComponent(BorrarCategoria)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CrearCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearCategoriaActionPerformed
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre de la categoría:");

        if (nombre != null && !nombre.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL INSERTAR_CATEGORIA(?,?)}")) {

                stmt.setInt(1, obtenerNuevoID());
                stmt.setString(2, nombre);
                stmt.execute();

                JOptionPane.showMessageDialog(this, "Categoría creada.");
                tablaCategorias.setModel(CategoriaDAO.obtenerCategorias());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_CrearCategoriaActionPerformed

    private void EditarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarCategoriaActionPerformed
        int fila = tablaCategorias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría.");
            return;
        }

        int id = (int) tablaCategorias.getValueAt(fila, 0);
        String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", tablaCategorias.getValueAt(fila, 1));

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            try (Connection con = ConexionOracle.getConnection(); CallableStatement stmt = con.prepareCall("{CALL ACTUALIZAR_CATEGORIA(?,?)}")) {

                stmt.setInt(1, id);
                stmt.setString(2, nuevoNombre);
                stmt.execute();

                JOptionPane.showMessageDialog(this, "Categoría actualizada.");
                tablaCategorias.setModel(CategoriaDAO.obtenerCategorias());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_EditarCategoriaActionPerformed

    private void BorrarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarCategoriaActionPerformed
int fila = tablaCategorias.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una categoría.");
        return;
    }

    int id = (int) tablaCategorias.getValueAt(fila, 0);

    int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection con = ConexionOracle.getConnection();
             CallableStatement stmt = con.prepareCall("{CALL ELIMINAR_CATEGORIA(?)}")) {

            stmt.setInt(1, id);
            stmt.execute();

            JOptionPane.showMessageDialog(this, "Categoría eliminada.");
            tablaCategorias.setModel(CategoriaDAO.obtenerCategorias());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_BorrarCategoriaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Categorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Categorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Categorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Categorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Categorias().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BorrarCategoria;
    private javax.swing.JButton CrearCategoria;
    private javax.swing.JButton EditarCategoria;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tablaCategorias;
    // End of variables declaration//GEN-END:variables
}
