import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;

public class demo extends JPanel {
    public JPanel parent;
    public JButton openButton;
    public JButton saveButton;
    public JPanel imageJpanel;
    private JLabel jLabel;
    private JButton zoomOutButton;
    private JButton zoomInButton;
    private JScrollPane jScrollPanel;
    private JSlider contrast;
    private JSlider brightnessN;
    private JSlider red;
    private JSlider green;
    private JSlider blue;
    private JSlider hue;
    private JSlider saturation;
    private JSlider brightness;
    private JButton applyButton;
    private JButton resetButton;
    private JButton resetButton1;
    private JButton resetButton2;
    private JButton resetButton3;
    private JButton resetButton4;
    private JButton resetButton5;
    private JButton resetButton6;
    private JButton resetButton7;
    private JButton resetButton8;
    private static JFrame frame;

    private BufferedImage image;
    private BufferedImage tempImage;
    private Image scaledImage;
    private File selectedFile;
    private ImageInfo ii;

    public static JPanel main(String[] args) {
        args = new String[]{"sad", "admin"};
//        if(!(args[0].equals("just a r@ndom s@ftey key") && MsAccessDatabaseConnection.query("SELECT * FROM Users").contains(args[1]))){
//            infoBox("Nice try but please login!", "Hack proof!");
//            return;
//        }

        return new demo().parent;
    }

