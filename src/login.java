import com.alee.laf.WebLookAndFeel;
import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class login {
    public JPanel parent;
    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private static JFrame frame;

    public static void main(String[] args) {
//        try {
////            UIManager.put( "control", new Color( 128, 128, 128) );
////            UIManager.put( "info", new Color(128,128,128) );
////            UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
////            UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
////            UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
////            UIManager.put( "nimbusFocus", new Color(115,164,209) );
////            UIManager.put( "nimbusGreen", new Color(176,179,50) );
////            UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
////            UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
////            UIManager.put( "nimbusOrange", new Color(191,98,4) );
////            UIManager.put( "nimbusRed", new Color(169,46,34) );
////            UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
////            UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
////            UIManager.put( "text", new Color( 230, 230, 230) );
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // cross platform
//            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // metal
//            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel"); // MotifLookAndFeel
//            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // nimbus
//            //UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
//            //UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
//            //UIManager.setLookAndFeel ( "com.alee.laf.WebLookAndFeel" );
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }

        BasicLookAndFeel darcula = new DarculaLaf();
        try {
            UIManager.setLookAndFeel(darcula);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame = new JFrame("Login");
        frame.setContentPane(new login().parent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setSize(200, 160);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class loginButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List result = MsAccessDatabaseConnection.query("SELECT * FROM Users WHERE Username = \"" + username.getText() + "\"");
            if (result.size() == 0) {
                demo.infoBox("Wrong username or password!", "Error!");
            } else if (result.get(3).equals("false")) {
                demo.infoBox("User Is inactive!", "Error!");
            } else if (result.get(1).equals(password.getText())) {
                Main.main(new String[]{"just a r@ndom s@ftey key", username.getText()});
                frame.dispose();
            } else {
                demo.infoBox("Wrong username or password!", "Error!");
            }
        }
    }

    private login() {
        loginButton.addActionListener(new login.loginButtonClick());
    }
}
