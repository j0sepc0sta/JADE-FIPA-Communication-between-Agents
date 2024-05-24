package factory;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.ContainerController;

public class MainContainer {

	private ContainerController mainContainer;
	public MainContainer() {
		Runtime rt = Runtime.instance();
		//Properties p = new ExtendedProperties();
		//p.setProperty("gui", "true");
		ProfileImpl profile = new ProfileImpl();
		profile.setParameter(Profile.MAIN_HOST,"localhost");
		profile.setParameter(Profile.GUI, "true"); // Setting parameter for GUI
		mainContainer = rt.createMainContainer(profile);
	}

	public ContainerController getContainer() {return mainContainer; }
}
