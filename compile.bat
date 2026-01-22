@echo off
if not exist build\classes mkdir build\classes
dir /s /b src\*.java > sources.txt
javac -cp "libs/HytaleServer.jar;lib/*" -d build/classes @sources.txt
if %errorlevel% neq 0 (
    echo Compilation Failed!
    echo Please ensure HytaleServer.jar is in the 'libs/' folder.
    exit /b %errorlevel%
)
copy src\hytale.json build\classes\hytale.json
copy src\hytale.json build\classes\manifest.json
echo Compilation Successful!
jar cvf ServerWebLink.jar -C build/classes .
echo Build finished: ServerWebLink.jar
