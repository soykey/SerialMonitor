import javax.swing.*;
import java.awt.*;

public class ErrorFrame extends JFrame {
    JPanel J_Panel = new JPanel();
    ErrorFrame(String title, String message) {
        setTitle(title);
        setSize(300, 150);
        setBounds(Main.width/3, Main.height/3,400, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        J_Panel.setLayout(new GridBagLayout());
        J_Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel J_Label = new JLabel(message);
        J_Panel.add(J_Label);
        add(J_Panel);
    }
}
