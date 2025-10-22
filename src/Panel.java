import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    present_cart cart = null;

    public Panel(present_cart cart) {
        this.cart = cart;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.cart.present_map(g);
    }

}
