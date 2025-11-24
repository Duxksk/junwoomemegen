@ECHO OFF

SET DIRNAME=%~dp0
IF "%DIRNAME%" == "" SET DIRNAME=.
SET APP_HOME=%DIRNAME%
SET DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

IF NOT "%JAVA_HOME%" == "" (
  SET JAVACMD="%JAVA_HOME%\bin\java.exe"
) ELSE (
  SET JAVACMD=java
)

IF NOT EXIST %JAVACMD% (
  ECHO ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
  EXIT /B 1
)

SET CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

%JAVACMD% %DEFAULT_JVM_OPTS% -classpath %CLASSPATH% org.gradle.wrapper.GradleWrapperMain %*
