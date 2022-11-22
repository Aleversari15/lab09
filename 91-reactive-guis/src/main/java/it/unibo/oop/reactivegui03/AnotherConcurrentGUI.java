package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final JLabel display = new JLabel();
    public AnotherConcurrentGUI(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        new Thread(() -> {
            try{
                Thread.sleep(10000);
                agent.stopCounting();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }).start();
        stop.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e) -> agent.setUp());
        down.addActionListener((e) -> agent.setDown());
     }

     private class Agent implements Runnable {
        private volatile boolean stop;
        private int counter;
        private volatile boolean up;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if(up){
                        this.counter++;

                    }
                    else{
                        this.counter--;
                    }
                    
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            try{
                this.stop = true;
                SwingUtilities.invokeAndWait(() -> {
                AnotherConcurrentGUI.this.up.setEnabled(false);
                AnotherConcurrentGUI.this.down.setEnabled(false);
                AnotherConcurrentGUI.this.stop.setEnabled(false);
                });
            }catch(InvocationTargetException | InterruptedException e){
                e.printStackTrace();
            }
        }

        public void setUp(){
            this.up=true;
        }

        public void setDown(){
            this.up=false;
        }
       
    }

}

