echo off
cls
echo Calling matlab evaluation ...
REM cd c:\matlab6p5\work\1dayprediction
REM matlab -nosplash  -r metaScript

cd c:\matlab6p5\work\5dayprediction
matlab -nosplash  -r GAMetaScript(1)
matlab -nosplash  -r reEvaluateOriginalWinnerNet

echo Matlab called ... calling mailer....
cd c:\eclipse\workspace\___CCAPI

set CLASSPATH=%CLASSPATH%;c:\work\javamail-1.3.2\mail.jar;c:\work\jaf-1.0.2\activation.jar;.
cd bin
REM java Examples.NeuralAnalysisMailer
