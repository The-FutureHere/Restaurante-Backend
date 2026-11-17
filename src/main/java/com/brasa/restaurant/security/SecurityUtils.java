package com.brasa.restaurant.security;

import com.brasa.restaurant.exception.ApiExceptions;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static TenantPrincipal current() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (p instanceof TenantPrincipal t) {
            return t;
        }
        throw new ApiExceptions.Forbidden("Usuário não autenticado");
    }
}