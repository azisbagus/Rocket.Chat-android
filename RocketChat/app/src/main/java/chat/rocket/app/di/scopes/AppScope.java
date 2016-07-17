package chat.rocket.app.di.scopes;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio on 16/12/15.
 */
@Scope
@Retention(RUNTIME)
public @interface AppScope {
}
