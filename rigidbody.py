#!/usr/bin/python3
import sys
import json

# The first argument is always the script name
sys.argv.pop(0)

# A list of possible help parameters
helplist = ["h", "-h", "--h", "/h", "help", "-help", "--help", "/help", "?", "-?", "--?", "/?"]


def printUsage():
	print("Usage: rigidbody.py <file name> - Converts a box-2d json to a destsol json")


def main():

	if len(sys.argv) < 1:
		printUsage()
		return
	elif sys.argv[0] in helplist:
		printUsage()
		return

	with open(sys.argv[0]) as json_data:
		json_dict = json.load(json_data)

	if "rigidBodies" in json_dict:
		json_dict["rigidBody"] = json_dict["rigidBodies"]
		del json_dict["rigidBodies"]

	if type(json_dict["rigidBody"]) is list:
		rigidbody_dict = json_dict["rigidBody"][0]
		json_dict["rigidBody"] = rigidbody_dict

	if "imagePath" in json_dict["rigidBody"]:
		del json_dict["rigidBody"]["imagePath"]

	if "name" in json_dict["rigidBody"]:
		del json_dict["rigidBody"]["name"]

	json_to_write = json.dumps(json_dict, indent=4, sort_keys=True)

	with open(sys.argv[0], 'w') as file:
		file.write(json_to_write)


main()
