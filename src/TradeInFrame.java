import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * TradeInFrame is a frame that allows the specified player to trade in some of
 * their resource cards in exchange for a resource card of their choosing. Each
 * of the specified player's resource cards is represented by an icon in the
 * top panel. The player chooses the cards to trade in by clicking on them.
 * When one of these icons is clicked, it is removed from the top panel and
 * placed in the middle panel with the other selected cards. If the player does
 * not want to trade in a card that they have already selected, they can click
 * on said card and it will be moved back to the top panel. The player selects
 * the type of resource card they would like to receive by selecting the
 * corresponding radio button in the bottom panel. When the player is finished
 * selecting the cards to trade in, they can click the "Trade" button at the
 * very bottom of the frame. The player is asked to confirm their selection
 * before the trade is completed. If the player has any settlements or cities
 * that are on a harbor, each harbor is displayed above the top panel.
 * @author Connor Barnes
 */
public class TradeInFrame extends JFrame {
    //GUI variables
    private GameIcons icons;
    private JButton triggerButton;
    private JLayeredPane keepPane, discardPane;
    private JComboBox<ImageIcon> resourceComboBox;
    private JButton confirmTradeInButton;
    //Information variables
    private Player player;
    private ArrayList<ResourceLabel> keepLabels, discardLabels;
    private int resourceWidth, resourceHeight, discardedResource, desiredResource, numDiscardedResources;

