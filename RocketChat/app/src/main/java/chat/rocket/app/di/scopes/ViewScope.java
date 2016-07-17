package chat.rocket.app.di.scopes;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio-biva on 21/09/15.
 */
@Scope
@Retention(RUNTIME)
public @interface ViewScope {
}
