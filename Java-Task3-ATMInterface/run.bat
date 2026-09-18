@echo off
cd /d "%~dp0"
javac Main.java ATM.java Bank.java Account.java Transaction.java Input.java DataStore.java
if errorlevel 1 (
    echo Compilation failed. Make sure Java JDK is installed.
    pause
    exit /b 1
)
java Main
pause