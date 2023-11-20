# Stardust
  
### A 2D arcade shooter where enemies from historic arcade titles combine forces in an attempt to destroy you! Features clones of classics including Missile Command, Space Invaders, Gradius, and Sinistar, all haphazardly thrown into an endless asteroid field to harass the player.
   
  
You can download the latest build [here](https://github.com/kakdoncheng/stardust/releases/latest).
   
  
### Controls
   
  
Aim - Mouse  
Move/Strafe - W, A, S, D  
Fire Primary - Space/LMB  
Fire Secondary - RMB  
Bullet Time - LShift  
Rainbows - Enter/Return  
  
Debug controls are enabled by default:  
  
Give Power - F  
Disable Hitbox - G  
Enable Hitbox - H  
Jump to Stage - <0-9>, LShift+<0-9>  
   
  
### Bulding from source

***Requires JDK 8 to compile.***
  
Windows:  
  
Use `build.cmd`, then run with `run.cmd`.  
  
Linux:  
  
`javac` arguments:  
`-cp "lib/lwjgl2/lwjgl.jar;lib/lwjgl2/lwjgl_util.jar;"`  
`-d bin`  
`-encoding UTF8`  
  
Launching:  
`java -Djava.library.path=natives/linux -cp "bin:lib/lwjgl.jar:lib/lwjgl_util.jar" main.Launcher`  




