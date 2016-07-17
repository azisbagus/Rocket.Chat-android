package chat.rocket.rc.enumerations;

public enum LoginService {
    GOOGLE("clientId"),
    GITLAB("clientId"),
    GITHUB("clientId"),
    TWITTER("consumerKey"),
    METEOR_DEVELOPER("meteor-developer"),
    FACEBOOK("appId"),
    LINKEDIN("clientId");
    public String identifier;

    LoginService(String identifier) {
        this.identifier = identifier;
    }

}

