## SOABA - A Service Oriented Approach for Building Automation ##
##
[
![Building Automation](https://bitbucket.org/repo/enyMp6/images/1779593493-Screen%20Shot%202015-03-11%20at%2001.45.50.png)
](http://soaba.icodebox.net)

### Project Goals ###
 
Development of a Gateway Driver for interaction with KNX devices. The Gateway Driver is configurable from a XML file containing all the KNX device addresses and their underlying types. The Gateway Driver will be accessible via HTTP by exposing a REST interface, receiving and sending serialized JSON data. The Web Prototype App will make use of this REST interface to control and expose the Gateway Driver functionalities.

### Components and Objectives ###

* **Gateway Driver**, for interaction with KNX devices. A gateway driver main purpose is to abstract the complexities of dealing with a particular type of gateway, thus abstracting their subjacent particularities of communication and so forth. With this in mind the proposed objectives for a gateway driver, specific for the KNX technology are:
– To establish a communication with KNX devices through an ethernet KNX gateway
– Support data point reading functionality from de- vices by address/group address
– Support data point writing functionality from de- vices by address/group address
– Support for manipulating basic data point data types, such as percentage, float and boolean
#
* **RESTful Service**, for remote interaction with the system. Gateway drivers by them selfs are not enough, over that some orchestration of the available drivers are need to make the complete architecture abstract from it’s implementation. The RESTful Service developed provides and demonstrates that abstraction, by dealing with data points and gateways that are completely independent of their representative technology. This service exposes basic operations to read and control data points, such as:
– Read operation from data point address/group address
– Write operation to data point address/group ad- dress
– Gateway awareness of the devices bound to it, based on a XML file containing the data point addresses, specifying their respective data types and source gateways
#
* **HTML5 Demo Application**, for demonstration of the system. This application exposes some of the basic applications that this system as to offer, and demonstrates how easy their are to develop, by depending only on the data point concept. This demo contains the following functionalities:
 – List all the data points configured in the application
 – Interface for reading and manipulating data point values
 – Sample application to preview meteo station data points, such as outside temperature, exterior entalpia and absolute/relative humidity
 – Sample application to preview energy measure- ments from meters installed on nucleus 14
 – Sample application to control switchs, HVACs, blinders and dimmers

### SOABA Screen Shots ###

**Management Interface - Data Points Listing**
#
![Management Interface 1](https://bitbucket.org/jpinho/soaba/downloads/mminterface1.png)
#
**Management Interface - Data Points read/write Interface**
#
![Management Interface 2](https://bitbucket.org/jpinho/soaba/downloads/mminterface2.png)
#
#
**Applications**
#
**Meteo Station**
#
![Meteo Station](https://bitbucket.org/jpinho/soaba/downloads/meteo-station.png)
#
**Energy Viewer**
#
![Energy Viewer](https://bitbucket.org/jpinho/soaba/downloads/energyviewer.png)
#
**Control Panel**
#
![Control Panel](https://bitbucket.org/jpinho/soaba/downloads/controlpanel.png)
#

### Available Documentation ###

Checkout SOABA's Website: [soaba.icodebox.net](soaba.icodebox.net).

- [Initial Project Report](https://bitbucket.org/jpinho/soaba/downloads/MEIC-TP-AI-66047_FINAL.pdf)!

- [Project Presentation](https://bitbucket.org/jpinho/soaba/downloads/slides.pdf).

### Rights and License ###

This repository is a contribution to the Open Source Community, feel free to use this code how you like.
If you have any difficulties putting this together, please drop me an email at jpe[dot]pinho[at]gmail[dot]com.