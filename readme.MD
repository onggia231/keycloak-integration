# Start Project
- Start docker KeyCloak, Mongo DB

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
- Create realm ( tao ten realm - luu y khi tao xong nhung lan dang nhap sau chon real can su dung)
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
- Real role:
    - Add role nho (VIEW_POST) vao role lon(USER): Vao role USER -> Action -> Add associated roles -> Filter by realm roles -> Chon role nho(VIEW_POST)
    -> Trong mục Realm roles (USER) -> muc Associated roles chua VIEW_POST

## Tao Role tren web KetCloak (http://localhost:8180/)
1. Chon Realm roles -> Create role (ADMIN,USER, STAFF) -> SAVE
2. Chon Users (Vd: chon user la james) -> Role mapping -> Assign role -> Filter by realm roles -> Tich Role -> Assign
   -> Kiem tra xem da gan thanh cong hay chua -> Vao postman call api exchange Token (username, pass muon check) -> Lay token roi vao https://jwt.io/ de giai ma xem da co Role chua (xem: realm_access -> roles)
3. Trong KeyCloak no phan chia Role va role nho hon (Giong kieu Role, Permission)
   -> Tao role nho hon -> Create role (vd: VIEW_POST) de assign no cho Role USER -> Realm roles (USER) (Vao Role lon de add role nho) -> Combobox chon Add associated roles -> Filter by realm roles (Chon: VIEW_POST) 
4. Group (Tao nhieu Role trong do) 
   -> Chon Group -> Create a group(Test Group) -> Chon Test Group vua tao -> Role mapping -> Assign role -> Filter by realm roles -> Vd: Chon role USER, STAFF
   -> Assign cai Group cho 1 user -> Chon Users (johndoe) -> Group -> Join Group -> Chon Test Group (user chua role lon va role nho luon)
   -> Group nen biet con chu yeu dung Realm roles
5. Tao Role mac dinh (Khi 1 user duoc tao se co Role do) 
   -> Chon Realm settings -> User registration -> Default roles -> Assign role -> Filter by realm roles (USER - phan quyen mac dinh la USER cho cac user duoc tao moi)






- Luu y:
 - Duong dan api (discovery Endpoint): http://localhost:8180/realms/devteria/.well-known/openid-configuration (realms/devteria: realms theo ten da dat config tren web KeyCloak)
 - Duong dan api (exchange Token): Postman phan Body khai: client_id(devteria_app) , client_secret(trong Clients), username, password (user, pass da  dang ky tren web KeyCloak), scope (openid)
 - Duong dan https://www.keycloak.org/docs-api/latest/rest-api/openapi.yaml: de xem api tuong tac voi KeyCloak va https://editor.swagger.io/: de giai ma file yaml kia
 - Duong dan api (exchange Client Token): Clients -> devteria_app -> Service accounts roles (Tich vao cai nay) (Nhu the moi lay duoc token cua client - no dai dien cho client chu ko phai user): Thong tin body gửi request grant_type (client_credentials), client_id(devteria_app), client_secret,scope
 - Duong dan api (get Users): Dung token exchange Client Token de goi api (Neu loi 403 xem config Service accounts roles)
 - Tang thoi gian token (mac dinh 5 phut): Clients -> Advanced -> Access Token Lifespan -> Save
 - Bao mat client_ai, client_secret

- 8 Phân quyền Authorization với Spring boot 3
 -