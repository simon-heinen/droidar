@echo off

::Used to supress a warning. 
set CYGWIN=nodosfilewarning

%CYGWIN_PATH%\bin\bash --login -i %CD%\./compile.sh
