import java.awt.event.*;
import java.awt.Window;

public class BasicWindowMonitor extends WindowAdapter {

  public void windowClosing(WindowEvent e) {
    Window w = e.getWindow();
    w.setVisible(false);
    w.dispose();
    System.out.println("Exit");
    System.exit(0);
  }
}