## ToDo App
This is an educational application which can create Users, ToDo Lists for them and Tasks inside of ToDo Lists.

I used Spring Boot to develop it, PostgreSQL to store data and Spring Sequrity to authorize and authenticate users.

After first lounch, please delete file named ```schema.sql``` and set ```spring.sql.init.mode=always``` in the ```application.properties``` file.<br/> Additionally, appropriated database settings must be defined in the ```application.properties``` file. Than restart the app to fill data base with starter data.

I have created a Postman Collection of requests to test this app. It can be find by the link: [Postman Collection](https://github.com/matsevkoVM/PostmanCollections) 
I have implemented JWT for securing access and authentication. Consequently, the 'Log In' method is the only available option for unauthorized users.
Any of three predefined users can be used for signing in.<br/> After signing in, it is necessary to copy the token from the response body and paste it into the Authorization tab of the required method with the type 'Bearer Token'.<br/> Every other method exept "Log In" requires authorization. While some methods are developed for the USER role, others can only be run with the ADMIN role.<br/> To change the role just relogin with appropriate user. 
USER role methods are: 
<ul>
  <li>Users Related</li>
    <ul>
      <li>Get User</li>
      <li>Get All Users</li>
      <li>Create New User</li>
    </ul>
  <li>ToDo Related</li>
    <ul>
      <li>Get ToDo</li>
      <li>Get All ToDos</li>
      <li>Get All User's ToDod</li>
      <li>Create New ToDo</li>
      <li>Get All Collaborators</li>
    </ul>
  <li>Task Related</li>
    <ul>
      <li>Create Task</li>
      <li>Read Task</li>
      <li>Update Task</li>
        <ul>
          <li>(Can do only owner of Task's ToDo)</li>
        </ul>
      <li>Delete Task</li>
        <ul>
          <li>(Can do only owner of Task's ToDo)</li>
        </ul>
      <li>Gat All Tasks</li>
    </ul>
</ul>
All other methods (related with editing and removing Users or ToDos) are allowed to be run by the ADMIN role.

There are three predefined users in the DB with roles ADMIN and USER.

| Login         | Password | Role  |
|---------------|:--------:|:-----:|
| mike@mail.com |   1111   | ADMIN |
| nick@mail.com |   2222   | USER  |
| nora@mail.com |   3333   | USER  |
