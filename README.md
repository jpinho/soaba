## SOABA - A Service Oriented Architecture for Building Automation ##
##

 ![Building Automation](https://bitbucket.org/repo/enyMp6/images/4034431923-Screen%20Shot%202014-11-19%20at%2017.21.27.png)

###### *Illustration/Photo Credit: Compexin Experience Innovation – http://compexin.ro*


### Project Goals ###

Development of Gateway Driver for interaction with KNX devices. The Gateway Driver will be configured from a XML file containing all the KNX devices address and their underlying types. 

The GatewayDriver will be accessible via HTTP by exposing a REST interface, receiving and sending serialized JSON data. The Web Prototype App will make use of this REST interface to control and expose the Gateway Driver functionalities.

The developed architecture will allow new drivers to be written for the system, exposing data points from different types of gateways via different kinds of protocols, not only KNX via TCP/IP sockets.

### Components and Objectives ###

### 1. Implementation of a Gateway Driver to Interact with KNX Devices ###

##### Objectives #####

- Establish communication with KNX devices through an Ethernet KNX Gateway
- Provide datapoint reading functionality from devices by address/group address
- Provide datapoint writing functionality from devices by address/group address
- Support basic datapoint data types, such as percentage (integer ∈ [0-100]), float and
switch (boolean)

### 2. Development of a REST Interface to Interact with the KNX Gateway ###

##### Objectives #####

- Support read operation from datapoint address/group address
- Support write operation to datapoint address/group address
- Support gateway awareness of the devices set available, based on a XML file containing
the KNX addresses, specifying their respective types (switch, percentage or float)
- Support configuration reading operation

### 3. Develop an HTML5 Prototype Web App to demonstrate the system ###

##### Objectives #####

- Read the configuration of the Gateway Driver REST Interface available at a predefined IP Address
- Display all the visual controls required to interact with the KNX devices

#### Rights and License ####

This repository is a contribution to the Open Source Community, fill free to use this code how you like.
If you have any difficulties putting this together, please drop me an email at jpe[dot]pinho[at]gmail[dot]com.

P.S: Sorry for the markdown notation on my email, but net bots are crazy this days.