package org.bookmc.loader.ui.renderer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return new Color(27, 27, 27);
    }

    @Override
    public Color getTextNonSelectionColor() {
        return Color.WHITE;
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return new Color(121, 134, 203);
    }
}
