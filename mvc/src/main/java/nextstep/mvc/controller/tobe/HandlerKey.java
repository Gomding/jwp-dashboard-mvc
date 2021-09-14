package nextstep.mvc.controller.tobe;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.support.RequestMethod;

import java.util.Objects;

public class HandlerKey {

    private final String url;
    private final RequestMethod requestMethod;

    private HandlerKey(String url, RequestMethod requestMethod) {
        this.url = url;
        this.requestMethod = requestMethod;
    }

    public static HandlerKey of(HttpServletRequest request) {
        return new HandlerKey(request.getRequestURI(), RequestMethod.valueOf(request.getMethod()));
    }

    public static HandlerKey of(RequestMapping requestMapping) {
        return new HandlerKey(requestMapping.value(), requestMapping.method()[0]);
    }

    @Override
    public String toString() {
        return "HandlerKey{" +
                "url='" + url + '\'' +
                ", requestMethod=" + requestMethod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandlerKey)) return false;
        HandlerKey that = (HandlerKey) o;
        return Objects.equals(url, that.url) && requestMethod == that.requestMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, requestMethod);
    }
}
