package chat.rocket.app.db.collections;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.rc.enumerations.LoginService;
import chat.rocket.app.utils.Util;

/**
 * Created by julio on 24/11/15.
 */
public class LoginServiceConfiguration {

    private static final String collectionName = "meteor_accounts_loginServiceConfiguration";

    public static String query(LoginService service) {
        List<CollectionDAO> daos = CollectionDAO.query(collectionName);
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        String value = null;
        for (CollectionDAO dao : daos) {
            Map<String, String> map = Util.GSON.fromJson(dao.getNewValuesJson(), mapType);
            if (service.name().toLowerCase().equals(map.get("service"))) {
                value = map.get(service.identifier);
                break;
            }
        }

        return value;
    }
}
