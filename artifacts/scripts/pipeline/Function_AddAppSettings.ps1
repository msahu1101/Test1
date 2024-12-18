param (
   [Parameter(Mandatory=$true)]
   $FunctionResourceGroup,
   [Parameter(Mandatory=$true)]
   $FunctionName,
   [Parameter(Mandatory=$true)]
   $CommaSeparatedKeyValues
)

$KeyValues = $CommaSeparatedKeyValues.Split(",")
foreach ($KeyValue in $KeyValues) {
   az functionapp config appsettings set --name $FunctionName --resource-group $FunctionResourceGroup --settings "$KeyValue"
}
