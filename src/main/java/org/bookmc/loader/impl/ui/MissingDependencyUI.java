package org.bookmc.loader.impl.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.impl.ui.cell.CustomTreeCellRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Map;

public class MissingDependencyUI {

    public static void failed(Map<String, ArrayList<String>> missingDependencies) {
        JFrame frame = new JFrame("Failed to launch! (Dependency problems!)");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Mods");
        generateNodes(root, missingDependencies);

        JTree tree = new JTree(root);
        expandAllNodes(tree, 0, tree.getRowCount());
        tree.setCellRenderer(new CustomTreeCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tree);
        tree.setBackground(new Color(27, 27, 27));
        frame.add(scrollPane);

        frame.addWindowListener(new WindowListener() {
            private final Logger logger = LogManager.getLogger(this);

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                logger.error("Exiting the game due to missing dependencies");
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        frame.setVisible(true);
    }

    private static void generateNodes(DefaultMutableTreeNode root, Map<String, ArrayList<String>> missingDependencies) {
        for (String key : missingDependencies.keySet()) {
            DefaultMutableTreeNode dependencyRoot = new DefaultMutableTreeNode(key);
            root.add(dependencyRoot);
            for (String reason : missingDependencies.get(key)) {
                dependencyRoot.add(new DefaultMutableTreeNode(reason));
            }
        }
    }

    private static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; i++) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}
