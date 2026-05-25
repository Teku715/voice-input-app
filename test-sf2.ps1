$headers = @{"Authorization" = "Bearer sk-ffzpunutxfeipjuhabhlqhwfngabcqrcqowgxacnjkknttqm"; "Content-Type" = "application/json"}
$body = @"
{"model":"FunAudioLLM/SenseVoiceSmall","input_file_url":"https://filehelper.tools.baidu.com/resource/assets/audio/15s.wav"}
"@
$resp = Invoke-WebRequest -Uri "https://api.siliconflow.cn/v1/audio/transcriptions" -Method POST -Headers $headers -Body ([System.Text.Encoding]::UTF8.GetBytes($body))
$resp.StatusCode
$resp.Content