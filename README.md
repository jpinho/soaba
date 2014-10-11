## SOABA KNX - A Service Oriented Architecture for Building Automation ##

### Project Goals ###

Development of Gateway Driver for interaction with KNX devices. The Gateway Driver will be configured from a XML file containing all the KNX devices address and their underlying types. 

The GatewayDriver will be accessible via HTTP by exposing a REST interface, receiving and sending serialized JSON data. The Web Prototype App will make use of this REST interface to control and expose the Gateway Driver functionalities.

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

## Author ##

João Pinho