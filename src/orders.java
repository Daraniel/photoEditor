import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class orders {
    private JPanel parent;
    private JButton editButton;
    private JButton deleteButton;
    private JButton sendToLabButton;
    private JTable table1;

    static String username;
    private List<String> result;
    private List<String> result2;
    private List<String> result3;
    private DefaultTableModel model;
    private static JPanel mainFrame;
    static JFrame jFrame;
    static JFrame owner;
    static String editOrder;

    public static JPanel main(JPanel frame, String[] args) {
        mainFrame = frame;
        username = args[1];
        editOrder = args[2];
        return new orders().parent;
    }

    private orders() {
        table1.setRowSelectionInterval(0, 0);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = table1.getModel().getValueAt(table1.getSelectedRow(), 0).toString();
                if (editOrder.equals("2")) {
                    JPanel edit = pictureManager.main(new String[]{"just a r@ndom s@ftey key", username, input, "true"});
                    menu.openPopUp(edit, (JFrame) SwingUtilities.getWindowAncestor(parent));
                } else if (editOrder.equals("1")) {
                    JPanel edit = pictureManager.main(new String[]{"just a r@ndom s@ftey key", username, input, "false"});
                    menu.openPopUp(edit, (JFrame) SwingUtilities.getWindowAncestor(parent));
                }
            }
        });

        if (!editOrder.equals("1")) {
            deleteButton.setEnabled(false);
            deleteButton.setVisible(false);
        } else {
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String input;
                    input = table1.getModel().getValueAt(table1.getSelectedRow(), 0).toString();
                    int dialogResult = JOptionPane.showConfirmDialog(null,
                            "Are you sure to delete order " + input + "?", "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {

                        MsAccessDatabaseConnection.query("delete from OrderDetails where OrderNo = "
                                + input, true);

                        List<String> result = MsAccessDatabaseConnection.query("delete from Orders where OrderNo = "
                                + input, true);
                        if (result.contains("error")) {
                            demo.infoBox("Failed to delete order!", "Error");
                        } else {
                            File folder = new File(System.getProperty("user.dir") + File.separator + input + File.separator);
                            File[] selectedFiles = folder.listFiles();
                            if (selectedFiles == null) {
                                return;
                            }
                            for (File file : selectedFiles) {
                                file.delete();
                            }
                            demo.infoBox("Delete successful!", "Complete");
                        }
                    }
                    setTable1();
                }
            });
        }

        if (editOrder.equals("2")) {
            sendToLabButton.setText("Send to Print");
        } else if (editOrder.equals("1")) {
            sendToLabButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String input = table1.getModel().getValueAt(table1.getSelectedRow(), 0).toString();

                    File folder = new File(System.getProperty("user.dir") + File.separator + input + File.separator);
                    File[] selectedFiles = folder.listFiles();
                    if (selectedFiles == null) {
                        return;
                    }
                    for (int i = 0; i < selectedFiles.length; i++) {
                        List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM OrderDetails Where OrderNo =" + input +
                                " and PhotoFileName ='" + selectedFiles[i].getName() + "'");
                        if(result.size()==0){
                            demo.infoBox("Please add at least one size to each picture!", "Error");
                            return;
                        }
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0000'");
                    Date date = new Date(System.currentTimeMillis());

                    List<String> result3 = MsAccessDatabaseConnection.query("INSERT INTO OrderStatus VALUES('" + input + "','" +
                            formatter.format(date) + "', '" + username + "', '" + 2 + "')", true);
                    setTable1();
                }
            });
        }
    }

    private void createUIComponents() {
        model = new DefaultTableModel();
        model.addColumn("Order No.");
        model.addColumn("Reception Date");
        model.addColumn("Customer");
        model.addColumn("Priority");
        model.addColumn("Comments");
        model.addColumn("Status Change Date");
        model.addColumn("Operator");
        model.addColumn("Status");

        table1 = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTable1();
        table1.setSelectionModel(new ForcedListSelectionModel());

        table1.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        TableColumnModel colModel = table1.getColumnModel();
        colModel.getColumn(1).setPreferredWidth(colModel.getColumn(1).getWidth() * 2);
        colModel.getColumn(5).setPreferredWidth(colModel.getColumn(5).getWidth() * 2);

        JTableHeader anHeader = table1.getTableHeader();
        anHeader.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    private void setTable1() {
        int row = model.getRowCount();
        for (int i = 0; i < row; i++) {
            model.removeRow(0);
        }

        result = MsAccessDatabaseConnection.query("SELECT * FROM Orders");
        for (int i = 0; i < result.size() / 5; i += 1) {
            result2 = MsAccessDatabaseConnection.query("SELECT * FROM OrderStatus Where OrderNo ='" + result.get(i * 5) + "'");
            result3 = MsAccessDatabaseConnection.query("SELECT CustomerName FROM Customers  Where CustomerID='" + result.get(i * 5 + 2) + "'");
            if (result2.get(result2.size() - 1).equals(editOrder)) {
                model.addRow(new Object[]{result.get(i * 5), result.get(i * 5 + 1).split("\\.")[0], result3.get(0), result.get(i * 5 + 3),
                        result.get(i * 5 + 4), result2.get(result2.size() - 3).split("\\.")[0], result2.get(result2.size() - 2), result2.get(result2.size() - 1)});
            }
        }
    }

    public static class ForcedListSelectionModel extends DefaultListSelectionModel {

        public ForcedListSelectionModel() {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        @Override
        public void clearSelection() {
        }

        @Override
        public void removeSelectionInterval(int index0, int index1) {
        }

    }
}
