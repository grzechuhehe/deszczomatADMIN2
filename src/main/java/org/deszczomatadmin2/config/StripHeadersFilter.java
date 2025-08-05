package org.deszczomatadmin2.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StripHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HeaderCleaner wrapper = new HeaderCleaner(response);

        chain.doFilter(req, wrapper);

        // Usuwanie nagłówków po przetworzeniu
        wrapper.removeHeader("Vary");
        wrapper.removeHeader("X-Frame-Options");
        wrapper.removeHeader("X-Content-Type-Options");
        wrapper.removeHeader("X-XSS-Protection");
        wrapper.removeHeader("Cache-Control");
        wrapper.removeHeader("Pragma");
        wrapper.removeHeader("Expires");
    }

    static class HeaderCleaner extends HttpServletResponseWrapper {

        public HeaderCleaner(HttpServletResponse response) {
            super(response);
        }

        public void removeHeader(String name) {
            // Nadpisanie pustą wartością usuwa nagłówek
            super.setHeader(name, null);
        }
    }
}
