package input;

import cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyBoard implements KeyListener {
    private Cena cena;

    public KeyBoard(Cena cena) {
        this.cena = cena;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        // Movimenta��o da bola com colis�o
        if (e.getKeyCode() == KeyEvent.VK_W)
            cena.getBola().move(0, -0.1f, cena.getMapa());

        if (e.getKeyCode() == KeyEvent.VK_S)
            cena.getBola().move(0, 0.1f, cena.getMapa());

        if (e.getKeyCode() == KeyEvent.VK_A)
            cena.getBola().move(-0.1f, 0, cena.getMapa());

        if (e.getKeyCode() == KeyEvent.VK_D)
            cena.getBola().move(0.1f, 0, cena.getMapa());
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}