# Guide de Configuration Keycloak pour Spring Boot & React

## Configuration Client

### Paramètres de Base
```json
{
  "clientId": "my-app",
  "enabled": true,
  "publicClient": true,
  "redirectUris": ["http://localhost:3000/*"],
  "webOrigins": ["http://localhost:3000"]
}
```
*Configuration standard pour une application React.*

### Mappers Essentiels
1. **Username Mapper**
    - Type: User Property
    - Property: username
    - Token Claim Name: preferred_username

2. **Email Mapper**
    - Type: User Property
    - Property: email
    - Token Claim Name: email

3. **Groups Mapper**
    - Type: Group Membership
    - Token Claim Name: groups
    - Full Path: ON

4. **Roles Mapper**
    - Type: User Realm Role
    - Token Claim Name: roles
    - Multivalued: ON

## Rôles Standard

### Rôles Système
- `ADMIN`: Accès complet
- `USER`: Accès de base
- `MANAGER`: Gestion intermédiaire

### Rôles Fonctionnels
- `PRODUCT_MANAGER`: Gestion des produits
- `ORDER_MANAGER`: Gestion des commandes
- `CUSTOMER_SERVICE`: Service client

## Groupes Recommandés

### Structure Standard
```
/Administrators
/Managers
  /ProductManagers
  /OrderManagers
/Users
  /CustomerService
  /StandardUsers
```

## Configuration Spring Boot

