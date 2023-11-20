import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class permissions {
    private JComboBox comboBox1;
    private JTable table1;
    private JPanel parent;
    private JLabel userRealName;

    private DefaultTableModel model;
    private List<String> result;

    static JPanel main(String[] args) {
        return new permissions().parent;
    }

    private permissions() {
        result = MsAccessDatabaseConnection.query("SELECT * FROM Users");

        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox1.getModel();
        comboBoxModel.addElement("");
        for (int i = 0; i < result.size() / 4; i++) {
            if(!result.get(i * 4).equals("admin"))
                comboBoxModel.addElement(result.get(i * 4));
        }

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBox1.getSelectedIndex()>0){
                    int t = model.getRowCount();
                    for(int i=0; i<t; i++){
                        model.removeRow(0);
                    }
                    List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM Users Where Username = '" +
                            comboBox1.getSelectedItem() + "'");
                    userRealName.setText(result.get(2));

                    result = MsAccessDatabaseConnection.query("SELECT * FROM Permissions Where Username = '" + comboBox1.getSelectedItem() + "'");
                    for (int i = 0; i < result.size() / 6; i += 1) {
                        model.addRow(new Object[]{result.get(i * 6), Boolean.parseBoolean(result.get(i * 6 + 2)),
                                Boolean.parseBoolean(result.get(i * 6 + 3)), Boolean.parseBoolean(result.get(i * 6 + 4)),
                                Boolean.parseBoolean(result.get(i * 6 + 5))});
                    }
                }
            }
        });
    }

    private void createUIComponents() {
        model = new DefaultTableModel();
        model.addColumn("Form Name");
        model.addColumn("Can View");
        model.addColumn("Can New");
        model.addColumn("Can Edit");
        model.addColumn("Can Delete");

        table1 = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };

        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table1.rowAtPoint(evt.getPoint());
                int col = table1.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    MsAccessDatabaseConnection.query("Update Permissions Set CanView = '" + table1.getValueAt(row, 1).toString() +
                            "', CanNew = '" +  table1.getValueAt(row, 2).toString() +
                            "', CanEdit = '" +  table1.getValueAt(row, 3).toString() +
                            "', CanDelete = '" +  table1.getValueAt(row, 4).toString() +
                            "' Where Username = '" +  comboBox1.getSelectedItem() + "' and FormName = '" + table1.getValueAt(row, 0) +
                            "'", true);
                }
            }
        });

        JTableHeader anHeader = table1.getTableHeader();
        anHeader.setBorder(BorderFactory.createLineBorder(Color.gray));
    }
}
