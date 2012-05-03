How to build Module for Platform
- it is only for modules are needed to using Android build system 
- Please check its own install informaion under its folder for other module.


[Step to build]

1. Get original android open source.
    : version info - Android IceCreamSandwich 4.0.3
    ( Download site : http://source.android.com )

2. Copy module that you want to build - to original android open source
   If same module exist in android open source, you should replace it. (no overwrite)
   
   # It is possible to build all modules at once.
   
3. Check module is in 'build\core\user_tags.mk'
   If not, you should add your module name in it.
ex1) external\libjpega : Write "libjpega \" into "build\core\user_tags.mk"
ex2) external\iproute2 : Write "ip \" into "build\core\user_tags.mk"  


4. In case of 'external\bluetooth', 
   you should add following text in 'build\target\board\generic\BoardConfig.mk'

BOARD_HAVE_BLUETOOTH := true
BOARD_HAVE_BLUETOOTH_BCM := true

5. execute build command
   - make update-api
   - make