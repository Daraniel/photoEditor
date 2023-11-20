import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.byteSources.ByteSource;
import org.apache.sanselan.common.byteSources.ByteSourceFile;
import org.apache.sanselan.formats.jpeg.JpegImageParser;
import org.apache.sanselan.formats.jpeg.segments.UnknownSegment;
import org.w3c.dom.Element;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addComponentListener;
import static java.nio.file.StandardCopyOption.*;

public class pictureManager {

    static String username;
    private JPanel parent;
    private JTabbedPane tabbedPane1;
    private JComboBox ratio;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JButton newButton1;
    private JComboBox comboBox4;
    private JButton newButton2;
    private JTextField textField1;
    private JTextField textField2;
    private JButton lockButton;
    private JSpinner spinner1;
    private JCheckBox fillCheckBox;
    private JCheckBox fitCheckBox;
    private JCheckBox foamBoardCheckBox;
    private JCheckBox chassisCheckBox;
    private JCheckBox lamintaeCheckBox;
    private JButton setButton;
    private JButton saveOrderButton;
    private JLabel ad;
    private JLabel de;
    private JPanel scrollPane;
    private JPanel panel;
    private JTextArea commentTextArea;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JScrollPane container;
    private JLabel cl;
    private JLabel dpi;
    private JPanel orderPanel;
    private JPanel editPanel;
    private JSlider contrast;
    private JSlider brightnessN;
    private JSlider red;
    private JSlider green;
    private JSlider blue;
    private JSlider hue;
    private JSlider saturation;
    private JSlider brightness;
    private JButton resetButton2;
    private JButton resetButton1;
    private JButton resetButton3;
    private JButton resetButton4;
    private JButton resetButton5;
    private JButton resetButton6;
    private JButton resetButton7;
    private JButton resetButton8;
    int previd = -1;
    Resizable[] resizable;

    public static final int COLOR_TYPE_RGB = 1;
    public static final int COLOR_TYPE_CMYK = 2;
    public static final int COLOR_TYPE_YCCK = 3;
    private int colorType = COLOR_TYPE_RGB;
    private boolean hasAdobeMarker = false;

    private static JPanel[] jPanels;
    private static CountDownLatch latch;
    static int id;
    static List<String> MinMax;
    static boolean edit;
    int size;

    static String[] ratios;
    static JLabel[] ratioHolders;

    public static JPanel main(String[] args) {
        username = args[1];
        id = Integer.parseInt(args[2]);
        edit = Boolean.parseBoolean(args[3]);
        return new pictureManager().parent;
    }

