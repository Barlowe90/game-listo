package com.gamelisto.usuarios_service.domain.usuario;

public final class Avatar {
    
    private final String url;
    private final static int MAX_URL_LENGTH = 500;

    private Avatar(String url) {
        if (url != null && !url.trim().isEmpty()) {
            String trimmedUrl = url.trim();
            if (trimmedUrl.length() > MAX_URL_LENGTH) {
                throw new IllegalArgumentException("La URL del avatar no puede exceder " + MAX_URL_LENGTH + " caracteres");
            }
            this.url = trimmedUrl;
        } else {
            this.url = null;
        }
    }

    public static Avatar of(String url) {
        return new Avatar(url);
    }

    public static Avatar empty() {
        return new Avatar(null);
    }

    public String url() {
        return url;
    }

    public boolean isEmpty() {
        return url == null;
    }

    @Override
    public String toString() {
        return url != null ? url : "[SIN AVATAR]";
    }
}
