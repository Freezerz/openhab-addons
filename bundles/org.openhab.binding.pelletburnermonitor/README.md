# PelletBurnerMonitor Binding

This binding is for communicating with pelletburners which use the NBE software.
This includes Woody, Scotty and perhaps others.
This should work as long as the pelletburner is connected to the local network through either wifi or LAN.

_If possible, provide some resources like pictures, a YouTube video, etc. to give an impression of what can be done with this binding. You can place such resources into a `doc` folder next to this README.md._

## Supported Things

Currently pelletburners with software version 13.1005 are supported.
Other versions might work also, but issues are likely to occur across major versions.

Only a subset of the readable values are currently implemented.
No writing/setting of values are implemented yet.

Verified: Woody BS+ 16kW with software version V13.1005.9

## Discovery

Discovery of pelletburner happens based on the Thing configuration.
In the simplest case only IP and serial are necessary to specify.
Broadcast discovery is not yet implemented.

## Binding Configuration

None yet.

## Thing Configuration

To configure pelletburner, create a thing-file with the following contents.
pelletburnermonitor:pelletburner_nbe_v13_1005:myburner     [ remoteaddress="W.X.Y.Z", serial="123456", refresh="9" ]

pelletburner_nbe_v13_1005 is the Thing specifying which protocol version is to be used
"W.X.Y.Z" is the IP address of the pelletburner on the local network
"123456" is the serial number of the pelletburner
"9" is the refresh interval of minutes (the time between the binding fetches data from the pelletburner)

Check the Setup part of the pelletburner screen/app for IP, software version and serial number. 

## Channels

| channel  | type   | description                  |
|----------|--------|------------------------------|
| boilerCurrentTemperature | Number | This is the current temperature  |
| boilerTargetTemperature | Number | This is the target temperature  |
| boilerLimitTemperatureBelow | Number | This is the temperature difference at which the burner starts  |
| boilerLimitTemperatureAbove | Number | This is the temperature difference at which the burner stops |
| siloContents | Number | This is the amount of pellets in the silo  |
| siloMinimumContents | Number | This is the warning limit for amount of pellets in silo  |
| augerConsumption | Number | This is the consumption of the auger  |
| boilerCleaningCountdown | Number | This is amount of pellets to burn before ash tray should be cleaned  |
| consumptionPreviousHour | Number | This is the consumption of pellets during the previous full hour  |
| boilerPowerOutputPercentage | Number | This is the power output in percentage (%) |
| boilerPowerOutputKilowatts | Number | This is the power output in kW  |
| alarmCode | Number | This is the status code of the burner |
| alarmText | String | This is the text corresponding to the status code  |
| timestampLastUpdate | DateTime | This is the time for the last update when fetching from server  |
| refillSilo | Switch | This is on if silo should be refilled  |

## Full Example

###Example pelletburner.things

```
pelletburnermonitor:pelletburner_nbe_v13_1005:myburner     [ remoteaddress="W.X.Y.Z", serial="123456", refresh="9" ]
```

###Example pelletburner.items

```
Number BoilerCurrentTemperature     "Current temperature [%.1f °C]"     { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerCurrentTemperature" }
Number BoilerTargetTemperature      "Target temperature [%.1f °C]"      { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerTargetTemperature" }
Number BoilerLimitTemperatureBelow  "Lower temperature limit [%.1f °C]" { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerLimitTemperatureBelow" }
Number BoilerLimitTemperatureAbove  "Upper temperature limit [%.1f °C]" { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerLimitTemperatureAbove" }

Number BoilerContents               "Silo contents [%.0f kg]"           { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:siloContents" }
Number BoilerMinimumContents        "Silo warning limit [%.0f kg]"      { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:siloMinimumContents" }
Number AugerConsumption             "Auger consumption [%.0f g]"        { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:augerConsumption" }
Number CleaningCountdown            "Empty ash tray after [%.0f kg]"    { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerCleaningCountdown" }
Number ConsumptionPreviousHour      "Consumption last hour [%.3f kg]"   { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:consumptionPreviousHour"}

Number BoilerPowerOutputPercentage  "Output [%d %%]"                    { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerPowerOutputPercentage" }
Number BoilerPowerOutputKilowatts   "Output [%.0f kW]"                  { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:boilerPowerOutputKilowatts" }

Number AlarmCode                    "Alarm code [%d]"                   { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:alarmCode" }
String AlarmText                    "Alarm text [%s]"                   { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:alarmText" }
DateTime LastUpdate                 "Last update [%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS]" <time> { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:timestampLastUpdate" }
Switch RefillSilo                   "Should silo be refilled"           { channel="pelletburnermonitor:pelletburner_nbe_v13_1005:myburner:refillSilo" }
```

###Example pelletburner.sitemap

```
sitemap demo label="Main Menu"
{
                Text label="Pelletburner" icon="heating"
        {
                Frame label="Status"
                {
                        Text item=AlarmCode
                        Text item=AlarmText
                        Text item=LastUpdate
                }

                Frame label="Output"
                {
                        Text item=BoilerPowerOutputPercentage
                        Text item=BoilerPowerOutputKilowatts
                }

                Frame label="Temperature"
                {
                        Text item=BoilerTargetTemperature
                        Text item=BoilerCurrentTemperature
                        Text item=BoilerLimitTemperatureBelow
                        Text item=BoilerLimitTemperatureAbove
                }

                Frame label="Silo"
                {
                        Text item=BoilerContents icon="chart"
                        Text item=BoilerMinimumContents
                        Text item=ConsumptionPreviousHour
                        Text item=RefillSilo label="Refill silo: [MAP(pelletburner.map):%s]" icon=siren
                }

                Frame label="Burner"
                {
                        Text item=AugerConsumption
                        Text item=CleaningCountdown
                }
        }
}
```

###Example pelletburner.map

```
ON=Yes
OFF=No
-=No data yet
```

## Any custom content here!

### General information

The functionality of this binding has been created mainly by reverseengineering using Wireshark to figure out parts of the protocol.
Only reading of data and not writing of data is implemented, as that would require getting hold of the documentation as I don't want to risk bricking my burner.

### Adding new products/models

If it's a completely different model, it should be necessary to create a new Thing.
If it's a different software model of an already implemented model, it should be enough to create a new set of subclasses for Protocol, Request, RequestType and Response.

### Troubleshooting

Sometimes the burner becomes unreachable.
In those cases it can be necessary to powercycle the burner; remove power cord from outlet, and after a few seconds reattach cord to outlet.