    private void openFiles() {
        MinMax = MsAccessDatabaseConnection.query("SELECT * FROM Settings");
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
//        FileFilter imageFilter = new FileNameExtensionFilter(
//                "Image files", ImageIO.getReaderFileSuffixes());
//        fileChooser.addChoosableFileFilter(imageFilter);
//        fileChooser.setMultiSelectionEnabled(true);
//        fileChooser.setAcceptAllFileFilterUsed(false);

//        String OS = System.getProperty("os.name").toLowerCase();


        JFrame frame = new JFrame();
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        FileDialog fileDialog = new FileDialog(frame, "Choose a file");
        fileDialog.setMultipleMode(true);
        fileDialog.setFile("*.JPG;*.jpg;*.jpeg;*.png;*.svg");
        fileDialog.setDirectory("user.home");
        fileDialog.setVisible(true);

        //int result = fileChooser.showOpenDialog(parent);
        if (true) { //result == JFileChooser.APPROVE_OPTION
            try {
                //File[] selectedFiles = fileChooser.getSelectedFiles();
                File[] selectedFiles = fileDialog.getFiles();
                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                jPanels = new JPanel[selectedFiles.length];
                ImageLoadingTask task;
                ExecutorService service = Executors.newFixedThreadPool(9);
                latch = new CountDownLatch(selectedFiles.length);

                for (int i = 0; i < selectedFiles.length; i++) {
                    task = new ImageLoadingTask(i, selectedFiles[i], scrollPane, container, cl, ratio);
                    service.submit(task);
                }
//                latch.await();
//                scrollPane.revalidate();
                service.shutdown();

                for (int i = 0; i < selectedFiles.length; i++) {
                    File directory = new File(System.getProperty("user.dir") + File.separator + id + File.separator);
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            Files.createDirectory(directory.toPath());
                        }
                    }
                    Files.copy(Paths.get(selectedFiles[i].getPath()),
                            Paths.get(System.getProperty("user.dir") + File.separator + id + File.separator + selectedFiles[i].getName()),
                            COPY_ATTRIBUTES, REPLACE_EXISTING);
                }
                System.out.println("copied!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Invalid File!");
                ex.printStackTrace();
            }
        }
    }

    private void loadFiles() {
        MinMax = MsAccessDatabaseConnection.query("SELECT * FROM Settings");
        File folder = new File(System.getProperty("user.dir") + File.separator + id + File.separator);
        File[] selectedFiles = folder.listFiles();
        if (selectedFiles == null) {
            return;
        }
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        jPanels = new JPanel[selectedFiles.length];
        ImageLoadingTask task;
        ExecutorService service = Executors.newFixedThreadPool(9);
        latch = new CountDownLatch(selectedFiles.length);

        for (int i = 0; i < selectedFiles.length; i++) {
            task = new ImageLoadingTask(i, selectedFiles[i], scrollPane, container, cl, ratio);
            service.submit(task);
        }
//                latch.await();
//                scrollPane.revalidate();
        service.shutdown();
    }

    public class ImageLoadingTask implements Callable<JPanel> {

        private final File url;
        private final int i;
        private final JPanel scrollPane;
        private final JScrollPane container;
        private final JLabel cl;
        private final JComboBox ratio;

        ImageLoadingTask(int i, File url, JPanel scrollPane, JScrollPane container, JLabel cl, JComboBox ratio) {
            this.url = url;
            this.i = i;
            this.scrollPane = scrollPane;
            this.container = container;
            this.cl = cl;
            this.ratio = ratio;
        }

        @Override
        public JPanel call() {
            File file = Paths.get(url.toURI()).toFile();
            try {
//                if (!file.renameTo(file)) {
//                    demo.infoBox("Failed to open file" + file.getName() + " maybe file is open?", "Error");
//                    return new JPanel();
//                }

                ImageInfo ii = new ImageInfo();
                ImageInputStream iis = ImageIO.createImageInputStream(file);
                ii.setInput(iis); // in can be InputStream or RandomAccessFile
                ii.setDetermineImageNumber(true); // default is false
                ii.setCollectComments(true); // default is false
                String date = file.getName();
                final String DPI;

                if (date.length() > 20) {
                    date = date.substring(0, 18) + "...";
                }

                if (ii.check()) {
                    DPI = ii.getPhysicalWidthDpi() + " x " + ii.getPhysicalHeightDpi();
                } else {
                    DPI = "Unable to get file information!";
                }

//                ImageInfo ii = Sanselan.getImageInfo(file);

                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image;
                ICC_Profile profile = Sanselan.getICCProfile(file);

                if (profile == null) {
                    // image is rgb
                    image = toolkit.getImage(url.getPath());
                } else {
                    // image isn't rgb maybe cmyk or ycck or ...
                    image = readImage(url, ii.getWidth(), ii.getHeight());
                }

                Image resizedImage = setImage(image, ii.getWidth(), ii.getHeight(), container.getWidth() - 50, container.getHeight() - 50);

                final ImageIcon icon2 = new ImageIcon(resizedImage);
                resizedImage = setImage(resizedImage, ii.getWidth(), ii.getHeight(), 150, 150);
                ImageIcon icon = new ImageIcon(resizedImage);
                JLabel label = new JLabel();
                label.setIcon(icon);

                if (edit) {
                    List<String> rs = MsAccessDatabaseConnection.query("SELECT * FROM OrderDetails Where OrderNo =" + id + " and PhotoFileName='" + file.getName() + "'");
                    String str = rs.get(0) + "'" + rs.get(15) + "'" + rs.get(16) + "'" + rs.get(17) + "'" + rs.get(18) + "'" + rs.get(19) + "'" + rs.get(20);

                    BufferedImage img2 = new BufferedImage(resizedImage.getWidth(null), resizedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    Graphics bg = img2.getGraphics();
                    bg.drawImage(resizedImage, 0, 0, null);
                    bg.dispose();

                    img2 = rgb(img2, Integer.parseInt(rs.get(17).split(",")[0]), Integer.parseInt(rs.get(17).split(",")[1]),
                            Integer.parseInt(rs.get(17).split(",")[2]));
                    img2 = hsv(img2, Integer.parseInt(rs.get(19).split(",")[0]), Integer.parseInt(rs.get(19).split(",")[1]),
                            Integer.parseInt(rs.get(19).split(",")[2]));
                    img2 = cb(img2, Integer.parseInt(rs.get(20).split(",")[0]), Integer.parseInt(rs.get(20).split(",")[1]));

                    label.setIcon(new ImageIcon(img2));
                }

                resizedImage.flush();
                resizedImage = null;
                jPanels[i] = new JPanel();
                jPanels[i].setLayout(new BoxLayout(jPanels[i], BoxLayout.Y_AXIS));
                JLabel label1 = new JLabel();
                label1.setIcon(icon);
                label1.setVisible(false);
                jPanels[i].add(label);
                JLabel jLabel = new JLabel(DPI);
                jLabel.setVisible(false);
                JLabel jLabel3 = new JLabel(date);
                if (!edit) {
                    if (Integer.parseInt(MinMax.get(1)) > ii.getPhysicalHeightDpi() ||
                            Integer.parseInt(MinMax.get(2)) < ii.getPhysicalHeightDpi() ||
                            Integer.parseInt(MinMax.get(1)) > ii.getPhysicalWidthDpi() ||
                            Integer.parseInt(MinMax.get(2)) < ii.getPhysicalWidthDpi()) {
                        jLabel3.setBorder(BorderFactory.createLineBorder(Color.red));
                    }
                }

                jPanels[i].add(jLabel);
                JLabel jLabel1 = new JLabel("");
                jLabel1.validate();
                jPanels[i].add(jLabel1);
                JLabel jLabel2 = new JLabel(file.getName());
                jLabel2.setVisible(false);
                icon = null;
                //icon2 = null;
                iis.flush();
                iis.close();
                iis = null;
                jPanels[i].add(jLabel2);
                jPanels[i].add(jLabel3);
                JPanel jPanel2 = new JPanel();
                jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));

                if (!edit) {
                    List<String> rs = MsAccessDatabaseConnection.query("SELECT * FROM OrderDetails Where OrderNo =" + id + " and PhotoFileName='" + file.getName() + "'");
                    if (rs.size() > 0) {
                        for (int i = 0; i < rs.size(); i += 21) {
                            String string = "";
                            for (int j = i; j < i + 21; j++) {
                                string += rs.get(j) + ",";
                            }
                            JPanel jPanel = jPanel2;
                            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
                            JLabel jLabel5 = new JLabel();

                            List<String> rs2 = MsAccessDatabaseConnection.query("SELECT * FROM PhotoSizes Where PhotoSizeID =" + rs.get(i + 4));
                            List<String> rs3 = MsAccessDatabaseConnection.query("SELECT PaperTypeTitle FROM PaperTypes Where PaperTypeID =" + rs2.get(4));

                            jLabel5.setText(rs3.get(0) + " " + rs2.get(3) + " x " + rs.get(i + 5));
                            jPanel.add(jLabel5);
                            JLabel jLabel6 = new JLabel();
                            jLabel6.setText(string);
                            jLabel6.setVisible(false);
                            jPanel.add(jLabel6);
                            jPanel.revalidate();

                            jLabel5.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    super.mouseClicked(e);
                                    String string[] = ((JLabel) jPanel.getComponent(jPanel.getComponentZOrder(jLabel5) + 1)).getText().split(",");
                                    for (int i = 0; i < jPanel.getComponentCount(); i += 2) {
                                        if (((JLabel) jPanel.getComponent(i)).getBorder() != null) {
                                            ((JLabel) jPanel.getComponent(i)).setBorder(null);
                                        }
                                    }
                                    jLabel5.setBorder(BorderFactory.createLineBorder(Color.blue));
                                    for (int i = 1; i < comboBox3.getItemCount(); i++) {
                                        if (comboBox3.getItemAt(i) == rs3.get(0)) {
                                            comboBox3.setSelectedIndex(i);
                                            break;
                                        }
                                    }
                                    comboBox4.setEnabled(true);
                                    lockButton.setEnabled(true);
                                    List<String> result4 = MsAccessDatabaseConnection.query("SELECT PhotoSizeTitle FROM PhotoSizes Where PaperTypeID = " +
                                            comboBox3.getSelectedIndex());
                                    DefaultComboBoxModel model4 = new DefaultComboBoxModel(result4.toArray(new String[result4.size()]));
                                    comboBox4.setModel(model4);
                                    comboBox4.setSelectedIndex(0);
                                    for (int i = 1; i < comboBox4.getItemCount(); i++) {
                                        if (comboBox4.getItemAt(i) == rs2.get(3)) {
                                            comboBox4.setSelectedIndex(i);
                                            break;
                                        }
                                    }
                                    spinner1.setValue(Integer.parseInt(string[5]));
                                    foamBoardCheckBox.setSelected(Boolean.parseBoolean(string[6]));
                                    chassisCheckBox.setSelected(Boolean.parseBoolean(string[7]));
                                    lamintaeCheckBox.setSelected(Boolean.parseBoolean(string[8]));
                                    fitCheckBox.setSelected(Boolean.parseBoolean(string[9]));
                                    fillCheckBox.setSelected(Boolean.parseBoolean(string[10]));

                                    Icon icon3 = ((JLabel) jPanel.getParent().getComponent(6)).getIcon();
                                    int w = icon3.getIconWidth();
                                    int h = icon3.getIconHeight();
                                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                                    GraphicsDevice gd = ge.getDefaultScreenDevice();
                                    GraphicsConfiguration gc = gd.getDefaultConfiguration();
                                    BufferedImage image = gc.createCompatibleImage(w, h);
                                    int size = (int) Math.abs(Math.sqrt(w * w + h * h));
                                    Graphics2D g = image.createGraphics();
                                    icon3.paintIcon(null, g, 0, 0);
                                    // this line magnifies the icon
                                    image = image.getSubimage(Integer.parseInt(string[11]) * size / 1000, Integer.parseInt(string[12]) * size / 1000,
                                            Integer.parseInt(string[13]) * size / 1000, Integer.parseInt(string[14]) * size / 1000);
                                    // this lines draw border on the icon
//                                g.setColor(Color.BLUE);
//                                g.drawRect(Integer.parseInt(string[11]) * size / 1000, Integer.parseInt(string[12]) * size / 1000,
//                                        Integer.parseInt(string[13]) * size / 1000, Integer.parseInt(string[14]) * size / 1000);
                                    g.dispose();
                                    ((JLabel) jPanel.getParent().getComponent(0)).setIcon(new ImageIcon(image));
                                }
                            });
                        }
                    }

                    jLabel3.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            label.setIcon(label1.getIcon());
                            for (int i = 0; i < jPanel2.getComponentCount(); i += 2) {
                                if (((JLabel) jPanel2.getComponent(i)).getBorder() != null &&
                                        (((LineBorder) ((JLabel) jPanel2.getComponent(i)).getBorder()).getLineColor().equals(Color.blue))) {
                                    ((JLabel) jPanel2.getComponent(i)).setBorder(null);
                                }
                            }
                        }
                    });
                }

                jPanels[i].add(jPanel2);
                jPanels[i].add(label1);
                if (edit) {
                    List<String> rs = MsAccessDatabaseConnection.query("SELECT * FROM OrderDetails Where OrderNo =" + id + " and PhotoFileName='" + file.getName() + "'");
                    String str = rs.get(0) + "'" + rs.get(15) + "'" + rs.get(16) + "'" + rs.get(17) + "'" + rs.get(18) + "'" + rs.get(19) + "'" + rs.get(20);
                    JLabel label4 = new JLabel(str);
                    label4.setVisible(false);
                    jPanels[i].add(label4);
                }
                jPanels[i].setBorder(BorderFactory.createLineBorder(Color.white));
                jPanels[i].addMouseListener(new MouseAdapter() {
                    boolean checked = false;
                    int dClkRes = 300;    // double-click speed in ms
                    long timeMouseDown = 0; // last mouse down time

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (e.getClickCount() == 2 && !edit) {
                            Component component = jPanels[i].getComponent(5);
                            if (component instanceof JLabel) {
                                String[] strings = new String[1];
                                strings[0] = "";
                                ratios = new String[1];
                                ratios[0] = "";
                                DefaultComboBoxModel model = new DefaultComboBoxModel(strings);
                                ratio.setModel(model);
                            } else {
                                JPanel jPanel = (JPanel) component;
                                String[] strings = new String[jPanel.getComponentCount() / 2];
                                ratios = new String[jPanel.getComponentCount() / 2];
                                ratioHolders = new JLabel[jPanel.getComponentCount() / 2];
                                for (int i = 0; i < jPanel.getComponentCount(); i += 2) {
                                    boolean bool = true;
                                    strings[i / 2] = ((JLabel) jPanel.getComponent(i)).getText();
                                    while (bool) {
                                        bool = false;
                                        for (int j = 0; j < i / 2; j++) {
                                            if (strings[j].contains(strings[i / 2]) && j != i / 2) {
                                                bool = true;
                                                strings[i / 2] = strings[i / 2] + " ";
                                                break;
                                            }
                                        }
                                    }
                                    ratios[i / 2] = ((JLabel) jPanel.getComponent(i + 1)).getText();
                                    ratioHolders[i / 2] = (JLabel) jPanel.getComponent(i + 1);
                                }
                                DefaultComboBoxModel model = new DefaultComboBoxModel(strings);
                                ratio.setModel(model);
                            }

                            System.out.println("double click!");
                            container.getViewport().remove(scrollPane);
                            container.getViewport().revalidate();
                            container.getViewport().repaint();
                            cl.setEnabled(true);
                            cl.setVisible(true);
                            dpi.setVisible(false);
                            ratio.setEnabled(true);
                            ratio.setVisible(true);
                            loadImage(container, icon2);
                        }
                        JPanel jPanel = (JPanel) e.getComponent();
                        if (!checked) {
                            checked = true;
                            jPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
                            if (getChecked().length == 1) {
                                dpi.setText(DPI);
                            } else {
                                dpi.setText("-");
                            }
                        } else {
                            checked = false;
                            jPanel.setBorder(BorderFactory.createLineBorder(Color.white));
                            if (getChecked().length == 0) {
                                dpi.setText("");
                            } else if (getChecked().length == 1) {
                                dpi.setText(((JLabel) getChecked()[0].getComponent(1)).getText());
                            }
                        }
                    }
                });
                ii = null;
                image.flush();
                image = null;
                JPanel jPanel = new JPanel();
                jPanel.add(jPanels[i]);
                jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
                jPanel.setBackground(Color.gray);

                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
                gridBagConstraints.weightx = 1.0;

                scrollPane.add(jPanel, gridBagConstraints);
                scrollPane.revalidate();
                latch.countDown();
                return jPanels[i];
            } catch (Exception ex) {
                ex.printStackTrace();
                latch.countDown();
                demo.infoBox("Failed to open file maybe file is open?", "Error");
                return new JPanel();
            }
        }
    }

    public BufferedImage readImage(File file, int width, int height) throws IOException, ImageReadException, InterruptedException {
        colorType = COLOR_TYPE_RGB;
        hasAdobeMarker = false;
        ImageInputStream stream = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
        while (iter.hasNext()) {
            ImageReader reader = iter.next();
            reader.setInput(stream);

            BufferedImage image;
            ICC_Profile profile = null;
            try {
                image = reader.read(0);
            } catch (Exception e) {
                colorType = COLOR_TYPE_CMYK;
                checkAdobeMarker(file);
                profile = Sanselan.getICCProfile(file);
                WritableRaster raster = (WritableRaster) reader.readRaster(0, null);
                if (colorType == COLOR_TYPE_YCCK)
                    convertYcckToCmyk(raster);
                if (hasAdobeMarker)
                    convertInvertedColors(raster);
                image = convertCmykToRgb(raster, profile);
            }
            stream.close();
            return image;
        }
        stream.close();
        return null;
    }

    public void checkAdobeMarker(File file) throws IOException, ImageReadException {
        JpegImageParser parser = new JpegImageParser();
        ByteSource byteSource = new ByteSourceFile(file);
        @SuppressWarnings("rawtypes")
        ArrayList segments = parser.readSegments(byteSource, new int[]{0xffee}, true);
        if (segments != null && segments.size() >= 1) {
            UnknownSegment app14Segment = (UnknownSegment) segments.get(0);
            byte[] data = app14Segment.bytes;
            if (data.length >= 12 && data[0] == 'A' && data[1] == 'd' && data[2] == 'o' && data[3] == 'b' && data[4] == 'e') {
                hasAdobeMarker = true;
                int transform = app14Segment.bytes[11] & 0xff;
                if (transform == 2)
                    colorType = COLOR_TYPE_YCCK;
            }
        }
    }

    public static void convertYcckToCmyk(WritableRaster raster) {
        int height = raster.getHeight();
        int width = raster.getWidth();
        int stride = width * 4;
        int[] pixelRow = new int[stride];
        for (int h = 0; h < height; h++) {
            raster.getPixels(0, h, width, 1, pixelRow);

            for (int x = 0; x < stride; x += 4) {
                int y = pixelRow[x];
                int cb = pixelRow[x + 1];
                int cr = pixelRow[x + 2];

                int c = (int) (y + 1.402 * cr - 178.956);
                int m = (int) (y - 0.34414 * cb - 0.71414 * cr + 135.95984);
                y = (int) (y + 1.772 * cb - 226.316);

                if (c < 0) c = 0;
                else if (c > 255) c = 255;
                if (m < 0) m = 0;
                else if (m > 255) m = 255;
                if (y < 0) y = 0;
                else if (y > 255) y = 255;

                pixelRow[x] = 255 - c;
                pixelRow[x + 1] = 255 - m;
                pixelRow[x + 2] = 255 - y;
            }

            raster.setPixels(0, h, width, 1, pixelRow);
        }
    }

    public static void convertInvertedColors(WritableRaster raster) {
        int height = raster.getHeight();
        int width = raster.getWidth();
        int stride = width * 4;
        int[] pixelRow = new int[stride];
        for (int h = 0; h < height; h++) {
            raster.getPixels(0, h, width, 1, pixelRow);
            for (int x = 0; x < stride; x++)
                pixelRow[x] = 255 - pixelRow[x];
            raster.setPixels(0, h, width, 1, pixelRow);
        }
    }

    public static BufferedImage convertCmykToRgb(Raster cmykRaster, ICC_Profile cmykProfile) throws IOException {
        if (cmykProfile == null)
            cmykProfile = ICC_Profile.getInstance(System.getProperty("user.dir") + File.separator + "/ISOcoated_v2_300_eci.icc");

        if (cmykProfile.getProfileClass() != ICC_Profile.CLASS_DISPLAY) {
            byte[] profileData = cmykProfile.getData();

            if (profileData[ICC_Profile.icHdrRenderingIntent] == ICC_Profile.icPerceptual) {
                intToBigEndian(ICC_Profile.icSigDisplayClass, profileData, ICC_Profile.icHdrDeviceClass); // Header is first

                cmykProfile = ICC_Profile.getInstance(profileData);
            }
        }

        ICC_ColorSpace cmykCS = new ICC_ColorSpace(cmykProfile);
        BufferedImage rgbImage = new BufferedImage(cmykRaster.getWidth(), cmykRaster.getHeight(), BufferedImage.TYPE_INT_RGB);
        WritableRaster rgbRaster = rgbImage.getRaster();
        ColorSpace rgbCS = rgbImage.getColorModel().getColorSpace();
        ColorConvertOp cmykToRgb = new ColorConvertOp(cmykCS, rgbCS, null);
        cmykToRgb.filter(cmykRaster, rgbRaster);
        return rgbImage;
    }

    static void intToBigEndian(int value, byte[] array, int index) {
        array[index] = (byte) (value >> 24);
        array[index + 1] = (byte) (value >> 16);
        array[index + 2] = (byte) (value >> 8);
        array[index + 3] = (byte) (value);
    }

    private BufferedImage rgb(BufferedImage image, int r, int g, int b) {
        for (int Y = 0; Y < image.getHeight(); Y++) {
            for (int X = 0; X < image.getWidth(); X++) {
                int RGB = image.getRGB(X, Y);
                int R = (RGB >> 16) & 0xff;
                int G = (RGB >> 8) & 0xff;
                int B = (RGB) & 0xff;

                image.setRGB(X, Y, new Color(demo.ensureRange(R + r, 0, 255),
                        demo.ensureRange(G + g, 0, 255),
                        demo.ensureRange(B + b, 0, 255)).getRGB());
            }
        }
        return image;
    }

    private BufferedImage hsv(BufferedImage image, int h, int s, int v) {
        for (int Y = 0; Y < image.getHeight(); Y++) {
            for (int X = 0; X < image.getWidth(); X++) {
                int RGB = image.getRGB(X, Y);
                int R = (RGB >> 16) & 0xff;
                int G = (RGB >> 8) & 0xff;
                int B = (RGB) & 0xff;

                float HSV[] = new float[3];
                Color.RGBtoHSB(R, G, B, HSV);
                image.setRGB(X, Y, Color.getHSBColor(
                        demo.ensureRange(HSV[0] + (float) h / 100, 0, 1),
                        demo.ensureRange(HSV[1] + (float) s / 100, 0, 1),
                        demo.ensureRange(HSV[2] + (float) v / 100, 0, 1)).getRGB());
            }
        }
        return image;
    }

    private BufferedImage cb(BufferedImage image, int c, int b) {
        RescaleOp op = new RescaleOp(c + 1.0f, b, null);
        return op.filter(image, image);
    }

    private void loadImage(JScrollPane container, Icon icon) {
        JPanel panel = new JPanel(null);
        JLabel label = new JLabel();
        label.setIcon(icon);
        label.setBounds(4, 4, icon.getIconWidth(), icon.getIconHeight());
        panel.add(label, 1, 0);
        resizable = new Resizable[1];

        Icon finalIcon = icon;
        ratio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0000'");
                Date date = new Date(System.currentTimeMillis());

                List<String> result3 = MsAccessDatabaseConnection.query("INSERT INTO OrderStatus VALUES('" + id + "','" +
                        formatter.format(date) + "', '" + username + "', '" + 1 + "')", true);

                size = (int) Math.abs(Math.sqrt(finalIcon.getIconWidth() * finalIcon.getIconWidth() + finalIcon.getIconHeight() * finalIcon.getIconHeight()));
                List<String> rs = MsAccessDatabaseConnection.query("UPDATE OrderDetails SET StartPositionX= " + resizable[0].getX() * 1000 / size
                        + ", StartPositionY= " + resizable[0].getY() * 1000 / size + ", SizeWidth= " + resizable[0].getWidth() * 1000 / size +
                        ", SizeHeight= " + resizable[0].getHeight() * 1000 / size + " Where id= " + ratios[previd].split(",")[0], true);
                String[] rat = ratios[previd].split(",");
                rat[11] = String.valueOf(resizable[0].getX() * 1000 / size);
                rat[12] = String.valueOf(resizable[0].getY() * 1000 / size);
                rat[13] = String.valueOf(resizable[0].getWidth() * 1000 / size);
                rat[14] = String.valueOf(resizable[0].getHeight() * 1000 / size);
                StringBuilder str = new StringBuilder(rat[0]);
                for (int i = 1; i < rat.length; i++) {
                    str.append(",").append(rat[i]);
                }
                ratios[previd] = str.toString();
                ratioHolders[previd].setText(str.toString());

                int i = ratio.getSelectedIndex();
                previd = i;
                int x = size * Integer.parseInt(ratios[i].split(",")[11]) / 1000;
                int y = size * Integer.parseInt(ratios[i].split(",")[12]) / 1000;
                int w = size * Integer.parseInt(ratios[i].split(",")[13]) / 1000;
                int h = size * Integer.parseInt(ratios[i].split(",")[14]) / 1000;
                resizable[0].xRatio = Integer.parseInt(ratios[i].split(",")[13]);
                resizable[0].yRatio = Integer.parseInt(ratios[i].split(",")[14]);
                resizable[0].setBounds(x, y, w, h);
                resizable[0].resize();
            }
        });

        try {
            if (ratios != null && ratios[0] != "") {
                previd = 0;
                size = (int) Math.abs(Math.sqrt(icon.getIconWidth() * icon.getIconWidth() + icon.getIconHeight() * icon.getIconHeight()));
                int x = size * Integer.parseInt(ratios[0].split(",")[11]) / 1000;
                int y = size * Integer.parseInt(ratios[0].split(",")[12]) / 1000;
                int w = size * Integer.parseInt(ratios[0].split(",")[13]) / 1000;
                int h = size * Integer.parseInt(ratios[0].split(",")[14]) / 1000;
                JPanel border = new JPanel();
                border.setBackground(new Color(0, 0, 0, 0));
                resizable[0] = new Resizable(border, Integer.parseInt(ratios[0].split(",")[13]), Integer.parseInt(ratios[0].split(",")[14]), icon.getIconWidth() + 9, icon.getIconHeight() + 9);
                resizable[0].setBounds(x, y, w, h);
                icon = null;
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent me) {
                        panel.requestFocus();
                        resizable[0].repaint();
                    }
                });
                panel.add(resizable[0], 2, 0);
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {

        } catch (Exception ex) {

        }

        container.getViewport().add(panel);
        container.getViewport().revalidate();
        container.getViewport().repaint();
        panel.revalidate();
        panel.repaint();
    }

    static Boolean writeImage(BufferedImage sourceImage, File destinationFile, int xdpi, int ydpi) {
        ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
        ImageOutputStream ios;
        try {
            ios = ImageIO.createImageOutputStream(destinationFile);
            imageWriter.setOutput(ios);
            ImageWriteParam jpegParams = imageWriter.getDefaultWriteParam();
            IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(sourceImage), jpegParams);
            Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");

            Element jfif;
            if (xdpi != -1 && ydpi != -1) {
                jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("Xdensity", Integer.toString(xdpi));
                jfif.setAttribute("Ydensity", Integer.toString(ydpi));
                jfif.setAttribute("resUnits", "1"); // density is dots per inch
                data.mergeTree("javax_imageio_jpeg_image_1.0", tree);
            }

// Write and clean up
            imageWriter.write(data, new IIOImage(sourceImage, null, data), jpegParams);
            ios.flush();
            ios.close();
            ios = null;
            imageWriter.dispose();
            imageWriter = null;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static Image setImage(Image source, int Width, int Height, int destWidth, int destHeight) {
        if (source.getHeight(null) / destHeight > source.getWidth(null) / destWidth) {
            return source.getScaledInstance(Width * destHeight / Height, destHeight, Image.SCALE_SMOOTH);
        } else if (source.getHeight(null) / destHeight < source.getWidth(null) / destWidth) {
            return source.getScaledInstance(destWidth, Height * destWidth / Width, Image.SCALE_SMOOTH);
        } else {
            return source.getScaledInstance(Width * destHeight / Height, destHeight, Image.SCALE_SMOOTH);
        }
    }

    private pictureManager() {
        label1.setText(String.valueOf(id));
        List<String> rs = MsAccessDatabaseConnection.query("SELECT * FROM Orders Where OrderNo=" + id);
        if (rs.get(3).equals("0")) {
            label3.setText("Low");
        } else if (rs.get(3).equals("1")) {
            label3.setText("Normal");
        } else {
            label3.setText("High");
        }
        label2.setText(MsAccessDatabaseConnection.query("SELECT CustomerName FROM Customers Where CustomerID=" + rs.get(2)).get(0));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scrollPane.setBackground(Color.GRAY);
        //scrollPane.setLayout(new GridLayout(-1, 7, 5, 5));
        scrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                GridBagLayout layout = (GridBagLayout) scrollPane.getLayout();
                JPanel panel = (JPanel)evt.getComponent();
                int width = panel.getWidth();
                int height = panel.getHeight();
                int wGap = panel.getInsets().left+panel.getInsets().right;
                int hGap = panel.getInsets().top+panel.getInsets().bottom;
                layout.columnWidths = new int[]{width/2-wGap, width/2-wGap};
                layout.rowHeights = new int[]{height-hGap};
            }
        });


        ratio.setVisible(false);
        ratio.setEnabled(false);
        cl.setForeground(Color.blue);
        cl.setVisible(false);
        cl.setEnabled(false);

        if (edit) {
            ad.setVisible(false);
            ad.setEnabled(false);
            de.setVisible(false);
            de.setEnabled(false);
            dpi.setVisible(false);
            dpi.setEnabled(false);
            tabbedPane1.remove(orderPanel);
            tabbedPane1.revalidate();
            parent.revalidate();

            contrast.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[6] = contrast.getValue() + "," + Integer.parseInt(str[6].split(",")[1]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    contrast.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[6] = 0 + "," + Integer.parseInt(str[6].split(",")[1]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            brightnessN.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[6] = Integer.parseInt(str[6].split(",")[0]) + "," + brightnessN.getValue();

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    brightnessN.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[6] = Integer.parseInt(str[6].split(",")[0]) + "," + 0;

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            red.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[3] = red.getValue() + "," + Integer.parseInt(str[3].split(",")[1]) +
                                    "," + Integer.parseInt(str[3].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    red.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[3] = 0 + "," + Integer.parseInt(str[3].split(",")[1]) +
                                    "," + Integer.parseInt(str[3].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            green.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[3] = Integer.parseInt(str[3].split(",")[0]) + "," + green.getValue() + "," +
                                    Integer.parseInt(str[3].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    green.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[3] = Integer.parseInt(str[3].split(",")[0]) + "," + 0 +
                                    "," + Integer.parseInt(str[3].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            blue.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[3] = Integer.parseInt(str[3].split(",")[0]) + "," + Integer.parseInt(str[3].split(",")[1]) +
                                    "," + blue.getValue();

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton5.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    blue.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[3] = Integer.parseInt(str[3].split(",")[0]) + "," + Integer.parseInt(str[3].split(",")[1]) +
                                    "," + 0;

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            hue.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[5] = hue.getValue() + "," + Integer.parseInt(str[5].split(",")[1]) + "," +
                                    Integer.parseInt(str[5].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton6.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hue.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[5] = 0 + "," + Integer.parseInt(str[5].split(",")[1]) + "," +
                                    Integer.parseInt(str[5].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            saturation.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[5] = Integer.parseInt(str[5].split(",")[0]) + "," + saturation.getValue() +
                                    "," + Integer.parseInt(str[5].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton7.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saturation.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[5] = Integer.parseInt(str[5].split(",")[0]) + "," + 0 + "," +
                                    Integer.parseInt(str[5].split(",")[2]);

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            brightness.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[5] = Integer.parseInt(str[5].split(",")[0]) + "," + Integer.parseInt(str[5].split(",")[1]) +
                                    "," + brightness.getValue();

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            resetButton8.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    brightness.setValue(0);
                    JPanel[] selected = getChecked();
                    if (selected != null && selected.length > 0) {
                        for (int i = 0; i < selected.length; i++) {
//                            Icon img = ((JLabel) selected[i].getComponent(6)).getIcon();
                            Icon icon3 = ((JLabel) selected[i].getComponent(6)).getIcon();
                            int w = icon3.getIconWidth();
                            int h = icon3.getIconHeight();
                            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                            GraphicsDevice gd = ge.getDefaultScreenDevice();
                            GraphicsConfiguration gc = gd.getDefaultConfiguration();
                            BufferedImage img2 = gc.createCompatibleImage(w, h);
                            Graphics2D g = img2.createGraphics();
                            icon3.paintIcon(null, g, 0, 0);
                            g.dispose();

                            String[] str = ((JLabel) selected[i].getComponent(7)).getText().split("'");

                            str[5] = Integer.parseInt(str[5].split(",")[0]) + "," + Integer.parseInt(str[5].split(",")[1]) + "," + 0;

                            img2 = rgb(img2, Integer.parseInt(str[3].split(",")[0]), Integer.parseInt(str[3].split(",")[1]),
                                    Integer.parseInt(str[3].split(",")[2]));
                            img2 = hsv(img2, Integer.parseInt(str[5].split(",")[0]), Integer.parseInt(str[5].split(",")[1]),
                                    Integer.parseInt(str[5].split(",")[2]));
                            img2 = cb(img2, Integer.parseInt(str[6].split(",")[0]), Integer.parseInt(str[6].split(",")[1]));

                            ((JLabel) selected[i].getComponent(0)).setIcon(new ImageIcon(img2));
                            String string = str[0];
                            for (int j = 1; j < str.length; j++) {
                                string += "'" + str[j];
                            }
                            ((JLabel) selected[i].getComponent(7)).setText(string);

                            MsAccessDatabaseConnection.query("UPDATE OrderDetails SET rgb= '" + str[3]
                                    + "', hsb= '" + str[5] + "', cb= '" + str[6] + "' Where OrderNo= " + id + " and PhotoFileName='"
                                    + ((JLabel) selected[i].getComponent(3)).getText() + "'", true);
                        }
                    }
                }
            });

            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    loadFiles();
                }
            }, 100, TimeUnit.MILLISECONDS);

            return;
        } else {
            tabbedPane1.remove(editPanel);
            tabbedPane1.revalidate();
        }
        ad.setForeground(Color.blue);
        ad.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                openFiles();
            }
        });

        de.setForeground(Color.blue);
        de.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JPanel[] jLabels = getChecked();
                for (int i = 0; i < jLabels.length; i++) {
                    File file = new File(System.getProperty("user.dir") + File.separator + id + File.separator + ((JLabel) jLabels[i].getComponent(3)).getText());
                    if (file.delete()) {
                        List<String> rs = MsAccessDatabaseConnection.query("DELETE FROM OrderDetails Where OrderNo =" + id + " and PhotoFileName='" + file.getName() + "'", true);
                        scrollPane.remove(jLabels[i].getParent());
                        scrollPane.revalidate();
                        scrollPane.repaint();
                    } else {
                        demo.infoBox("Failed to delete file " + file.getName() + "maybe it's open?", "Error");
                    }
                }
            }
        });

        fillCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fitCheckBox.setSelected(false);
            }
        });

        fitCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillCheckBox.setSelected(false);
            }
        });

        foamBoardCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chassisCheckBox.setSelected(false);
            }
        });

        chassisCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                foamBoardCheckBox.setSelected(false);
            }
        });

        cl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (resizable[0] != null) {
                    List<String> rs = MsAccessDatabaseConnection.query("UPDATE OrderDetails SET StartPositionX= " + resizable[0].getX() * 1000 / size
                            + ", StartPositionY= " + resizable[0].getY() * 1000 / size + ", SizeWidth= " + resizable[0].getWidth() * 1000 / size +
                            ", SizeHeight= " + resizable[0].getHeight() * 1000 / size + " Where id= " + ratios[previd].split(",")[0], true);
                    String[] rat = ratios[previd].split(",");
                    rat[11] = String.valueOf(resizable[0].getX() * 1000 / size);
                    rat[12] = String.valueOf(resizable[0].getY() * 1000 / size);
                    rat[13] = String.valueOf(resizable[0].getWidth() * 1000 / size);
                    rat[14] = String.valueOf(resizable[0].getHeight() * 1000 / size);
                    StringBuilder str = new StringBuilder(rat[0]);
                    for (int i = 1; i < rat.length; i++) {
                        str.append(",").append(rat[i]);
                    }
                    ratios[previd] = str.toString();
                    ratioHolders[previd].setText(str.toString());

                    previd = -1;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0000'");
                Date date = new Date(System.currentTimeMillis());

                List<String> result3 = MsAccessDatabaseConnection.query("INSERT INTO OrderStatus VALUES('" + id + "','" +
                        formatter.format(date) + "', '" + username + "', '" + 1 + "')", true);

                container.getViewport().remove(0);
                container.getViewport().add(scrollPane);
                cl.setVisible(false);
                cl.setEnabled(false);
                dpi.setVisible(true);
                ratio.setVisible(false);
                ratio.setEnabled(false);
                container.getViewport().revalidate();
                container.getViewport().repaint();
            }
        });

        List<String> result3 = MsAccessDatabaseConnection.query("SELECT PaperTypeTitle FROM PaperTypes");
        DefaultComboBoxModel model3 = new DefaultComboBoxModel(result3.toArray(new String[result3.size()]));
        comboBox3.setModel(model3);

        comboBox3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBox3.getSelectedIndex() > 0) {
                    comboBox4.setEnabled(true);
                    lockButton.setEnabled(true);
                    List<String> result4 = MsAccessDatabaseConnection.query("SELECT PhotoSizeTitle FROM PhotoSizes Where PaperTypeID = " +
                            comboBox3.getSelectedIndex());
                    DefaultComboBoxModel model4 = new DefaultComboBoxModel(result4.toArray(new String[result4.size()]));
                    comboBox4.setModel(model4);
                    comboBox4.setSelectedIndex(0);
                } else {
                    comboBox4.setEnabled(false);
                    lockButton.setEnabled(false);
                }
            }
        });

        comboBox4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM PhotoSizes Where PhotoSizeTitle = '" +
                        comboBox4.getSelectedItem() + "' and PaperTypeID = " + comboBox3.getSelectedIndex());
                textField1.setText(result.get(1));
                textField2.setText(result.get(2));
            }
        });

        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel[] selected = getChecked();
                if (selected.length > 0 && comboBox3.getSelectedIndex() > 0) {
                    for (int i = 0; i < selected.length; i++) {
                        JPanel jPanel = (JPanel) selected[i].getComponent(5);
                        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
                        JLabel jLabel = new JLabel();
                        jLabel.setText(comboBox3.getSelectedItem() + " " + comboBox4.getSelectedItem() + " x " + spinner1.getValue());
                        jPanel.add(jLabel);
                        JLabel jLabel1 = new JLabel();
                        int number;
                        try {
                            number = Integer.parseInt(MsAccessDatabaseConnection.query("SELECT MAX(id) FROM OrderDetails").get(0)) + 1;
                        } catch (java.lang.NumberFormatException ex) {
                            number = 1;
                        }
                        List<String> rs = MsAccessDatabaseConnection.query("SELECT * FROM PhotoSizes Where PhotoSizeTitle ='" + comboBox4.getSelectedItem() + "'");
                        Icon icon = ((JLabel) selected[i].getComponent(0)).getIcon();
                        double w1 = Integer.parseInt(textField1.getText());
                        double h1 = Integer.parseInt(textField2.getText());
                        if (icon.getIconWidth() / w1 < icon.getIconHeight() / h1) {
                            h1 = icon.getIconWidth() * h1 / w1;
                            w1 = icon.getIconWidth();
                        } else {
                            w1 = icon.getIconHeight() * w1 / h1;
                            h1 = icon.getIconHeight();
                        }
                        Double size = Math.sqrt(icon.getIconWidth() * icon.getIconWidth() + icon.getIconHeight() * icon.getIconHeight());
                        int w = (int) Math.abs(w1 * 1000.0 / size);
                        int h = (int) Math.abs(h1 * 1000.0 / size);
                        String str = number + "," + id + "," + ((JLabel) selected[i].getComponent(3)).getText() + "," + "'JPEG'," +
                                rs.get(0) + "," + spinner1.getValue() + "," + foamBoardCheckBox.isSelected() + "," +
                                chassisCheckBox.isSelected() + "," + lamintaeCheckBox.isSelected() + "," +
                                fitCheckBox.isSelected() + "," + fillCheckBox.isSelected() + "," + 0 + "," + 0 + "," + w +
                                "," + h + "," + 0 + "," + false + "," + "0,0,0" + "0,0,0,0" + "0,0,0" + "0,0";
                        String str2 = number + ", " + id + ", '" + ((JLabel) selected[i].getComponent(3)).getText() + "', 'JPEG', " +
                                rs.get(0) + ", " + spinner1.getValue() + ", '" + foamBoardCheckBox.isSelected() + "', '" +
                                chassisCheckBox.isSelected() + "', '" + lamintaeCheckBox.isSelected() + "', '" +
                                fitCheckBox.isSelected() + "', '" + fillCheckBox.isSelected() + "', " + 0 + ", " + 0 + ", " + w +
                                ", " + h + ", " + 0 + ", '" + false + "', " + "'0,0,0' , " + "'0,0,0,0' , " + "'0,0,0' , " + "'0,0'";
                        MsAccessDatabaseConnection.query("INSERT INTO OrderDetails VALUES(" + str2 + ")", true);

                        jLabel1.setText(str);
                        jLabel1.setVisible(false);
                        jPanel.add(jLabel1);
//                        if (label.getText().equals("")) {
//                            label.setText("<html>" + comboBox3.getSelectedItem() + " " +
//                                    comboBox4.getSelectedItem() + " x " + spinner1.getValue() + "</html>");
//                        } else {
//                            label.setText("<html>" + label.getText().split("<html>")[1].split("</html>")[0] +
//                                    "<br>" + comboBox3.getSelectedItem() + " " + comboBox4.getSelectedItem() + " x " +
//                                    spinner1.getValue() + "</html>");
//                        }
                        jPanel.validate();

                        jLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                super.mouseClicked(e);
                                String string[] = ((JLabel) jPanel.getComponent(jPanel.getComponentZOrder(jLabel) + 1)).getText().split(",");
                                List<String> rs2 = MsAccessDatabaseConnection.query("SELECT * FROM PhotoSizes Where PhotoSizeID =" + string[4]);
                                List<String> rs3 = MsAccessDatabaseConnection.query("SELECT PaperTypeTitle FROM PaperTypes Where PaperTypeID =" + rs2.get(4));
                                for (int i = 0; i < jPanel.getComponentCount(); i += 2) {
                                    if (((JLabel) jPanel.getComponent(i)).getBorder() != null &&
                                            (((LineBorder) ((JLabel) jPanel.getComponent(i)).getBorder()).getLineColor().equals(Color.blue))) {
                                        ((JLabel) jPanel.getComponent(i)).setBorder(null);
                                    }
                                }
                                jLabel.setBorder(BorderFactory.createLineBorder(Color.blue));
                                for (int i = 1; i < comboBox3.getItemCount(); i++) {
                                    if (comboBox3.getItemAt(i) == rs3.get(0)) {
                                        comboBox3.setSelectedIndex(i);
                                        break;
                                    }
                                }
                                comboBox4.setEnabled(true);
                                lockButton.setEnabled(true);
                                List<String> result4 = MsAccessDatabaseConnection.query("SELECT PhotoSizeTitle FROM PhotoSizes Where PaperTypeID = " +
                                        comboBox3.getSelectedIndex());
                                DefaultComboBoxModel model4 = new DefaultComboBoxModel(result4.toArray(new String[result4.size()]));
                                comboBox4.setModel(model4);
                                comboBox4.setSelectedIndex(0);
                                for (int i = 1; i < comboBox4.getItemCount(); i++) {
                                    if (comboBox4.getItemAt(i) == rs2.get(3)) {
                                        comboBox4.setSelectedIndex(i);
                                        break;
                                    }
                                }
                                spinner1.setValue(Integer.parseInt(string[5]));
                                foamBoardCheckBox.setSelected(Boolean.parseBoolean(string[6]));
                                chassisCheckBox.setSelected(Boolean.parseBoolean(string[7]));
                                lamintaeCheckBox.setSelected(Boolean.parseBoolean(string[8]));
                                fitCheckBox.setSelected(Boolean.parseBoolean(string[9]));
                                fillCheckBox.setSelected(Boolean.parseBoolean(string[10]));

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0000'");
                                Date date = new Date(System.currentTimeMillis());

                                List<String> result3 = MsAccessDatabaseConnection.query("INSERT INTO OrderStatus VALUES('" + id + "','" +
                                        formatter.format(date) + "', '" + username + "', '" + 1 + "')", true);

                                Icon icon3 = ((JLabel) jPanel.getParent().getComponent(6)).getIcon();
                                int w = icon3.getIconWidth();
                                int h = icon3.getIconHeight();
                                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                                GraphicsDevice gd = ge.getDefaultScreenDevice();
                                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                                BufferedImage image = gc.createCompatibleImage(w, h);
                                int size = (int) Math.abs(Math.sqrt(w * w + h * h));
                                Graphics2D g = image.createGraphics();
                                icon3.paintIcon(null, g, 0, 0);
                                // this line magnifies the icon
                                image = image.getSubimage(Integer.parseInt(string[11]) * size / 1000, Integer.parseInt(string[12]) * size / 1000,
                                        Integer.parseInt(string[13]) * size / 1000, Integer.parseInt(string[14]) * size / 1000);
                                // this lines draw border on the icon
//                                g.setColor(Color.BLUE);
//                                g.drawRect(Integer.parseInt(string[11]) * size / 1000, Integer.parseInt(string[12]) * size / 1000,
//                                        Integer.parseInt(string[13]) * size / 1000, Integer.parseInt(string[14]) * size / 1000);
                                g.dispose();
                                ((JLabel) jPanel.getParent().getComponent(0)).setIcon(new ImageIcon(image));
                            }
                        });
                    }
                    scrollPane.revalidate();
                } else {
                    demo.infoBox("Please select some images and paper size!", "Error");
                }
            }
        });

        spinner1.setValue(1);
        ((SpinnerNumberModel) spinner1.getModel()).setMinimum(1);

        List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM Orders Where OrderNo = " + id);
        commentTextArea.setText(result.get(4));
        commentTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == '\b') {
                    MsAccessDatabaseConnection.query("UPDATE Orders SET OrderComments='" +
                            commentTextArea.getText().substring(0, commentTextArea.getText().length() - 1) + "'Where OrderNo = " + id, true);
                } else {
                    MsAccessDatabaseConnection.query("UPDATE Orders SET OrderComments='" + commentTextArea.getText() + e.getKeyChar() + "'Where OrderNo = " + id, true);
                }
            }
        });

        lockButton.setText("\uD83D\uDD12");
        lockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lockButton.getText().equals("\uD83D\uDD12")) {
                    lockButton.setText("\uD83D\uDD13");
                    textField1.setEnabled(true);
                    textField2.setEnabled(true);
                    List<String> result = MsAccessDatabaseConnection.query("SELECT * FROM PhotoSizes Where PhotoSizeTitle = '" +
                            comboBox4.getSelectedItem() + "'");
                    textField1.setText(result.get(1));
                    textField2.setText(result.get(2));
                } else {
                    lockButton.setText("\uD83D\uDD12");
                    textField1.setEnabled(false);
                    textField2.setEnabled(false);
                }
            }
        });

        saveOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0000'");
