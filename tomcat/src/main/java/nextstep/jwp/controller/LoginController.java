package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.QueryStringConverter;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.support.View;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private static final String COOKIE_SESSION_KEY = "JSESSIONID";

    private final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final IdGenerator idGenerator;

    public LoginController(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void service(final Request request, final Response response) {
        final LoginRequest loginRequest = convert(request.getContent());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());
        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                final Session session = makeSession(user);
                final HttpCookie responseCookie = makeCookie(session);
                response.header(HttpHeader.SET_COOKIE, responseCookie.parse())
                        .header(HttpHeader.LOCATION, View.INDEX.getValue())
                        .httpStatus(HttpStatus.FOUND);
                return;
            }
        }
        throw new UnauthorizedException();
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }

    private Session makeSession(final User user) {
        final Session session = new Session(idGenerator.generate());
        session.setAttribute("user", user);
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(session);
        return session;
    }

    private HttpCookie makeCookie(final Session session) {
        final HttpCookie responseCookie = HttpCookie.create();
        responseCookie.put(COOKIE_SESSION_KEY, session.getId());
        return responseCookie;
    }
}
