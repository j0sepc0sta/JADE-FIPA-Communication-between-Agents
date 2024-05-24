// Import necessary packages
package factory;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductAgent extends Agent {
    private JFrame frame;
    private JTextField clientField, productField, quantityField, deadlineField;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private List<String[]> data;
    String clientname;
    String product;
    int quantity;
    int deadline;
    protected void setup() {
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ProductAgent");
        sd.setName(getLocalName()+"ProductAgent");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] existingAgents = DFService.search(this, dfd);
            if (existingAgents != null && existingAgents.length > 0) {
                System.out.println("Product Agent already registered.");
            } else {
                DFService.register(this, dfd);
                System.out.println("Product Agent registered successfully.");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            showGui();
            loadData();
        });
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Received message: " + msg.getContent() + ", performative: " + msg.getPerformative());
                    if (msg.getPerformative() == ACLMessage.CONFIRM && msg.getContent().equals("Client exists")) {
                        product = productField.getText();
                        quantity = Integer.parseInt(quantityField.getText());
                        deadline = Integer.parseInt(deadlineField.getText());
                        if (!product.equals("A") && !product.equals("B")) {
                            JOptionPane.showMessageDialog(null, "Invalid Product", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(null, getAID().getLocalName() + ": " + clientname + " request " + quantity + " quantity of product " + product + ". Deadline " + deadline + " days");
                        ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                        String content = "Produce " + quantity + " units of " + product + " for " + clientname + " with deadline " + deadline + " days.";
                        acl.setContent(content);
                        acl.addReceiver(new AID("ResourceAgent", AID.ISLOCALNAME));
                        send(acl);
                        ACLMessage reply = blockingReceive();
                        if (reply != null) {
                            String[] rowData = {getAID().getName(), getLocalName(), clientname, product, String.valueOf(quantity), String.valueOf(deadline), reply.getContent()};
                            model.addRow(rowData);
                            saveData();
                            clearFields();
                        } else {
                            block();
                        }
                    } else if (msg.getPerformative() == ACLMessage.FAILURE && msg.getContent().equals("Client doesn't exist")) {
                        JOptionPane.showMessageDialog(null, "Invalid Client", "Invalid Message Received from Client Agent", JOptionPane.ERROR_MESSAGE);
                        clientField.setText("");
                    }
                } else {
                    block();
                }
            }
        });
    }
    public ProductAgent() {
        frame = new JFrame("Product Agent");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                addBehaviour(new OneShotBehaviour() {
                    public void action() {
                        myAgent.doDelete();
                    }
                });
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2,0,10));
        inputPanel.add(new JLabel("Client Name:"));
        clientField = new JTextField(50);
        clientField.setPreferredSize(new Dimension(500, 20));
        inputPanel.add(clientField);
        inputPanel.add(new JLabel("Product:"));
        productField = new JTextField(50);
        inputPanel.add(productField);
        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(50);
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Deadline:"));
        deadlineField = new JTextField(50);
        inputPanel.add(deadlineField);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            clientname = clientField.getText();
            ACLMessage aclname = new ACLMessage(ACLMessage.REQUEST);
            aclname.setContent(clientname); // Envie o nome do cliente como conteúdo da mensagem
            aclname.addReceiver(new AID("ClientAgent", AID.ISLOCALNAME)); // Adicione o receptor como ClientAgent
            send(aclname);
        });
        inputPanel.add(addButton);
        panel.add(inputPanel, BorderLayout.NORTH);
        model = new DefaultTableModel(new String[]{"Repository","Agent", "Client Name","Product", "Quantity", "Deadline","Status"}, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton clearAllButton = new JButton("Clear all");
        clearAllButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear all rows from the table?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                model.setRowCount(0);
                saveData();
            }
        });
        buttonPanel.add(clearAllButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
    }
    private void loadData() {
        data = new ArrayList<>();
        File file = new File("products.csv");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    data.add(values);
                    model.addRow(values);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveData() {
        File file = new File("products.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        writer.write(value.toString());
                    }
                    if (j < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        frame.setLocation(centerX, centerY - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                JOptionPane.showMessageDialog(frame, "Products Agent Control Panel Terminated");
            }
        });
    }
    private void clearFields() {
        clientField.setText("");
        productField.setText("");
        quantityField.setText("");
        deadlineField.setText("");
    }

}
