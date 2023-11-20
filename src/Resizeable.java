import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

//For testing

///*
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
//*/

/**
 * tjacobs.ui.Resizeable<p>
 * Makes a component Resizeable. Does not work if there's a layout manager
 * messing with things<p>
 * <code>
 *  usage: 
 *  			Component c = ...
 *  			new Resizeable(c);
 *  			parent.add(c);
 *  </code>
 */

public class Resizeable extends MouseAdapter implements MouseMotionListener {

    public static final boolean DEBUG = false;

    int fix_pt_x = -1;
    int fix_pt_y = -1;
    private boolean mXAdjusting, mYAdjusting;
    Component mResizeable;
    private Double mAspectRatio;
    private Cursor mDefaultCursor;
    private Dimension mMinSize, mMaxSize;
    private boolean mResizeInAllDirections = false;
    private boolean mSetPreferredSize = false;

    public Resizeable(Component c) {
        mResizeable = c;
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
    }

    public boolean isRespectingMinSize() {
        return mMinSize == null;
    }

    public boolean isRespectingMaxSize() {
        return mMaxSize == null;
    }
    /**
     * If this is switched on, Resizeable will respect
     * the maximum size set on the component
     *
     * @param b whether to respect the maximum size or not
     */
    public void setRespectingMaxSize(boolean b) {
        mMaxSize = b ? mResizeable.getMaximumSize() : null;
    }

    /**
     * If this is switched on, Resizeable will respect
     * the minimum size set on the component
     *
     * @param b whether to respect the minimum size or not
     */
    public void setRespectingMinSize(boolean b) {
        mMinSize = b ? mResizeable.getMinimumSize() : null;
    }

    /**
     * Maintain the aspect ratio between the width and the
     * height.
     *
     * Enabling this will disable 8 way resizing
     *
     * @param x the x length value
     * @param y the y length value
     */
    public void setMaintainAspect(double x, double y) {
        setMaintainAspect(new Double(Math.atan2(y, x)));
    }

    /**
     * Maintain the aspect ratio between the width and the
     * height.
     *
     * Enabling this will disable 8 way resizing
     *
     * @param angle the aspect angle to maintain
     */
    public void setMaintainAspect(Double angle) {
        mAspectRatio = angle;
        if (angle != null) {
            setResizeInAllDirections(false);
        }
    }

    public void mouseEntered(MouseEvent me) {
        setCursorType(me.getPoint());
    }

    /**
     * Resizeable can be set so that the source component can
     * be resized in all directions
     * //@see setResizeInAllDirections(boolean)
     * @return true if the component is set to resize in
     * all directions
     */
    public boolean isResizingInAllDirections() {
        return mResizeInAllDirections;
    }

    /**
     * Set to true to enable resizing in all directions
     * when false component will only be Resizeable from
     * the bottom and right. Enabling this will disable
     * maintaining aspect ratio<p>
     *
     * default for this field is false
     * @param b whether to enable resize in all directions
     */
    public void setResizeInAllDirections(boolean b) {
        mResizeInAllDirections = b;
        if (b) setMaintainAspect(null);
    }

    protected void setCursorType(Point p) {
        boolean n = false;
        boolean w = false;
        boolean s = p.y + WindowUtilities.RESIZE_MARGIN_SIZE >= mResizeable.getHeight();
        boolean e = p.x + WindowUtilities.RESIZE_MARGIN_SIZE >= mResizeable.getWidth();
        if (mResizeInAllDirections) {
            n = p.y <= WindowUtilities.RESIZE_MARGIN_SIZE;
            w = p.x <= WindowUtilities.RESIZE_MARGIN_SIZE;
        }
        if (e) {
            if (s) {
                mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                return;
            }
            if (n) {
                mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                return;
            }
            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            return;
        }
        if (w) {
            if (s) {
                mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                return;
            }
            if (n) {
                mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                return;
            }
            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            return;
        }
        if(s) {
            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            return;
        }
        if (n) {
            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        }
        else if (mDefaultCursor != null) {
            mResizeable.setCursor(mDefaultCursor);
        }
    }


    /*
        private void setCursorType(Point p) {
            //boolean n = p.y <= WindowUtilities.RESIZE_MARGIN_SIZE;
            //boolean w = p.x <= WindowUtilities.RESIZE_MARGIN_SIZE;
            boolean s = p.y + WindowUtilities.RESIZE_MARGIN_SIZE >= mResizeable.getHeight();
            boolean e = p.x + WindowUtilities.RESIZE_MARGIN_SIZE >= mResizeable.getWidth();
            if (e) {
                if (s) {
                    mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    return;
                }
                mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                return;
            }
            if(s) {
                mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                return;
            }
            else if (mDefaultCursor != null) {
                mResizeable.setCursor(mDefaultCursor);
            }
        }
        */
    public void setDefaultMouseCursor(Cursor c) {
        mDefaultCursor = c;
    }

