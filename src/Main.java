import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;

public class Main {
    static java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
    static java.awt.DisplayMode displayMode = env.getDefaultScreenDevice().getDisplayMode();
    public static int width = displayMode.getWidth();
    public static int height = displayMode.getHeight();
    public static void main(String[] args) {
        try {
            SerialPort port = SerialPort.getCommPorts()[0];
            port.openPort();
            OutputStream out = port.getOutputStream();

            JFrame f = new JFrame("SerialMonitor");
            f.setBounds(width/3, height/3,650, 342);
            f.setResizable(false);
            JPanel panel = new JPanel();
            GridBagLayout layout = new GridBagLayout();
            panel.setLayout(layout);
            GridBagConstraints gbc = new GridBagConstraints();



            JTextArea logArea = new JTextArea(14,50);

            port.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public void serialEvent(SerialPortEvent serialPortEvent) {
                    byte[] Data = serialPortEvent.getReceivedData();
                    logArea.append(new String(Data));
                }
            });
            logArea.setBorder(new LineBorder(Color.BLACK,1));
            logArea.setEditable(false);
            logArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
            //ユーザーの操作があれば自動スクロールを停止したい
            gbc.gridx=0;
            gbc.gridy=0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(0,0,1,0);
            layout.setConstraints(scrollPane,gbc);

            JTextArea sendForm = new JTextArea(4,50);
            sendForm.setLineWrap(true);
            sendForm.setBorder(new LineBorder(Color.BLACK,1));
            sendForm.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String data = sendForm.getText()+'\n';
                        sendForm.setText("");
                        try {
                            out.write(data.getBytes());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                @Override
                public void keyReleased(KeyEvent e) {}
            });
            gbc.gridx=0;
            gbc.gridy=1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(0,0,0,0);
            JScrollPane scrollPane1 = new JScrollPane(sendForm, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            layout.setConstraints(scrollPane1,gbc);

            Button sendButton = new Button("send");
            sendButton.addActionListener(e -> {
                String data = sendForm.getText()+'\n';
                try {
                    out.write(data.getBytes());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            gbc.gridx=1;
            gbc.gridy=1;
            gbc.insets = new Insets(0,1,0,0);
            gbc.fill = GridBagConstraints.BOTH;
            layout.setConstraints(sendButton,gbc);

            panel.add(scrollPane);
            panel.add(scrollPane1);
            panel.add(sendButton);
            f.getContentPane().add(panel);



            f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                    port.closePort();
                    System.exit(0);
                }
            });

            f.setVisible(true);
        } catch (Exception e) {
            ErrorFrame f = new ErrorFrame("Error", "Cannot Find SerialPorts.");
            f.setVisible(true);
        }
    }
}

