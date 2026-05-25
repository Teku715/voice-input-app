$headers = @{"Authorization" = "Bearer sk-ffzpunutxfeipjuhabhlqhwfngabcqrcqowgxacnjkknttqm"}
$resp = Invoke-WebRequest -Uri "https://api.siliconflow.cn/v1/audio/transcriptions" -Method POST -Headers $headers
$resp.StatusCode
$resp.Content