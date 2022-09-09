package nextstep.jwp.controller;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginControllerTest {

    private final Controller controller = new LoginController(() -> "1");

    @Test
    void account값과_password값이_일치하면_index로_리다이렉트한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gugu&password=password");
        final Response response = new Response();

        final Response expected = new Response().header(HttpHeader.LOCATION, "/index.html")
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
        assertThatThrownBy(() -> controller.service(request, new Response()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void paasword값이_일치하지_않으면_UNAUTHORIZED_예외를_발생한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gugu&password=password1");

        // when, then
        assertThatThrownBy(() -> controller.service(request, new Response()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 요청헤더의_쿠키에_세션아이디가_없다면_응답에_세션아이디를_포함시킨다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(POST, "/login", null);
        final Request request = new Request(requestInfo, new Headers(), "account=gugu&password=password");
        final Response response = new Response();

        final Response expected = new Response().header(HttpHeader.LOCATION, "/index.html")
                .header(HttpHeader.SET_COOKIE, "JSESSIONID=1")
                .httpStatus(HttpStatus.FOUND);

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
