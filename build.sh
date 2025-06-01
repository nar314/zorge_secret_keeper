#!/bin/bash

mvn clean package assembly:single

ls -la target/*.jar