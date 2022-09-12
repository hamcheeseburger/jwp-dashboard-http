package nextstep.jwp.controller;

import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import org.apache.coyote.Headers;
import nextstep.jwp.http.MockOutputStream;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.RequestInfo;
import org.apache.coyote.support.Response;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {

    private final Controller controller = new LoginController(() -> "1");

    @AfterEach
    void setDown() {
        final SessionManager sessionManager = SessionManager.get();
        final Session session = sessionManager.findSession("1");
        sessionManager.remove(session);
    }

    @Test
    void account값과_password값이_일치하면_index로_리다이렉트한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gugu&password=password");
        final Response response = new Response(new MockOutputStream());

        final Response expected = new Response(new MockOutputStream()).header(HttpHeader.LOCATION, "/index.html")
                .header(HttpHeader.SET_COOKIE, "JSESSIONID=1")
                .httpStatus(HttpStatus.FOUND);

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void account값이_일치하지_않으면_UNAUTHORIZED_예외를_발생한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gonggong&password=password");

        // when, then
        assertThatThrownBy(() -> controller.service(request, new Response(new MockOutputStream())))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void paasword값이_일치하지_않으면_UNAUTHORIZED_예외를_발생한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gugu&password=password1");

        // when, then
        assertThatThrownBy(() -> controller.service(request, new Response(new MockOutputStream())))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 요청헤더의_쿠키에_세션아이디가_없다면_응답에_세션아이디를_포함시킨다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gugu&password=password");
        final Response response = new Response(new MockOutputStream());

        final Response expected = new Response(new MockOutputStream()).header(HttpHeader.LOCATION, "/index.html")
                .header(HttpHeader.SET_COOKIE, "JSESSIONID=1")
                .httpStatus(HttpStatus.FOUND);

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
