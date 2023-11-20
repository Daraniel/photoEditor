import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class menu {

    static JPanel edit = null;

    private static void changePasswordClick(String username, JPanel parent) {
        if (edit != null) {
            parent.removeAll();
            parent.revalidate();
            parent.repaint();
        }
        JPasswordField pass = new JPasswordField();
        JPasswordField pass1 = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();
        Object[] message = {
                "Current Password:", pass,
                "New Password:", pass1,
                "Confirm Password:", pass2,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM Users Where Username = '" +
                    username + "'");
            if (pass.getText().equals(result.get(1))) {
                if (pass1.getText().equals(pass2.getText())) {
                    if (pass1.getText().matches("[a-zA-Z0-9]+")) {
                        List<String> result2 = MsAccessDatabaseConnection.query("Update Users Set Password = '" +
                                pass1.getText() + "' Where Username = '" +
                                username + "'", true);
                        demo.infoBox("Password changed successfully!", "Success");
                        if (result2.contains("error")) {
                            demo.infoBox("Failed tp change password!", "Error");
                        }
                    } else {
                        demo.infoBox("Passwords must be alphanumeric!", "Error");
                    }
                } else {
                    demo.infoBox("Passwords don't match!", "Error");
                }
            } else {
                demo.infoBox("Wrong current password!", "Error");
            }
        }
    }

    static JMenuBar addMenu(JFrame frame, String username, JPanel parent) {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu menu = new JMenu("Edit");
        JMenu switchTab = new JMenu("Settings");
        JMenu order = new JMenu("Order");
        JMenu print = new JMenu("Print");
        JMenu help = new JMenu("Help");

        JMenuItem exit = new JMenuItem(new AbstractAction("Exit") {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        JMenuItem logOut = new JMenuItem(new AbstractAction("Logout") {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                login.main(new String[]{""});
            }
        });

        file.add(logOut);
        file.add(exit);

        JMenuItem users = new JMenuItem(new AbstractAction("Manage Users") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                JPanel edit = user.main(new String[]{"just a r@ndom s@ftey key", username});
                openPopUp(edit, frame);
            }
        });

        JMenuItem permission = new JMenuItem(new AbstractAction("Manage Permissions") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                JPanel edit = permissions.main(new String[]{"just a r@ndom s@ftey key", username});
                openPopUp(edit, frame);
            }
        });

        JMenuItem changePassword = new JMenuItem(new AbstractAction("Change Password") {

            public void actionPerformed(ActionEvent e) {
                changePasswordClick(username, parent);
            }
        });

        JMenuItem paperTypes = new JMenuItem(new AbstractAction("Paper Types") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });

        JMenuItem paperSizes = new JMenuItem(new AbstractAction("Paper Sizes") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });

        JMenuItem dpi = new JMenuItem(new AbstractAction("Allowed DPI") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }

                JTextField pass = new JTextField();
                JTextField pass1 = new JTextField();
                Object[] message = {
                        "Min DPI:", pass,
                        "Max DPI:", pass1,
                };

                List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM Settings");
                pass.setText(result.get(1));
                pass1.setText(result.get(2));

                int option = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    if (Integer.parseInt(pass.getText()) > Integer.parseInt(pass1.getText())) {
                        demo.infoBox("Minimum is bigger than Maximum!", "Error");
                        return;
                    }
                    if (pass1.getText().matches("[0-9]+") && pass.getText().matches("[0-9]+")) {
                        MsAccessDatabaseConnection.query("UPDATE Settings SET Min = " +
                                pass.getText() + ", Max = " + pass1.getText() + " Where ID=0", true);
                    } else {
                        demo.infoBox("Please enter numbers only!", "Error");
                    }
                }
            }
        });

        JMenuItem editPicture = new JMenuItem(new AbstractAction("Edit Picture") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                edit = demo.main(new String[]{"just a r@ndom s@ftey key", username});
                edit.setPreferredSize(parent.getSize());
                parent.add(edit);
                parent.revalidate();
                parent.repaint();
            }
        });

        JMenuItem Orders = new JMenuItem(new AbstractAction("Orders") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                JPanel edit = orders.main(parent, new String[]{"just a r@ndom s@ftey key", username, "1"});
                openPopUp(edit, frame);
            }
        });

        JMenuItem newOrders = new JMenuItem(new AbstractAction("New Order") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                JPanel edit = newOrder.main(parent, new String[]{"just a r@ndom s@ftey key", username});
                openPopUpSmall(edit, frame);                System.out.println("sda");

            }
        });

        JMenuItem customers = new JMenuItem(new AbstractAction("Customers") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });

        JMenuItem printOrders = new JMenuItem(new AbstractAction("Orders") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                JPanel edit = orders.main(parent, new String[]{"just a r@ndom s@ftey key", username, "3"});
                openPopUp(edit, frame);
            }
        });

        JMenuItem about = new JMenuItem(new AbstractAction("About...") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });

        JMenuItem editOrders = new JMenuItem(new AbstractAction("Orders") {
            public void actionPerformed(ActionEvent e) {
                if (edit != null) {
                    parent.removeAll();
                    parent.revalidate();
                    parent.repaint();
                }
                JPanel edit = orders.main(parent, new String[]{"just a r@ndom s@ftey key", username, "2"});
                openPopUp(edit, frame);
            }
        });

        order.add(newOrders);
        order.add(Orders);
        order.add(customers);

        menu.add(editOrders);
        menu.add(editPicture);

        help.add(about);

        print.add(printOrders);

//        switchTab.add(editPicture);
        switchTab.add(users);
        switchTab.add(permission);
        switchTab.add(changePassword);
        switchTab.add(new JSeparator());
        switchTab.add(paperTypes);
        switchTab.add(paperSizes);
        switchTab.add(new JSeparator());
        switchTab.add(dpi);

//        switch (currentFrame){
//            case "demo":
//                switchTab.remove(editPicture);
//                break;
//            case "user":
//                switchTab.remove(users);
//                break;
//        }

        menuBar.add(file);
        menuBar.add(order);
        menuBar.add(menu);
        menuBar.add(print);
        menuBar.add(switchTab);
        menuBar.add(help);
        frame.setJMenuBar(menuBar);
        return menuBar;
    }

    static void openPopUp(JPanel edit, JFrame frame) {
        JFrame jFrame = new JFrame();
        jFrame.getContentPane().add(edit);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setSize(600, 600);
        jFrame.setLocationRelativeTo(null);
        orders.jFrame = jFrame;
        orders.owner = frame;
        ModalFrameUtil.showAsModal(jFrame, frame);
    }

    static void openPopUpSmall(JPanel edit, JFrame frame) {
        JFrame jFrame = new JFrame();
        jFrame.getContentPane().add(edit);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setSize(280, 260);
        jFrame.setLocationRelativeTo(null);
        orders.jFrame = jFrame;
        orders.owner = frame;
        ModalFrameUtil.showAsModal(jFrame, frame);
    }
}
