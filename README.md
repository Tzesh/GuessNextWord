# GuessNextWord
A simple project to understand basics and differences of TCP/UDP. This project is the homework of Computer Networks course.

### What is this?
Actually, this project allows you to:
* Host a game/server to allow 2 clients communicate
* Join a game/server and become a client to communicate another client

It's obvious that the game is about guessing a new world using the previous one that given by the rival of yours, but the thing is you have to guess a world that starts with 2 letters which are the 2 last letters of previous word. If you can't manage to guess in 30 seconds then your opponent wins the game.

### Usage
You have 2 options:
1. Just use the latest release of the project, or
2. Clone the repository then build the project with `gradlew build` command.

Since it's a command line application, simply just use a command line (git bash recommended due to it has UTF-8 support) -command prompt on Windows is not recommended because it does not support UTF-8 characters- to run .jar file. As follows:

![java command](https://imgur.com/42KJUCr.png)

You can open theoretically as much as git bash as you wish, but we'll need 3 of them. One is for hosting, rest of for playing the game. You have to login as administrator according the password that you've used for hosting the server.

![logging as administrator](https://i.imgur.com/SvB2H2s.png)

Then you can start the game:
- Either in English: `start the game EN`
- Or in Turkish: `start the game TR`

We have such 2 use cases due to lowering the words has to be applied according to their locales.

A simple game can be seen as below:

![simple game in TR](https://i.imgur.com/fc10LU7.png)

You can type 'quit' to disconnect from server.
