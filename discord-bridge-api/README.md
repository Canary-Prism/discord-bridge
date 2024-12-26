## discord-bridge-api

the api module for Discord Bridge

you don't,,, have to declare a dependency on this module, you can depend on `discord-bridge` directly which brings in all implementations by default, and that is customisable  
but you may still want to specifically only depend on the api if say you're making your own library and don't want to bring in any implementations

the api design is largely inspired by Javacord with some parts modified to be closer to official api terminology
and some parts modified to intentionally be different from api terminology when i feel stuff is better communicated this way