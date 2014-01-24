#!/usr/bin/python
import simplejson as json
import random
import time
from engine import *

print "Testing SicsthSense python module..."

e = Engine("localhost:8080")
#e = Engine("presense.sics.se:8080")
print e.hostname
username = "newuser"+str(random.randint(0,99))
newUserId = 2 #e.registerUser('{"username": "'+username+'", "email":"'+username+'@anon.com"}')
token = "0f593a09-19e6-4ae4-84e6-08fb99336dc2"

e.setUserId(newUserId)
e.setToken(token)
print "User ID:",newUserId," token: ",token

# create a resource
resourceLabel = "demo"+str(random.randint(0,99))
#newresource = {"label": resourceLabel,"polling_url":"http://130.238.8.151:8888/test.json","polling_period":0}
newresource = {"label": resourceLabel}
jsonstr = json.dumps(newresource)
print jsonstr

resourceId = e.createResource(jsonstr)
print "Made resource: "+str(resourceId);

# Use auto creation of streams and parsers
if False:
    tempname = "temperature"+str(random.randint(0,20)) 
    for x in range(0,10):
        data = { tempname : random.randint(0,20) }
        datastr = json.dumps(data)
        print "Sending....",datastr
        result = e.postResourceData(resourceId,json.dumps(data))
        print result
        time.sleep(2)


# Manually create stream and parser
if True:
        # Create streams in resource
	if True:
                newstream = { "description": "input1" }
		streamjsonstr = json.dumps(newstream)
		#print streamjsonstr
		antStreamId1 = e.createStream(resourceId,streamjsonstr)
		print "Made ant stream: "+str(antStreamId1)+" - "+streamjsonstr;

                newstream = { "description": "input2" }
		streamjsonstr = json.dumps(newstream)
		#print streamjsonstr
		antStreamId2 = e.createStream(resourceId,streamjsonstr)
		print "Made ant stream: "+str(antStreamId2)+" - "+streamjsonstr;

                newstream = { "description": "group median", "function": "median", "antecedents": [antStreamId1,antStreamId2] }
		streamjsonstr = json.dumps(newstream)
		#print streamjsonstr
		streamId = e.createStream(resourceId,streamjsonstr)
		print "Made stream: "+str(streamId)+" - "+streamjsonstr;

	# Create a Parser for this resource
	if False:
		newparser = { "streamId":streamId, "input_parser":"/tets" }
		parserjsonstr = json.dumps(newparser)
		#print parserjsonstr
		newId = e.createParser(resourceId,parserjsonstr)
		print "new parser ID: "+str(newId);

        if True:
		# POST data to made stream
		for x in range(0,10):
			data = {"value": str(random.randint(0,99))}
			result = e.postStreamData(resourceId,antStreamId1,json.dumps(data))

			data = {"value": str(random.randint(0,99))}
			result = e.postStreamData(resourceId,antStreamId2,json.dumps(data))
			print result
			#print json.dumps(json.loads(result), sort_keys = False, indent = 4)
                        time.sleep(2)


# GET data
if False:
    result = e.getStreamData(resourceId,streamId)
    print json.dumps(json.loads(result), sort_keys = False, indent = 4)

# delete resource and everything (streams and parsers) under it
if False:
    print "Now deleting it all..."
    e.deleteResource(resourceId)


