import com.sun.javafx.css.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Main {
    public JPanel parent;
    private JButton editPicturesButton;
    private JButton manageUsersButton;
    private static JFrame frame;
    private static String username;

    public static void main(String[] args) {
        username = args[1];
        frame = new JFrame("Main");
        demo.setSize(frame);
        frame.setContentPane(new Main().parent);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Main() {
        menu.addMenu(frame, username, parent);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                try {
                    parent.getComponent(0).setSize(parent.getSize());
                    parent.getComponent(0).getComponentAt(300, 300).setSize(new Dimension(parent.getWidth()-500, parent.getHeight()-500));
                    parent.getComponent(0).getComponentAt(300, 300).getComponentAt(50, 50).setSize(new Dimension(parent.getWidth()-500, parent.getHeight()-500));
                    parent.revalidate();
                    parent.repaint();
                } catch (Exception ex){

                }
            }
        });
    }
}
