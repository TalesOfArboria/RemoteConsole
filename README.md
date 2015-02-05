# RemoteConsole
A simple remote console for Bukkit/Spigot servers designed to aid in debugging plugins in development on a remote host. This console works in reverse from what you might expect. There is no console server on your Minecraft server. The RemoteConsole plugin is a client that connects to the RemoteConsole server running on your computer. This allows a connection via whatever outgoing port is free on the Minecraft server and helps prevents unauthorized connections since the RemoteConsole plugin is incapable of accepting them. Unfortunately, this also means a basic understanding of networking and port forwarding is required.

The gradle file included with the source can be used to build the jar file. The output jar file is both a Bukkit/Spigot plugin and a console program.

## Installing on your Bukkit/Spigot server
 * Include a copy of the jar file in your servers 'plugins' folder and start the server.

 * Create a new connection to your computer using the following chat command: `/rcon add <computerName> <address> <port>` where `computerName` is a unique name for the connection, `address` is the IP that the remote console on your computer can be reached at, and `port` is the port number your remote console will be listening on.

## Running the RemoteConsole server on your computer
 * Include a copy of the jar file on your local computer and open a command prompt or shell in the same folder.
 
 * Run the following command: `java -jar RemoteConsole.jar -port <portNumber>` where `portNumber` is the port your computer will listen on for the connection from the Minecraft server.
 
 * Make sure your port forwarding is properly setup to forward the connection from your router to your computer.
 
## Connect to remote console from Bukkit/Spigot server
After you have setup your local RemoteConsole server and your remote Bukkit/Spigot server, you simply need to run the following command from the Bukkit/Spigot server to connect: `/rcon connect <computerName>` where `computerName` is the name you assigned when you added the connection.

## Other commands

If you need the connection to auto connect when the Bukkit/Spigot server starts, use the following command: `/rcon startup <computerName> true`

If you need to list the current stored remote consoles: `/rcon list`

Delete a remote console: `/rcon del <computerName>`

Disconnect from a remote console `/rcon disconnect <computerName>`

## Build dependencies
See the [gradle script](https://github.com/JCThePants/RemoteConsole/blob/master/build.gradle) for build dependencies.

## Plugin dependencies
 * [NucleusFramework](https://github.com/JCThePants/NucleusFramework)

## Console dependencies
 * None



