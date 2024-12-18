package com.mgmresorts.loyalty.data.support;

public interface AzureSerchSupportTestData {
     String AZURE_SEARCH_NAME = "profilesearch";
     String AZURE_SEARCH_INDEX = "profiledob-index";
     String baseURL = "https://"+ AZURE_SEARCH_NAME+ ".search.windows.net/indexes/"+AZURE_SEARCH_INDEX+"/docs?api-version=2019-05-06&search=";
     String QUERY_TYPE = "&queryType=full&$count=false";
     String FIRST_NAME_FIELD = "customer_profiles%2Ffirst_name%3A%22";
     String LAST_NAME_FIELD = "customer_profiles%2Flast_name%3A%22";
     String STREET_1_FIELD = "addresses%2Fstreet_1%3A%22";
     String CITY_FIELD = "addresses%2Fcity%3A%22";
     String STATE_FIELD ="addresses%2Fstate%3A%22";
     String POSTAL_CODE_FIELD = "addresses%2Fpostal_code%3A%22";
     String EMAIL_FIELD = "email_addresses%2Femail%3A%22";
     String PHONE_FIELD = "phone_numbers%2Fphone_number%3A%22";
     String FIRST_NAME = FIRST_NAME_FIELD + "prem%22";
     String LAST_NAME = LAST_NAME_FIELD + "chitikeshi%22";
     String STREET_1 = STREET_1_FIELD + "123+Main+St%22";
     String CITY = CITY_FIELD +"Las+Vegas%22";
     String STATE = STATE_FIELD + "NV%22";
     String POSTAL_CODE = POSTAL_CODE_FIELD + "89139%22";
     String EMAIL = EMAIL_FIELD + "pchitikeshi%40mgmresorts.com%22";
     String PHONE = PHONE_FIELD +"1234567891%22";
}
