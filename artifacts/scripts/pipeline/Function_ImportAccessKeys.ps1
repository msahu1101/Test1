$subscriptionId = az account show --query id -o tsv
$resourceGroup = $args[0]
$webAppName = $args[1]

$resourceId = "/subscriptions/$subscriptionId/resourceGroups/$resourceGroup/providers/Microsoft.Web/sites/$webAppName"
Write-Host "Endpoint prefix: " $resourceId
$defaultToken = az rest --method post --uri "$resourceId/host/default/listKeys?api-version=2018-11-01"  --query functionKeys.default
Write-Host "Default token : " $defaultToken
$defaultTokenTrimmed = $defaultToken.Replace("`"","")
Write-Host "Access token: " $defaultTokenTrimmed

$masterToken = az rest --method post --uri "$resourceId/host/default/listKeys?api-version=2018-11-01"  --query masterKey
$masterTokenTrimmed = $masterToken.Replace("`"","")
Write-Host "Access token: " $masterTokenTrimmed

Write-Host "Functions APP URL: " $env:AZUREFUNCTIONAPP_APPSERVICEAPPLICATIONURL

Write-Host "##vso[task.setvariable variable=TARGET_APP_SERVICE_ENDPOINT]$env:AZUREFUNCTIONAPP_APPSERVICEAPPLICATIONURL"
Write-Host "##vso[task.setvariable variable=TARGET_APP_SERVICE_ENDPOINT_AUTH_TOKEN]$defaultTokenTrimmed"
Write-Host "##vso[task.setvariable variable=TARGET_APP_SERVICE_ENDPOINT_MASTER_TOKEN]$masterTokenTrimmed"
