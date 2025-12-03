package app;

import view.MenuUI;
import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuUI ui = new MenuUI();
            ui.setVisible(true);
        });
    }
}
