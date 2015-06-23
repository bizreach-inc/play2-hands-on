%~d0
cd %~d0%~p0
java -cp h2-1.4.177.jar org.h2.tools.Server -tcp -web -baseDir .
@if errorlevel 1 pause