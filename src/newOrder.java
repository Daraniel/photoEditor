import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;

public class newOrder {
    private JComboBox comboBox2;
    private JComboBox comboBox1;
    private JLabel orderNo;
    private JLabel customerID;
    private JPanel parent;
    private JButton okButton;
    private JTextPane commentTextArea;
    private JButton button1;
    private JPanel oI;
    private static JPanel mainFrame;
    private static String username;

    public static JPanel main(JPanel frame, String[] args) {
        mainFrame = frame;
        username = args[1];
        return new newOrder().parent;
    }

    private newOrder(){
        int number = Integer.parseInt(MsAccessDatabaseConnection.query("SELECT MAX(OrderNo) FROM Orders").get(0)) + 1;
        orderNo.setText(String.valueOf(number));

        List<String> result = MsAccessDatabaseConnection.query("SELECT CustomerName FROM Customers");
        DefaultComboBoxModel model1 = new DefaultComboBoxModel(result.toArray(new String[result.size()]));
        comboBox1.setModel(model1);
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerID.setText(String.valueOf(comboBox1.getSelectedIndex()));
            }
        });

        DefaultComboBoxModel model2 = new DefaultComboBoxModel(new String[]{"Low", "Normal", "High"});
        comboBox2.setModel(model2);
        comboBox2.setSelectedIndex(1);

        ((TitledBorder)oI.getBorder()).setTitleColor(Color.blue);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBox1.getSelectedIndex()<=0){
                    demo.infoBox("Please select a customer", "Error");
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0000'");
                Date date = new Date(System.currentTimeMillis());

                List<String> result2 = MsAccessDatabaseConnection.query("INSERT INTO Orders VALUES('" + number + "','" +
                        formatter.format(date) + "', '" + comboBox1.getSelectedIndex() + "', '" + comboBox2.getSelectedIndex() + "','" +
                        commentTextArea.getText() + "')", true);
                if (result2.contains("error")) {
                    demo.infoBox("failed", "test");
                } else {
                    List<String> result3 = MsAccessDatabaseConnection.query("INSERT INTO OrderStatus VALUES('" + number + "','" +
                            formatter.format(date) + "', '" + username + "', '" + 1 + "')", true);
                    if (result3.contains("error")) {
                        MsAccessDatabaseConnection.query("delete from Orders where OrderNo = '"
                                + number + "'", true);
                        demo.infoBox("failed", "test");
                    } else {
                        JPanel edit = pictureManager.main(new String[]{"just a r@ndom s@ftey key",
                                username, String.valueOf(number), "false"});
                        ((JFrame) SwingUtilities.getWindowAncestor(parent)).dispose();
                        menu.openPopUp(edit, (JFrame) SwingUtilities.getWindowAncestor(mainFrame));
                    }
                }
            }
        });
    }
}
