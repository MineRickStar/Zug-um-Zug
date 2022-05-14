package application;

public class ClientSettings {

	private static final ClientSettings instance = new ClientSettings();

	public static ClientSettings getInstance() {
		return ClientSettings.instance;
	}

	private ClientSettings() {}

}
