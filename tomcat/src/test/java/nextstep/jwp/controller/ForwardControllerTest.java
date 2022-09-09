package nextstep.jwp.controller;


import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class ForwardControllerTest {

    private final Controller controller = new ForwardController();

    @Test
    void uri와_동일한_명의_html_파일을_읽어_OK_응답을_반환한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/login");
        final Request request = new Request(requestInfo, new Headers(), null);
        final Response response = new Response();

        final Response expected = new Response().header(HttpHeader.CONTENT_LENGTH, "3797")
                .header(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expected);
    }
}
