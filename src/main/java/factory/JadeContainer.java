package factory;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class JadeContainer {
	private ContainerController containerResource;
	private ContainerController containerProducts;
	private ContainerController containerClients;

	public JadeContainer() {
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);

		// Create the "Resource" container
		ProfileImpl profileResource = new ProfileImpl(false);
		profileResource.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		profileResource.setParameter(ProfileImpl.MAIN_PORT, "1099");
		profileResource.setParameter(ProfileImpl.CONTAINER_NAME, "Resource Container");
		containerResource = rt.createAgentContainer(profileResource);

		// Create the "Products" container
		ProfileImpl profileProducts = new ProfileImpl(false);
		profileProducts.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		profileProducts.setParameter(ProfileImpl.MAIN_PORT, "1099");
		profileProducts.setParameter(ProfileImpl.CONTAINER_NAME, "Product Container");
		containerProducts = rt.createAgentContainer(profileProducts);

		// Create the "Client" container
		ProfileImpl profileClients = new ProfileImpl(false);
		profileClients.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		profileClients.setParameter(ProfileImpl.MAIN_PORT, "1099");
		profileClients.setParameter(ProfileImpl.CONTAINER_NAME, "Client Container");
		containerClients = rt.createAgentContainer(profileClients);
	}

	public ContainerController getContainerResource() {
		return containerResource;
	}

	public ContainerController getContainerProducts() {
		return containerProducts;
	}

	public ContainerController getContainerClients() {
		return containerClients;
	}
}
