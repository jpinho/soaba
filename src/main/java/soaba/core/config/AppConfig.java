package soaba.core.config;

public class AppConfig {
    private static final String APP_CONFIG_FILE = "soaba.config";
    private static AppConfig instance;

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        if (instance != null)
            return instance;
        return instance = new AppConfig().init();
    }

    public AppConfig init() {
        //TODO: read config from json file
        return this;
    }
}
