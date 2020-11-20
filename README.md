# README #

An example to demonstrate how spring can handle STOMP messages.

* stomp-chat-auth -- authorization server with embedded keycloak
* stomp-chat-client -- console client built with spring-shell
* stomp-chat-core -- common dependencies to read properties
* stomp-chat-server -- STOMP server with authorization. Server uses RabbitMQ to store messages

## stomp-chat-auth

To run server use mvn spring-boot:run. Server configured to use port 8083, by default it runs on
http://localhost:8083/auth 

Configuration uses embedded database, so all changes will be cleared on restart.

Server creates one realm _stompchat_ and one user to administrate realm login _chat-admin_, password _chat_.
Server creates secured clientId _chat-cli_ with root URL "http://localhost:8084" if someone will need test web client.

ClientId then used by _stomp-chat-client_ project. Startup configuration also creates 2 demo chat users, they are 
_cu1_, password is _cu1_ and _cu2_, password is _cu2_

## stomp-chat-server

STOMP server which checks authorization on connect. Server also use RabbitMQ relay to process topics and queues.

Inbound message destinations:

* /direct/{user}.message --> destination which handles direct messages, each message router to RabbitMQ queue /queue/{user}.messages
* /message/{room} --> destination which handles message to room, room is created when the first message sent. Topic is emulated by RabbitMQ topic exchange. 
All users which are joined some room receive message  

You need running RabbitMQ with STOMP Plugin enabled on port 61613 https://www.rabbitmq.com/stomp.html

## stomp-chat-client

Console stomp client. Client supports direct messages and rooms.

To run use mvn command or java -jar after package. 

You need to authorize in stomp-chat-auth at first, than can send messages. To direct messages server use queues, 
to handle room messages server use topics. Sample use case is 

1. login cu1 cu1 --> request access token from keycloak
2. connect --> first user connect to chat server with access token
3. to cu2 'Hello to cu2' --> message to cu2

Then from another stomp-chat-client (just use another console)

1. login cu2 cu2 --> request access token from keycloak
2. connect --> second user connect to chat server with access token
3. received message from cu1 which was send when cu1 was offline

### Helpful urls

https://stackoverflow.com/questions/50573461/spring-websockets-authentication-with-spring-security-and-keycloak
https://stackoverflow.com/questions/45405332/websocket-authentication-and-authorization-in-spring
https://www.baeldung.com/websockets-api-java-spring-client
https://dzone.com/articles/multi-threading-in-spring-boot-using-completablefu
https://kiberstender.github.io/miscelaneous-spring-websocket-stomp-specific-user/
https://docs.spring.io/spring-framework/docs/5.0.0.M4/spring-framework-reference/html/websocket.html



