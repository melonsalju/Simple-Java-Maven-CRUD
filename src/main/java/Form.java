import java.awt.HeadlessException;
import javax.swing.JCheckBox;
import java.sql.*;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ITBSS
 */
public class Form extends javax.swing.JFrame {
    /**
     * Creates new form Form
     */
    private final String url = "jdbc:mysql://localhost:3306/mahasiswa?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private final String user = "root";
    private final String password = "";
    
    private Connection conn;
    private PreparedStatement pst;
    private Statement st;
    private ResultSet rs;
    
    private String id;
    
    String hobi = "";
    JCheckBox[] checkBoxes;
    
    public Form(String id) {       
        initComponents();
                
        checkBoxes = new JCheckBox[]{
            this.makan, this.masak, this.membaca,
            this.musik, this.nonton, this.olahraga,
            this.seni, this.tidur
        };
        
        if (!"".equals(id)) {
            getBiodata(id);
        }
    }
    
    private ArrayList<String> filterEmptyStringArray(String[] q) {
        ArrayList<String> result = new ArrayList<>();
        for (String r : q) {
            if (!r.equals(" ")) {
                result.add(r);
            }
        }
        
        return result;
    }
    
    private void clear() {
        Date currentDate = new Date();
        this.nama_input.setText("");
        this.nim_input.setText("");
        this.jurusan_input.setSelectedIndex(0);
        this.tptlahir_input.setText("");
        this.tgl_lahir_input.setDate(currentDate);
        this.alamat_input.setText("");
        this.jk_button_group.clearSelection();
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(false);
        }
        this.tb_input.setValue(0);
        this.bb_input.setValue(0);
        hobi = "";
    }
    
    private void getBiodata(String id) {       
        this.id = id;
        try {
            this.conn = DriverManager.getConnection(this.url, this.user, this.password);
            this.st = conn.createStatement();
            
            this.rs = this.st.executeQuery("SELECT * FROM biodata WHERE id = " + id);
            
            if (this.rs.next()) {
                ArrayList<String> hobbies = new ArrayList<>();
                hobbies = this.filterEmptyStringArray(rs.getString("hobi").split(" "));
                
                this.nama_input.setText(this.rs.getString("nama"));
                this.nim_input.setText(this.rs.getString("nim"));
                this.jurusan_input.setSelectedItem(this.rs.getString("jurusan"));
                this.tptlahir_input.setText(this.rs.getString("tpt_lahir"));
                this.tgl_lahir_input.setDate(this.rs.getDate("tgl_lahir"));
                this.alamat_input.setText(this.rs.getString("alamat"));
                
                if (this.L.getActionCommand().equals(this.rs.getString("jenis_kelamin"))) {
                    this.L.setSelected(true);                    
                } else if (this.P.getActionCommand().equals(this.rs.getString("jenis_kelamin"))) {
                    this.P.setSelected(true);
                }
                
                for (String hobby : hobbies) {
                    for (JCheckBox checkBox : this.checkBoxes) {
                        if (checkBox.getText().equals(hobby)) {
                            checkBox.setSelected(true);
                        }
                    }
                }
                
                this.tb_input.setValue(this.rs.getInt("tb"));
                this.bb_input.setValue(this.rs.getInt("bb"));
                
                this.submitButton.setActionCommand("update");
                this.submitButton.setText("Update");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void addMahasiswa(String action) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        
        String nama = this.nama_input.getText();
        String nim = this.nim_input.getText();
        String jurusan = this.jurusan_input.getSelectedItem().toString();
        String tempat_lahir = this.tptlahir_input.getText();
        String tgl_lahir = f.format(this.tgl_lahir_input.getDate());
        String alamat = this.alamat_input.getText();
        String jenis_kelamin = this.jk_button_group.getSelection().getActionCommand();
        int tb = Integer.parseInt(this.tb_input.getValue().toString());
        int bb = Integer.parseInt(this.bb_input.getValue().toString());
        
        for(JCheckBox checkBox : checkBoxes)
            if (checkBox.isSelected())
                hobi += checkBox.getText() + " ";
        
        if (action.equals("update")) {
            try {
                this.conn = DriverManager.getConnection(this.url, this.user, this.password);
                
                String q = "UPDATE biodata SET "
                        + "nama = ?, nim = ?, jurusan = ?, tpt_lahir = ?, "
                        + "tgl_lahir = ?, alamat = ?, jenis_kelamin = ?, "
                        + "hobi = ?, tb = ?, bb = ? WHERE id = " + this.id;
                
                this.pst = this.conn.prepareStatement(q);
                
                this.pst.setString(1, nama);
                this.pst.setString(2, nim);
                this.pst.setString(3, jurusan);
                this.pst.setString(4, tempat_lahir);
                this.pst.setDate(5, java.sql.Date.valueOf(tgl_lahir));
                this.pst.setString(6, alamat);
                this.pst.setString(7, jenis_kelamin);
                this.pst.setString(8, hobi);
                this.pst.setInt(9, tb);
                this.pst.setInt(10, bb);
                
                int rs = this.pst.executeUpdate();
                
                if (rs == 1) {
                    this.getBiodata(this.id);
                    JOptionPane.showMessageDialog(null, "Biodata berhasil di update!");
                    this.hobi = "";
                    return;
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                return;
            }
        }
        
        try {
            this.conn = DriverManager.getConnection(this.url, this.user, this.password);
            String q = "INSERT INTO biodata "
                    + "(nama, nim, jurusan, tpt_lahir, tgl_lahir, "
                    + "alamat, jenis_kelamin, hobi, tb, bb) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement st = this.conn.prepareStatement(q)) {
                st.setString(1, nama);
                st.setString(2, nim);
                st.setString(3, jurusan);
                st.setString(4, tempat_lahir);
                st.setDate(5, java.sql.Date.valueOf(tgl_lahir));
                st.setString(6, alamat);
                st.setString(7, jenis_kelamin);
                st.setString(8, hobi);
                st.setInt(9, tb);
                st.setInt(10, bb);
                
                int rs = st.executeUpdate();
                
                if (rs > 0) {
                    JOptionPane.showMessageDialog(null, "Biodata Tersimpan!");
                    this.hobi = "";
                }
            }
        }
        catch(HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jk_button_group = new javax.swing.ButtonGroup();
        Title = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        nama_input = new javax.swing.JTextField();
        nim_input = new javax.swing.JTextField();
        jurusan_input = new javax.swing.JComboBox<>();
        tptlahir_input = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        alamat_input = new javax.swing.JTextArea();
        L = new javax.swing.JRadioButton();
        P = new javax.swing.JRadioButton();
        makan = new javax.swing.JCheckBox();
        tidur = new javax.swing.JCheckBox();
        olahraga = new javax.swing.JCheckBox();
        masak = new javax.swing.JCheckBox();
        nonton = new javax.swing.JCheckBox();
        seni = new javax.swing.JCheckBox();
        musik = new javax.swing.JCheckBox();
        membaca = new javax.swing.JCheckBox();
        tb_input = new javax.swing.JSpinner();
        bb_input = new javax.swing.JSpinner();
        submitButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        goToIndexButton = new javax.swing.JButton();
        tgl_lahir_input = new com.toedter.calendar.JCalendar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Title.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        Title.setText("BIODATA MAHASISWA");

        jLabel1.setText("Nama");

        jLabel2.setText("NIM");

        jLabel3.setText("Jurusan");

        jLabel4.setText("Tempat Lahir");

        jLabel5.setText("Tanggal Lahir");

        jLabel6.setText("Alamat");

        jLabel7.setText("Jenis Kelamin");

        jLabel8.setText("Hobby");

        jLabel9.setText("Tinggi Badan");

        jLabel10.setText("Berat Badan");

        nama_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nama_inputActionPerformed(evt);
            }
        });

        nim_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nim_inputActionPerformed(evt);
            }
        });

        jurusan_input.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sistem dan Teknologi Informasi", "Bisnis Digital", "Kewirausahaan" }));

        alamat_input.setColumns(20);
        alamat_input.setRows(5);
        jScrollPane2.setViewportView(alamat_input);

        jk_button_group.add(L);
        L.setText("Laki-Laki");
        L.setActionCommand("L");
        L.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LActionPerformed(evt);
            }
        });

        jk_button_group.add(P);
        P.setText("Perempuan");
        P.setActionCommand("P");
        P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PActionPerformed(evt);
            }
        });

        makan.setText("Makan");
        makan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makanActionPerformed(evt);
            }
        });

        tidur.setText("Tidur");

        olahraga.setText("Olahraga");

        masak.setText("Masak");

        nonton.setText("Nonton");

        seni.setText("Seni");

        musik.setText("Musik");

        membaca.setText("Membaca");

        submitButton.setText("Simpan");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        jButton2.setText("Hapus");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        goToIndexButton.setText("Home");
        goToIndexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToIndexButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tb_input, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(makan)
                                        .addGap(18, 18, 18)
                                        .addComponent(tidur))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(olahraga)
                                            .addComponent(masak)
                                            .addComponent(nonton))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(seni)
                                            .addComponent(musik)
                                            .addComponent(membaca)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(L)
                                        .addGap(18, 18, 18)
                                        .addComponent(P))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bb_input, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 138, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addComponent(tptlahir_input, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tgl_lahir_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2))
                                        .addGap(47, 47, 47)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(nama_input, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(nim_input)
                                            .addComponent(jurusan_input, javax.swing.GroupLayout.Alignment.TRAILING, 0, 599, Short.MAX_VALUE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(goToIndexButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(submitButton)))
                            .addComponent(Title))
                        .addContainerGap(49, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(Title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nama_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nim_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jurusan_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(tptlahir_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tgl_lahir_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submitButton)
                        .addGap(44, 44, 44)
                        .addComponent(jButton2)
                        .addGap(47, 47, 47)
                        .addComponent(goToIndexButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(L)
                            .addComponent(P)))
                    .addComponent(jLabel6))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(makan)
                            .addComponent(tidur))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(olahraga)
                            .addComponent(seni))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(masak)
                            .addComponent(musik))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nonton)
                            .addComponent(membaca))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tb_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(bb_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nama_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nama_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nama_inputActionPerformed

    private void nim_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nim_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nim_inputActionPerformed

    private void LActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LActionPerformed

    private void PActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // TODO add your handling code here:
        this.addMahasiswa(evt.getActionCommand());
    }//GEN-LAST:event_submitButtonActionPerformed

    private void makanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_makanActionPerformed

    private void goToIndexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToIndexButtonActionPerformed
        // TODO add your handling code here:
        Index index = new Index();
        index.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_goToIndexButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.clear();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Form("").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton L;
    private javax.swing.JRadioButton P;
    private javax.swing.JLabel Title;
    private javax.swing.JTextArea alamat_input;
    private javax.swing.JSpinner bb_input;
    private javax.swing.JButton goToIndexButton;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.ButtonGroup jk_button_group;
    private javax.swing.JComboBox<String> jurusan_input;
    private javax.swing.JCheckBox makan;
    private javax.swing.JCheckBox masak;
    private javax.swing.JCheckBox membaca;
    private javax.swing.JCheckBox musik;
    private javax.swing.JTextField nama_input;
    private javax.swing.JTextField nim_input;
    private javax.swing.JCheckBox nonton;
    private javax.swing.JCheckBox olahraga;
    private javax.swing.JCheckBox seni;
    private javax.swing.JButton submitButton;
    private javax.swing.JSpinner tb_input;
    private com.toedter.calendar.JCalendar tgl_lahir_input;
    private javax.swing.JCheckBox tidur;
    private javax.swing.JTextField tptlahir_input;
    // End of variables declaration//GEN-END:variables
}
