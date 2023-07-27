# Client-Server-Socket-Programming
<h1 align="center">Pub/Sub Middleware Architecture Implementation</h1>

## Objective

The main objective of this project is to design and implement a Pub/Sub middleware architecture. The implementation aims to showcase client-server socket communication and support multiple concurrent client connections. Clients can act as either Publishers or Subscribers as per their choice.

## Task Outline

The assignment is divided into three tasks:

### Task 1: The Client-Server Application
Implement a client-server socket application. The server listens for connections on a predefined PORT, and clients can connect to the server using its IP and PORT as command-line arguments. Communication between the client and server is done through a Command Line Interface (CLI). The Client perpetually runs until the user types the "terminate" keyword, at which point it disconnects from the server and terminates.

### Task 2: Publishers and Subscribers
Enhance the client-server implementation to handle multiple concurrent client connections. Clients can act as either "Publisher" or "Subscriber" based on a third command-line argument. Messages sent by "Publisher" clients are echoed to all "Subscriber" clients' terminals.

### Task 3: Publishers and Subscribers Filtered on Topics/Subjects
Improve the implementation from Task 2 to include "topic/subject" based message filtering between Publishers and Subscribers. Clients can specify the topic/subject of interest as a fourth command-line argument. Messages from Publishers are routed to relevant Subscribers based on the specified topic.

## How to Use

1. Clone the repository to your local machine.
2. Compile and run the server application by providing the desired PORT as a command-line argument.
3. Compile and run the client application by providing the Server IP, Server PORT, and the role (Publisher or Subscriber) as command-line arguments.
4. Follow the CLI prompts to communicate between the server and clients.

