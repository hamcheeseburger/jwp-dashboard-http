package nextstep.jwp.support;

public enum View {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    UNAUTHORIZED("/401.html"),
    BAD_REQUEST("/400.html"),
    NOT_FOUND("/404.html"),
    INTERNAL_SERVER_ERROR("/500.html");

    private final String value;

    View(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
