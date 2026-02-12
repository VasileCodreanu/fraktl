package com.fraktl.common.context;

import jakarta.servlet.http.HttpServletRequest;

public record RequestContext(
    String userId,
    String ip,
    String userAgent,
    String referrer
) {

    public static RequestContext from(HttpServletRequest request) {
        return new RequestContext(
            request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : "Some-user",
            request.getRemoteAddr(),//TODO find location tehn hash(ipHash)/or some similar
            request.getHeader("User-Agent"),
            request.getHeader("Referer")
        );
    }

}

