package com.brasa.restaurant.security;import com.brasa.restaurant.enums.Enums.Perfil;public record TenantPrincipal(String userId,String restaurantId,String email,Perfil perfil){}