//                Date date = new Date(System.currentTimeMillis());
//
//                List<String> result2 = MsAccessDatabaseConnection.query("INSERT INTO Orders VALUES('" + id + "','" +
//                        formatter.format(date) + "', '" + ratio.getSelectedIndex() + "', '" + comboBox2.getSelectedIndex() + "','" +
//                        commentTextArea.getText() + "')", true);
//                if (result2.contains("error")) {
//                } else {
//                    List<String> result3 = MsAccessDatabaseConnection.query("INSERT INTO OrderStatus VALUES('" + id + "','" +
//                            formatter.format(date) + "', '" + username + "', '" + 1 + "')", true);
//                    if (result3.contains("error")) {
//                        MsAccessDatabaseConnection.query("delete from Orders where OrderNo = '"
//                                + id + "'", true);
//                    }
//                }
            }
        });

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                loadFiles();
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    JPanel[] getChecked() {
        ArrayList<JPanel> jPanelList = new ArrayList<JPanel>();
        for (int i = 0; i < scrollPane.getComponentCount(); i++) {
            JPanel jPanel = (JPanel) scrollPane.getComponent(i).getComponentAt(50, 50);
            if (((LineBorder) jPanel.getBorder()).getLineColor().equals(Color.blue))
                jPanelList.add(jPanel);
        }
        return jPanelList.toArray(new JPanel[jPanelList.size()]);
    }
}
