package com.gamelisto.usuarios_service.domain.usuario;

public final class Avatar {
    
    private final String url;

    private Avatar(String url) {
        if (url != null && !url.trim().isEmpty()) {
            String trimmedUrl = url.trim();
            if (trimmedUrl.length() > 500) {
                throw new IllegalArgumentException("La URL del avatar no puede exceder 500 caracteres");
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
