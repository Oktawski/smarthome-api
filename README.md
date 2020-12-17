# iot-server
Java Spring + Arduino + ESP8266

Spring server to communicate with Arduino devices via WiFi module.\
User needs to create account, and each user can have own WiFi devices.\

Devices can be added, removed, edited and turned by user. Light's color and intesity can be changed.\

Security is implemented via JWT Token, each user is given token once logged in.\
User is authenticated with each request and each user can modify only it's own devices.\

In my other repository is Android application to communicate with this server: (https://github.com/Oktawski/android-iot-app)
