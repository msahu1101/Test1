param (
    $ResourceName,
    $ResourceType,
    $ApiVersion,
    $ResourceGroupForResource,
    $ResourceGroup,
    $AppName,
    $Subscription,
    $ConfigKey,
    $Variable
)

connect-azaccount -identity
set-azcontext "$Subscription"
if ($ResourceGroupForResource -eq $null) {
    Write-Host "ResourceGroupForResource argument value not found. Adding ResourceGroup value within this........."
    $ResourceGroupForResource=$ResourceGroup
}
$keys = Invoke-AzResourceAction -Action listKeys -ResourceType "$ResourceType" -ApiVersion "$ApiVersion" -ResourceGroupName $ResourceGroupForResource -Name "$ResourceName" -Force
$requested = $keys.psobject.properties.Where({$_.name -eq $Variable}).value
write-host "Requested: $requested"
az functionapp config appsettings set -n $AppName -g $ResourceGroup --settings "$ConfigKey=$requested"
