# RemoteConsole
A simple remote console for Bukkit/Spigot servers designed to aid in debugging plugins in development on a remote host. This console works in reverse from what you might expect. There is no console server on your Minecraft server. The RemoteConsole plugin is a client that connects to the RemoteConsole server running on your computer. This allows a connection via whatever outgoing port is free on the Minecraft server and helps prevents unauthorized connections since the RemoteConsole plugin is incapable of accepting them. Unfortunately, this also means a basic understanding of networking and port forwarding is required.

The gradle file included with the source can be used to build the jar file. The output jar file is both a Bukkit/Spigot plugin and a console program.

## Installing on your Bukkit/Spigot server
 * Include a copy of the jar file in your servers 'plugins' folder and start the server to create the 'plugins/RemoteConsole' folder and create an empty config.yml file.
 * Turn off the server. Alternatively, you can manually create the 'plugins/RemoteConsole' folder and config.yml file.
 
 * Open the config.yml file and add the following (No tabs, follow YAML file conventions)

        servers:
          servername:
            address: 'your ip address'
            port: portNumber`

`servername` can be whatever you want to name your computer. The `address` is the ip address of your computer. `port` is the port number that your RemoteConsole server will be listening on.

## Running the RemoteConsole server on your computer
 * Include a copy of the jar file on your local computer and open a command prompt or shell in the same folder.
 
 * Run the following command: `java -jar RemoteConsole.jar -port <portNumber>` where `portNumber` is the port your computer will listen on for the connection from the Minecraft server.
 
 * Make sure your port forwarding is properly setup to forward the connection from your router to your computer.
 
And last but not least, don't forget to turn your Minecraft server back on. =p 

## Plugin Dependencies
 * [NucleusFramework](https://github.com/JCThePants/NucleusFramework)
 
## Console Dependencies
 * None



