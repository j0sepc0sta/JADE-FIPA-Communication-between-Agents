package factory;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    private ContainerController mainContainer, containerClients, containerProducts, containerResource;
    private AgentController clients, product, resource;

    public static void main(String[] args) throws StaleProxyException {
        new Main();
    }

    public Main() throws StaleProxyException {
        mainContainer = new MainContainer().getContainer();
        JadeContainer jadeContainer = new JadeContainer();

        containerResource = jadeContainer.getContainerResource();
        if (containerResource != null) {
            try {
                resource = containerResource.createNewAgent("ResourceAgent", "factory.ResourceAgent", null);
                resource.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        containerProducts = jadeContainer.getContainerProducts();
        if (containerProducts != null) {
            try {
                product = containerProducts.createNewAgent("ProductAgent", "factory.ProductAgent", null);
                product.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        containerClients = jadeContainer.getContainerClients();
        if (containerClients != null) {
            try {
                clients = containerClients.createNewAgent("ClientAgent", "factory.ClientAgent", null);
                clients.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