    public void mouseExited(MouseEvent me) {
//		if (mOldcursor != null)
//			((Component)me.getSource()).setCursor(mOldcursor);
//		mOldcursor = null;
    }

    public void mousePressed(MouseEvent me) {
        Cursor c = mResizeable.getCursor();
        Point loc = mResizeable.getLocation();
        mXAdjusting = false;
        mYAdjusting = false;
        //mResizeable.getParent().add(mResizeable, 0);
        mResizeable.getParent().repaint();
        if (c.equals(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))) {
            fix_pt_x = loc.x;
            fix_pt_y = loc.y;
            return;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))) {
            fix_pt_x = loc.x;
            fix_pt_y = -1;
            return;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))) {
            fix_pt_x = -1;
            fix_pt_y = loc.y;
            return;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))) {
            fix_pt_x = loc.x;
            fix_pt_y = loc.y;
            mXAdjusting = true;
            return;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))) {
            fix_pt_x = loc.x;
            fix_pt_y = -1;
            mXAdjusting = true;
            return;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))) {
            fix_pt_x = loc.x;
            fix_pt_y = loc.y;
            mXAdjusting = true;
            mYAdjusting = true;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))) {
            fix_pt_y = loc.y;
            fix_pt_x = -1;
            mYAdjusting = true;
        }
        if (c.equals(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))) {
            fix_pt_x = loc.x;
            fix_pt_y = loc.y;
            mYAdjusting = true;
        }
    }

    public void mouseReleased(MouseEvent me) {
        fix_pt_x = -1;
        fix_pt_y = -1;
    }

    public void mouseMoved(MouseEvent me) {
        setCursorType(me.getPoint());
    }

    public void mouseDragged(MouseEvent me) {
        //System.out.println("dragged");
        Point p = me.getPoint();
        if (DEBUG) System.out.println(p);
        if (fix_pt_x == -1 && fix_pt_y == -1) return;
        int width = fix_pt_x == -1 ? mResizeable.getWidth() : p.x;
        int height = fix_pt_y == -1 ? mResizeable.getHeight() : p.y;
        if (mXAdjusting) {
            //width variable is now the new x location
            if (mAspectRatio == null) {
                mResizeable.setLocation(new Point(mResizeable.getX() + p.x, mResizeable.getY()));
                width = mResizeable.getWidth() - p.x;
            }
            else {
                width = mResizeable.getWidth() - p.x;
            }
        }
        if (mYAdjusting) {
            //height variable is now the new y location
            if (mAspectRatio == null) {
                mResizeable.setLocation(new Point(mResizeable.getX(), mResizeable.getY() + p.y));
                height = mResizeable.getHeight() - p.y;
            }
            else {
                height = mResizeable.getHeight() - p.y;
            }
        }

        if (mMinSize != null) {
            width = width < mMinSize.width ? mMinSize.width : width;
            height = height < mMinSize.height ? mMinSize.height : height;
        }
        if (mMaxSize != null) {
            width = width > mMaxSize.width ? mMaxSize.width : width;
            height = height > mMaxSize.height ? mMaxSize.height : height;
        }
        if (mAspectRatio == null) {
            if (mSetPreferredSize) {
                mResizeable.setPreferredSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                Container c = mResizeable.getParent();
                if (c != null && c instanceof JComponent) {
                    ((JComponent) c).revalidate();
                }
            }
            else {
                mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
            }
        }
        else {
            //do something
            double distance = Math.sqrt(width * width + height * height);
            width = (int) (distance * Math.cos(mAspectRatio));
            height = (int) (distance * Math.sin(mAspectRatio));
            if (mMinSize != null) {
                width = width < mMinSize.width ? mMinSize.width : width;
                height = height < mMinSize.height ? mMinSize.height : height;
            }
            if (mMaxSize != null) {
                width = width > mMaxSize.width ? mMaxSize.width : width;
                height = height > mMaxSize.height ? mMaxSize.height : height;
            }
            if (mSetPreferredSize) {
                mResizeable.setPreferredSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                //mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                //mResizeable.getParent().invalidate();
                //mResizeable.getParent().validate();

            }
            else {
                mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
            }
        }
    }

