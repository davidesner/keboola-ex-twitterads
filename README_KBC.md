## TwitterAds extractor
TwitterAds extractor docker component for Keboola Connection.

## Functionality
The component allows retrieving campaign performance data from Twitter Ads API Analytics endpoints. Specifically it utilizes TwiterAds Asynchronous Analytics endpoint to retrieve historical data in large amounts and high granularity.

The datasets produced by this component is divided in two categories: Entity data which contains metadata for supported entities and Performance data which contains particular performance metrics in chosen granularity and sliced by chosen dimension / entity scope.

The application allows to retrieve data for multiple Twitter accounts belonging to single customer.

### Entities
**List of supported entity datasets:**

- **Campaign** ([campaigns](https://dev.twitter.com/ads/reference/post/accounts/%3Aaccount_id/campaigns))
- **Line Item** ([LineItem](https://dev.twitter.com/ads/reference/post/accounts/:account_id/line_items))

For more information on Twiter ads entities please refer to [campaigns](https://dev.twitter.com/ads/campaigns).

### Performance Data

As stated earlier, the component uses Asynchronous analytics endpoint to retrieve large volumes of performance data.

The metrics contained within the resulting data are determined by the dimension level specified by the chosen entity. Metrics are divided into groups and some of the metric groups are available only for certain levels.

This version of component supports only `Campaign` and `Line Item` levels. 

####Metrics
Here is a list of supported metrics divided into groups.

Please note that metric groups
`WEB_CONVERSION` , `MOBILE_CONVERSION` and `LIFE_TIME_VALUE_MOBILE_CONVERSION` are not supported yet.

For complete information about metrics in Twitter Analytics please refer to [metrics-and-segmentation](https://dev.twitter.com/ads/analytics/metrics-and-segmentation)
and [metrics-by-objective](https://dev.twitter.com/ads/analytics/metrics-by-objective)

**NOTE:** The component does not allow to specify metric segmentation and hence it always returns unsegmented data.

## Configuration

### Parameters

- **Access Token **– (REQ) Your TwitterAds API access token 
- **Consumer Secret**– (REQ) Your TwitterAds consumer secret 
- **Access Token Secret **–	(REQ)  TwitterAds API access token secret. 
- **Consumer Key – **(REQ)TwitterAds Consumer Key 
- **Twitter Account Names** – List of account names related to the Twitter Ads API credentials. Account names can be found in the Twitter UI. Note that account names must be specified **exactly** as in the UI.  
- **Changed since* ** – (REQ) starting date from where the data will be retrieved in format `yyyy-mm-dd` Affects both `Entity` and `Performance Data` datasets. Only records that changed since this	specified date will be retrieved. For more information see the	footnote. 
- **Granularity – ** Specify	granularity of the performance data. Hourly and Daily granularity is supported. 
- **Include deleted entities –** specifies whether data for deleted entities will be included. 
- **Entity datasets to download – ** List the entity datasets to	download. 
- **Storage upload mode** – (DEFAULT _INCREMENTAL_) specifies whether to upload incrementally. If set to _INCREMENTAL_,	the primary keys must be specified. 
- **Data since last run* –** determines whether the component should retrieve only data changed since the last run on	each consecutive run. 

***NOTE: ** Because the `Change since` parameter affects both the `Entity` and the `Performance` data it is recommended to set the parameter as far in past as possible. This way it is ensured to retrieve all Entities required, although the first run might take a bit longer. When `Data since last run` parameter is set to true, each
consequent run retrieves only records that have changed from the previous run. That means that each consequent run the `Changed since` parameter is ignored. To override this behaviour, set `Data since last run` to false.
