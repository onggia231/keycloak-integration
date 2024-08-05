# KeyCloak integration
- Tich hop Profile Service voi KeyCloak
- Xay dung Identity va Profile co the verify email, thay doi pass
- Config Security voi KeyCloak
- Config KeyCloak login voi ReactJs Web-app
- Configure SSO (Day la tinh nang khi vao gmail, dang nhap tinh nang khac cua Google thi no tu dang nhap khong can dang nhap lai)
- Social Login with Google, Facebook...
- Config KeyCloak ready for Production
   +) Security
   +) Database
   +) Monitoring

## Prerequisites

### Install KeyCloak on Docker

`docker pull quay.io/keycloak/keycloak:25.0.0`

Run keyCloak on port 8180

`docker run -d --name keycloak-25.0.0 -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:25.0.0 start-dev`

## Tao system tren web KetCloak (http://localhost:8180/)
- Create realm ( tao ten realm)
- Chon realm
    - Client: 
      - Create client 
        - General settings: (Client type: OpenID Connect, Clien ID: devteria_app, Name: Devteria Webapp) -> Next
        - Capability config: Client authentication: On, Authentication flow: Standard flow, Direct access grants, Implicit flow -> On
        - Login settings: 
                          - Root URL: http://localhost:3000
                          - Home URL: http://localhost:3000
                          - Valid redirect URIs: http://localhost:3000/authenticate
                          - Valid post logout redirect URIs: http://localhost:3000/logout
                          - Web origins: *
      -> Tao ra 1 client id, 1 client secret (trong tab Credentials) de phuc vu send 1 token
        - Service accounts roles (Duoc tao ra khi tich muc Service accounts roles) -> Assign role -> Tim den manage-users (Tich vao) -> Assign (Co role manage-users moi tuong tac duoc api admin)
    - User:
      - Create user: Username: baochau, email:ngobaochau0103@gmail.com, Firstname: Chau, Lastname: Bao
        - Credentials( Create password - 12345678): Temporary: off (Neu de on thi ban dau se bat tao password moi)


- Luu y:
 - Duong dan api (discovery Endpoint): http://localhost:8180/realms/devteria/.well-known/openid-configuration (realms/devteria: realms theo ten da dat config tren web KeyCloak)
 - Duong dan api (exchange Token): Postman phan Body khai: client_id, client_secret, username, password (user, pass da  dang ky tren web KeyCloak)
 - Duong dan https://www.keycloak.org/docs-api/latest/rest-api/openapi.yaml: de xem api tuong tac voi KeyCloak va https://editor.swagger.io/: de giai ma file yaml kia
 - Duong dan api (exchange Client Token): Clients -> devteria_app -> Service accounts roles (Tich vao cai nay) (Nhu the moi lay duoc token cua client - no dai dien cho client chu ko phai user)
 - Duong dan api (get Users): Dung token exchange Client Token de goi api (Neu loi 403 xem config Service accounts roles)
 - Tang thoi gian token (mac dinh 5 phut): Clients -> Advanced -> Access Token Lifespan -> Save
 - Bao mat client_ai, client_secret

      