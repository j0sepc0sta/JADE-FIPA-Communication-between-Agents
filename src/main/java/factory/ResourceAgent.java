package factory;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.*;
import javax.swing.*;

public class ResourceAgent extends Agent {
    private JFrame frame;
    String Clientname;
    String Product;
    int Quantity;
    int Deadline;
    private static final long serialVersionUID = 1L;
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ResourceAgent");
        sd.setName(getLocalName()+"ResourceAgent");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] existingAgents = DFService.search(this, dfd);
            if (existingAgents != null && existingAgents.length > 0) {
                System.out.println("Resource Agent already registered.");
            } else {
                DFService.register(this, dfd);
                System.out.println("Resource Agent registered successfully.");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    int option = JOptionPane.showConfirmDialog(frame, "Accept the request?", "Resource Agent Confirmation", JOptionPane.YES_NO_OPTION);
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.addReceiver(msg.getSender());
                    if (option == JOptionPane.YES_OPTION) {
                        reply.setPerformative(ACLMessage.CONFIRM);
                        reply.setContent("Accepted");
                    } else {
                        reply.setPerformative(ACLMessage.FAILURE);
                        reply.setContent("Refused");
                    }
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}