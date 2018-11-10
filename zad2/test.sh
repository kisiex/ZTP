#!/usr/bin/bash

echo "$(curl -X GET http://localhost:8080/servlet110092?n=500)" 
echo "$(curl -X POST http://localhost:8080/servlet110092?n=500)"

read -p "Press enter to continue"
