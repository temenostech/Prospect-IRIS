NordeaDec2014
=============

Prospect - simple address validation

jBC example:
p="vejnavn=Fægangen,husnr=1,etage=1,dør=13,postnr=4180"
CALLJ "com.temenos.demo.nordea.ValidateAddress","validate", p SETTING ret
CRT ret