    static void setSize(JFrame frame1) {
        if (!System.getProperty("os.name").startsWith("Windows")) {
            frame1.getContentPane().setPreferredSize(
                    Toolkit.getDefaultToolkit().getScreenSize());
            frame1.pack();
            frame1.setVisible(true);
//            frame.show();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Point p = new Point(0, 0);
                    SwingUtilities.convertPointToScreen(p, frame1.getContentPane());
                    Point l = frame1.getLocation();
                    l.x -= p.x;
                    l.y -= p.y;
                    frame1.setLocation(p);
                }
            });
        }
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame1.setBounds(100, 100, (int) dim.getWidth() - 200, (int) dim.getHeight() - 150);
    }

    static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    private void setImage() {
        try {
            imageJpanel.removeAll();
            JLabel label = new JLabel();

            int width = image.getWidth();
            int height = image.getHeight();
            int boundHeight = imageJpanel.getHeight() / 9 * 8;
            int boundWidth = imageJpanel.getWidth() / 9 * 8;
            if (height > boundHeight) {
                scaledImage = tempImage.getScaledInstance(width * boundHeight / height, boundHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else if (width > boundWidth) {
                scaledImage = tempImage.getScaledInstance(boundWidth, height * boundWidth / width, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else {
                scaledImage = tempImage;
                label.setIcon(new ImageIcon(scaledImage));
            }
            TitledBorder border = new TitledBorder(selectedFile.getName());
            imageJpanel.setBorder(border);
            //JLabel label2;
            //imageJpanel.add(label2);
            imageJpanel.setLayout(new BoxLayout(imageJpanel, BoxLayout.Y_AXIS));
            imageJpanel.add(label);
            imageJpanel.revalidate();
            imageJpanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "File is damaged!");
            ex.printStackTrace();
        }
    }

    private void editImage() {
        RescaleOp op = new RescaleOp(contrast.getValue(), brightnessN.getValue(), null);
        tempImage = op.filter(image, tempImage);

        //L = 0.2126*R + 0.7152*G + 0.0722*B

        for (int Y = 0; Y < image.getHeight(); Y++) {
            for (int X = 0; X < image.getWidth(); X++) {
                int RGB = tempImage.getRGB(X, Y);
                int R = (RGB >> 16) & 0xff;
                int G = (RGB >> 8) & 0xff;
                int B = (RGB) & 0xff;
                float HSV[] = new float[3];
                Color.RGBtoHSB(ensureRange(R + red.getValue(), 0, 255),
                        ensureRange(G + green.getValue(), 0, 255),
                        ensureRange(B + blue.getValue(), 0, 255), HSV);
                tempImage.setRGB(X, Y, Color.getHSBColor(
                        ensureRange(HSV[0] + (float) hue.getValue() / 100, 0, 1),
                        ensureRange(HSV[1] + (float) saturation.getValue()/100, 0, 1),
                        ensureRange(HSV[2] + (float) brightness.getValue()/100, 0, 1)).getRGB());
            }
        }
        setImage();
    }

    static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    static float ensureRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    static boolean inRange(int value, int min, int max) {
        return (value>= min) && (value<= max);
    }

    private class openButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileFilter imageFilter = new FileNameExtensionFilter(
                    "Image files", ImageIO.getReaderFileSuffixes());
            fileChooser.addChoosableFileFilter(imageFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    selectedFile = fileChooser.getSelectedFile();
                    image = ImageIO.read(selectedFile);
                    tempImage = ImageIO.read(selectedFile);
                    setImage();

                    ii = new ImageInfo();
                    ImageInputStream iis = ImageIO.createImageInputStream(selectedFile);
                    ii.setInput(iis); // in can be InputStream or RandomAccessFile
                    ii.setDetermineImageNumber(true); // default is false
                    ii.setCollectComments(true); // default is false
                    if (!ii.check()) {
                        System.err.println("Not a supported image file format.");
                        return;
                    }

                    String date = ii.getFormatName() + ", " + ii.getMimeType() +
                            ", " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " +
                            ii.getBitsPerPixel() + " bits per pixel,  " + ii.getPhysicalWidthDpi() +
                            "  width dpi &  " + ii.getPhysicalHeightDpi() + "  height dpi.";

                    BasicFileAttributes attr = Files.readAttributes(Paths.get(selectedFile.getAbsolutePath()), BasicFileAttributes.class);

                    try {
                        String string = "<html>" + date + "<br>Creation Time: " + attr.creationTime() + "   Last Access Time: "
                                + attr.lastAccessTime() + "   Last Modified Time: " + attr.lastModifiedTime() + "</html>";
                        jLabel.setText(string);
                        //label2 = new JLabel(string);
                    } catch (Exception ex) {
                        jLabel.setText("Unable to get file information!");
                        //label2 = new JLabel("Unable to get file information!");
                        ex.printStackTrace();
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, "Invalid File!");
                    ex.printStackTrace();
                }
            }
        }
    }

    private class saveButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileFilter imageFilter = new FileNameExtensionFilter(
                    "Image files", ImageIO.getReaderFileSuffixes());
            fileChooser.addChoosableFileFilter(imageFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showSaveDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                String format;
                boolean bool = false;
                try {
                    if (fileChooser.getSelectedFile().toString().split("\\.").length - 1 == 0) {
                        throw new Exception("error!");
                    }
                    format = fileChooser.getSelectedFile().toString().split("\\.")
                            [fileChooser.getSelectedFile().toString().split("\\.").length - 1];
                } catch (Exception ex) {
                    format = ii.getFormatName();
                    bool = true;
                }
                try {
                    if (bool) {
                        ImageIO.write(tempImage, format, new File(
                                fileChooser.getSelectedFile().getAbsoluteFile() + "." + ii.getFormatName()));
                        JOptionPane.showMessageDialog(parent, "Image Saved!");
                    } else {
                        ImageIO.write(tempImage, format, fileChooser.getSelectedFile());
                        JOptionPane.showMessageDialog(parent, "Image Saved!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Unable to Save!");
                    ex.printStackTrace();
                }
            }
        }
    }

    private class brightnessClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String scale = JOptionPane.showInputDialog("Please Input Brightness Scale: ");
            try {
                RescaleOp op = new RescaleOp(1, Float.parseFloat(scale), null);
                tempImage = op.filter(image, tempImage);
                setImage();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Please Input a Number!");
            }
        }
    }

    private class contrastClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String scale = JOptionPane.showInputDialog("Please Input Contrast Scale: ");
            try {
                if (Float.parseFloat(scale) < 0) {
                    JOptionPane.showMessageDialog(parent, "Please Input a Positive Number!");
                } else {
                    RescaleOp op = new RescaleOp(Float.parseFloat(scale), 0, null);
                    tempImage = op.filter(image, tempImage);
                    setImage();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Please Input a Positive Number!");
            }
        }
    }

    private class zoomInClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            imageJpanel.removeAll();
            JLabel label = new JLabel();
            scaledImage = tempImage.getScaledInstance((int) Math.floor(scaledImage.getWidth(null) * 0.9),
                    (int) Math.floor(scaledImage.getHeight(null) * 0.9), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
            imageJpanel.add(label);
            imageJpanel.revalidate();
            imageJpanel.repaint();
        }
    }

    private class zoomOutClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            imageJpanel.removeAll();
            JLabel label = new JLabel();
            scaledImage = tempImage.getScaledInstance((int) Math.floor(scaledImage.getWidth(null) * 1.1),
                    (int) Math.floor(scaledImage.getHeight(null) * 1.1), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
            imageJpanel.add(label);
            imageJpanel.revalidate();
            imageJpanel.repaint();
        }
    }

    private class applyButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editImage();
        }
    }

    private class resetButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            contrast.setValue(1);
            brightnessN.setValue(0);
            red.setValue(0);
            green.setValue(0);
            blue.setValue(0);
            hue.setValue(0);
            saturation.setValue(0);
            brightness.setValue(0);
            editImage();
        }
    }

    MouseAdapter ma = new MouseAdapter() {
        private Point offset;
        private Point clickPoint;
        private JLabel clickedPanel;

        @Override
        public void mousePressed(MouseEvent e) {
            // Get the current clickPoint, this is used to determine if the
            // mouseRelease event was part of a drag operation or not
            clickPoint = e.getPoint();
            // Determine if there is currently a selected panel or nor
            if (clickedPanel != null) {
                // Move the selected panel to a new location
                moveSelectedPanelTo(e.getPoint());
                // Reset all the other stuff we might other was have set eailer
                offset = null;
                clickedPanel = null;
            } else {
                // Other wise, find which component was clicked
                findClickedComponent(e.getPoint());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // Check to see if the current point is equal to the clickedPoint
            // or not.  If it is, then this is part of a "clicked" operation
            // meaning that the selected panel should remain "selected", otherwise
            // it's part of drag operation and should be discarded
            if (!e.getPoint().equals(clickPoint)) {
                clickedPanel = null;
            }
            clickPoint = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // Drag the selected component to a new location...
            if (clickedPanel != null) {
                moveSelectedPanelTo(e.getPoint());
            }
        }

        protected void findClickedComponent(Point p) {
            Component comp = imageJpanel.getComponentAt(p);
            //if (comp instanceof JPanel && !comp.equals(demo.this))
            if (comp instanceof JLabel) {
                clickedPanel = (JLabel) comp;
                int x = p.x - clickedPanel.getLocation().x;
                int y = p.y - clickedPanel.getLocation().y;
                offset = new Point(x, y);
            }

        }

        private void moveSelectedPanelTo(Point p) {
            if (clickedPanel != null) {
                int x = p.x - offset.x;
                int y = p.y - offset.y;
                System.out.println(x + "x" + y);
                clickedPanel.setLocation(x, y);
            }
        }

    };

    private demo() {
        jScrollPanel.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPanel.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        openButton.addActionListener(new openButtonClick());
        saveButton.addActionListener(new saveButtonClick());
//       brightnessButton.addActionListener(new brightnessClick());
        //    contrastButton.addActionListener(new contrastClick());
        applyButton.addActionListener(new applyButtonClick());
        imageJpanel.addMouseListener(ma);
        imageJpanel.addMouseMotionListener(ma);
        zoomOutButton.addActionListener(new zoomInClick());
        zoomInButton.addActionListener(new zoomOutClick());
        resetButton.addActionListener(new resetButtonClick());
        resetButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contrast.setValue(1);
            }
        });
        resetButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brightnessN.setValue(0);
            }
        });
        resetButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                red.setValue(0);
            }
        });
        resetButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                green.setValue(0);
            }
        });
        resetButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blue.setValue(0);
            }
        });
        resetButton6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hue.setValue(0);
            }
        });
        resetButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saturation.setValue(0);
            }
        });
        resetButton8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brightness.setValue(0);
            }
        });
    }
}
