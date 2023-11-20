import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.RescaleOp;
import java.util.List;

public class user {
    private JPanel parent;
    private JTable table1;
    private JButton addUserButton;
    private JButton updateButton;
    private JButton deleteUserButton;
    private static JFrame frame;

    private List<String> result;
    private DefaultTableModel model;

    private void generateTable(List<String> result) {
        for (int i = 0; i < result.size() / 4; i += 1) {
            if(!result.get(i * 4).equals("admin"))
                model.addRow(new Object[]{result.get(i * 4), result.get(i * 4 + 2), Boolean.parseBoolean(result.get(i * 4 + 3))});
        }
    }

    static JPanel main(String[] args) {
        return new user().parent;
    }

    private class updateButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            result = MsAccessDatabaseConnection.query("SELECT * FROM Users");
            String input;

            try {
                input = table1.getModel().getValueAt(table1.getSelectedRow(), 0).toString();
                JOptionPaneTwoInput(input, result.get(result.indexOf(input) + 1), result.get(result.indexOf(input) + 2),
                        Boolean.parseBoolean(result.get(result.indexOf(input) + 3)));
            } catch (Exception ex) {
                demo.infoBox("PLease select a row!", "Error!");
            }
        }
    }

    private class deleteUserButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            result = MsAccessDatabaseConnection.query("SELECT * FROM Users");
            String input;
            try {
                input = table1.getModel().getValueAt(table1.getSelectedRow(), 0).toString();
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure to delete username " + input + "?", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    List<String> result = MsAccessDatabaseConnection.query("delete from Users where Username = '"
                            + input + "'", true);
                    if (result.contains("error")) {
                        demo.infoBox("Failed to delete user!", "Error");
                    } else {
                        List<String> result2 = MsAccessDatabaseConnection.query("delete from Permissions where Username = '"
                                + input + "'", true);

                        demo.infoBox("Delete successful!", "Complete");
                        result = MsAccessDatabaseConnection.query("SELECT * FROM Users");
                        int rowCount = model.getRowCount();
                        //Remove rows one by one from the end of the table
                        for (int i = rowCount - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }
                        generateTable(result);
                    }
                }
            } catch (Exception ex) {
                demo.infoBox("PLease select a row!", "Error!");
            }
        }
    }

    private class addUserButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPaneThreeInput();
        }
    }

    private void JOptionPaneTwoInput(String Username, String Password, String RealName, boolean isActive) {
        JPasswordField pass = new JPasswordField(Password);
        JPasswordField pass2 = new JPasswordField(Password);
        JTextField realName = new JTextField(RealName);
        JCheckBox active = new JCheckBox();
        active.setSelected(isActive);
        Object[] message = {
                "Password:", pass,
                "Real Name:", realName,
                "Is Active:", active
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (pass.getText().equals("") || realName.getText().equals("")) {
                demo.infoBox("Empty password or real name field!", "Error");
                return;
            } else if (!pass.getText().matches("[a-zA-Z0-9]+")) {
                demo.infoBox("User name and password must be alphanumeric!", "Error");
                return;
            } else if (!pass.getText().equals(pass2.getText())) {
                demo.infoBox("Passwords dose not match!", "Error");
                return;
            }
            List<String> result = MsAccessDatabaseConnection.query("update Users SET Password='" + pass.getText()
                    + "', RealName='" + realName.getText() + "', IsActive='" + active.isSelected() + "' Where Username='" + Username + "'", true);
            if (result.contains("error")) {
                demo.infoBox("Failed to update the database!", "Error");
            } else {
                demo.infoBox("Update successful!", "Complete");
                result = MsAccessDatabaseConnection.query("SELECT * FROM Users");
                int rowCount = model.getRowCount();
                //Remove rows one by one from the end of the table
                for (int i = rowCount - 1; i >= 0; i--) {
                    model.removeRow(i);
                }
                generateTable(result);
            }
        }
    }

    private void JOptionPaneThreeInput() {
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();
        JTextField realName = new JTextField();
        JCheckBox active = new JCheckBox();
        Object[] message = {
                "Username:", user,
                "Password:", pass,
                "Confirm Password:", pass2,
                "Real Name:", realName,
                "Is Active:", active
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (user.getText().equals("") || pass.getText().equals("") || pass2.getText().equals("") || realName.getText().equals("")) {
                demo.infoBox("Empty password or real name field!", "Error");
                return;
            } else if (!user.getText().matches("[a-zA-Z0-9]+") || !pass.getText().matches("[a-zA-Z0-9]+")) {
                demo.infoBox("User name and password must be alphanumeric!", "Error");
                return;
            } else if (!pass.getText().equals(pass2.getText())) {
                demo.infoBox("Passwords dose not match!", "Error");
                return;
            }
            List<String> result = MsAccessDatabaseConnection.query("INSERT INTO Users Values('"
                    + user.getText() + "','" + pass.getText() + "','" + realName.getText() + "','" + active.isSelected() + "')", true);
            if (result.contains("error")) {
                demo.infoBox("Failed add new user!\nMaybe User name is already in use?", "Error");
            } else {
                List result2 = MsAccessDatabaseConnection.query("Select * from Forms", false);
                if (result2.contains("error")) {
                    MsAccessDatabaseConnection.query("delete from Users where Username = '"
                            + user.getText() + "'", true);
                    demo.infoBox("Failed add new user!\nMaybe User name is already in use?", "Error");
                } else {
                    try {
                        for (int i = 0; i < result2.size(); i++) {
                            MsAccessDatabaseConnection.query("INSERT INTO Permissions Values('" +
                                    result2.get(i) + "' , '" + user.getText() + "', 'false', 'false', 'false', 'false')", true);
                        }
                        demo.infoBox("User Added!", "Complete");
                        result = MsAccessDatabaseConnection.query("SELECT * FROM Users");
                        int rowCount = model.getRowCount();
                        //Remove rows one by one from the end of the table
                        for (int i = rowCount - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }
                        generateTable(result);
                    } catch (Exception ex) {
                        MsAccessDatabaseConnection.query("delete from Users where Username = '"
                                + user.getText() + "'", true);
                        demo.infoBox("Failed add new user!\nMaybe User name is already in use?", "Error");
                    }
                }
            }
        }
    }

    private user() {
        updateButton.addActionListener(new user.updateButtonClick());
        addUserButton.addActionListener(new user.addUserButtonClick());
        deleteUserButton.addActionListener(new user.deleteUserButton());
    }

    private void createUIComponents() {
        result = MsAccessDatabaseConnection.query("SELECT * FROM Users");
        model = new DefaultTableModel();
        model.addColumn("Username");
        model.addColumn("Real Name");
        model.addColumn("Is Active?");

        generateTable(result);

        table1 = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }

            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };

        table1.setSelectionModel(new orders.ForcedListSelectionModel());
        JTableHeader anHeader = table1.getTableHeader();
        anHeader.setBorder(BorderFactory.createLineBorder(Color.gray));
    }
}
