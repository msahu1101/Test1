param (
   $AppResourceGroupName,
   $AppName,
   $Envionment,
   $NetworkEnvionment
)

$secretName = $Envionment+"-keyvault-appregistration-key"

$keyvaultname = "cigloyaltypub-uw-kv-"+$NetworkEnvionment
$keyvaultRg = 'cigloyaltyKeyVault-uw-rg-'+$NetworkEnvionment

$secretId = az keyvault secret show -n $secretName --vault-name $keyvaultname --query "id" -o tsv
$principalId = az functionapp identity show -n $AppName -g $AppResourceGroupName --query principalId -o tsv


pip install --upgrade pip
pip show cryptography
pip install --upgrade cryptography
pip show cryptography



az keyvault set-policy -n $keyvaultname -g $keyvaultRg --object-id $principalId --secret-permissions get
az functionapp config appsettings set -n $AppName -g $AppResourceGroupName --settings "keyVault.client.key=@Microsoft.KeyVault(SecretUri=$secretId^^)"