### application.yml
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/{realm-name}
          jwk-set-uri: ${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/certs

keycloak:
  realm: {realm-name}
  auth-server-url: http://localhost:8080
  resource: {client-id}
  public-client: true
```

### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .build();
    }
}
```

## Configuration React

### keycloak-config.js
```javascript
import Keycloak from 'keycloak-js';

const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: '{realm-name}',
  clientId: '{client-id}'
};

export const keycloak = new Keycloak(keycloakConfig);
```

## Bonnes Pratiques Sécurité

### Tokens
- Access Token: 5 minutes
- Refresh Token: 30 minutes
- SSO Session: 8 heures

### Politique de Mot de Passe
```
length(8) and 
digits(1) and 
upperCase(1) and 
specialChars(1)
```

### Authentification à Deux Facteurs
- OTP recommandé pour les rôles admin
- Configuration par groupe possible

## Configurations Avancées

### CORS
```json
{
  "allowedOrigins": ["*"],
  "allowedMethods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  "allowedHeaders": ["*"]
}
```

### SSL/TLS
```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: your-password
    keyStoreType: PKCS12
    keyAlias: your-alias
```

### Audit Logs
```yaml
logging:
  level:
    org.keycloak: DEBUG
    org.springframework.security: DEBUG
```

## Gestion des Sessions

### Paramètres Recommandés
- SSO Session Idle: 30 minutes
- SSO Session Max: 10 heures
- Client Session Idle: 15 minutes
- Client Session Max: 8 heures

## Intégration CI/CD

### Configuration Automatisée
```bash
#!/bin/bash
./kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user admin
  
./kcadm.sh create realms \
  -s realm={realm-name} \
  -s enabled=true
```

# Guide Complet de Configuration Keycloak

## 1. Création du Realm

### Via Interface Admin
```json
{
  "realm": "my-realm",
  "enabled": true,
  "displayName": "Mon Application",
  "displayNameHtml": "<div class=\"kc-logo-text\">Mon Application</div>",
  "bruteForceProtected": true,
  "permanentLockout": false,
  "maxFailureWaitSeconds": 900,
  "minimumQuickLoginWaitSeconds": 60,
  "waitIncrementSeconds": 60,
  "quickLoginCheckMilliSeconds": 1000,
  "maxDeltaTimeSeconds": 43200,
  "failureFactor": 3
}
```

### Via CLI
```bash
./kcadm.sh create realms -s realm=my-realm -s enabled=true
```

## 2. Configuration des Clients

### Client Public (Front-end)
```json
{
  "clientId": "frontend-client",
  "enabled": true,
  "publicClient": true,
  "directAccessGrantsEnabled": true,
  "standardFlowEnabled": true,
  "implicitFlowEnabled": false,
  "serviceAccountsEnabled": false,
  "redirectUris": [
    "http://localhost:3000/*",
    "http://localhost:8080/*"
  ],
  "webOrigins": [
    "http://localhost:3000",
    "http://localhost:8080"
  ]
}
```

### Client Confidentiel (Back-end)
```json
{
  "clientId": "backend-client",
  "enabled": true,
  "publicClient": false,
  "bearerOnly": true,
  "serviceAccountsEnabled": true,
  "authorizationServicesEnabled": true
}
```

## 3. Configuration des Mappers

### Mappers Essentiels pour le Client
```json
[
  {
    "name": "username",
    "protocol": "openid-connect",
    "protocolMapper": "oidc-usermodel-property-mapper",
    "config": {
      "userinfo.token.claim": "true",
      "user.attribute": "username",
      "id.token.claim": "true",
      "access.token.claim": "true",
      "claim.name": "preferred_username"
    }
  },
  {
    "name": "roles",
    "protocol": "openid-connect",
    "protocolMapper": "oidc-usermodel-realm-role-mapper",
    "config": {
      "multivalued": "true",
      "userinfo.token.claim": "true",
      "id.token.claim": "true",
      "access.token.claim": "true",
      "claim.name": "roles"
    }
  },
  {
    "name": "groups",
    "protocol": "openid-connect",
    "protocolMapper": "oidc-group-membership-mapper",
    "config": {
      "full.path": "true",
      "id.token.claim": "true",
      "access.token.claim": "true",
      "userinfo.token.claim": "true",
      "claim.name": "groups"
    }
  }
]
```

## 4. Configuration des Rôles

### Rôles Realm
```json
[
  {
    "name": "ADMIN",
    "description": "Administrateur système",
    "composite": true,
    "composites": {
      "realm": ["USER", "MANAGER"]
    }
  },
  {
    "name": "MANAGER",
    "description": "Gestionnaire",
    "composite": true,
    "composites": {
      "realm": ["USER"]
    }
  },
  {
    "name": "USER",
    "description": "Utilisateur standard",
    "composite": false
  }
]
```

### Rôles Client
```json
[
  {
    "name": "manage-account",
    "description": "Gérer son compte",
    "composite": false,
    "clientRole": true,
    "containerId": "account"
  }
]
```

## 5. Configuration des Groupes

### Structure Hiérarchique
```json
[
  {
    "name": "Administrators",
    "path": "/Administrators",
    "realmRoles": ["ADMIN"],
    "subGroups": []
  },
  {
    "name": "Managers",
    "path": "/Managers",
    "realmRoles": ["MANAGER"],
    "subGroups": [
      {
        "name": "ProductManagers",
        "path": "/Managers/ProductManagers",
        "realmRoles": ["PRODUCT_MANAGER"]
      }
    ]
  }
]
```

## 6. Configuration des Utilisateurs

### Création d'Utilisateur
```json
{
  "username": "admin",
  "enabled": true,
  "emailVerified": true,
  "firstName": "Admin",
  "lastName": "System",
  "email": "admin@system.com",
  "credentials": [
    {
      "type": "password",
      "value": "initial_password",
      "temporary": true
    }
  ],
  "groups": ["/Administrators"],
  "realmRoles": ["ADMIN"],
  "attributes": {
    "locale": ["fr"],
    "phone": ["123456789"]
  }
}
```

## 7. Configuration des Sessions

### Paramètres de Session Realm
```json
{
  "ssoSessionMaxLifespan": 36000,
  "ssoSessionIdleTimeout": 1800,
  "offlineSessionMaxLifespan": 5184000,
  "offlineSessionIdleTimeout": 2592000,
  "accessTokenLifespan": 300,
  "accessTokenLifespanForImplicitFlow": 900,
  "actionTokenGeneratedByUserLifespan": 300,
  "actionTokenGeneratedByAdminLifespan": 43200
}
```

### Paramètres de Session Client
```json
{
  "clientSessionMaxLifespan": 28800,
  "clientSessionIdleTimeout": 1800,
  "clientOfflineSessionMaxLifespan": 2592000,
  "clientOfflineSessionIdleTimeout": 1296000
}
```

## 8. Configuration de l'Authentification

### Flow d'Authentification
```json
{
  "alias": "browser-with-2fa",
  "description": "Browser authentication with 2FA",
  "providerId": "basic-flow",
  "topLevel": true,
  "builtIn": false,
  "authenticationExecutions": [
    {
      "authenticator": "auth-cookie",
      "requirement": "ALTERNATIVE",
      "priority": 10
    },
    {
      "authenticator": "auth-username-password-form",
      "requirement": "REQUIRED",
      "priority": 20
    },
    {
      "authenticator": "auth-otp-form",
      "requirement": "REQUIRED",
      "priority": 30
    }
  ]
}
```

## 9. Configuration des Événements

### Audit et Logging
```json
{
  "eventsEnabled": true,
  "eventsListeners": ["jboss-logging", "email"],
  "enabledEventTypes": [
    "LOGIN", 
    "LOGIN_ERROR", 
    "LOGOUT", 
    "UPDATE_PROFILE"
  ],
  "adminEventsEnabled": true,
  "adminEventsDetailsEnabled": true
}
```

## 10. Intégration CI/CD

### Script d'Automatisation
```bash
#!/bin/bash

# Configuration des variables
KEYCLOAK_URL="http://localhost:8080"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="password"
REALM_NAME="my-realm"

# Connexion
./kcadm.sh config credentials \
  --server $KEYCLOAK_URL \
  --realm master \
  --user $ADMIN_USERNAME \
  --password $ADMIN_PASSWORD

# Création du realm
./kcadm.sh create realms \
  -s realm=$REALM_NAME \
  -s enabled=true

# Création des clients
./kcadm.sh create clients \
  -r $REALM_NAME \
  -s clientId="frontend-client" \
  -s publicClient=true \
  -s 'redirectUris=["http://localhost:3000/*"]'

# Création des rôles
./kcadm.sh create roles \
  -r $REALM_NAME \
  -s name=ADMIN \
  -s description="Administrateur système"

# Création des groupes
./kcadm.sh create groups \
  -r $REALM_NAME \
  -s name=Administrators

# Création d'utilisateur
./kcadm.sh create users \
  -r $REALM_NAME \
  -s username=admin \
  -s enabled=true \
  -s email=admin@system.com
```

## 11. Configuration de la Sécurité

### Politique de Mot de Passe
```json
{
  "passwordPolicy": "length(8) and digits(1) and upperCase(1) and specialChars(1) and notUsername()",
  "bruteForceProtected": true,
  "permanentLockout": false,
  "maxFailureWaitSeconds": 900,
  "minimumQuickLoginWaitSeconds": 60,
  "waitIncrementSeconds": 60,
  "quickLoginCheckMilliSeconds": 1000,
  "maxDeltaTimeSeconds": 43200,
  "failureFactor": 3
}
```

### Headers de Sécurité
```json
{
  "browserSecurityHeaders": {
    "contentSecurityPolicyReportOnly": "",
    "xContentTypeOptions": "nosniff",
    "xRobotsTag": "none",
    "xFrameOptions": "SAMEORIGIN",
    "contentSecurityPolicy": "frame-src 'self'; frame-ancestors 'self'; object-src 'none';",
    "xXSSProtection": "1; mode=block",
    "strictTransportSecurity": "max-age=31536000; includeSubDomains"
  }
}
```

## 12. Monitoring et Maintenance

### Métriques Prometheus
```yaml
metrics:
  enabled: true
  endpoint: /metrics
```

### Healthcheck
```json
{
  "healthcheck": {
    "enabled": true,
    "endpoint": "/health",
    "responseTimeout": 2000
  }
}
```