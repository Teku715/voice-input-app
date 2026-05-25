# Download a small test wav first
$audioPath = "$env:TEMP\test_audio.wav"
Invoke-WebRequest -Uri "https://filehelper.tools.baidu.com/resource/assets/audio/15s.wav" -OutFile $audioPath

# Test with curl
curl.exe -X POST "https://api.siliconflow.cn/v1/audio/transcriptions" ^
  -H "Authorization: Bearer sk-ffzpunutxfeipjuhabhlqhwfngabcqrcqowgxacnjkknttqm" ^
  -F "file=@$audioPath" ^
  -F "model=FunAudioLLM/SenseVoiceSmall"