//	public void mouseDragged(MouseEvent me) {
//		Point p = me.getPoint();
//		if (DEBUG) System.out.println(p);
//		if (fix_pt_x == -1 && fix_pt_y == -1) return;
//		int width = fix_pt_x == -1 ? mResizeable.getWidth() : p.x;
//		int height = fix_pt_y == -1 ? mResizeable.getHeight() : p.y;
//		if (mMinSize != null) {
//			width = width < mMinSize.width ? mMinSize.width : width; 
//			height = height < mMinSize.height ? mMinSize.height : height;
//		}
//		if (mMaxSize != null) {
//			width = width > mMaxSize.width ? mMaxSize.width : width; 
//			height = height > mMaxSize.height ? mMaxSize.height : height;
//		}
//		if (mAspectRatio == null) {
//			mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
//		}
//		else {
//			//do something
//			double distance = Math.sqrt(width * width + height * height);
//			width = (int) (distance * Math.cos(mAspectRatio));
//			height = (int) (distance * Math.sin(mAspectRatio));
//			if (mMinSize != null) {
//				width = width < mMinSize.width ? mMinSize.width : width; 
//				height = height < mMinSize.height ? mMinSize.height : height;
//			}
//			if (mMaxSize != null) {
//				width = width > mMaxSize.width ? mMaxSize.width : width; 
//				height = height > mMaxSize.height ? mMaxSize.height : height;
//			}
//			mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
//		}
//	}

//	public void mouseDragged(MouseEvent me) {
//		Point p = me.getPoint();
//		if (DEBUG) System.out.println(p);
//		if (fix_pt_x == -1 && fix_pt_y == -1) return;
//		int width = fix_pt_x == -1 ? mResizeable.getWidth() : p.x;
//		int height = fix_pt_y == -1 ? mResizeable.getHeight() : p.y;
//		if (mMinSize != null) {
//			width = width < mMinSize.width ? mMinSize.width : width; 
//			height = height < mMinSize.height ? mMinSize.height : height;
//		}
//		if (mMaxSize != null) {
//			width = width > mMaxSize.width ? mMaxSize.width : width; 
//			height = height > mMaxSize.height ? mMaxSize.height : height;
//		}
//		if (mAspectRatio == null) {
//			mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
//		}
//		else {
//			//do something
//			double distance = Math.sqrt(width * width + height * height);
//			width = (int) (distance * Math.cos(mAspectRatio));
//			height = (int) (distance * Math.sin(mAspectRatio));
//			if (mMinSize != null) {
//				width = width < mMinSize.width ? mMinSize.width : width; 
//				height = height < mMinSize.height ? mMinSize.height : height;
//			}
//			if (mMaxSize != null) {
//				width = width > mMaxSize.width ? mMaxSize.width : width; 
//				height = height > mMaxSize.height ? mMaxSize.height : height;
//			}
//			mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
//		}
//	}

    public void setAdjustPerferredSize(boolean b) {
        mSetPreferredSize = b;
    }

    //For Testing
///*	
    public static void main(String args[]) {
        //Arrays.printArray(args);
        JPanel jp = new JPanel();
        JButton jb = new JButton("hello");
        //JLabel jb = new JLabel("Hello");
        //JLabel jb2 = new JLabel("hello2");
        JButton jb2 = new JButton("hello2");
        //JLabel jb3 = new JLabel("hello3");
        JButton jb3 = new JButton("hello3");
        jp.setPreferredSize(new Dimension(300,300));
        jp.setSize(300,300);
        jp.setLayout(null);
        jp.add(jb);
        jp.add(jb2);
        jp.add(jb3);
        jb.setSize(40,40);
        jb.setLocation(2,2);
        jb2.setSize(30,20);
        jb2.setLocation(30,10);
        jb3.setSize(30,30);
        jb3.setLocation(10,50);
        Resizeable _r = new Resizeable(jb);
        //Resizeable _r = new Resizeable(c);
        //Resizeable r = new Resizeable(c);
        //Resizeable r2 = new Resizeable(c);
        _r.setDefaultMouseCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        Resizeable r = new Resizeable(jb2);
        r.setMaintainAspect(30, 20);
        r.setDefaultMouseCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        Resizeable r2 = new Resizeable(jb3);
        r2.setResizeInAllDirections(true);
        //r2.setMaintainAspect(30, 20);
        jb2.setMinimumSize(new Dimension(15,10));
        jb2.setMaximumSize(new Dimension(210,140));
        r.setRespectingMaxSize(true);
        r.setRespectingMinSize(true);
        JFrame jf = new JFrame();
        jf.add(jp);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocation(100,100);
        jf.pack();
        jf.setVisible(true);
    }
    //*/
}