    /**
     * Creates and displays a frame that allows the specified player to trade
     * in some of their resource cards for a resource card of their choosing.
     * @param inIcons the icons used to display each card and harbor
     * @param triggerListener the ActionListener that will be triggered when
     *                        the player is finished.
     * @param inPlayer the player who wants to trade in some of their cards
     */
    public TradeInFrame(GameIcons inIcons, ActionListener triggerListener, Player inPlayer) {
        super("Trade In Resource Cards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        icons = inIcons;
        resourceWidth = icons.getResourceIcon(GameController.RESOURCE_TYPES[0]).getIconWidth();
        resourceHeight = icons.getResourceIcon(GameController.RESOURCE_TYPES[0]).getIconHeight();
        player = inPlayer;
        keepLabels = new ArrayList<ResourceLabel>(player.getSumResourceCards());
        discardLabels = new ArrayList<ResourceLabel>(4);

        //Create the trigger button and add the given ActionListener to it
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);

        //Construct a label for each of the player's card and add them to keepLabels
        ResourceLabel tempLabel;
        for (int resource = 0; resource < GameController.RESOURCE_TYPES.length; resource++) {
            for (int i = 0; i < player.getNumResourceCards(GameController.RESOURCE_TYPES[resource]); i++) {
                tempLabel = new ResourceLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[resource]), resource);
                tempLabel.addMouseListener(new KeepListener());
                tempLabel.setSize(resourceWidth, resourceHeight);
                keepLabels.add(tempLabel);
            }
        }

        //Create the contents of the frame
        JPanel harborPanel = buildHarborPanel();
        JPanel cardPanel = buildKeepPanel();
        JPanel discardPanel = buildDiscardPanel();
        JPanel buttonPanel = buildButtonPanel();
        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.add(harborPanel, BorderLayout.NORTH);
        tempPanel.add(cardPanel, BorderLayout.CENTER);

        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(tempPanel, BorderLayout.NORTH);
        add(discardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        //Display the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        update();
    }

    /**
     * Returns the index of the type of resource that the player chose to trade
     * in.
     * @return the index of the type of resource that the player chose to trade
     * in
     */
    public int getDiscardedResource() {
        return discardedResource;
    }

    /**
     * Returns the number of resource cards the player chose to trade in.
     * @return the number of resource cards the player chose to trade in
     */
    public int getNumDiscardedResources() {
        return numDiscardedResources;
    }

    /**
     * Returns the index of the type of resource that the player chose to receive.
     * @return the index of the type of resource that the player chose to receive
     */
    public int getDesiredResource() {
        return desiredResource;
    }

    /*
     * Constructs and returns a JPanel that shows the harbors that the player
     * can access.
     * @return a JPanel that shows the harbors that the player can access
     */
    private JPanel buildHarborPanel() {
        JPanel harborPanel = new JPanel();
        harborPanel.setBorder(BorderFactory.createTitledBorder("Your Harbors"));
        for (String harbor : player.getHarbors()) {
            harborPanel.add(new JLabel(icons.getHarborIcon(harbor)));
        }
        return harborPanel;
    }

    /*
     * Constructs keepPane and returns a JPanel that contains it.
     * @return a JPanel containing keepPane
     */
    private JPanel buildKeepPanel() {
        keepPane = new JLayeredPane();
        if (player.getSumResourceCards() * resourceWidth > icons.getBoardIcon().getIconWidth()) {
            keepPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), resourceHeight));
        } else {
            keepPane.setPreferredSize(new Dimension(player.getSumResourceCards() * resourceWidth, resourceHeight));
        }
        JPanel cardPanel = new JPanel();
        cardPanel.add(keepPane);
        cardPanel.setBorder(BorderFactory.createTitledBorder("Your Resource Cards"));
        return cardPanel;
    }

    /*
     * Constructs discardPane and resourceComboBox, adds them to a JPanel, and
     * returns the JPanel.
     * @return a JPanel containing discardPane and resourceComboBox
     */
    private JPanel buildDiscardPanel() {
        //Construct discardPane
        discardPane = new JLayeredPane();
        discardPane.setPreferredSize(new Dimension((int) (resourceWidth * 1.5), resourceHeight));
        //Construct the JComboBox that holds each resource type
        ImageIcon[] resourceIcons = new ImageIcon[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceIcons.length; i++) {
            resourceIcons[i] = icons.getResourceIcon(GameController.RESOURCE_TYPES[i]);
        }
        resourceComboBox = new JComboBox<ImageIcon>(resourceIcons);
        resourceComboBox.setSelectedIndex(0);
        resourceComboBox.setMaximumRowCount(3);
        resourceComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        //Construct and add the renderer
        ResourceListRenderer renderer = new ResourceListRenderer();
        renderer.setPreferredSize(new Dimension(resourceWidth, resourceHeight));
        resourceComboBox.setRenderer(renderer);
        //Construct the rest of the panel and return it
        JPanel discardPanel = new JPanel(new GridLayout(1, 0));
        discardPanel.add(new JLabel("Trade", JLabel.CENTER));
        discardPanel.add(discardPane);
        discardPanel.add(new JLabel("for", JLabel.CENTER));
        discardPanel.add(resourceComboBox);
        discardPanel.add(new JLabel("?", JLabel.CENTER));
        return discardPanel;
    }

    /*
     * Constructs and returns a JPanel containing the "Cancel" and "Trade"
     * buttons.
     * @return a JPanel containing the "Cancel" and "Trade" buttons
     */
    private JPanel buildButtonPanel() {
        //Create the buttons
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ConfirmTradeListener());
        cancelButton.setActionCommand(cancelButton.getText());
        confirmTradeInButton = new JButton("Trade");
        confirmTradeInButton.addActionListener(new ConfirmTradeListener());
        confirmTradeInButton.setActionCommand(confirmTradeInButton.getText());
        confirmTradeInButton.setEnabled(false);
        //Add the buttons to a panel and return it
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmTradeInButton);
        return buttonPanel;
    }

    /*
     * Updates keepPane and discardPane to show the cards in keepLabels and
     * discardLabels, respectively.
     */
    private void update() {
        //Remove all the labels and re-add them to their panes in order
        keepPane.removeAll();
        discardPane.removeAll();
        //Add the keep labels to keepPane
        int offset, margin;
        if (keepLabels.size() * resourceWidth > icons.getBoardIcon().getIconWidth()) {
            offset = resourceWidth - ((keepLabels.size() * resourceWidth - icons.getBoardIcon().getIconWidth()) / (keepLabels.size() - 1));
            margin = 0;
        } else {
            offset = resourceWidth;
            margin = (icons.getBoardIcon().getIconWidth() - (keepLabels.size() * resourceWidth)) / 2;
        }
        for (int i = 0; i < keepLabels.size(); i++) {
            keepLabels.get(i).setLocation(offset * i + margin, 0);
            keepPane.add(keepLabels.get(i), new Integer(i));
        }
        //Add the discard labels to discardPane
        if (discardLabels.size() > 1) {
            offset = resourceWidth - ((discardLabels.size() * resourceWidth - discardPane.getWidth()) / (discardLabels.size() - 1));
            margin = 0;
        } else {
            offset = resourceWidth;
            margin = (discardPane.getWidth() - (discardLabels.size() * resourceWidth)) / 2;
        }
        for (int i = 0; i < discardLabels.size(); i++) {
            discardLabels.get(i).setLocation(offset * i + margin, 0);
            discardPane.add(discardLabels.get(i), new Integer(i));
        }

        revalidate();
        repaint();
    }

    /*
     * Moves the minimum amount of cards needed for the player to receive a
     * resource card of their choice. Only moves resource cards of the type
     * that was clicked by the player. This listener is added to every label in
     * keepPane/keepLabels.
     */
    private class KeepListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            ResourceLabel labelClicked = (ResourceLabel) e.getComponent();
            if (!discardLabels.isEmpty()) {
                //If the resource that was clicked on is not the same type as the resource(s) already selected for discard,
                //remove all the labels in discardLabels
                if (labelClicked.resourceIndex != discardLabels.get(0).resourceIndex) {
                    discardLabels.get(0).dispatchEvent(new MouseEvent(discardLabels.get(0), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 1, false));
                } else { //If they are the same resource type, then don't do anything because the minimum amount of resource cards has already been added
                    return;
                }
            }
            //Move the minimum amount of labels of the same resource type as the card selected from keepLabels to discardLabels
            int min = 4;
            if (player.getHarbors().contains(GameController.RESOURCE_TYPES[labelClicked.resourceIndex])) {
                min = 2;
            } else if (player.getHarbors().contains("Any")) {
                min = 3;
            }
            for (int i = 0; i < keepLabels.size(); i++) {
                if (discardLabels.size() == min) {
                    break;
                } else if (keepLabels.get(i).resourceIndex == labelClicked.resourceIndex) {
                    //Replace the label's current mouse listener with a new DiscardListener
                    for (MouseListener listener : keepLabels.get(i).getMouseListeners()) {
                        keepLabels.get(i).removeMouseListener(listener);
                    }
                    keepLabels.get(i).addMouseListener(new DiscardListener());
                    discardLabels.add(keepLabels.remove(i));
                    i--;
                }
            }
            if (discardLabels.size() < min) {
                confirmTradeInButton.setEnabled(false);
            } else {
                confirmTradeInButton.setEnabled(true);
            }
            update();
        }
    }

    /*
     * Moves every label in discardLabels to keepLabels. This listener is added
     * to every label in discardLabels.
     */
    private class DiscardListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            //Move all the labels in discardLabels to keepLabels
            boolean labelTransferred;
            while (!discardLabels.isEmpty()) {
                labelTransferred = false;
                for (MouseListener listener : discardLabels.get(0).getMouseListeners()) {
                    discardLabels.get(0).removeMouseListener(listener);
                }
                discardLabels.get(0).addMouseListener(new KeepListener());
                for (int i = 0; i < keepLabels.size(); i++) {
                    if (keepLabels.get(i).resourceIndex >= discardLabels.get(0).resourceIndex) {
                        keepLabels.add(i, discardLabels.remove(0));
                        labelTransferred = true;
                        break;
                    }
                }
                if (!labelTransferred) {
                    keepLabels.add(discardLabels.remove(0));
                }
            }
            confirmTradeInButton.setEnabled(false);
            update();
        }
    }

    /**
     * Disposes the frame if the "Cancel" button was clicked. If the "Trade"
     * button was clicked, the relevant data is collected and stored and the
     * trigger button is clicked. This listener is added to both the "Cancel"
     * and the "Trade" buttons
     */
    private class ConfirmTradeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                desiredResource = resourceComboBox.getSelectedIndex();
                discardedResource = discardLabels.get(0).resourceIndex;
                numDiscardedResources = discardLabels.size();
                if (desiredResource == discardedResource) {
                    JLabel topWarning = new JLabel("You have selected to receive the same type of resource that you are discarding.");
                    topWarning.setHorizontalAlignment(JLabel.CENTER);
                    topWarning.setVerticalAlignment(JLabel.CENTER);
                    JLabel bottomWarning = new JLabel("Are you sure you want to make this trade?");
                    bottomWarning.setHorizontalAlignment(JLabel.CENTER);
                    bottomWarning.setVerticalAlignment(JLabel.CENTER);
                    JPanel warning = new JPanel(new BorderLayout());
                    warning.add(topWarning, BorderLayout.NORTH);
                    warning.add(bottomWarning, BorderLayout.CENTER);
                    if (JOptionPane.showConfirmDialog(null, warning, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } //else
                triggerButton.doClick();
            }
        }
    }

    /*
     * Represents a resource card in either the keepPane or the discardPane.
     * Stores the card's resource type.
     */
    private class ResourceLabel extends JLabel {
        private int resourceIndex;

        public ResourceLabel(ImageIcon resourceIcon, int inResourceIndex) {
            super(resourceIcon);
            resourceIndex = inResourceIndex;
        }
    }

    /* Renders the icon for each option in resourceComboBox. */
    private class ResourceListRenderer extends JLabel implements ListCellRenderer<Object> {
        private ResourceListRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setPreferredSize(new Dimension(resourceWidth, resourceHeight + 7));
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setIcon((ImageIcon) value);
            return this;
        }
    }
}