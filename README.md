## ToDo App
This is an educational application which can create Users, ToDo Lists for them and Tasks inside of ToDo Lists.

I used Spring Boot to develop it, PostgreSQL to store data and Spring Sequrity to authorize and authenticate users.

After first lounch, please delete file named ```schema.sql``` and set ```spring.sql.init.mode=always``` in the ```application.properties``` file. Than restart the app to fill data base with starter data.

I created Posman Collection of requests to test this app. It can be find by the link: [Postman Collection](https://github.com/matsevkoVM/PostmanCollections) 

There are three predefined users in the DB with roles ADMIN and USER.

| Login         | Password | Role  |
|---------------|:--------:|:-----:|
| mike@mail.com |   1111   | ADMIN |
| nick@mail.com |   2222   | USER  |
| nora@mail.com |   3333   | USER  |
