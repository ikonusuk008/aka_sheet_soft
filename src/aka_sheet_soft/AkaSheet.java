package aka_sheet_soft;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.border.LineBorder;
public class AkaSheet extends JFrame implements CpoyScreen,KeyListener,WindowListener {

   private BufferedImage       backImage;
   private Capture             capture;
   private final static Insets NO_INSETS = new Insets(0, 0, 0, 0);
   GraphicsEnvironment         env       = GraphicsEnvironment.getLocalGraphicsEnvironment();
   Rectangle                   rect      = env.getMaximumWindowBounds();
   final Robot                 robot     = new Robot();
   boolean                     flag      = false;
   File                        file;
   FileWriter                  f;
   Properties                  prop      = new Properties();
   int                         hoji_x    = 0;
   int                         hoji_y    = 0;
   int                         hoji_w    = 0;
   int                         hoji_h    = 0;
   int                         idou_haba = 0;

   public AkaSheet(String title) throws FileNotFoundException, IOException, AWTException {
      super(title);
      JFrame frame = new JFrame();
      frame.setUndecorated(true);
      JButton button = new JButton("×");
      button.setFont(new Font("", Font.BOLD, 20));
      button.setBackground(Color.red);
      button.addActionListener(new ActionListener(){

         public void actionPerformed(ActionEvent e) {
            System.exit(0);
         }
      });
      frame.add(button);
      frame.setVisible(true);
      frame.setSize(50, 50);
      ImageIcon icon = new ImageIcon("./resource/img/akasheet.jpg");
      frame.setIconImage(icon.getImage());
      try {
         // TODO [important]　Make it a relative path
         prop.load(new FileInputStream("./resource/prop1.properties"));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      idou_haba = Integer.parseInt(prop.getProperty("hennsuu"));
      try {
         file = new File("./resource/debug.txt");
         if (checkBeforeWritefile(file)) {
            f = new FileWriter(file, true);
         } else {
            System.out.println("ファイルに書き込めません");
         }
      } catch (IOException e) {
         System.out.println(e);
      }
      f.write("public Aka_sheet(String title) \n");
      System.out.println("public Aka_sheet(String title) ");
      double_start_lock();
      env  = GraphicsEnvironment.getLocalGraphicsEnvironment();
      rect = env.getMaximumWindowBounds();
      this.addWindowListener(this);
      this.addKeyListener(this);
      init();
   }

   public synchronized void init() throws IOException {
      f.write("public synchronized void init() throws IOException {\n");
      System.out.println("public synchronized void init() throws IOException {");
      TransGlassPane transGlassPane = new TransGlassPane();
      transGlassPane.setBorder(new LineBorder(Color.red, 2));
      this.setRootPane(transGlassPane);
      JComponent content = (JComponent) getContentPane();
      content.setOpaque(false);
      capture = new Capture(this);
   }

   public void setCapturing(boolean flag) throws IOException {
      f.write("public void setCapturing(boolean flag) throws IOException {\n");
      if (flag && !capture.isCapturing()) {
         f.write("if (flag && !capture.isCapturing()) {\n");
         capture.startCapturing();
         repaint();
      } else if (!flag && capture.isCapturing()) {
         f.write("} else if (!flag && capture.isCapturing()) {\n");
         capture.stopCapturing();
         repaint();
      }
   }

   private synchronized void setBackImage(BufferedImage image) throws IOException {
      f.write("private synchronized void setBackImage(BufferedImage image) throws IOException {\n");
      backImage = image;
   }

   private synchronized BufferedImage getBackImage() throws IOException {
      f.write("private synchronized BufferedImage getBackImage() throws IOException {\n");
      return backImage;
   }

   public synchronized boolean getIgnoreRepaint() {
      try {
         f.write("public synchronized boolean getIgnoreRepaint() {\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
      capture.notifyPaint();
      return super.getIgnoreRepaint();
   }

   public void addNotify() {
      try {
         f.write("public void addNotify() {\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
      if (!capture.isCapturing()) {
         try {
            f.write("if (!capture.isCapturing()) {\n");
         } catch (IOException e) {
            e.printStackTrace();
         }
         capture.startCapturing();
      }
      // enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
      enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
      super.addNotify();
   }

   public void copyScreen() throws IOException {
      f.write("public void copy_screen() throws IOException {\n");
      Rectangle bounds = getBounds();
      Insets    insets = getInsets();
      if (!isUndecorated() && insets.equals(NO_INSETS) || bounds.width <= 0 || bounds.height <= 0) {
         f.write("if (!isUndecorated() && insets.equals(NO_INSETS) || bounds.width <= 0 || bounds.height <= 0) {\n");
         capture.notifyPaint();
         return;
      }
      bounds = new Rectangle(bounds.x + insets.left, bounds.y + insets.top, bounds.width - insets.left - insets.right,
                                                         bounds.height - insets.top - insets.bottom);
      if (bounds.width > 0 && bounds.height > 0) {
         f.write("if (bounds.width > 0 && bounds.height > 0) {\n");
         hide();
         backImage = capture.captureScreen(bounds);
         ImageIcon imageIcon = new ImageIcon(backImage);
         process(imageIcon);
         f.write("process(imageIcon)\n");
         capture.setShow(true);
         show();
      }
   }

   protected void processComponentEvent(ComponentEvent e) {
      try {
         f.write("protected void processComponentEvent(ComponentEvent e) {\n");
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      if (capture.isCapturing()) {
         try {
            f.write("if (capture.isCapturing()) {\n");
         } catch (IOException e1) {
            e1.printStackTrace();
         }
         if (e.getID() == ComponentEvent.COMPONENT_MOVED || e.getID() == ComponentEvent.COMPONENT_RESIZED) {
            try {
               f.write("if (e.getID() == ComponentEvent.COMPONENT_MOVED || e.getID() == ComponentEvent.COMPONENT_RESIZED) {\n");
            } catch (IOException e1) {
               e1.printStackTrace();
            }
            capture.notifyMove();
         }
      }
      super.processComponentEvent(e);
   }

   class TransGlassPane extends JRootPane {

      public final void paintComponent(Graphics g) {
         try {
            f.write("public final void paintComponent(Graphics g) {\n");
         } catch (IOException e) {
            e.printStackTrace();
         }
         if (capture.isCapturing()) {
            try {
               f.write("if (capture.isCapturing()) {\n");
            } catch (IOException e1) {
               e1.printStackTrace();
            }
            try {
               if (getBackImage() != null) {
                  f.write("if (getBackImage() != null) {\n");
                  g.drawImage(getBackImage(), 0, 0, this);
                  capture.setShow(false);
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   void process(ImageIcon imageIcon) throws IOException {
      f.write("void process(ImageIcon imageIcon) throws IOException {\n");
      int           width    = imageIcon.getIconWidth();
      int           height   = imageIcon.getIconHeight();
      BufferedImage bi       = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
      int[]         rgbArray = new int[width * height];
      PixelGrabber  pg       = new PixelGrabber(imageIcon.getImage(), 0, 0, width, height, rgbArray, 0, width);
      pg.startGrabbing();
      int[] invArray = new int[rgbArray.length];
      for (int i = 0; i < rgbArray.length; i++) {
         int v = 0xff000000 & rgbArray[i];
         if (getInvR(rgbArray[i]) >= 250) {
            v |= 0xffffff;
            v |= 0xffffff;
            v |= 0xffffff;
         } else {
            v |= getInvR(rgbArray[i]) << 16;
            v |= getInvG(rgbArray[i]) << 8;
            v |= getInvB(rgbArray[i]);
         }
         invArray[i] = v;
      }
      bi.setRGB(0, 0, width, height, invArray, 0, width);
      setBackImage(bi);
   }

   static int getInvR(int rgb) {
      int r = (rgb >> 16) & 0xff;
      return r;
   }

   static int getInvG(int rgb) {
      int g = (rgb >> 8) & 0xff;
      return g;
   }

   static int getInvB(int rgb) {
      int b = rgb & 0xff;
      return b;
   }

   public void keyPressed(KeyEvent e) {
      System.out.println("key==" + e.getKeyChar());
      System.out.println("keycode==" + e.getKeyCode());
      /*
       * 横幅を狭く
       */
      if (e.getKeyChar() == '7') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (d.width <= 10 == false) {
            d.width -= 50;
            robot.mouseMove(p.x + 1, p.y + 1); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /*
          * 高さを低く
          */
      } else if (e.getKeyChar() == '1') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (d.height <= 10 == false) {
            d.height -= 50;
            robot.mouseMove(p.x + 1, p.y + 1); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /*
          * 横幅を広く
          */
      } else if (e.getKeyChar() == '9') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (d.width >= rect.width == false) {
            d.width += 50;
            robot.mouseMove(p.x + 1, p.y + 1); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /*
          * 高さを高く
          */
      } else if (e.getKeyChar() == '3') {// Enter
         Dimension d = getSize();
         Point     p = getLocation();
         if (d.height >= rect.height == false) {
            d.height += 50;
            robot.mouseMove(p.x + 1, p.y + 1); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /* up */
      } else if (e.getKeyChar() == '8') {
         Dimension d = getSize();
         Point     p = getLocation();
         System.out.println("p.y==" + p.y);
         System.out.println("(-rect.height)==" + (-rect.height));
         System.out.println("(-rect.height)==" + (-rect.width));
         System.out.println("(this.getSize().height)==" + (this.getSize().height));
         System.out.println("(this.getSize().width)==" + (this.getSize().width));
         if ((p.y <= (-this.getSize().height + 50)) == false) {
            p.y -= idou_haba;
            robot.mouseMove(p.x, p.y); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /* left */
      } else if (e.getKeyChar() == '4') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (p.x <= (-this.getSize().width + 50) == false) {
            p.x -= idou_haba;
            robot.mouseMove(p.x + 1, p.y + 1); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /* down */
      } else if (e.getKeyChar() == '2') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (p.y >= rect.height == false) {
            p.y += idou_haba;
            robot.mouseMove(p.x, p.y); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /* right */
      } else if (e.getKeyChar() == '6') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (p.x >= rect.width - 50 == false) {
            p.x += idou_haba;
            robot.mouseMove(p.x + 1, p.y + 1); // 移動
            this.setSize(d);
            this.setLocation(p);
            this.validate();
         }
         /* largest */
      } else if (e.getKeyChar() == '5') {
         Dimension d = getSize();
         Point     p = getLocation();
         if (flag) {
            this.setBounds(hoji_x, hoji_y, hoji_w, hoji_h);
            flag = false;
         } else {
            hoji_x = p.x;
            hoji_y = p.y;
            hoji_w = d.width;
            hoji_h = d.height;
            this.setBounds(0, 0, rect.width, rect.height);
            flag = true;
         }
      }
   }

   public void keyReleased(KeyEvent keyevent) {
   }

   public void keyTyped(KeyEvent keyevent) {
   }

   private void double_start_lock() throws FileNotFoundException, IOException {
      /*
       * 起動チェック 2重起動しない
       */
      final FileOutputStream fos  = new FileOutputStream(new File("./resource/lock_control2"));
      final FileChannel      fc   = fos.getChannel();
      final FileLock         lock = fc.tryLock();
      if (lock == null) {
         /*
          * 既に起動されているので終了する
          */
         System.out.println("lock****************************");
         try {
            "hello".charAt(-1);
         } catch (Exception e) {
            System.exit(0);
         }
         return;
      }
      Runtime.getRuntime().addShutdownHook(new Thread(){// ロック開放処理を登録

         public void run() {
            if (lock != null && lock.isValid()) {
               try {
                  lock.release();
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
            try {
               fc.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
            try {
               fos.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      });
   }

   private static boolean checkBeforeWritefile(File file) {
      if (file.exists()) {
         if (file.isFile() && file.canWrite()) {
            return true;
         }
      }
      return false;
   }

   public static void main(String[] args) throws FileNotFoundException, IOException, AWTException {
      // f.write("\n");
      AkaSheet jTransFrame = new AkaSheet("赤シートソフト");
      jTransFrame.setSize(700, 450);
      jTransFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      jTransFrame.setUndecorated(true);
      jTransFrame.setAlwaysOnTop(true);
      ImageIcon icon = new ImageIcon("./resource/img/akasheet.jpg");
      jTransFrame.setIconImage(icon.getImage());
      jTransFrame.setVisible(true);
   }

   public void windowActivated(WindowEvent windowevent) {
   }

   public void windowClosed(WindowEvent windowevent) {
   }

   public void windowClosing(WindowEvent windowevent) {
      try {
         f.write("*******windowClosing*******\n");
         f.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void windowDeactivated(WindowEvent windowevent) {
   }

   public void windowDeiconified(WindowEvent windowevent) {
   }

   public void windowIconified(WindowEvent windowevent) {
   }

   public void windowOpened(WindowEvent windowevent) {
   }

   public void copy_screen() throws IOException {
   }
}
