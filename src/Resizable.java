import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Resizable extends JComponent {
    int xRatio;
    int yRatio;
    final int maxX;
    final int maxY;
    private Double mAspectRatio;


    public Resizable(Component comp, int xRatio, int yRatio, int maxX, int maxY) {
        this(comp, new ResizableBorder(8), xRatio, yRatio, maxX, maxY);
    }

    public Resizable(Component comp, ResizableBorder border, int xRatio, int yRatio, int maxX, int maxY) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.maxX = maxX;
        this.maxY = maxY;
        setLayout(new BorderLayout());
        add(comp);
        addMouseListener(resizeListener);
        addMouseMotionListener(resizeListener);
        setBorder(border);
    }

    void resize() {
        if (getParent() != null) {
            getParent().revalidate();
        }
    }

    MouseInputListener resizeListener = new MouseInputAdapter() {

        @Override
        public void mouseMoved(MouseEvent me) {
            if (hasFocus()) {
                ResizableBorder resizableBorder = (ResizableBorder) getBorder();
                setCursor(Cursor.getPredefinedCursor(resizableBorder.getCursor(me)));
            }
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            setCursor(Cursor.getDefaultCursor());
        }

        private int cursor;
        private Point startPos = null;

        @Override
        public void mousePressed(MouseEvent me) {
            ResizableBorder resizableBorder = (ResizableBorder) getBorder();
            cursor = resizableBorder.getCursor(me);
            startPos = me.getPoint();

            requestFocus();
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            mAspectRatio = Math.atan2(yRatio, xRatio);
            if (startPos != null) {
                int x = getX();
                int y = getY();
                int w = getWidth();
                int h = getHeight();

                int dx = me.getX() - startPos.x;
                int dy = me.getY() - startPos.y;

                if (cursor != Cursor.MOVE_CURSOR) {
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance > 10) {
                        boolean b = Math.abs(dx) > Math.abs(dy);
                        if (cursor == Cursor.NW_RESIZE_CURSOR) {
                            if (!(w - dx < 50) && !(h - dy < 50)) {
                                int sign;
                                if (b) {
                                    sign = (int) Math.signum(dx);
                                } else {
                                    sign = (int) Math.signum(dy);
                                }
                                dx = (int) (distance * Math.cos(mAspectRatio) * sign);
                                dy = (int) (distance * Math.sin(mAspectRatio) * sign);

                                setBounds(x + dx, y + dy, w - dx, h - dy);
                                startPos = me.getPoint();
                                resize();
                            }
                        } else if (cursor == Cursor.NE_RESIZE_CURSOR) {
                            int sign;
                            if (b) {
                                sign = -(int) Math.signum(dx);
                            } else {
                                sign = (int) Math.signum(dy);
                            }
                            dx = (int) (distance * Math.cos(mAspectRatio) * sign);
                            dy = (int) (distance * Math.sin(mAspectRatio) * sign);
                            if (!(w + dx < 50) && !(h - dy < 50)) {
                                setBounds(x, y + dy, w - dx, h - dy);
                                startPos = me.getPoint();
                                resize();
                            }
                        } else if (cursor == Cursor.SW_RESIZE_CURSOR) {
                            int sign;
                            if (b) {
                                sign = (int) Math.signum(dx);
                            } else {
                                sign = -(int) Math.signum(dy);
                            }
                            dx = (int) (distance * Math.cos(mAspectRatio) * sign);
                            dy = (int) (distance * Math.sin(mAspectRatio) * sign);
                            if (!(w - dx < 50) && !(h + dy < 50)) {
                                setBounds(x + dx, y, w - dx, h - dy);
                                startPos = me.getPoint();
                                resize();
                            }
                        } else if (cursor == Cursor.SE_RESIZE_CURSOR) {
                            int sign;
                            if (b) {
                                sign = (int) Math.signum(dx);
                            } else {
                                sign = (int) Math.signum(dy);
                            }
                            dx = (int) (distance * Math.cos(mAspectRatio) * sign);
                            dy = (int) (distance * Math.sin(mAspectRatio) * sign);

                            if (!(w + dx < 50) && !(h + dy < 50)) {
                                setBounds(x, y, w + dx, h + dy);
                                startPos = me.getPoint();
                                resize();
                            }
                        }
                    }
                    dy = 0;
                    h = getWidth() * yRatio / xRatio;
                    if (cursor == Cursor.NE_RESIZE_CURSOR || cursor == Cursor.NW_RESIZE_CURSOR) {
                        dy = h - getHeight();
                    }
                    setBounds(getX(), getY() - dy, getWidth(), h);
                } else {
                    Rectangle bounds = getBounds();
                    bounds.translate(dx, dy);
                    setBounds(bounds);
                    resize();
                }

                if (getX() < 0) {
                    setBounds(0, getY(), getWidth(), getHeight());
                }
                if (getY() < 0) {
                    setBounds(getX(), 0, getWidth(), getHeight());
                }
                if (getWidth() + getX() > maxX) {
                    if (maxX - getWidth() > 0) {
                        setBounds(maxX - getWidth(), getY(), getWidth(), getHeight());
                    }
                    if (getWidth() + getX() > maxX) {
                        setBounds(0, getY(), getWidth() + maxX - getWidth(), (getWidth() + maxX - getWidth()) * yRatio / xRatio);
                    }
                }
                if (getHeight() + getY() > maxY) {
                    if (maxY - getHeight() > 0) {
                        setBounds(getX(), maxY - getHeight(), getWidth(), getHeight());
                    }
                    if (getHeight() + getY() > maxY) {
                        setBounds(getX(), 0, (getHeight() + maxY - getHeight()) * xRatio / yRatio, getHeight() + maxY - getHeight());
                    }
                }
                resize();

//                switch (cursor) {
//                    case Cursor.N_RESIZE_CURSOR:
//                        if (!(h - dy < 50)) {
//                            setBounds(x, y + dy, w, h - dy);
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.S_RESIZE_CURSOR:
//                        if (!(h + dy < 50)) {
//                            setBounds(x, y, w, h + dy);
//                            startPos = me.getPoint();
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.W_RESIZE_CURSOR:
//                        if (!(w - dx < 50)) {
//                            setBounds(x + dx, y, w - dx, h);
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.E_RESIZE_CURSOR:
//                        if (!(w + dx < 50)) {
//                            setBounds(x, y, w + dx, h);
//                            startPos = me.getPoint();
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.NW_RESIZE_CURSOR:
//                        if (!(w - dx < 50) && !(h - dy < 50)) {
//                            setBounds(x + dx, y + dy, w - dx, h - dy);
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.NE_RESIZE_CURSOR:
//                        if (!(w + dx < 50) && !(h - dy < 50)) {
//                            setBounds(x, y + dy, w + dx, h - dy);
//                            startPos = new Point(me.getX(), startPos.y);
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.SW_RESIZE_CURSOR:
//                        if (!(w - dx < 50) && !(h + dy < 50)) {
//                            setBounds(x + dx, y, w - dx, h + dy);
//                            startPos = new Point(startPos.x, me.getY());
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.SE_RESIZE_CURSOR:
//                        if (!(w + dx < 50) && !(h + dy < 50)) {
//                            setBounds(x, y, w + dx, h + dy);
//                            startPos = me.getPoint();
//                            resize();
//                        }
//                        break;
//
//                    case Cursor.MOVE_CURSOR:
//                        Rectangle bounds = getBounds();
//                        bounds.translate(dx, dy);
//                        setBounds(bounds);
//                        resize();
//                }
                setCursor(Cursor.getPredefinedCursor(cursor));
//                System.out.println(getWidth());
//                System.out.println(getHeight());
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            startPos = null;
        }
    };
}